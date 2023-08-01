package org.sonicframework.utils.dto;

import org.sonicframework.context.common.annotation.FieldMapper;
import lombok.Data;


@Data
public class CoordinateTableEntity{

    @FieldMapper(field = "ID")
    private String id;

    @FieldMapper(field = "STI_ID")
    private String stiId;

    @FieldMapper(field = "USEMODE")
    private String SAT_USEM_1;

    @FieldMapper(field = "用海方式")
    private String satUsemod;

    @FieldMapper(field = "方式面积")
    private Double satUseare;

    @FieldMapper(field = "面标识")
    private String satMarker;

    @FieldMapper(field = "项目名称")
    private String proUsename;

    @FieldMapper(field = "申请人")
    private String proTitleuser;

    @FieldMapper(field = "所属区域")
    private String stiPositionName;

    @FieldMapper(field = "姓名")
    private String proTouchname;

    @FieldMapper(field = "联系电话")
    private String proTouchtel;

    @FieldMapper(field = "申请日期",format = "yyyy-MM-dd")
    private String satApplydate;

    @FieldMapper(field = "用海类型")
    private String satPurposeName;

    @FieldMapper(field = "用海面积")
    private Double proUsearea;

    @FieldMapper(field = "海岸线")
    private Double proUsesealine;

    @FieldMapper(field = "录入人")
    private String stiEntryuser;

    @FieldMapper(field = "录入单位")
    private String stiEntryunit;

    @FieldMapper(field = "录入时间",format = "yyyy-MM-dd")
    private String stiEntrytime;

    //矢量
    @FieldMapper(field = "SHAPE")
    private String shape;

}
