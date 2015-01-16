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
/**
 *  @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 */
package net.frontlinesms.plugins.forms.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import net.frontlinesms.plugins.forms.FormFormula;
import net.frontlinesms.plugins.forms.exceptions.FormFormulaException;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;

import org.springframework.orm.jpa.JpaAccessor;

import com.sun.mail.handlers.text_html;
/**
@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
  www.fabaris.it <http://www.fabaris.it/>  
*/
public class FormulaEditorPanel extends JPanel implements MouseListener,
		KeyListener, HierarchyBoundsListener {

	public JTextArea textArea = new JTextArea();
	public JButton addButton = new JButton();
	public JButton substractButton = new JButton();
	public JButton divideButton = new JButton();
	public JButton multiplyButton = new JButton();
	public JButton openPButton = new JButton();
	public JButton closePButton = new JButton();
	//public JButton stringButton = new JButton();
	public JPanel buttonsPanel = new JPanel();
	public JPanel bottomPanel = new JPanel();
	public JButton okButton = new JButton();
	public JButton cancelButton = new JButton();
	public PreviewComponent forComponent;
	public FormulaEditorPanel() {
		init();
	}
	
	private void init(){
		addHierarchyBoundsListener(this);
		setBorder(BorderFactory.createTitledBorder("Formula Editor"));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));

		addButton.setSize(20, 20);
		addButton.setText("+");
		addButton.addMouseListener(this);
		addButton.setFocusable(false);
		substractButton.setSize(20, 20);
		substractButton.setText("-");
		substractButton.addMouseListener(this);
		substractButton.setFocusable(false);
		divideButton.setSize(20, 20);
		divideButton.setText("/");
		divideButton.addMouseListener(this);
		divideButton.setFocusable(false);
		multiplyButton.setSize(20, 20);
		multiplyButton.setText("*");
		multiplyButton.addMouseListener(this);
		multiplyButton.setFocusable(false);
		openPButton.setSize(20, 20);
		openPButton.setText("(");
		openPButton.addMouseListener(this);
		openPButton.setSize(20, 20);
		openPButton.setFocusable(false);
		closePButton.setText(")");
		closePButton.addMouseListener(this);
		closePButton.setSize(20, 20);
		closePButton.setFocusable(false);
//		stringButton.setText("[text]");
//		stringButton.setSize(40, 20);
//		stringButton.addMouseListener(this);
//		stringButton.setFocusable(false);
		
		buttonsPanel.add(addButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonsPanel.add(substractButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonsPanel.add(divideButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonsPanel.add(multiplyButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonsPanel.add(openPButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonsPanel.add(closePButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		//buttonsPanel.add(stringButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.setPreferredSize(new Dimension(345, 25));
		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(345, 100));
		textArea.setMaximumSize( new Dimension(400, 300));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setDragEnabled(true);
		try {
			textArea.getDropTarget().addDropTargetListener(new DropTargetListener() {
				public void dropActionChanged(DropTargetDragEvent dtde) {}
				public void drop(DropTargetDropEvent dtde) {
					textArea.setSelectionStart(textArea.getSelectionEnd());
				}
				public void dragOver(DropTargetDragEvent dtde) {}
				public void dragExit(DropTargetEvent dte) {}
				public void dragEnter(DropTargetDragEvent dtde) {}
			});
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		okButton.setText("Ok");
		okButton.addMouseListener(this);
		okButton.setFocusable(false);
		cancelButton.setText("Cancel");
		cancelButton.addMouseListener(this);
		cancelButton.setFocusable(false);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(okButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		bottomPanel.add(cancelButton);
		bottomPanel.setPreferredSize(new Dimension(345, 30));
		
		add(buttonsPanel);
		add(textArea);
		add(bottomPanel);
		add(Box.createVerticalGlue());
	}

	public void close(){
		//forComponent = null;
		FormsUiController.getInstance().hideFormulaEditor();
	}
	
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1){
			if(e.getSource().equals(cancelButton)){
				PreviewComponent selectedC = FormsUiController.getInstance().getMainFrame().getSelectedComponent();
				close();
				selectedC.setSelected(false);
				if(forComponent != null ){ 
					forComponent.setSelected(true);
				}
			}else if(e.getSource().equals(addButton)){
//				textArea.setText(textArea.getText() + "+");
				textArea.replaceSelection("+");
			}else if(e.getSource().equals(substractButton)){
//				textArea.setText(textArea.getText() + "-");
				textArea.replaceSelection("-");
			}else if(e.getSource().equals(divideButton)){
//				textArea.setText(textArea.getText() + "/");
				textArea.replaceSelection("/");
			}else if(e.getSource().equals(multiplyButton)){
//				textArea.setText(textArea.getText() + "*");
				textArea.replaceSelection("*");
			}else if(e.getSource().equals(openPButton)){
//				textArea.setText(textArea.getText() + "(");
				textArea.replaceSelection("(");
			}else if(e.getSource().equals(closePButton)){
//				textArea.setText(textArea.getText() + ")");
				textArea.replaceSelection(")");
			}
//			else if(e.getSource().equals(stringButton)){
//				String value = JOptionPane.showInputDialog(this, "What text to add?");
//				if (value != null){
//					value = value.replace("'", "''");
////					textArea.setText(textArea.getText() + "'"+value+"'");
//					textArea.replaceSelection("'"+value+"'");
//				}
//			}
			else if(e.getSource().equals(okButton)){
				try {
					FormFormula ff = FormFormula.parseFormula(textArea.getText(), forComponent, FormsUiController.getInstance().getMainFrame().getVisualForm());
					if(forComponent!=null){
						forComponent.setFormula(ff);
						PreviewComponent selectedC = FormsUiController.getInstance().getMainFrame().getSelectedComponent();
						if(selectedC!=null) selectedC.setSelected(false);
						forComponent.setSelected(true);
					}
					close();
//					System.out.println("---Printing formula tree---");
//					System.out.print(forComponent== null? "": "---for componet:"+forComponent.getComponent().getName()+"---\n");
//					System.out.println(ff.getFormulaTree());
//					System.out.println("Reconverting formula to String:"+ff.toFormulaString());
//					System.out.println(ff.toXFormString(forComponent));
				} catch (FormFormulaException e1) {
					System.err.println(e1.getMessage());
					if(e1.type== FormFormulaException.COMPONENT_NOT_FOUND){
						JOptionPane.showMessageDialog(this, "Component '"+e1.text+"' not found in the form.");
					}else if(e1.type == FormFormulaException.CYCLIC_FORMULA){
						JOptionPane.showMessageDialog(this, "Formula of field '"+e1.text+"' can not contain it self");
					}else if(e1.type == FormFormulaException.EMPTY_FORMULA){
						// do nothing
					}else if(e1.type == FormFormulaException.EMPTY_PARENTESIS){
						JOptionPane.showMessageDialog(this, "Formula can not contain empty parentesis.");
					}else if(e1.type == FormFormulaException.EXPECTED_APOS){
						JOptionPane.showMessageDialog(this, "Expected (') to close static text in formula");
					}else if(e1.type == FormFormulaException.EXPECTED_CLOSE_PARENTESIS){
						JOptionPane.showMessageDialog(this, "Missing "+e1.count+" closed parentesis.");
					}else if(e1.type == FormFormulaException.EXPECTED_RETURN_NUMBER){
						JOptionPane.showMessageDialog(this, "Formula returns text but number expected.");
					}else if(e1.type == FormFormulaException.EXPECTED_VALUE_AFTER_OPERATION){
						JOptionPane.showMessageDialog(this, "Expected value or field after operation sign '"+e1.text+"' in formula.");
					}else if(e1.type == FormFormulaException.EXPECTED_VALUE_BEFORE_OPERATION){
						JOptionPane.showMessageDialog(this, "Expected value or field before operation sign '"+e1.text+"' in formula.");
					}else if(e1.type == FormFormulaException.INVALID_CHAR){
						JOptionPane.showMessageDialog(this, "Invalid characted outside of text:'"+e1.chr+"'");
					}else if(e1.type == FormFormulaException.INVALID_OPERATION){
						JOptionPane.showMessageDialog(this, "Invalid operation using text operand:'"+e1.text+"'");
					}else if(e1.type == FormFormulaException.OPERATION_EXPECTED){
						JOptionPane.showMessageDialog(this, "Expected operation sign between formula operands.");
					}else if(e1.type == FormFormulaException.UNEXPECTED_CLOSE_PARENTESIS){
						JOptionPane.showMessageDialog(this, "Formula contain and unexpected closed parentesis");
					}else if(e1.type == FormFormulaException.UNEXPECTED_FOR_COMPONENT_TYPE){
						JOptionPane.showMessageDialog(this, "Field '"+forComponent.getComponent().getName()+"' of type '"+forComponent.getType().toString()+"' can not have a formula associated.");
					}else if(e1.type == FormFormulaException.UNSUPPOERTED_COMPONENT){
						JOptionPane.showMessageDialog(this, "Field '"+e1.formulaPart.component.getComponent().getName()+"' of type '"+e1.formulaPart.component.getType().toString()+"' can not be part of a formula.");
					}
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void ancestorMoved(HierarchyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void ancestorResized(HierarchyEvent e) {
		//System.out.println("Resized:"+e);
	}

}
