package org.sonicframework.context.common.constaints;

/**
 * @author lujunyi
 */
public enum SwitchType {

	/**
	 *  默认
	 */
	DEFAULT(null),
	/**
	 *    开启
	 */
	ON(true),
	/**
	 *关闭
	 */
	OFF(false);
	
	private Boolean statusOn;
	
	private SwitchType(Boolean statusOn) {
		this.statusOn = statusOn;
	}
	
	public boolean isStatusOn(boolean defaultStatus) {
		if(statusOn != null) {
			return statusOn;
		}else {
			return defaultStatus;
		}
	}
}
