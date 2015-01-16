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
package net.frontlinesms;

import java.awt.Dimension;

import net.frontlinesms.ui.settings.GraspContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.awt.GridLayout; 
import javax.swing.*; 
/** Class for the dialog box of login page 
 * Gets the username and password of the login user
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 * */


public class Login extends JPanel {		
	/** Logging object */
	private static final Logger LOG = FrontlineUtils.getLogger(DesktopLauncher.class);

	public String name ;
	public String pword;	 
	public LoginDetail ld=new LoginDetail();
	public static final String USER = "admin";

	public boolean createAndShowUI(FrontlineSMS frontline,String id) 
	{
		LOG.trace("ENTER");
		boolean exists=false;

		LoginPanel loginP = new LoginPanel();		
		
		//SETTO IL LOGIN AUTOMATICO COME SERVIZIO DI AMMINISTRAZIONE
		
		String context = (String) GraspContext.getInstance().getAttribute("user");
		
		if(context==null)
		{
			int response = JOptionPane.showConfirmDialog(null, loginP, "Please Enter UserId and Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);		      
			if (response == JOptionPane.OK_OPTION) 
			{ 		    
				name = loginP.getName();
				pword = loginP.getPassword();
				ld.setUsername(name);	
				
				/**check if exists username and password in table User_Credential*/
				exists = frontline.getUserDao().getByUsernameAndPassword_check(name, pword);		    	
				if(exists)
				{	
					/**Added to insert frontlinesms_id for the corresponding username and password if it does not exists*/    		
					if(frontline.getUserDao().getByUsernameAndPassword_Id(name,pword) == null||frontline.getUserDao().getByUsernameAndPassword_Id(name,pword) == "NULL")
					{ 		    				
						frontline.getUserDao().saveFrontlineSMS_Id(name,pword,id);
					}
					GraspContext.getInstance().setAttribute("user", name);
					return true;
				}			
				else
				{
					String msg = "Incorrect username and password.";
					JOptionPane.showMessageDialog(null, msg);		    		
				}
				LOG.trace("EXIT");	
			}
			if (response == JOptionPane.OK_CANCEL_OPTION)
			{ 	
				System.exit(0);	      
			}
			if (response == JOptionPane.CLOSED_OPTION) 
			{ 	
				System.exit(0);	      
			}
			//return exists;
		}
		else exists = true;
		
		return exists;
	}
}	
class LoginPanel extends JPanel {
	private JTextField nameField = new JTextField(10);
	private JPasswordField passwordField = new JPasswordField(10);

	public LoginPanel() {
		setLayout(new GridLayout(5, 5, 8, 8)); 
		add(new JLabel("Name:"));
		add(nameField);
		add(new JLabel("Password:"));
		add(passwordField);
	}    
	public String getName() {
		return nameField.getText();
	}    
	public String getPassword() {
		return new String(passwordField.getPassword()); 
	}
}
