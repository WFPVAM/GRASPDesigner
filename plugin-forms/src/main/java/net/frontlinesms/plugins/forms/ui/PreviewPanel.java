/*
 * FrontlineSMS <http://www.frontlinesms.com>

 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.ui.FrontlineUI;
import net.frontlinesms.ui.SimpleConstraints;
import net.frontlinesms.ui.SimpleLayout;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * This class represents the Preview.
 * 
 * @author Carlos Eduardo Genz <li>kadu(at)masabi(dot)com
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *   www.fabaris.it <http://www.fabaris.it/>  
 */
public class PreviewPanel extends JPanel implements MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	//TODO:remember to manage the translation
	private static final String enumerator = "enumerator";
	private static final String date = "date";
	private static final String section0 = "section0";
        private static final String gps = "gps";
	private VisualForm form = null;
	private static final int GAP = 1; // GAP between the components
	private DragListener dragListener;
	private DragSource dragSource;
	
	public static int numMemberSeparator;
	public static int numMemberMultiline = 0;
	public static int numMemberSingleline;
	
	private int dragY = -1; // Coordinate to draw a line (helping user to drop
							// component)
	//private int lastSep = -1;
	//private int index = 0;
	
	/**
	 * Fabaris_a.zanchi this value is changed from 235 to 325 to achieve
	 * choerence with the value of 340 the DrawingPanel sets for this panel.
	 * 
	 * BUT WHY??
	 */
	// Fabaris_A.zanchi
	private DrawingPanel parentPnDrawing;

	// private int WIDTH = 325;
	private int WIDTH = 430;// Fabaris_raji - modified from 325 to 435 since
							// PreviePanel is modified from 340 to 450

	// Fabaris_a.zanchi field to determine is the preview panel is covered by a
	// repeatable preview panel
	private boolean isIdle = false;

	public PreviewPanel(DragListener dragListener, DragSource dragSource, DrawingPanel parent) {
		setLayout(new SimpleLayout());
		// We have to set the correct font for some languages
		TitledBorder titledBorder = new TitledBorder(InternationalisationUtils.getI18nString(FormsThinletTabController.COMMON_PREVIEW));
		titledBorder.setTitleFont(FrontlineUI.currentResourceBundle.getFont());
		setBorder(titledBorder);
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.dragListener = dragListener;
		this.dragSource = dragSource;
		// Fabaris_A.zanchi
		this.parentPnDrawing = parent;
		this.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, -2, GAP));
	}

	/**
	 * Show the supplied form on the preview.
	 * 
	 * @param form
	 */
	public void showForm(VisualForm form) {
		this.form = form;
		deselectAll();
		refresh();
		FormsUiController.getInstance().showProperties();
	}

	/**
	 * Adds the supplied component to the preview finding the right position
	 * according to the supplied coordinates.
	 * 
	 * @param comp
	 *            The component to be added.
	 * @param x
	 * @param y
	 */
	public void addComponent(PreviewComponent comp, int x, int y) {
		int index = getIndex(y);

		// checks if we are adding before the last default component
		int lastDefaultIndex = 0;
		int i = 0;
		for (PreviewComponent pc : form.getComponents()) {
			if (pc.isDefaultComponent())
				lastDefaultIndex = i;
			i++;

		}

		if (index == -1 || index >= form.getComponents().size()) {
			// We are adding to the end.
			form.getComponents().add(comp);
		} else {
			// We are adding to a specified position.
			form.getComponents().add(index, comp);
		}

		deselectAll();// Fabaris a.zanchi and Fabaris_m.cilione

		comp.setSelected(true); // Fabaris a.zanchi and Fabaris_m.cilione

		refresh();
	}

	/**
	 * This method finds the closest component to the supplied coordinate and
	 * returns its index.
	 * 
	 * @param y
	 * @return
	 */
	public int getIndex(int y) {
		int index = 0;
		for (PreviewComponent c : form.getComponents()) {
			Rectangle b = c.getDrawComponent().getBounds();
			if (y > b.y && y < b.y + b.height + GAP) {
				// We find the component
				if (y < b.y + ((b.height + GAP) / 2)) {
					// User dropped the component in the upper part of this
					// component.
					index = form.getComponents().indexOf(c);
				} else {
					// User dropped the component in the lower part of this
					// component.
					index = form.getComponents().indexOf(c) + 1;
				}
				break;
			}

		}
		// If we get index = 0 and the list is not empty, we verify if the user
		// dropped the component in the end of the list, so we'll have to add
		// the component to the end of the panel.
		if (index == 0 && !form.getComponents().isEmpty()) {
			Rectangle b = form.getComponents().get(form.getComponents().size() - 1).getDrawComponent().getBounds();
			index = y > b.y + b.height ? form.getComponents().size() : index;
		}

		return index;
	}

	/**
	 * Returns the selected component.
	 * 
	 * @return
	 */
	public PreviewComponent getSelectedItem() {
		int index = getSelectedIndex();
		return index == -1 ? null : form.getComponents().get(index);
	}

	/**
	 * a.zanchi
	 * 
	 * Returns the PreviewComponent FROM a certain Component in the PreviewPane
	 * 
	 * 
	 * @param
	 * @return an Object of type PreviewComponent
	 */
	public PreviewComponent getPComponentFromComponent(Component c) {
		return findComponent(c);
	}

	/**
	 * Returns the selected component index.
	 * 
	 * @return
	 */
	private int getSelectedIndex() {
		for (int i = 0; i < form.getComponents().size(); i++) {
			PreviewComponent c = form.getComponents().get(i);
			if (c.isSelected()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Moves the supplied component finding the right position according to the
	 * supplied coordinates.
	 * 
	 * @param comp
	 * @param x
	 * @param y
	 */
	public void moveComponent(Component comp, int x, int y) {
		int index = getIndex(y); // Index to be moved.
		PreviewComponent toRemove = findComponent(comp);
		int exIndex = form.getComponents().indexOf(toRemove); // Index before
																// being moved.
		if (index != exIndex) {
			form.getComponents().remove(toRemove);
			// If the user are moving down, we need to decrease the future index
			// by one,
			// because we have just removed the component.
			if (index > exIndex)
				index--;
			if (index != -1 && index <= form.getComponents().size()) {
				form.getComponents().add(index, toRemove);
			}
		}
		refresh();
	}

	/**
	 * Searches and returns the preview component that contains the supplied
	 * component.
	 * 
	 * @param comp
	 * @return
	 */
	public PreviewComponent findComponent(Component comp) {
		for (PreviewComponent c : form.getComponents()) {
			if (c.getDrawComponent().equals(comp)) {
				return c;
			}
			for (PreviewComponent r : c.getRepeatables()) {
				if (r.getDrawComponent().equals(comp)) {
					return r;
				}
			}
		}
		return null;
	}

	/**
	 * Fabaris_a.zanchi retrieves the position index of a certain component
	 * 
	 * @param comp
	 * @return the position index. If the component is not present in the
	 *         preview panel returns -1.
	 */
	public int indexOfComponent(Component comp) {
		for (PreviewComponent c : form.getComponents()) {
			if (c.getDrawComponent().equals(comp))
				return form.getComponents().indexOf(c);
		}
		return -1;
	}

	/**
	 * Removes the selected component from preview.
	 * 
	 * @param c
	 */
	public PreviewComponent removeComponent(Component c) {
		PreviewComponent cc = findComponent(c);
		form.getComponents().remove(cc);
		refresh();
		return cc;
	}
	
	
	public PreviewComponent removeComponent(PreviewComponent c){
		form.getComponents().remove(c);
		refresh();
		return c;
	}
	
	/**
	 * Refreshes the preview.
	 */
	public void refresh() {
		int indexMaxML = -1;
		this.removeAll();
		if (form != null) {
			int x = 9;
			int y = 20;
			int index = -1;
			
			int indexSingleLine = -1;
			
			int lastSep = -1;
			int lastMulti = -1;
			int lastSingle = -1;
			String ts2 = "";

//			System.out.println("---Refreshing---");
			// Deactivate all components
			for (PreviewComponent c : form.getComponents()) {
				c.setActive(false);
				c.getDrawComponent().setBackground(PreviewComponent.DEFAULT_BACKGROUND);
			}
			for (PreviewComponent c : form.getComponents()) 
			{
				index++;
				c.updateDrawComponent();
				
				try{
					//SEPARATOR
					if(c.getType()==FormFieldType.SEPARATOR) 
					{
						lastSep = index;
						numMemberSeparator = lastSep;
					}
					//**********************************************************
					//MULTILINE
					
					
					else if(c.getType()==FormFieldType.WRAPPED_TEXT) 
					{
						
						java.util.Date date= new java.util.Date();
						ts2 = new Timestamp(date.getTime()).toString();
						numMemberMultiline = ts2.hashCode();//lastMulti;
						c.getComponent().setRequired(false);
						/*
						lastMulti = index;
						numMemberMultiline = lastMulti; //questo e' quello che c'era non andava bene perche' cosi' non sempre dava un valore univoco al nome del multiline*/
						
					}
					//SINGLELINE
					else if(c.getType()==FormFieldType.TRUNCATED_TEXT) 
					{
						
						java.util.Date date= new java.util.Date();
						ts2 = new Timestamp(date.getTime()).toString();
						numMemberSingleline = ts2.hashCode();//lastMulti;
						c.getComponent().setRequired(false);
						/*
						lastSingle = index;
						numMemberSingleline = lastSingle;
						*/
					}
					//***********************************************************
					
					if(c.isSelected())
					{
						if(FormsUiController.getInstance().getMainFrame().getDrawingPanel().isRepeatableOpen()){
							c.getDrawComponent().setBackground(new Color(0xFBFBFB));
						}else{
							c.getDrawComponent().setBackground(PreviewComponent.ACTIVE_BACKGROUND);
						}
						if(c.getType()!=FormFieldType.SEPARATOR){
							//if the selected component is not the separator highlight the section
							for(int i=lastSep+1; i<form.getComponents().size(); i++){
								PreviewComponent cc = form.getComponents().get(i);
								if(cc.getType() == FormFieldType.SEPARATOR)
								{
									//cc.getComponent().setName("SEPARATOR"+lastSep);
									System.out.println("SEPARATOR:" + cc.getComponent().getName());
									break;//end of section break the for
								}
								if(!FormsUiController.getInstance().getMainFrame().getDrawingPanel().isRepeatableOpen())
								{
//									System.out.println("ACTIVE:"+cc.getComponent().getName());
									cc.getDrawComponent().setBackground(PreviewComponent.ACTIVE_BACKGROUND);
									cc.setActive(true);
								}
							}
						}
					}else if(!c.isActive()){ //if it is not active set the default background 
						c.getDrawComponent().setBackground(PreviewComponent.DEFAULT_BACKGROUND);
					}else if(c.isActive()){
						if(!FormsUiController.getInstance().getMainFrame().getDrawingPanel().isRepeatableOpen()){
//							System.out.println("ACTIVE2:"+c.getComponent().getName());
							c.getDrawComponent().setBackground(PreviewComponent.ACTIVE_BACKGROUND);
						}
					}		
				}catch (Exception e) {
					System.out.println("error:"+e.getMessage());
				}
				/* Fabaris_Raji to get the Position of each component */
				
				Container cv = c.getDrawComponent();
				int i = indexOfComponent(cv);
				i = i + 1;
				
				// JLabel jl= new JLabel("["+i+"]");
				// cv.add(jl,0); //Fabaris_a.zanchi slightly modified to put
				// position on the left using method add(component, int )
				// instead of add(component)
				JLabel posLabel = (JLabel) cv.getComponent(0);
				i = i > 4 ? (i-2) : i;
				posLabel.setText("[" + i + "]");
				// raji
				int width = this.getBounds().width == 0 ? WIDTH : this.getBounds().width;
				// this.add(c.getDrawComponent(), new SimpleConstraints(x, y,
				// width -18, c.getComponent().getHeight()));
				this.add(c.getDrawComponent());
				addListenerRecursevely(c.getDrawComponent());
				y += c.getComponent().getHeight() + GAP;
			}
			// this.setSize(this.getSize().width, y+20);
		}
		this.validate();
		this.repaint();
		dragY = -1;
	}

	/**
	 * Adds mouse/key/drag listeners to the supplied component and its children.
	 * 
	 * @param c
	 */
	private void addListenerRecursevely(Container c) {
		c.addMouseListener(this);
		c.addKeyListener(this);
		dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_COPY, dragListener);
		for (Component c1 : c.getComponents()) {
			if (c1 instanceof Container) {
				addListenerRecursevely((Container) c1);
			}
		}
	}

	/**
	 * Fabaris_a.zanchi changed method visibility from private to public Remove
	 * the selection.
	 */
	public void deselectAll() {
		if (form != null) {
			for (PreviewComponent c : form.getComponents()) {
				c.setSelected(false);
				c.getDrawComponent().validate();
				c.getDrawComponent().repaint();
			}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		for (PreviewComponent c : form.getComponents()) {
			if((!c.getComponent().getName().equals("des_version") && !c.getComponent().getName().equals("client_version"))){
				if(FormsUiController.getInstance().getMainFrame().getDrawingPanel().isRepeatableOpen())break;
				if (c.isSelected()) {
					// Draw selection
					g.setColor(PreviewComponent.SELECTION_RECT_COLOR);
					Rectangle b = c.getDrawComponent().getBounds();
					g.drawRect(this.getBounds().x + 3, b.y - 1, this.getBounds().x+b.width, b.height + 1);
					break;
				}
			}
		}
		if (dragY != -1) {
			// Draw line to help users to drop components.
			g.setColor(Color.BLACK);
			g.drawLine(this.getBounds().x + 3, dragY, this.getBounds().width - 3, dragY);
			g.drawLine(this.getBounds().x + 3, dragY - 4, this.getBounds().x + 3, dragY + 4);
			g.drawLine(this.getBounds().width - 3, dragY - 4, this.getBounds().width - 3, dragY + 4);
		}
	}

	public void mouseClicked(MouseEvent e) {
		FormsUiController formUiController = FormsUiController.getInstance();
		if (isIdle) {
			Object source = e.getSource();
			Component comp = (Component) source;
			PreviewComponent found = findComponent(getContainerParent(comp));
			if (found.getType() == FormFieldType.REPEATABLES || found.getType() == FormFieldType.REPEATABLES_BASIC) {
				found.setSelected(true);
				if (e.getClickCount() == 2) {
					formUiController.showRepeatableContainerDetails();
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			// If it's a left-click, we change the selection.
			// deselectAll(); fabaris_a.zanchi we'll use a wider method instead
			formUiController.deSelectAllBeforeShowProperties();
			Object source = e.getSource();
			if (source instanceof PreviewPanel) {
				// User clicked on the panel, so we check coordinates.
				for (PreviewComponent c : form.getComponents()) {
					Rectangle b = c.getDrawComponent().getBounds();
					if (e.getY() > b.y && e.getY() <= b.y + b.height + GAP) {
						c.setSelected(true);
						break;
					}
				}

			} else {
				// User clicked in a component, so we just need to find it.
				Component comp = (Component) source;
				findComponent(getContainerParent(comp)).setSelected(true);
				/*
				 * Fabaris_A.zanchi checks if component is double clicked. If it
				 * is and if it is a repeatable container a new panel is opened
				 */
				if (e.getClickCount() == 2) {
					formUiController.showRepeatableContainerDetails();
				}
			}
			// We need to repaint to show the selection and show properties for
			// this component.
			//this.validate();
			//this.repaint();
			this.requestFocus();
			PreviewComponent pc = formUiController.getSelectedComponent();
			if(pc != null){
				if (pc.getComponent().getName().equals(PreviewPanel.enumerator) ||  
						pc.getComponent().getName().equals(PreviewPanel.date) || 
						pc.getComponent().getName().equals(PreviewPanel.section0) ||
                                                pc.getComponent().getName().equals(PreviewPanel.gps)){
						formUiController.clickOnHeaderFieldInPreviewPanel();
				}else{
                                        formUiController.enablePropertyPanel(true);	
                                        formUiController.enableBindingTable(true);
					formUiController.enableConstraintTable(true);
					formUiController.enablePropertiesTable(true);
					formUiController.showProperties();
				}
			}
		}
	}
	
	
	
	/**
	 * Searches the preview panel in the component.
	 * 
	 * @param obj
	 * @return
	 */
	private static Component getContainerParent(Component obj) {
		if (obj instanceof JPanel || obj instanceof JScrollPane)
			return obj;
		return getContainerParent(obj.getParent());
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			moveSelectionUp();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			moveSelecionDown();
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			// removeSelected();
			parentPnDrawing.deleteFromPreview(this.getSelectedItem().getDrawComponent());
		}
	}

	/**
	 * Removes the selected component after the user pressing 'delete'.
	 */
	private void removeSelected() {
		form.getComponents().remove(getSelectedItem());
		refresh();
		FormsUiController.getInstance().showProperties();
	}

	/**
	 * Moves the selection down.
	 */
	private void moveSelecionDown() {
		int index = getSelectedIndex();
		if (!isIdle) { // Fabaris_a.zanchi checks if there is a repeatable panel
						// open
			if (index != -1 && index != form.getComponents().size() - 1) {
				deselectAll();
				index++;
				form.getComponents().get(index).setSelected(true);
				this.validate();
				this.repaint();
				FormsUiController.getInstance().showProperties();
			}
		}
	}

	/**
	 * Moves the selection up.
	 */
	private void moveSelectionUp() {
		int index = getSelectedIndex();
		if (!isIdle) { // Fabaris_a.zanchi checks if there is a repeatable panel
						// open
			if (index > 0) {
				deselectAll();
				index--;
				form.getComponents().get(index).setSelected(true);
				this.validate();
				this.repaint();
				FormsUiController.getInstance().showProperties();
			}
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void setDragY(int dragY) {
		this.dragY = dragY;
	}

	public VisualForm getForm() {
		return form;
	}

	public boolean isIdle() {
		return isIdle;
	}

	public void setIdle(boolean isIdle) {
		this.isIdle = isIdle;
	}

	public void darken() {
		for (PreviewComponent comp : form.getComponents()) {
			comp.getComponent().darken();
		}

		this.validate();
		this.repaint();
		
		
	}
	
	public void lighten() {
		for (PreviewComponent comp : form.getComponents()) {
			comp.getComponent().lighten();
		}

		this.validate();
		this.repaint();
		
	}
	
	public static int getCountSeparator()
	{
		return numMemberSeparator;
	}
	
	public static int getCountMultiline()
	{
		return numMemberMultiline;
	}
	
	public static int getCountSingleline()
	{
		return numMemberSingleline;
	}
	
}
