package org.sonicframework.context.common.constaints;

/**
* @author lujunyi
*/
public interface FieldMapperConst {

	public final static int MAPPER_IMPORT = 1;
	public final static int MAPPER_EXPORT = 1 << 1;
	public final static int MAPPER_BOTH = MAPPER_IMPORT | MAPPER_EXPORT;
	
}
