package org.sonicframework.utils.sensitization;

/**
* @author lujunyi
*/
public class SensitizationContext {

	private SensitizationItemVo defaultSupport;
	private Boolean maskRepeat;
	private String mask;
	public SensitizationContext() {
	}
	public SensitizationItemVo getDefaultSupport() {
		return defaultSupport;
	}
	public void setDefaultSupport(SensitizationItemVo defaultSupport) {
		this.defaultSupport = defaultSupport;
	}
	public Boolean getMaskRepeat() {
		return maskRepeat;
	}
	public void setMaskRepeat(Boolean maskRepeat) {
		this.maskRepeat = maskRepeat;
	}
	public String getMask() {
		return mask;
	}
	public void setMask(String mask) {
		this.mask = mask;
	}

}
