package net.frontlinesms.plugins.forms.ui.components;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ComponentPanel extends JPanel {
	private JLabel nameLabel;
	private JLabel iconLabel;
	private JLabel posLabel;
	public ComponentPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{30, 117, 268, 12};
		gridBagLayout.rowHeights = new int[]{22, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		posLabel = new JLabel("");
		GridBagConstraints gbc_posLabel = new GridBagConstraints();
		gbc_posLabel.fill = GridBagConstraints.VERTICAL;
		gbc_posLabel.insets = new Insets(0, 0, 0, 5);
		gbc_posLabel.gridx = 0;
		gbc_posLabel.gridy = 0;
		add(posLabel, gbc_posLabel);
		
		nameLabel = new JLabel("");
		nameLabel.setForeground(Color.BLUE);
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.fill = GridBagConstraints.BOTH;
		gbc_nameLabel.insets = new Insets(0, 0, 0, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		add(nameLabel, gbc_nameLabel);
		
		iconLabel = new JLabel("");
		GridBagConstraints gbc_iconLabel = new GridBagConstraints();
		gbc_iconLabel.anchor = GridBagConstraints.WEST;
		gbc_iconLabel.fill = GridBagConstraints.VERTICAL;
		gbc_iconLabel.gridx = 2;
		gbc_iconLabel.gridy = 0;
		add(iconLabel, gbc_iconLabel);
		
		
	}
	public JLabel getNameLabel() {
		return nameLabel;
	}
	public JLabel getIconLabel() {
		return iconLabel;
	}
	public JLabel getPosLabel() {
		return posLabel;
	}
	public JTextArea getTextArea() {
		//return textArea;
		return null;
	}
	

}
