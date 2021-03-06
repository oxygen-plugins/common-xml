package com.github.oxygenPlugins.common.process.queues;

import javax.swing.SwingWorker;

public abstract class SimpleTask<Input, Output> extends SwingWorker<Void, Void> implements _Task<Input, Output> {
	
	private Input input = null;
	private Output output = null;
	private String title;
	private final Class<Input> inputClass;
	private final Class<Output> outputClass;
	public SimpleTask(String title, Class<Input> inputClass, Class<Output> outputClass){
		this.title = title;
		this.inputClass = inputClass;
		this.outputClass = outputClass;
	}
	public void start(Input input) {
		this.input = input;
		this.execute();
	}

	public Output getOutput() {
		return this.output;
	}

	public boolean isFinished() {
		return this.getState().equals(SwingWorker.StateValue.DONE);
	}

	public abstract Output process(Input Input) throws Exception;
	
	public Class<Output> getOutputClass(){
		return this.outputClass;
	}
	public Class<Input> getInputClass(){
		return this.inputClass;
	}

	@Override
	protected Void doInBackground() {
		try {
			this.output = process(this.input);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("stopped by Exception");
			System.out.println("task: " + this.title);
			System.out.println(e.getMessage());
		}
		this.setProgress(100);
		return null;
	}
	public String toString(){
		return this.title;
	}
}
