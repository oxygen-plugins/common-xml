package com.github.oxygenPlugins.common.process.log;

public interface ProcessStateListener {
	void start();
	void setProcessState(double state, String message);
	void end();
	void end(Exception e);
}
