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
package net.frontlinesms.ui.handler.importexport;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.server.LogStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;
import org.h2.util.FileUtils;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.csv.CsvExporter;
import net.frontlinesms.csv.CsvImporter;
import net.frontlinesms.csv.CsvParseException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.PluginController;
import net.frontlinesms.plugins.forms.FormsPluginController;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.resources.ResourceUtils;

import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.handler.core.FileChooser;
import net.frontlinesms.ui.handler.importexport.ImportDialogHandler.EntityType;

/**
 * This class handles the form import dialog.
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/> 
 * 
 */
 
public class SqlImportDialogHandler extends ImportDialogHandler {

	private static Logger LOG = FrontlineUtils.getLogger(FrontlineSMS.class);
	private Object importDialog;
	private boolean openChooseCompleted = false;
	private String filter;
	UiGeneratorController ui;
	
	public SqlImportDialogHandler(UiGeneratorController ui) {	
		super(ui,EntityType.SqlImportDialogHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	void doSpecialImport(String dataPath) throws CsvParseException {
		// TODO Auto-generated method stub

	}

	@Override
	protected CsvImporter getImporter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setImporter(String filename) throws CsvParseException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void appendPreviewHeaderItems(Object header) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object[] getPreviewRows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getWizardTitleI18nKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getOptionsFilePath() {
		return UI_FILE_OPTIONS_PANEL_FORM;
	}

	/**
	 * This method "breaks" the execution flow and takes the responsibility to
	 * create a custom dialog window. instead of letting superclass(es) do the
	 * work
	 */
	public void showWizard() {
		importDialog = uiController.loadComponentFromFile("/ui/core/importexport/pnImportSql.xml", this);
		uiController.add(importDialog);
		//populateList(filter);
	}

	/**
	 * Method call when the "Import" button is pressed
	 * 
	 */
	@Override
	public void doImport(String filename) {

		String aSQLScriptFilePath=filename;

		boolean isScriptExecuted = false;
		StringBuffer sb;
		Properties props=new Properties();
		try {
//			BufferedReader in = new BufferedReader(new FileReader(aSQLScriptFilePath));
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(aSQLScriptFilePath)), "UTF-8"));
			String str;
			sb = new StringBuffer();
			while ((str = in.readLine()) != null) {
				sb.append(str + "\n ");
			}
			in.close();
			String[] inst = sb.toString().split(";");

			//connection settings n
			String propPath=ResourceUtils.getUserHome()+File.separatorChar +"FrontlineSMS"+File.separatorChar+"properties"+File.separatorChar+ "sqlserver.properties";					
			props.load(new FileInputStream(propPath));
			String driverClassName = props.getProperty("driver.class");
			String driverURL = props.getProperty("driver.url");
			Connection dbConn = null;
			String msg=null;
			int j=0;
			try {
				Class.forName(driverClassName);
				dbConn = DriverManager.getConnection(driverURL, props);				
				Statement st = dbConn.createStatement();  
				String dbname=props.getProperty("dbName");
				dbConn.setAutoCommit(false);		//Implementing transaction
				String temp="SELECT id FROM "+ dbname+".dbo.Form";
				//Added by Fabaris_raji
				PreparedStatement stmt = dbConn.prepareStatement(temp);	
				msg="The import of some data failed:\n";
//				int id=0;
//				ResultSet rsformid= stmt.executeQuery();	
//				while (rsformid.next()) {								
//					 id=rsformid.getInt("id");
//					
//				}
//					if(id==0){
//						uiController.alert(msg);
//					}
//					else
//					{
//
//						//Added by Fabaris_raji
						boolean hasResult = st.execute(sb.toString()); 
						boolean foundResult = hasResult;
						String toLog = new String();
						int messagesCount=0;
						//Fabaris_Andrei Attila Aknai
						ResultSet rs = st.getResultSet();												
						while (foundResult == false && (st.getMoreResults() || st.getUpdateCount() >= 0)) {
							rs = st.getResultSet();
							if(rs != null){
								foundResult = true;
							}
						}
						if(rs != null){
							while (rs.next()) {
								String logString = rs.getString(2)+"\n";;
								toLog += logString;
								if(rs.getString(1).equals("1")){
									messagesCount++;
									msg += logString;
								}
								
							}
							rs.close();
							st.close();
						}else{							
							System.out.println("NO RESULTS");
						}
						if(toLog.length()>0){
							LOG.info(toLog);
							System.out.println(toLog);
						}
						if(messagesCount>0){
							JOptionPane.showMessageDialog(null, msg, "Import", JOptionPane.INFORMATION_MESSAGE);
//							uiController.alert(msg);
						}else{
							JOptionPane.showMessageDialog(null, "All data was correctly imported.", "Import", JOptionPane.INFORMATION_MESSAGE);
						}

						isScriptExecuted = true;
						dbConn.commit();
//						if(isScriptExecuted){					
//							uiController.alert("The file is imported successfully");
//						}
//					}//end else
				
			}//end try
			catch(SQLException e) {
				//LOG.debug("Unable to connect to database: "+e,e);
				e.printStackTrace();				
				/* while (e != null) {
				 String errorMessage = e.getMessage();
				 if(errorMessage.equalsIgnoreCase("Error raised in TRY block")){
				        	uiController.alert(msg);
				  System.err.println("sql error message:" + errorMessage);	
				  }   			     
				      
				 else */
				uiController.alert("The file is not imported due to error in query execution");
			//	 }
				dbConn.rollback();
			}

		}

		catch(Exception e){
			System.err.println("Failed to Execute" + aSQLScriptFilePath );

		}


	}
	@Override
	public void openChooseComplete(String filePath) {
		uiController.setText(uiController.find("tfFilename"), filePath);
		/*
		 * checks if the filepath points to an xml file to activate if
		 * (filePath.endsWith(".xml"))
		 */
		this.openChooseCompleted = true;
		validateSelection();

	}

	/**
	 * Override to use a filter for xml files	 
	 */
	@Override
	public void showOpenModeFileChooser() {
		FileChooser fc = FileChooser.createFileChooser(this.uiController, this, "openChooseComplete");
		fc.setFileFilter(new FileNameExtensionFilter("FrontlineSMS Exported script(" + ".sql" + ")", "sql"));
		fc.show();
	}

	/**
	 * Method to filter contacts
	 * 
	 * @param filter
	 */
	public void setContactFilter(String filter) {
		this.filter = filter;
	}

	public void filterContacts() {
		populateList(this.filter);
	}

	/**
	 * Support method to pouplate the contacts list
	 * 
	 */
	private void populateList(String filter) {
		uiController.removeAll(uiController.find("formImport_contactList"));
		ContactDao cDao = uiController.getFrontlineController().getContactDao();
		List<Contact> contacts = null;
		if (this.filter == null)
			contacts = cDao.getAllContacts();
		else {
			int count = cDao.getContactsFilteredByNameCount(filter);
			contacts = cDao.getContactsFilteredByName(filter, 0, count);
		}
			
		Object contactsTable = uiController.find("formImport_contactList");
		for (Contact c : contacts)
			uiController.add(contactsTable, uiController.createListItem(c));
	}

	/**
	 * This method is called when a contact is selected and it enables the
	 * import button.
	 */
	public void validateSelection() {
		if (openChooseCompleted)
			uiController.setEnabled(uiController.find("btDoImport"), true);
	}

}
