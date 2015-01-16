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
package net.frontlinesms.plugins.forms.ui.components;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import net.frontlinesms.plugins.forms.ui.FormsThinletTabController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * FComponent for displaying and editing a form text area (multiline text field).
 * @author Kadu
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
@SuppressWarnings("serial")
public class TextArea extends /*FComponent*/ NiceComponent {
	/** Create a new instance of this class and initialise its render height */
	public TextArea() {
		setRenderHeight(100);
	}

	/** @see FComponent#getDescription() */
	@Override
	public String getDescription() {
		return InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_FCOMP_TEXT_AREA);
	}

	/** @see FComponent#getIcon() */
	@Override
	public String getIcon() {
		return "textarea.png";
	}

	/** @see FComponent#getDrawingComponent() */
	/*@Override
	public Container getDrawingComponent() {
		
		JTextArea tf = new JTextArea();
		JScrollPane sp = new JScrollPane(tf);
		sp.setBorder(new TitledBorder(super.getDisplayLabel()));
		tf.setColumns(20);
		tf.setEditable(false);
		return sp;
		//Fabaris a.zanchi and Fabaris r.joseph 
		JPanel pn = new JPanel();
		pn.setLayout(new FlowLayout(FlowLayout.CENTER));
		JTextArea area = new JTextArea();
		area.setBorder(new TitledBorder(super.getDisplayLabel()));		 
		area.setPreferredSize(new Dimension(renderWidth-7, 90));
		area.setEditable(false);//Fabaris_a.aknai
		area.setEnabled(false);//Fabaris_a.aknai
		pn.add(area);
		return pn;
	}*/

}
