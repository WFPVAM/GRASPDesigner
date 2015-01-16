package net.frontlinesms.plugins.forms.ui;

import javax.swing.JTextArea;

public class MessageTextArea extends JTextArea {
	private static final long serialVersionUID = 1L;
	private int maxRows = 0;

	public MessageTextArea(int maxRows) {
		this.maxRows = maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public int getMaxRows() {
		return maxRows;
	}

	@Override
	public void replaceSelection(String content) {
		if (getMaxRows() > 0 && getLineCount() > getMaxRows()) {

			this.getToolkit().beep();
			return;
		}
		super.replaceSelection(content);
	}
}
