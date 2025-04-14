package org.sonicframework.core.fillup;

import org.springframework.expression.EvaluationContext;

/**
* 字典填充el表达式设置上下文服务接口
*/
public interface FillupExpressionContextProvider {

	void setExpressionContext(EvaluationContext context);

}
