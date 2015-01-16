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
package net.frontlinesms.plugins.forms.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import sun.swing.SwingUtilities2;

public class MyTitledBorder extends TitledBorder {
	
	public Border firstBorder; 

	public MyTitledBorder(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public MyTitledBorder(Border border) {
		super(border);
		// TODO Auto-generated constructor stub
	}

	public MyTitledBorder(Border border, String title) {
		super(border, title);
		// TODO Auto-generated constructor stub
	}

	public MyTitledBorder(Border border, String title, int titleJustification,
			int titlePosition) {
		super(border, title, titleJustification, titlePosition);
		// TODO Auto-generated constructor stub
	}

	public MyTitledBorder(Border border, String title, int titleJustification,
			int titlePosition, Font titleFont) {
		super(border, title, titleJustification, titlePosition, titleFont);
		// TODO Auto-generated constructor stub
	}

	public MyTitledBorder(Border border, String title, int titleJustification,
			int titlePosition, Font titleFont, Color titleColor) {
		super(border, title, titleJustification, titlePosition, titleFont,
				titleColor);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		if(firstBorder!=null){
			firstBorder.paintBorder(c, g, x, y, width, height);
		}
		super.paintBorder(c, g, x, y, width, height);g.setFont(getFont(c));

	}

}
