package com.github.oxygenPlugins.common.process.queues;

public abstract class ChangePropertyListener<T> implements WatchTask {
	
	private T oldValue = null;
	
	public ChangePropertyListener(){
		this.oldValue = getPropertyValue();
	}
	
	@Override
	public void watch() {
		T newValue = getPropertyValue();
		if(!newValue.equals(oldValue)){
			watch(oldValue, newValue);
			this.oldValue = newValue;
		}
	}
	
	public abstract T getPropertyValue();
	
	public abstract void watch(T oldvalue, T newValue);
}
