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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;


/**
 * This class defines a model for the properties table.
 * 
 * @author Carlos Eduardo Genz 
 * <li> kadu(at)masabi(dot)com
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 */
 
public class MyTableModel extends DefaultTableModel implements TableModelListener {
	private static final long serialVersionUID = 7207518673243281559L;
	
	public MyTableModel() {
		this.addTableModelListener(this);
	}
	
	//a.zanchi modificato per non rendere editable la colonna con la checkbox (da fare dinamicamente in futuro)
	public boolean isCellEditable(int row, int col) {
		// Only the second column is editable.
		return col == 1 && row != 0 && row != 3;
	} 
	
	
	
	@Override
	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return super.getColumnClass(arg0);
	}

	public void tableChanged(TableModelEvent e) {
		if (e.getFirstRow() > 0) {
			String property = (String) getValueAt(e.getFirstRow(), 0);
			String value = (String) getValueAt(e.getFirstRow(), 1);
			FormsUiController.getInstance().propertiesChanged(property, value);
		}
	}

	
}
