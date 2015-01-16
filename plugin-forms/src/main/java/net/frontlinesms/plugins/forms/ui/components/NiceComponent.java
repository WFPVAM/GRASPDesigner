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
package net.frontlinesms.plugins.forms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.hibernate.property.Getter;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.forms.ui.PalettePanel;
import net.frontlinesms.plugins.forms.ui.PreviewPanel;
import net.frontlinesms.ui.Icon;

/**Class modeling a pleasant to see component in the preview panel of the designer
 * 
 *  guide lines:
 *  - Width of component must be as wide as possibile in the preview panel
 *  - The components properties must be left aligned
 *  - The leftmost property should be the name of component (instead of label)
 *  - The icon should be esplicative of the component type (recalling the icon of palette panel)
 *  NB: the component position property is NOT defined here, but in the PreviewPanel.java in method refresh()
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 */

public abstract class NiceComponent extends FComponent{

	/** Max width, in pixels, of a line of text */
	private static final int MAX_TEXT_WIDTH_PER_LINE = 380; //Fabaris_A.zanchi matches the width of text area for label in ComponentPanel.java
	
	protected ComponentPanel pn;
	
	@Override
	public abstract String getIcon();

	@Override
	public abstract String getDescription();
		
	@Override
	public Container getDrawingComponent() {
		
		ComponentPanel panel = new ComponentPanel();
		
		String name = super.getDisplayName();
		if (name != null)
			panel.getNameLabel().setText(name + ":");
		else
			panel.getNameLabel().setText("");
		
		panel.getIconLabel().setIcon(new ImageIcon(FrontlineUtils.getImage("/icons/components/" + getIcon(), PreviewPanel.class)));
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridwidth= 2;
		c.anchor=GridBagConstraints.LINE_START;
		
		String labelText = super.getDisplayLabel();
		String text[] = wrapText(labelText, panel);
		int count = 0;
		for (String s : text) {
			if (!s.equals("")) {
				++count;
				JLabel newLine = new JLabel(s, JLabel.LEFT);
				panel.add(newLine, c);
			}
		}
		FontMetrics m = panel.getFontMetrics(panel.getFont());
		
		setRenderHeight((count)*m.getHeight()+30);

		pn = panel;
		
		if(this.getName().equals("des_version") || this.getName().equals("client_version")){
			panel.setVisible(false);
		}
		
		return panel;
	}
	
	
	private int countVisibleLines(String text, JTextPane textArea) {
		Font font = textArea.getFont();
		FontMetrics m = textArea.getFontMetrics(font);
		int width = m.stringWidth(text);
		return (width/MAX_TEXT_WIDTH_PER_LINE) + 1;
	}
	
	/**
	 * Splits a string of text ready to be displayed in a {@link JPanel}
	 * @param text the text to split
	 * @param pn the panel the text will be displayed in
	 * @return an array containing each line of text to show in the panel
	 */
	private String[] wrapText(String text, JPanel pn) {
		FontMetrics m = pn.getFontMetrics(pn.getFont());
		int width = m.stringWidth(text);
		String ret[] = new String[ (width / MAX_TEXT_WIDTH_PER_LINE) + 1];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = getString(text, m);
			text = text.substring(text.indexOf(ret[i]) + ret[i].length());
		}
		return ret;
	}

	/**
	 * Gets the portion of the string, starting at the beginning, which will fit on one line
	 * of the visible component. 
	 * @param text the text to split
	 * @param m font metrics for the panel which the string will be displayed on
	 * @return the next line of text to display
	 */
	private String getString(String text, FontMetrics m) {
		int end = 0;
		String ret = text.substring(0, end);
		while (m.stringWidth(ret) < MAX_TEXT_WIDTH_PER_LINE && (end + 1) <= text.length()) {
			end++;
			ret = text.substring(0, end);
		}
		return ret;
	}
	
	@Override
	public void darken() {
		for (Component comp : pn.getComponents()) {
			comp.setEnabled(false);
		}
	}
	
	@Override
	public void lighten() {
		for (Component comp : pn.getComponents()) {
			comp.setEnabled(true);
		}
	}

}
