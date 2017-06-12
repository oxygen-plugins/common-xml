package com.nkutsche.common.process.log;

import java.util.Date;

import com.nkutsche.common.process.exceptions.CancelException;


public class DefaultProcessLoger implements ProcessLoger {

	public void log(String message, boolean taskEnd) {
		log(message);
	}

	public void log(String message) {
		System.out.println(new Date().toString() + ": " + message);
	}
	
	
	
	public void log(Exception exception) throws CancelException {
		exception.printStackTrace();
	}

	public void log(Exception exception, boolean forceEnd)
			throws CancelException {
		log(exception);
	}

	public void end() {
		
	}


}
