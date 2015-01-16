/*
 *GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
 * Copyright © 2012 ,Fabaris s.r.l
 * This file is part of GRASP Designer Tool.  
 *  GRASP Designer Tool is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.  
 *  GRASP Designer Tool is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser  General Public License for more details.  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with GRASP Designer Tool. 
 *  If not, see <http://www.gnu.org/licenses/>
 */
package net.frontlinesms.plugins.forms.ui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Class to represent dialog box to receive name of a Form
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *         www.fabaris.it <http://www.fabaris.it/>
 * 
 */
public class JNamePanel extends JDialog {

	private static final long serialVersionUID = 1L;
	String text = null;
	JTextField nameField = new JTextField(15);
	FormsUiController ui;
	boolean flag = false;
	private FComponent comp;
	
	public JNamePanel(Frame owner) {
		super(owner, true);
	}
	
	public JNamePanel(Frame owner , FComponent comp){
		super(owner,true);
		this.comp = comp;
		this.nameField.addKeyListener(new NameKeyListener());
	}
	
	public void getName(String msg, final FComponent fc) {
		JButton okbtn;
		JButton canclbtn;
		okbtn = new JButton("OK");
		//canclbtn = new JButton("INSERT LATER");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 2));
		JPanel txtPanel = new JPanel();
		JLabel jl = new JLabel(msg);
		txtPanel.add(jl);
		txtPanel.add(nameField);
		mainPanel.add(txtPanel);
		JPanel bnPnl = new JPanel();
		bnPnl.add(okbtn);
		//bnPnl.add(canclbtn);
		mainPanel.add(bnPnl);
		add(mainPanel);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close(fc);
			}
		});
		
		okbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkName(fc);
			}
			
		});
	}
	
	private void close(final FComponent fc){
		FormsUiController.getInstance().getMainFrame().getDrawingPanel().getPnPreview().removeComponent(FormsUiController.getInstance().getSelectedComponent());
		dispose();
	}
	
	
	private void checkName(final FComponent fc) {
		text = nameField.getText().trim();
		Pattern p = Pattern.compile("[^a-zA-Z0-9 ]");
		Matcher m = p.matcher(text);
		boolean invalidCharacter = m.find();
		if (invalidCharacter || (text.equals(null))
				|| (text.length() == 0) || (text.length() > 15)) {
			JOptionPane.showMessageDialog(null,InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_INVALID).replace("\\n",System.getProperty("line.separator")));
			fc.setName("");
			nameField.setText("");
			dispose();
			FormsEditorDialog.createAndShowGUI(fc,false);
		} else {
			fc.setName(text);
			dispose();
		}
	}
	
	private class NameKeyListener implements KeyListener {
		public NameKeyListener() {
		}
		public void keyTyped(KeyEvent e) {
		}
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				checkName(comp);
			}
		}
		public void keyReleased(KeyEvent e) {
		}
	}
	
}