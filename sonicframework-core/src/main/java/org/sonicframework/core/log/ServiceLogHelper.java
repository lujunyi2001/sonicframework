package org.sonicframework.core.log;

import java.util.concurrent.ConcurrentHashMap;

import org.sonicframework.context.exception.DevelopeCodeException;

/**
* @author lujunyi
*/
public class ServiceLogHelper{

	private String requestId;
	private long start;
	private long end = -1;
	private ConcurrentHashMap<String, Step> stepMap = new ConcurrentHashMap<>();
	public ServiceLogHelper() {
	}
	public ServiceLogHelper(String requestId) {
		super();
		this.requestId = requestId;
		this.start = System.currentTimeMillis();
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public long stop() {
		if(this.end < 0) {
			this.end = System.currentTimeMillis();
		}
		return this.end - this.start;
	}
	public long reset() {
		long current = System.currentTimeMillis();
		long cost = current - this.start;
		this.start = current;
		return cost;
	}
	
	public long getRunningTime() {
		if(this.end < 0) {
			return System.currentTimeMillis() - this.start;
		}else {
			return this.end - this.start;
		}
	}
	
	public void startStep(String stepId) {
		stepMap.putIfAbsent(stepId, new Step());
	}
	public long stopStep(String stepId) {
		if(this.stepMap.containsKey(stepId)) {
			return this.stepMap.get(stepId).stopStep();
		}
		throw new DevelopeCodeException("未找到对应的日志step" + stepId);
	}
	public long getStepRunningTime(String stepId) {
		if(this.stepMap.containsKey(stepId)) {
			return this.stepMap.get(stepId).getRunntimeTime();
		}
		throw new DevelopeCodeException("未找到对应的日志step" + stepId);
	}
	
	class Step{
		private long stepStart;
		private long stepEnd = -1;
		public Step() {
			this.stepStart = System.currentTimeMillis();
		}
		
		public long stopStep() {
			if(this.stepEnd < 0) {
				this.stepEnd = System.currentTimeMillis();
			}
			return this.stepEnd - this.stepStart;
		}
		public long getRunntimeTime() {
			if(this.stepEnd < 0) {
				return System.currentTimeMillis() - this.stepStart;
			}else {
				return this.stepEnd - this.stepStart;
			}
		}
	}

}
