package net.frontlinesms.plugins.forms.helper;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseListener;

public class PanelHelper {
	
	/**
	 * Will enable the components three in a recursively way
	 * @param container
	 * @param enable
	 */
	public static void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
            	enableComponents((Container)component, enable);
            }
        }
    }
	
	public static void addMouseListenerToComponentChilds(Container container, MouseListener listener){
		Component[] components = container.getComponents();
        for (Component component : components) {
            component.addMouseListener(listener);
            if (component instanceof Container) {
            	addMouseListenerToComponentChilds((Container)component, listener);
            }
        }
	}
	
}
