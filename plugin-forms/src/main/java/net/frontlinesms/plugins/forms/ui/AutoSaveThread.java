package net.frontlinesms.plugins.forms.ui;


public class AutoSaveThread extends Thread {
	private FormsEditorDialog editorDialog;
	
	private boolean flag = true;
	
	private boolean stopAutoSave = false;
	
	private int time = -1;
	
	public AutoSaveThread(FormsEditorDialog editorDialog) {
		this.editorDialog = editorDialog;
		this.time = this.editorDialog.getAutoSaveTime();
	}
	
	@Override
	public void run() {
		while(flag) {
			try {
				Thread.sleep(this.time * 1000);
				editorDialog.refreshPreview();
				if(!stopAutoSave){
					editorDialog.autosave();
				}
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void stopAutoSave(){
		this.stopAutoSave = true;
	}
	
	public void kill(){
		this.flag = false;
	}
}
