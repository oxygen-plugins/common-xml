package com.github.oxygenPlugins.common.process.exceptions;


public interface ErrorViewer {
	public void viewException(Exception e) throws CancelException;
}
