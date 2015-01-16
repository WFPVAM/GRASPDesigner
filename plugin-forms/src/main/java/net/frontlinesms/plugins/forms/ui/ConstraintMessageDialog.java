package net.frontlinesms.plugins.forms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class ConstraintMessageDialog extends JDialog {

	private static final long serialVersionUID = 1134070716271846664L;
	
	private static final String TITLE =  "plugin.forms.binding.panelMessage.title";
	private static final String OK =	 "plugin.forms.binding.panelMessage.ok";
	private static final String CANCEL = "plugin.forms.binding.panelMessage.cancel";
	private static final String CLEAR =	 "plugin.forms.binding.panelMessage.clear";
	
	private final JPanel contentPanel = new JPanel();
	
	private JButton cancel = new JButton(InternationalisationUtils.getI18nString(CANCEL));
	
	private JButton clearMessage = new JButton(InternationalisationUtils.getI18nString(CLEAR));
	
	private JButton ok = new JButton(InternationalisationUtils.getI18nString(OK));
	
	private MessageTextArea messageArea = new MessageTextArea(76);
	
	public ConstraintMessageDialog() {
		super();
		this.setTitle(InternationalisationUtils.getI18nString(TITLE));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(500, 500);
		this.init();
		this.initListener();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - this.getWidth()) / 2;
		int y = (screenSize.height - this.getHeight()) / 2;
		this.setLocation(x, y);
		this.setVisible(true);
	}
	
	
	private void initListener(){
		this.clearMessage.addActionListener(new ClearMessageListener());
		this.ok.addActionListener(new SaveMessageListener());
		this.cancel.addActionListener(new CancelListener());
	}
	
	private void init(){
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 631, 402);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		JScrollPane scrollPane = new JScrollPane(messageArea);
		contentPanel.add(scrollPane);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.add(clearMessage);
		buttonPane.add(ok);
		getRootPane().setDefaultButton(ok);
		buttonPane.add(cancel);
	}
	
	private class ClearMessageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ConstraintMessageDialog.this.messageArea.setText("");
		}
	}
	
	private class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ConstraintMessageDialog.this.dispose();
		}
	}
	
	private class SaveMessageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}
	
}