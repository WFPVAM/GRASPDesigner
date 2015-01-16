package net.frontlinesms.plugins.forms.properties.tables.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LabelPropertyFieldDocument extends PlainDocument {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6124720084448924538L;
	private int limit;

	public LabelPropertyFieldDocument(int limit) {
		super();
		this.limit = limit;
	}
	
	@Override
	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
		if (str == null)
			return;
		if ((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, attr);
		}
	}
}
