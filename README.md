# sonicframework使用说明

[TOC]

## 引入maven
- 修改parent
``` xml
<parent>
	<groupId>org.sonicframework</groupId>
	<artifactId>sonicframework-parent</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</parent>
```
- 引入依赖
``` xml
<dependency>
	<groupId>org.sonicframework</groupId>
	<artifactId>sonicframework-core</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

## 统一返回格式
所有ajax返回的controller类把@Controller改为@WebApiController
``` java
import org.sonicframework.core.webapi.annotation.WebApiController; 
@WebApiController
public class TestWebApiController {
	@PostMapping("test")
	public String test(String a, String password) {
		return "Hello World";
	}
}

```
然后正常返回数据，数据会自动封装成下面的格式
``` json
{
    "result": 0,
    "message": null,
    "data":"Hello World"
}

```
- **result**:返回码:0为正常，其他为异常
- **message**:错误信息，result不为0是不为null
- **data**:接口返回的数据，返回结果可以是任意类型，void结果为null，result为0代表成功返回，可以直接返回org.sonicframework.context.webapi.dto.ResultDto，则框架不会再封装
  <font color=#FF0000 >**注**:</font>如不需要自动封装统一结果，则可以在方法上添加注解@ResponseResult(switchType = SwitchType.OFF)
``` java
import org.sonicframework.core.webapi.annotation.WebApiController;
import org.sonicframework.context.common.constaints.SwitchType;
import org.sonicframework.core.webapi.annotation.ResponseResult;
@WebApiController
public class TestWebApiController {
	@ResponseResult(switchType = SwitchType.OFF)
    @GetMapping("download")
	public void download(HttpServletResponse response) {
		
	}
}
```

### 统一异常处理
如果处理失败（比如验证失败），直接抛出下面异常即可（需要自定义异常信息），返回结果result为异常代码，message为自定义的异常信息
| 异常类名                                     | 场景                                   |
| ---------------------------------------- | ------------------------------------ |
| org.sonicframework.context.exception. DataNotValidException | 数据校验异常，用于接口接收的数据不合法的异常               |
| org.sonicframework.context.exception. DataNotExistException | 数据不存在异常，用于系统中必需的数据不存在异常              |
| org.sonicframework.context.exception. DataCheckException | 数据检查异常，用于持久化或者业务逻辑处理中的数据异常           |
| org.sonicframework.context.exception. DataVersionException | 数据版本异常，用于重复提交等造成的要修改的数据版本和传入的数据版本不一致 |
| org.sonicframework.context.exception. ExportFailException | 导出异常，用于数据导出时发生的异常                    |
| org.sonicframework.context.exception. LogInFailException | 登录失败异常                               |
| org.sonicframework.context.exception. FileCheckException | 文件检查异常，用于导入或者系统存在的文件格式或者内容不合法的异常     |
| org.sonicframework.context.exception. ResourceNotEnoughException | 系统繁忙异常，用于系统资源达到阈值，再次申请资源被拒绝的异常       |
| org.sonicframework.context.exception. UploadFailException | 上传异常，用于文件上传时发生的异常                    |
| org.sonicframework.context.exception. ConvertFailException | 转换异常，用于数据进行转换时发生的异常                  |
| org.sonicframework.context.exception.NotLogInException | 未登录异常                                |
| org.sonicframework.context.exception.NoAuthException | 未授权异常                                |
<font color=#FF0000 >**注**:</font>如果未捕获异常，返回的结果result为500，message为异常的toString方法返回值。也可以自定义异常，只需要自定义异常类继承org.sonicframework.context.exception.BaseBizException类，并且自定义异常代码就可以
``` java
public class CustomerException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;
    
    //异常代码需大于10000
	public static final int CODE = 10000;
	public CustomerException (String message) {
		super(CODE, message);
	}
	public CustomerException (String message, Throwable t) {
		super(CODE, message, t);
	}
}

```

## 后台验证
系统支持hibernate-validation验证框架并且自定义了一些验证注解
在模型中定义验证
``` java 
import org.sonicframework.context.valid.annotation.DoubleValid;
import org.sonicframework.context.valid.annotation.IntegerValid;
import org.sonicframework.context.valid.annotation.StringType;

public class TestDto {

	@StringType(nullable = false, max = 50, blankable = false, fieldLabel = "显示标签1", groups = {})
	private String str;
	@DoubleValid(nullable = false, zeroable = false, fieldLabel = "显示标签2")
	private Double dou;
    @IntegerValid(nullable = false, zeroable = false, fieldLabel = "显示标签2")
	private Integer inte;

```
StringType属性
| 字段名      | 类型      | 默认值  | 说明        |
| -------- | ------- | ---- | --------- |
| nullable | boolean | true | 是否可以为null |
| blankable | boolean | true | 是否可以为空白字符串
| min | int | -1 | 最小长度，小于0不限制 | 
| max | int | -1 | 最大长度，小于0不限制 | 
| fieldLabel | string | |  字段名称，可以根据不同的验证情况显示不同的结果 | 
| message | string | |  验证最终返回信息，字段的完整验证信息，设置时fieldLabel不生效 | 
| groups | class数组 |  | 验证分组 | 
DoubleValid属性
| 字段名        | 类型      | 默认值  | 说明                                  |
| ---------- | ------- | ---- | ----------------------------------- |
| nullable   | boolean | true | 是否可以为null                           |
| zeroable   | boolean | true | 是否可以为0                              |
| min        | string  |      | 最小数值                                |
| max        | string  |      | 最大数值                                |
| intLen     | int     | -1   | 整数最大长度，小于0不限制                       |
| decimalLen | int     | -1   | 小数最大长度，小于0不限制                       |
| fieldLabel | string  |      | 字段名称，可以根据不同的验证情况显示不同的结果             |
| message    | string  |      | 验证最终返回信息，字段的完整验证信息，设置时fieldLabel不生效 |
| groups     | class数组 |      | 验证分组                                |
IntegerValid属性
| 字段名        | 类型      | 默认值  | 说明                                  |
| ---------- | ------- | ---- | ----------------------------------- |
| nullable   | boolean | true | 是否可以为null                           |
| zeroable   | boolean | true | 是否可以为0                              |
| min        | string  |      | 最小数值                                |
| max        | string  |      | 最大数值                                |
| intLen     | int     | -1   | 整数最大长度，小于0不限制                       |
| fieldLabel | string  |      | 字段名称，可以根据不同的验证情况显示不同的结果             |
| message    | string  |      | 验证最终返回信息，字段的完整验证信息，设置时fieldLabel不生效 |
| groups     | class数组 |      | 验证分组                                |

在方法参数中要加入@Valid
``` java
import org.springframework.validation.annotation.Validated;

    @RequestMapping("validDto")
    public String validDto(@Validated TestDto dto) {
		return "验证成功";
    }

```
手动验证
	org.sonicframework.utils. ValidationUtil的checkValid方法进行验证，验证失败会抛出org.sonicframework.context.exception. DataNotValidException异常

## 配置跨域
在application.yml中添加
``` yaml
sonicframework:
  corss-origin:
    enable: true
```
即可实现跨域

## 请求参数处理
### 空字符串转为null
在application.yml中添加
``` yaml
sonicframework:
  web:
    argument-blank-to-null: true
```
即可实现请求参数的空字符串自动转为null，默认为false
### 字符串自动trim
在application.yml中添加
``` yaml
sonicframework:
  web:
    argument-trim: true
```
即可实现请求参数的字符串自动进行trim惭怍，默认为false


## 统一日志
### 系统会自动打印：
- 入参的url、httpmethod、客户端ip、自定义用户信息、request的param、request的body
- 正常返回参数的类名、方法名、参数值、返回值、耗时(毫秒)
- 发生异常的类名、方法名、参数值、异常信息、耗时(毫秒)
  **注:**所有继承org.sonicframework.context.exception.BaseBizException和配置可接受异常的异常不会打印异常堆栈，否则会打印异常堆栈
  添加可接受异常列表的配置方法方法如下
  在application.yml中添加
``` yaml
sonicframework:
  weblog:
    exclude-exception:
    - test.TestException
```
### 如何关闭统一日志
在application.yml中添加
``` yaml
sonicframework:
  weblog:
    enable-around-log: false
```

### 自定义用户信息的使用
实现org.sonicframework.context.log.service.LogUserService接口并声明为spring的@Service服务
``` java
package org.sonicframework.context.log.service;

import org.sonicframework.context.log.dto.LogUserDto;

public interface LogUserService {

	/**
	 * 操作用户提供方法
	 * @return 当前用户
	 */
	LogUserDto getCurrentUser();
}

```
LogUserDto的声明如下:
``` java
package org.sonicframework.context.log.dto;

import org.sonicframework.context.dto.BaseDto;

/**
* @author lujunyi
*/
public class LogUserDto extends BaseDto {

	private String id;//用户id
	private String name;//用户姓名
	private String account;//用户账户名
	private String unitId;//用户部门id
	private String unitCode;//用户部门编码
	private String unitName;//用户部门名称
	private String region;//用户区域

}

```

## 自动记录操作日志
1. 添加服务
  实现org.sonicframework.context.log.service.SystemLogService接口并注册为spring的@Service服务
``` java
package org.sonicframework.context.log.service;

import org.sonicframework.context.log.dto.SystemLogDto;


public interface SystemLogService {

	/**
	 * 保存操作日志
	 * @param log 操作日志模型
	 */
	void saveLog(SystemLogDto log);
}

```
SystemLogDto的声明如下
``` java
package org.sonicframework.context.log.dto;

import java.util.Date;

import org.sonicframework.context.dto.BaseDto;

public class SystemLogDto extends BaseDto{
	private Long id;//id
    protected String operateId;//操作人id
    protected String operateAccount;//操作人账户名
    protected String operateName;//操作人姓名
    protected String operateDeptId;//操作人部门id
    protected String operateDeptName;//操作人部门名称
    protected Date operateTime;//操作时间
    protected String operateIp;//操作人ip
    protected String accessUrl;//访问url
    protected String moduleName;//模块名称
    protected String optType;//操作名称
    protected boolean fail;//操作是否失败
    protected long costTime;//耗时
    protected String content;//操作内容
    protected String header;//请求头访问参数
    protected String param;//请求参数
    protected String requestBody;//请求body
    protected String result;//返回结果

}
```
2. 在controller的方法中添加注解@SystemLog即可实现记录操作日志
``` java
package org.sonicframework.context.log.annotation;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SystemLog {
    /** 模块名称 */
    String module();
    /** 操作名称 */
    String optType();
    /** 操作内容 */
    String content() default "";
    /** 操作内容自动填充 */
    ContentParam[] param() default {};
    /** 略过request中的某些值 */
    String[] skipRequestParam() default{};
	
}
```
``` java
package org.sonicframework.context.log.annotation;

import org.sonicframework.context.common.annotation.Match;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ContentParam {
    /** 根据参数索引获取的参数是否是从request,为true时为request */
    boolean isRequest() default false;
    /** 获取参数的索引 */
    int index() default 0;
    /** 获取参数中的属性值,isRequest为true时从request中的parameter中获取 */
    String field() default "";
    /** 匹配值 */
    Match[] match() default {};
    /** 默认值,match中的值匹配不到时获取该值 */
    String defaultMatchVal() default "";
	
}

```

``` java
package org.sonicframework.context.common.annotation;

/**
 * @author lujunyi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Match {
    /** 匹配期望值 */
    String key();
    /** 匹配替换值 */
    String val();
}
```

**例**
``` java 
@SystemLog(module = "测试模块", optType ="保存", content = "{0}项目:{1},id:{2}",
            param = {
            		@ContentParam(index = 0, field = "id", isRequest = false, defaultMatchVal = "编辑",
							match = {@Match(key = "", val = "新增"), @Match(key = "null", val = "新增")}),
                    @ContentParam(index = 0, field = "name", isRequest = false),
            		@ContentParam(index = 0, field = "id", isRequest = false)
            })
@PostMapping("save")
public boolean save(@RequestBody Entity entity) {
	return true;
}
```
- SystemLog注解中的param属性是为自动构建content提供服务的
- param参数的下标获取的值会替换content中的{下标数字}
- ContentParam为根据参数获取值: index为参数索引，field为参数属性名称(为空则为本身)，isRequest标识该参数是否为request，defaultMatchVal需配合@Match使用，为根据从参数中获取的值匹配，匹配不到的采用defaultMatchVal的值
- match为匹配项，如果参数的值为key中的配置，则返回val的值



3.自定义content
调用org.sonicframework.context.log.SystemLogContextHolder的getIfPresent()方法即可获得SystemLogDto
<font color=#FF0000 >**注**:</font>只可以修改content

## 导入导出
### 定义导入导出Mapper
#### 定义FieldMapper
在数据模型的成员变量上定义org.sonicframework.context.common.annotation.FieldMapper注解
- **field:** 导入导出文件的字段名
- **dictName:** 导入导出字段对应的字典名
- **format: ** 时间类型的格式化字符串，用于导入时文件为字符串而本地成员变量为java.util.Date类型
- **action:** 定义导入导出的动作。由org.sonicframework.context.common.constaints.FieldMapperConst中的常量定义
    - MAPPER_BOTH:导入导出全解析，默认为全解析
    - MAPPER_IMPORT:导入时解析
    - MAPPER_EXPORT:导出时解析
- **serialize:** 自定义导出导入策略值为实现org.sonicframework.context.common.annotation.SerializeSupport的子类
``` java
package org.sonicframework.context.common.annotation;

/**
 * 序列化策略
 * @author lujunyi
 */
public interface SerializeSupport<F, T> {

	/**
	 * 导出时序列化
	 * 
	 * @param f
	 * @return
	 */
	T serialize(F f);

	/**
	 * 导入时序列化
	 * 
	 * @param t
	 * @return
	 */
	F deserialize(T t);

}
```

- **groups:** 导入导出分组

#### 定义ClassFieldMapper
在数据模型的类上定义org.sonicframework.context.common.annotation.ClassFieldMapper注解
- **local:** 数据模型的成员变量名
- **other:** 导入导出文件的字段名
- **dictName:** 导入导出字段对应的字典名
- **format: ** 时间类型的格式化字符串，用于导入时文件为字符串而本地成员变量为java.util.Date类型
- **action:** 定义导入导出的动作。由org.sonicframework.context.common.constaints.FieldMapperConst中的常量定义
    - MAPPER_BOTH:导入导出全解析，默认为全解析
    - MAPPER_IMPORT:导入时解析
    - MAPPER_EXPORT:导出时解析
- **serialize:** 自定义导出导入策略值为实现org.sonicframework.context.common.annotation.SerializeSupport的子类
- **groups:** 导入导出分组

### 数据字典模型
``` java
package org.sonicframework.context.dto;


/**
 * 数据字典模型
 * @author lujunyi
 */
public class DictCodeDto extends BaseDto implements Cloneable {

	private static final long serialVersionUID = -951174762682382675L;
	/**
	 * 字典id
	 */
	private String id;
	/**
	 * 字典父级code
	 */
	private String pcode;
	/**
	 * 字典类型
	 */
	private String type;
	/**
	 * 字典代码
	 */
	private String code;
	/**
	 * 字典名称
	 */
	private String value;
	/**
	 * 字典扩展类型1
	 */
	private String param1;
	/**
	 * 字典扩展类型2
	 */
	private String param2;
	/**
	 * 字典扩展类型3
	 */
	private String param3;
	/**
	 * 字典扩展类型4
	 */
	private String param4;
	/**
	 * 字典扩展类型5
	 */
	private String param5;
	/**
	 * 字典序号
	 */
	private int sort;
	/**
	 * 字典是否已删除
	 */
	private boolean deleted;
	/**
	 * 字典描述
	 */
	private String desc;
}
```

### 分页查询生产者
``` java 
package org.sonicframework.utils;

import java.util.List;

/**
 * 分页查询生产者
 * 
 * @author lujunyi
 */
public interface PageQuerySupport<T> {

	/**
	 * 根据页数获取数据
	 * 
	 * @param pageNum 页码，从1开始
	 * @return 当前页数据
	 */
	List<T> getPageContent(int pageNum);

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	int getPages();
}

```

### 导入导出Excel
#### 定义映射上下文MapperContext
1. 使用org.sonicframework.utils.mapper.MapperContext的newInstance方法获取MapperContext实例，参数如下:
- **clazz:** java.lang.Class类型，映射类名
- **dataSupplier:** java.util.function.Supplier类型，为上一个参数实例的生产者
- **dictProvider:** java.util.function.Function类型，为根据数据字典类型获取数据字典列表的提供者
    - dictProvider的入参为数据字典类型
    - dictProvider的返回值为数据字典列表List(数据字典列表不能为树形结构)
2. 设置导入导出分组(可选)
``` java
context.setGroups(Group1.class);
```
**注:** 如果设置值会把FieldMapper(ClassFieldMapper)中group包含设置值和group为空的值全都获取出来
3.设置是否验证(仅导入时有效)
``` java
context.setValidEnable(true);
```
**注:** 默认为不验证
#### 导入Excel
1. 定义MapperContext
2. 获取Workbook对象，可调用org.sonicframework.utils.excel.ExcelUtil的openExcel方法获取，方法参数如下:
- **is:** java.io.InputStream类型，输入流
- **fileName:** String类型，文件名
3. 调用org.sonicframework.utils.excel.ExcelUtil的importForEntity方法解析Excel，方法参数如下:
- **sheet:** org.apache.poi.ss.usermodel.Sheet类型，Sheet页
- **context:** org.sonicframework.utils.mapper.MapperContext类型字段映射上下文，字段映射上下文
- **consumer:** org.sonicframework.utils.ConsumerImpEntity类型，导入模型消费者。具体描述如下
    - t: 导入后的数据模型
    - r: org.sonicframework.utils.ValidateResult类型，验证结果(hibernate-validator验证)，成员变量如下
        - validResult: boolean类型，是否验证通过，如果没有设置验证则一直为true
        - validMessage: String 类型，验证失败信息
- **postMapper(可选):** org.sonicframework.utils.mapper.PostMapper类型，实体类映射后置方法。在解析设置模型成员变量之后，消费者消费之前调用。具体参数如下
    - t: 导入后的数据模型
    - o: org.sonicframework.utils.excel.ExcelRowContextVo类型，解析excel上下文，具体成员变量如下:
        - dataMap: Map<String, Object>类型，解析的数据map，key为列名，value为字段值
        - sheetIndex: int类型，sheet页码
        - sheetName: String类型，sheet页名
        - rowIndex: int类型，行号

#### 导出Excel
1. 定义MapperContext
2. 调用org.sonicframework.utils.excel.ExcelUtil的export导出Excel，方法参数如下:
- **sheet(可选):** org.apache.poi.ss.usermodel.Sheet类型，sheet页
- **context:** org.sonicframework.utils.mapper.MapperContext类型字段映射上下文，字段映射上下文
- **pageSupport:** org.sonicframework.utils.PageQuerySupport类型

### 导入导出空间数据
#### 导入导出shp
##### 安装依赖
pom需要添加依赖
``` xml
<dependency>
	<groupId>org.sonicframework</groupId>
	<artifactId>sonicframework-utils-geo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<scope>compile</scope>
</dependency>
```
##### 定义映射上下文GeoMapperContext
1. 使用org.sonicframework.utils.geometry.mapper.GeoMapperContext，参数如下:
- **clazz:** java.lang.Class类型，映射类名
- **dataSupplier:** java.util.function.Supplier类型，为上一个参数实例的生产者
- **dictProvider:** java.util.function.Function类型，为根据数据字典类型获取数据字典列表的提供者
    - dictProvider的入参为数据字典类型
    - dictProvider的返回值为数据字典列表List(数据字典列表不能为树形结构)
2. 设置导入导出分组(可选)
``` java
context.setGroups(Group1.class);
```
**注:** 如果设置值会把FieldMapper(ClassFieldMapper)中group包含设置值和group为空的值全都获取出来
3.设置是否验证(仅导入时有效)
``` java
context.setValidEnable(true);
```
**注:** 默认为不验证

##### 导入shp
1. 定义GeoMapperContext
2. 调用org.sonicframework.utils.geometry.ShapeUtil的extractInfoEntity方法解析shp文件，方法参数如下:
- **path:** String类型，shp文件路径，如果path为目录，则解析该目录下所有shp文件
- **context:** org.sonicframework.utils.geometry.mapper.GeoMapperContext类型字段映射上下文，字段映射上下文
- **consumer:** org.sonicframework.utils.ConsumerImpEntity类型，导入模型消费者。具体描述如下
    - t: 导入后的数据模型
    - r: org.sonicframework.utils.ValidateResult类型，验证结果(hibernate-validator验证)，成员变量如下
        - validResult: boolean类型，是否验证通过，如果没有设置验证则一直为true
        - validMessage: String 类型，验证失败信息
- **postMapper(可选):** org.sonicframework.utils.mapper.PostMapper类型，实体类映射后置方法。在解析设置模型成员变量之后，消费者消费之前调用。具体参数如下
    - t: 导入后的数据模型
    - o: org.sonicframework.utils.geometry.ShpInfoVo类型，解析shp上下文，具体成员变量如下:
        - recordList: shp文件行数据的列表，数据字段如下:
            - name: 字段名
            - value: 字段值
            - type: 字段类型
        - sourceName: String类型，shp名
        - dataIndex: int类型，行号
        - coordinateReferenceSystem: org.opengis.referencing.crs.CoordinateReferenceSystem类型，shp坐标系
        - geo: org.locationtech.jts.geom.Geometry类型，shp该行的空间数据
        - geoStr: String类型，shp该行的空间数据wkt格式

##### 导出shp
1. 定义MapperContext
2. 调用org.sonicframework.utils.geometry.ShapeUtil的buildNewExport构建导出工具，方法参数如下:
- **path:** String类型，shp文件目录
- **fileName:** String类型，生成文件名
- **context:** org.sonicframework.utils.geometry.mapper.GeoMapperContext类型字段映射上下文，字段映射上下文
- **geoKey:** String类型，空间数据字段名，必须为"the_geom"
- **charset:** String类型，字符集
  方法返回org.sonicframework.utils.geometry.ExportShp类型导出工具
3. 调用writePageData方法写入数据，参数为org.sonicframework.utils.PageQuerySupport类型，也可以调用write方法写入单条数据
4. 调用close方法关闭写入
5. 调用downloadZipAndDelete方法压缩下载并删除源文件，参数为javax.servlet.http.HttpServletResponse类型
6. 对于超长字段的处理可以调用ExportShp的setStringExceedLengthPolicy方法来设置处理策略，参数为org.sonicframework.utils.geometry.StringExceedLengthPolicy枚举类型，包含类型如下
    - DEFAULT: 默认策略，超出255字节会抛出异常
    - NULL: 超出255字节设置为null
    - SUBSTRING: 超出255字节则会截取
7. 对于导出时错误的处理可以调用ExportShp的setExportErrorPolicy方法来设置处理策略，参数为org.sonicframework.utils.geometry.ExportErrorPolicy枚举类型，包含类型如下
    - DEFAULT: 默认策略，发生错误整体抛出异常
    - SKIP: 忽略错误


#### 导入导出gdb
##### 安装依赖
需要安装gdal环境，支持操作“FileGDB”，并依赖gdal.jar
pom需要添加依赖
``` xml
<dependency>
	<groupId>org.sonicframework</groupId>
	<artifactId>sonicframework-utils-gdb</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<scope>compile</scope>
</dependency>
```
##### 定义映射上下文GeoMapperContext
1. 使用org.sonicframework.utils.geometry.mapper.GeoMapperContext，参数如下:
- **clazz:** java.lang.Class类型，映射类名
- **dataSupplier:** java.util.function.Supplier类型，为上一个参数实例的生产者
- **dictProvider:** java.util.function.Function类型，为根据数据字典类型获取数据字典列表的提供者
    - dictProvider的入参为数据字典类型
    - dictProvider的返回值为数据字典列表List(数据字典列表不能为树形结构)
2. 设置导入导出分组(可选)
``` java
context.setGroups(Group1.class);
```
**注:** 如果设置值会把FieldMapper(ClassFieldMapper)中group包含设置值和group为空的值全都获取出来
3.设置是否验证(仅导入时有效)
``` java
context.setValidEnable(true);
```
**注:** 默认为不验证

##### 导入gdb
1. 定义GeoMapperContext
2. 调用GeoMapperContext的setMapperName方法设置图层的名称。
3. 调用org.sonicframework.utils.gdb.GdbUtil的extractInfoEntity方法解析shp文件，方法参数如下:
- **path:** String类型，gdb文件路径，目录名以.gdb结尾
- **context:** org.sonicframework.utils.geometry.mapper.GeoMapperContext类型字段映射上下文，字段映射上下文
- **consumer:** org.sonicframework.utils.ConsumerImpEntity类型，导入模型消费者。具体描述如下
    - t: 导入后的数据模型
    - r: org.sonicframework.utils.ValidateResult类型，验证结果(hibernate-validator验证)，成员变量如下
        - validResult: boolean类型，是否验证通过，如果没有设置验证则一直为true
        - validMessage: String 类型，验证失败信息
- **postMapper(可选):** org.sonicframework.utils.mapper.PostMapper类型，实体类映射后置方法。在解析设置模型成员变量之后，消费者消费之前调用。具体参数如下
    - t: 导入后的数据模型
    - o: org.sonicframework.utils.geometry.ShpInfoVo类型，解析shp上下文，具体成员变量如下:
        - recordList: shp文件行数据的列表，数据字段如下:
            - name: 字段名
            - value: 字段值
            - type: 字段类型
        - sourceName: String类型，图层名
        - dataIndex: int类型，行号
        - coordinateReferenceSystem: org.opengis.referencing.crs.CoordinateReferenceSystem类型，shp坐标系
        - geo: org.locationtech.jts.geom.Geometry类型，shp该行的空间数据
        - geoStr: String类型，shp该行的空间数据wkt格式

##### 导出gdb
1. 定义MapperContext
2. 调用org.sonicframework.utils.gdb.GdbUtil的buildNewExport构建导出工具，方法参数如下:
- **path:** String类型，shp文件目录，以".gdb"结尾
- **srid:** int类型，导出espg
- **charset:** String类型，导出字符集
  方法返回org.sonicframework.utils.gdb.ExportGdb类型导出工具
3. 调用ExportGdb的createNewLayer方法创建图层，参数如下
- **layerName:** String类型，图层名
- **context:** org.sonicframework.utils.geometry.mapper.GeoMapperContext类型字段映射上下文，字段映射上下文
- **geoKey:** String类型，数据模型中空间数据的映射名
4. 调用writePageData方法写入数据，参数为org.sonicframework.utils.PageQuerySupport类型，也可以调用write方法写入单条数据
5. 调用close方法关闭写入
6. 调用downloadZipAndDelete方法压缩下载并删除源文件，参数为javax.servlet.http.HttpServletResponse类型
