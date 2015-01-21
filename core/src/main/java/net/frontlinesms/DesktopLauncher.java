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

package net.frontlinesms;



import java.io.ByteArrayOutputStream;


import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.frontlinesms.encoding.Base64Utils;
import net.frontlinesms.resources.ResourceUtils;
import net.frontlinesms.ui.FirstTimeWizard;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.handler.core.DatabaseConnectionFailedDialog;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.i18n.LanguageBundle;
import net.frontlinesms.ui.settings.GraspContext;

import org.apache.log4j.Logger;

import thinlet.Thinlet;

/**
 * This class is the Launcher for FrontlineSMS as a desktop application.  It will
 * start the Frontline service and then open the graphical user interface to
 * control the service.  If any unhandled exceptions occur either starting the
 * FrontlineSMS service or opening the GUI, they will be diaplyed in an AWT window
 * so that they can easily be reported back to the development team.
 * N.B. Error messages CANNOT be i18ned in this class, as there may have been an error
 * loading language packs.
 * 
 * @author Alex Anderson alex(at)masabi(dot)com
 * @author Carlos Eduardo Genz kadu(at)masabi(dot)com
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 */
public class DesktopLauncher {
	/** Logging object */
	private static final Logger LOG = FrontlineUtils.getLogger(DesktopLauncher.class);
	
	/**
	 * Main class for launching the FrontlineSMS project.
	 * @param args
	 */
	public static void main(String[] args) {
            //JOptionPane.showMessageDialog(null, "My Goodness, this is so concise");
		FrontlineSMS frontline = null;
		try {
			try
			{
                                if(args !=null && args.length>0 && args[0]!=null)
				  GraspContext.getInstance().setAttribute("user", args[0]);
                                else
                                  GraspContext.getInstance().setAttribute("user", "admin");  
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			AppProperties appProperties = AppProperties.getInstance();
			final String VERSION = BuildProperties.getInstance().getVersion();
			LOG.info("FrontlineSMS version [" + VERSION + "] for test by WFP Palestine Team");
			LOG.info("WFP Palestine Development team version");
                        
                        
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			String lastVersion = appProperties.getLastRunVersion();
			InputStream defaultResourceArchive = ResourceUtils.class.getResourceAsStream("/resources.zip");
			if(defaultResourceArchive == null) {
				LOG.fatal("Default resources archive could not be found!");
				throw new Exception("Default resources archive could not be found!");
			}
			ResourceUtils.unzip(defaultResourceArchive, new File(ResourceUtils.getConfigDirectoryPath()), !VERSION.equals(lastVersion));
			// This should always get the English bundle, as other languages are only included in
			// resources.zip rather than in the resources/languages directory
			LanguageBundle englishBundle = InternationalisationUtils.getDefaultLanguageBundle();
			Thinlet.DEFAULT_ENGLISH_BUNDLE = englishBundle.getProperties();
			// If the user has currently no User ID defined
			// We generate one
			if (appProperties.getUserId() == null) {
				appProperties.setUserId(generateUserId());
			}
			//added by Fabaris_Raji
			String id=appProperties.getUserId();	
			boolean showWizard = appProperties.isShowWizard();
			appProperties.setLastRunVersion(VERSION);
			appProperties.saveToDisk();
			//frontline = initFrontline();   //initialise frontline
			/**initialise frontline and pass id*/
			frontline = initFrontline(id);	
			if (showWizard) {
				new FirstTimeWizard(frontline);
			} else {
				new UiGeneratorController(frontline, true);
			}
		} catch(Throwable t) {
			if (frontline != null) 
				frontline.destroy();
			ErrorUtils.showErrorDialog("Fatal error starting FrontlineSMS!", "A problem ocurred during FrontlineSMS startup.", t, true);
		}
	}
	
	/**
	 * Generate the User ID this user is going to keep for all its statistics
	 * @return The generated ID as a String
	 */
	private static String generateUserId() {
		Long currentTime = new Long(System.currentTimeMillis());
		byte[] bytes;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			new DataOutputStream(baos).writeLong(currentTime);
			bytes = baos.toByteArray();
		} catch (IOException e) { /* not gonna happen */ throw new IllegalStateException(e); }
		return Base64Utils.encode(bytes).replace('=', ' ').trim();
	}

	private static FrontlineSMS initFrontline(String id) throws Throwable {
		LOG.trace("ENTER");
		FrontlineSMS frontline = new FrontlineSMS();		
		try {
			/** Test if there is database connection */
			boolean connected = false;			
			while(!connected) {
				try {
					frontline.initApplicationContext();
					frontline.getContactDao().getContactByName("test");
					connected = true;
				} catch(RuntimeException ex) {
					LOG.warn("Problem initialising application context.", ex);
					frontline.deinitApplicationContext();
					DatabaseConnectionFailedDialog.create(ex).acquireSettings();
				}
			}			
			/**Initialise database connection and check username and password_Fabaris-Raji	*/		
			connected = false;
			boolean exists=false;
			Login login = new Login();	
			while(!connected) {				 			 
				try {
					frontline.initApplicationContext();										
					connected = true;					
					while(!exists){
						exists=login.createAndShowUI(frontline,id);						 
						frontline.startServices();						
					}
				} catch(RuntimeException ex) {
					LOG.warn("Problem initialising application context.", ex);
					frontline.deinitApplicationContext();
					DatabaseConnectionFailedDialog.create(ex).acquireSettings();
				}
			}				
			/**
			// Start FrontlineSMS services
			frontline.startServices();			
			LOG.trace("EXIT");			
			return frontline;
			*/			
			LOG.trace("EXIT");			
			return frontline;
		} catch(Throwable t) {
			LOG.info("Problem initialising FrontlineSMS", t);
			// completed.
			frontline.destroy();
			LOG.trace("EXIT");
			throw t;
		}
	}
}
