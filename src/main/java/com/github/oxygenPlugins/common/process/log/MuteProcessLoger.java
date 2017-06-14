package com.github.oxygenPlugins.common.process.log;

import com.github.oxygenPlugins.common.process.exceptions.CancelException;

public class MuteProcessLoger implements ProcessLoger {

	public void log(String message, boolean taskEnd) {}

	public void log(String message) {}

	public void log(Exception exception) throws CancelException {
		throw new CancelException(exception.getMessage());
	}

	public void log(Exception exception, boolean forceEnd)
			throws CancelException {
		if(forceEnd){
			throw new CancelException(exception.getMessage());
		}
	}

	public void end() {}

}
