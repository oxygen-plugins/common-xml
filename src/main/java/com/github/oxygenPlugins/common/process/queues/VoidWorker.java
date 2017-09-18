package com.github.oxygenPlugins.common.process.queues;

import javax.swing.SwingWorker;

import com.github.oxygenPlugins.common.process.log.ProcessLoger;


public abstract class VoidWorker extends SwingWorker<Void, Void> {
	
	private final ProcessLoger loger;

	public VoidWorker(ProcessLoger loger){
		this.loger = loger;
	}
	
	private VoidWorker nextWorker;
	
	public void execute(VoidWorker nextWorker){
		this.setNextWorker(nextWorker);
		this.execute();
	}
	
	public void setNextWorker(VoidWorker nextWorker){
		this.nextWorker = nextWorker;
	}
	
	public boolean hasNextWorker(){
		return this.nextWorker != null;
	}

	@Override
	protected void done() {
		super.done();
		if(hasNextWorker()){
			nextWorker.execute();
		} else {
			loger.end();
		}
	}
}
