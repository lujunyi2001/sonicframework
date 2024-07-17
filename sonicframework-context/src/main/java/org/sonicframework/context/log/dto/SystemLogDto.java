package org.sonicframework.context.log.dto;

import java.util.Date;

import org.sonicframework.context.dto.BaseDto;

public class SystemLogDto extends BaseDto{
	private static final long serialVersionUID = -2816152981640325650L;

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
	protected String ext1;//扩展字段1
	protected String ext2;//扩展字段2
	protected String ext3;//扩展字段3
	protected String ext4;//扩展字段4
	protected String ext5;//扩展字段5
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOperateId() {
		return operateId;
	}
	public void setOperateId(String operateId) {
		this.operateId = operateId;
	}
	public String getOperateName() {
		return operateName;
	}
	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}
	public String getOperateDeptId() {
		return operateDeptId;
	}
	public void setOperateDeptId(String operateDeptId) {
		this.operateDeptId = operateDeptId;
	}
	public String getOperateDeptName() {
		return operateDeptName;
	}
	public void setOperateDeptName(String operateDeptName) {
		this.operateDeptName = operateDeptName;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public String getOperateIp() {
		return operateIp;
	}
	public void setOperateIp(String operateIp) {
		this.operateIp = operateIp;
	}
	public String getAccessUrl() {
		return accessUrl;
	}
	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	public boolean isFail() {
		return fail;
	}
	public void setFail(boolean fail) {
		this.fail = fail;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCostTime() {
		return costTime;
	}
	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getOperateAccount() {
		return operateAccount;
	}
	public void setOperateAccount(String operateAccount) {
		this.operateAccount = operateAccount;
	}
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
	public String getExt4() {
		return ext4;
	}
	public void setExt4(String ext4) {
		this.ext4 = ext4;
	}
	public String getExt5() {
		return ext5;
	}
	public void setExt5(String ext5) {
		this.ext5 = ext5;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	


}