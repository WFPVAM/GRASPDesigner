package net.frontlinesms.plugins.forms.contraints.tables.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//import sun.swing.Dtable.DefaultTableCellHeaderRenderer;
//
//public class ConstrainHeaderRenderer extends DefaultTableCellHeaderRenderer{

public class ConstrainHeaderRenderer extends DefaultTableCellRenderer{
	DefaultTableCellRenderer renderer;
	public ConstrainHeaderRenderer(JTable table){
		renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
	}
	
	public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
		Component c = renderer.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
		renderer.setEnabled(arg0.isEnabled());
		arg0.getTableHeader().setEnabled(arg0.isEnabled());
		c.setEnabled(arg0.isEnabled());
		return c;
	}		
}
