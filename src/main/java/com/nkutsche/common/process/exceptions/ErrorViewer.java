package com.nkutsche.common.process.exceptions;


public interface ErrorViewer {
	public void viewException(Exception e) throws CancelException;
}
