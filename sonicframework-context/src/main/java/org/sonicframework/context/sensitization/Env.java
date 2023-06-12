package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public class Env {

	private int start;
	private int end;
	private String pattern = "@";
	private String mask = "*";
	private boolean maskRepeat = true;
	public Env() {
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public boolean isMaskRepeat() {
		return maskRepeat;
	}

	public void setMaskRepeat(boolean maskRepeat) {
		this.maskRepeat = maskRepeat;
	}

}
