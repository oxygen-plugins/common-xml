package com.github.oxygenPlugins.common.process.log;

import java.lang.instrument.Instrumentation;


public class DebugLogger extends DefaultProcessLoger implements ProcessLoger {

	
	private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
    	if(instrumentation != null){
    		return instrumentation.getObjectSize(o);
    	}
    	System.out.println("Instrumentation was not instanciated!");
    	return 0;
    }

}
