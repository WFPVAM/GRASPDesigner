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

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.frontlinesms.data.repository.UserDao;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldAndBinding;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.repository.SurveyDao;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * Class to represent dialog box  to receive name of a Form copied
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *   www.fabaris.it <http://www.fabaris.it/>  
 */
public class FormNamePanel extends JDialog {

	private static final long serialVersionUID = 1L;
	public String text = null;
	JTextField nameField = new JTextField(15);
	int counter = 0;
	Form clone;
	Form form;
	boolean check = false;
	Frame owner;
	VisualForm current;
	DrawingPanel pnDrawing;
	FormsEditorDialog mainFrame;
	
	public static int STATE_CANCEL = 0;

	public static int STATE_OK = 1;

	public int state = STATE_CANCEL;



	public FormNamePanel(Frame owner) {
		super(owner, true);
	}

	public void setNameForm(String msg,final VisualForm form,FormsEditorDialog mainframe) {//added by Fabaris_raji  15 may		
		// TODO Auto-generated method stub
		JButton okbtn;
		//JButton canclbtn;				
		okbtn = new JButton("OK");
		//canclbtn = new JButton("INSERT LATER");
		JPanel  mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 2));
		
		JPanel  txtPanel = new JPanel();				
		JLabel jl=new JLabel(msg);
		txtPanel.add(jl);
		txtPanel.add(nameField);
		mainPanel.add(txtPanel);
		
		JPanel bnPnl=new JPanel();
		bnPnl.add(okbtn);
		//bnPnl.add(canclbtn);
		mainPanel.add(bnPnl);		
		add(mainPanel);
		
	nameField.addKeyListener(new KeyListener() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				text = nameField.getText().trim();						
				form.setName(text);					
				state = STATE_OK;
				dispose();
				} 
	
		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}
	});

	okbtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {				
				text = nameField.getText().trim();						
				form.setName(text);					
				state = STATE_OK;
				dispose();
					
		}

	});

}
	
}
