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
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.ui.FrontlineUI;
import net.frontlinesms.ui.SimpleConstraints;
import net.frontlinesms.ui.SimpleLayout;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import java.util.ArrayList;
import java.util.List;

/**Preview Panel for repeatable components
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
public class PreviewPanelRepeatables extends JPanel implements MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	
	//a.zanchi
	private List<PreviewComponent> componentList = new ArrayList<PreviewComponent>();
	//a.zanchi
	private PreviewComponent currentRepeatableSection;
	
	//private VisualForm form = null;
	private static final int GAP = 1; // GAP between the components
	private DragListener dragListener;
	private DragSource dragSource;
	private DrawingPanel drawingPanel;

	MyTitledBorder titledBorder = new MyTitledBorder("");
	private int dragY = - 1; // Coordinate to draw a line (helping user to drop component)
	/** Fabaris_a.zanchi this value is changed from 235 to 325 to achieve choerence with the value of 340 the
	 * DrawingPanel sets for this panel.
	 * BUT WHY??
	 */
	//private int WIDTH = 325;
	private int WIDTH = 435;//Fabaris_raji - modified from 325 to 435 since PreviewPanel is modified from 340 to 450
	public PreviewPanelRepeatables(DragListener dragListener, DragSource dragSource, DrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;
//		setLayout(new SimpleLayout());
		
		//setBorder(BorderFactory.createLineBorder(new Color(234, 222, 100), 2));
		
		// We have to set the correct font for some languages
		
		//titledBorder = new TitledBorder(new LineBorder(Color.BLACK, 2), InternationalisationUtils.getI18nString(FormsThinletTabController.COMMON_PREVIEW_REPEATABLES));
		titledBorder.setTitleFont(FrontlineUI.currentResourceBundle.getFont());
		titledBorder.firstBorder = BorderFactory.createMatteBorder(7, 4, 4, 4, PreviewComponent.DEFAULT_BACKGROUND);
		titledBorder.setBorder(BorderFactory.createLineBorder(new Color(0x000000), 1));
//		System.out.println(titledBorder.getBorderInsets(this));
		setBorder(titledBorder);
		
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.dragListener = dragListener;
		this.dragSource = dragSource;
		
		this.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, -2, GAP));
		this.setBackground(PreviewComponent.ACTIVE_BACKGROUND);
	}
	
	/**
	 * Show the supplied form on the preview.
	 * 
	 * @param form
	 */
	/*public void showForm(VisualForm form) {
		this.form = form;
		deselectAll();
		refresh();
		FormsUiController.getInstance().showProperties();
	} */
	
	/**
	 * Adds the supplied component to the preview finding the right position
	 * according to the supplied coordinates.
	 * 
	 * @param comp The component to be added.
	 * @param x
	 * @param y
	 */
	public void addComponent(PreviewComponent comp, int x, int y) {
		int index = getIndex(y);
		if (index == -1 || index >= componentList.size()) {
			// We are adding to the end.
			//form.getComponents().add(comp);
			componentList.add(comp);
			currentRepeatableSection.addRepeatable(comp);
		} else {
			// We are adding to a specified position.
			//form.getComponents().add(index, comp);
			componentList.add(index, comp);
			currentRepeatableSection.addRepeatable(index, comp);
		}
		refresh();
	}

	/**
	 * This method finds the closest component to the supplied coordinate
	 * and returns its index.
	 * 
	 * @param y
	 * @return
	 */
	public int getIndex(int y) {
		int index = 0;
		for (PreviewComponent c : componentList) {
			Rectangle b = c.getDrawComponent().getBounds();
			if (y > b.y && y < b.y + b.height + GAP) {
				// We find the component
				if (y < b.y + ( (b.height + GAP)/ 2) ) {
					// User dropped the component in the upper part of this component. 
					index = componentList.indexOf(c);
				} else {
					// User dropped the component in the lower part of this component. 
					index = componentList.indexOf(c) + 1;
				}
				break;
			} 
		}
		// If we get index = 0 and the list is not empty, we verify if the user
		// dropped the component in the end of the list, so we'll have to add
		// the component to the end of the panel.
		if (index == 0 && !componentList.isEmpty()) {
			Rectangle b = componentList.get(componentList.size() - 1).getDrawComponent().getBounds();
			index = y > b.y + b.height ? componentList.size() : index; 
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
		return index == -1 ? null : componentList.get(index);
	}
	
	/**a.zanchi
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
		for (int i = 0; i < componentList.size(); i++) {
			PreviewComponent c = componentList.get(i);
			if (c.isSelected()) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Moves the supplied component finding the right position
	 * according to the supplied coordinates.
	 * 
	 * @param comp
	 * @param x
	 * @param y
	 */
	public void moveComponent(Component comp, int x, int y) {
		int index = getIndex(y); // Index to be moved.
		PreviewComponent toRemove = findComponent(comp);
		int exIndex = componentList.indexOf(toRemove); // Index before being moved.
		if (index != exIndex) {
			componentList.remove(toRemove);
			currentRepeatableSection.getRepeatables().remove(toRemove);
			// If the user are moving down, we need to decrease the future index by one,
			// because we have just removed the component.
			if (index > exIndex) index--;
			if (index != -1 && index <= componentList.size()) {
				componentList.add(index, toRemove);
				currentRepeatableSection.getRepeatables().add(index, toRemove);
				
			}
		}
		refresh();
	}

	/**
	 * Searches and returns the preview component that contains the supplied component.
	 * 
	 * @param comp
	 * @return
	 */
	private PreviewComponent findComponent(Component comp) {
		for (PreviewComponent c : componentList) {
			if (c.getDrawComponent().equals(comp)) {
				return c;
			}
		}
		return null;
	}
	
	/**Fabaris_a.zanchi retrieves the position index of a certain component
	 * 
	 * @param comp
	 * @return the position index. If the component is not present in the preview panel returns -1.
	 */
	public int indexOfComponent(Component comp) {
		for (PreviewComponent c :componentList) {
			if (c.getDrawComponent().equals(comp))
				return componentList.indexOf(c);
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
		componentList.remove(cc);
		currentRepeatableSection.removeRepeatable(cc);
		refresh();
		return cc;
	}
	
	/**
	 * Refreshes the preview.
	 */
	public void refresh() {
		this.removeAll();
		if (true) {
			int x = 9;
			int y = 20;
			for (PreviewComponent c : componentList) {
				c.updateDrawComponent();
				c.getDrawComponent().setBackground(PreviewComponent.ACTIVE_BACKGROUND);
				int width = this.getBounds().width == 0 ? WIDTH : this.getBounds().width;
			//	this.add(c.getDrawComponent(), new SimpleConstraints(x, y, width - 18, c.getComponent().getHeight()));
				this.add(c.getDrawComponent());
				addListenerRecursevely(c.getDrawComponent());
				y+= c.getComponent().getHeight() + GAP;
			}
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
		for (Component c1: c.getComponents()) {
			if (c1 instanceof Container) {
				addListenerRecursevely((Container) c1);
			}
		}
	}

	/** Fabaris_a.zanchi changed method visibility from private to public
	 * Remove the selection.
	 */
	public void deselectAll() {
		if (true) {
			for (PreviewComponent c : componentList) {
				c.setSelected(false);
				c.getDrawComponent().validate();
				c.getDrawComponent().repaint();
			}
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		PreviewComponent pc = null;
		for (PreviewComponent c : componentList) {
			Rectangle b = c.getDrawComponent().getBounds();
			Insets insets = getInsets();
			if(pc != null && !pc.isSelected()){
				g.setColor(PreviewComponent.DEFAULT_BACKGROUND);
				g.drawLine(this.getBounds().x + insets.left, b.y-1, this.getBounds().width -insets.right,  b.y-1);
			}
			if (c.isSelected()) {
				// Draw selection
				g.setColor(PreviewComponent.SELECTION_RECT_COLOR);
				g.drawRect(this.getBounds().x+insets.left-1, b.y - 1, this.getBounds().width-this.getBounds().x-insets.left-insets.right+1, b.height + 1);
//				break;
			}
			pc = c;
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
		if (e.getButton() == MouseEvent.BUTTON1) {
			// If it's a left-click, we change the selection.
			//deselectAll(); fabaris_a.zanchi we'll use a wider method instead
			FormsUiController.getInstance().deSelectAllBeforeShowProperties();
			Object source = e.getSource();
			if (source instanceof PreviewPanelRepeatables) {
				// User clicked on the panel, so we check coordinates.
				for (PreviewComponent c : componentList) {
					Rectangle b = c.getDrawComponent().getBounds();
					if (e.getY() > b.y && e.getY() <= b.y + b.height + GAP) {
						c.setSelected(true);
						break;
					}
				}
				
			} else {
				// User clicked in a component, so we just need to find it.
				//System.out.println("clicked a component in repeatable panel");
				Component comp = (Component) source;
				findComponent(getContainerParent(comp)).setSelected(true);
				
				/*Fabaris_A.zanchi checks if component is double clicked.
				If it is and if it is a repeatable container a new panel is opened */
//				if (e.getClickCount() == 2) {
//					FormsUiController.getInstance().showRepeatableContainerDetails();
//				}
			}
			// We need to repaint to show the selection and show properties for this component.
			this.validate();
			this.repaint();
			this.requestFocus();
			FormsUiController.getInstance().showProperties();
			
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

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			moveSelectionUp();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			moveSelecionDown();
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			removeSelected();
		}
	}

	/**
	 * Removes the selected component after the user pressing 'delete'.
	 */
	private void removeSelected() {
//		componentList.remove(getSelectedItem());
//		refresh();
//		FormsUiController.getInstance().showProperties();
		drawingPanel.deleteFromRepatablePreview(getSelectedItem().getDrawComponent());
	}

	/**
	 * Moves the selection down.
	 */
	private void moveSelecionDown() {
		int index = getSelectedIndex();
		if (index != -1 && index != componentList.size() - 1) {
			deselectAll();
			index++;
			componentList.get(index).setSelected(true);
			this.validate();
			this.repaint();
			FormsUiController.getInstance().showProperties();
		}
	}
	
	/**
	 * Moves the selection up.
	 */
	private void moveSelectionUp() {
		int index = getSelectedIndex();
		if (index > 0) {
			deselectAll();
			index--;
			componentList.get(index).setSelected(true);
			this.validate();
			this.repaint();
			FormsUiController.getInstance().showProperties();
		}
	}

	public void keyTyped(KeyEvent e) {}

	public void setDragY(int dragY) {
		this.dragY = dragY;
	}

	public VisualForm getForm() {
		return null;
	}

	public PreviewComponent getCurrentRepeatableSection() {
		return currentRepeatableSection;
	}

	public void setCurrentRepeatableSection(PreviewComponent currentRepeatableSection) {
		if (currentRepeatableSection.getType()==FormFieldType.REPEATABLES_BASIC) {
			titledBorder.setTitle(InternationalisationUtils.getI18nString(FormsThinletTabController.COMMON_PREVIEW_BASIC_REPEATABLES));
		}else{
			titledBorder.setTitle(InternationalisationUtils.getI18nString(FormsThinletTabController.COMMON_PREVIEW_REPEATABLES));
		}
		this.currentRepeatableSection = currentRepeatableSection;
		this.componentList = new ArrayList<PreviewComponent>();
		for (PreviewComponent pc : currentRepeatableSection.getRepeatables()) {
			componentList.add(pc);
		}
		refresh();
	}
	
	

}
