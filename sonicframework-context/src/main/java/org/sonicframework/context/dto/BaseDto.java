package org.sonicframework.context.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
* @author lujunyi
*/
public abstract class BaseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
