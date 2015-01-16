package net.frontlinesms.plugins.forms.ui;

import javax.swing.JDialog;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;

public class ConfirmDeleteControlDialog extends JDialog {
	public ConfirmDeleteControlDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(1,3));
	}

}
