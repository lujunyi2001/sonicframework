package org.sonicframework.utils.dto;

import org.sonicframework.context.common.annotation.FieldMapper;


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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStiId() {
		return stiId;
	}

	public void setStiId(String stiId) {
		this.stiId = stiId;
	}

	public String getSAT_USEM_1() {
		return SAT_USEM_1;
	}

	public void setSAT_USEM_1(String sAT_USEM_1) {
		SAT_USEM_1 = sAT_USEM_1;
	}

	public String getSatUsemod() {
		return satUsemod;
	}

	public void setSatUsemod(String satUsemod) {
		this.satUsemod = satUsemod;
	}

	public Double getSatUseare() {
		return satUseare;
	}

	public void setSatUseare(Double satUseare) {
		this.satUseare = satUseare;
	}

	public String getSatMarker() {
		return satMarker;
	}

	public void setSatMarker(String satMarker) {
		this.satMarker = satMarker;
	}

	public String getProUsename() {
		return proUsename;
	}

	public void setProUsename(String proUsename) {
		this.proUsename = proUsename;
	}

	public String getProTitleuser() {
		return proTitleuser;
	}

	public void setProTitleuser(String proTitleuser) {
		this.proTitleuser = proTitleuser;
	}

	public String getStiPositionName() {
		return stiPositionName;
	}

	public void setStiPositionName(String stiPositionName) {
		this.stiPositionName = stiPositionName;
	}

	public String getProTouchname() {
		return proTouchname;
	}

	public void setProTouchname(String proTouchname) {
		this.proTouchname = proTouchname;
	}

	public String getProTouchtel() {
		return proTouchtel;
	}

	public void setProTouchtel(String proTouchtel) {
		this.proTouchtel = proTouchtel;
	}

	public String getSatApplydate() {
		return satApplydate;
	}

	public void setSatApplydate(String satApplydate) {
		this.satApplydate = satApplydate;
	}

	public String getSatPurposeName() {
		return satPurposeName;
	}

	public void setSatPurposeName(String satPurposeName) {
		this.satPurposeName = satPurposeName;
	}

	public Double getProUsearea() {
		return proUsearea;
	}

	public void setProUsearea(Double proUsearea) {
		this.proUsearea = proUsearea;
	}

	public Double getProUsesealine() {
		return proUsesealine;
	}

	public void setProUsesealine(Double proUsesealine) {
		this.proUsesealine = proUsesealine;
	}

	public String getStiEntryuser() {
		return stiEntryuser;
	}

	public void setStiEntryuser(String stiEntryuser) {
		this.stiEntryuser = stiEntryuser;
	}

	public String getStiEntryunit() {
		return stiEntryunit;
	}

	public void setStiEntryunit(String stiEntryunit) {
		this.stiEntryunit = stiEntryunit;
	}

	public String getStiEntrytime() {
		return stiEntrytime;
	}

	public void setStiEntrytime(String stiEntrytime) {
		this.stiEntrytime = stiEntrytime;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

}
