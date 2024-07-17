package org.sonicframework.utils.dto;


import org.sonicframework.context.common.annotation.FieldMapper;
import org.sonicframework.context.common.annotation.Style;
import org.sonicframework.context.common.enums.Alignment;
import org.sonicframework.context.common.enums.Border;
import org.sonicframework.context.dto.BaseDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestDto2 extends BaseDto {

	private static final long serialVersionUID = 3523538263638783679L;
	@FieldMapper(field = "序号", order = 1
			, titleStyle = @Style(bgColor = "FF0000", borderTop = Border.THICK, borderBottom = Border.THICK
			, borderLeft = Border.THICK, borderRight = Border.THICK), 
			contentStyle = @Style(fgColor = "FF0000", fontColor = "00FF00"))
	private String str;
	@FieldMapper(field = "序号", titleGroups = {"情况", ""}, order = 2
			, titleStyle = {
					@Style(bgColor = "FF00FF", level = 1, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK, alignment = Alignment.CENTER)
					
			})
	private String str2;
	@FieldMapper(field = "状态", titleGroups = {"情况", "", "情况"}, order = 3
			, titleStyle = {
					@Style(bgColor = "FF00FF", level = 0, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 1, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 3, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 4, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK, alignment = Alignment.CENTER)
					
			}
			, contentStyle = @Style(fgColor = "FFFF00"))
	private String str3;
	@FieldMapper(field = "类型名称", titleGroups = {"情况", "", "情况", "类型"}, order = 4
			, titleStyle = {
					@Style(bgColor = "FF00FF", level = 0, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 1, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 3, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 4, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK, alignment = Alignment.CENTER)
					
			}
	)
	private String str4;
	@FieldMapper(field = "用途", titleGroups = {"情况", "", "情况", "类型"}, order = 5
			, titleStyle = {
					@Style(bgColor = "FF00FF", level = 0, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 1, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 3, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK)
					, @Style(bgColor = "FF00FF", level = 4, borderTop = Border.THICK, borderBottom = Border.THICK , borderLeft = Border.THICK, borderRight = Border.THICK, alignment = Alignment.CENTER)
					
			}
	)
	private String str5;
}
