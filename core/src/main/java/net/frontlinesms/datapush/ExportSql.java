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
package net.frontlinesms.datapush;

import java.sql.DatabaseMetaData;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes.Name;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.FrontlineUtils;

import org.apache.log4j.Logger;

/**
 * This class connects to a database and dumps all the tables and contents out to an sql file  in the form of
 * a set of SQL executable statements

 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph    
  www.fabaris.it <http://www.fabaris.it/>   
 */
public class ExportSql {

	private static Logger LOG = FrontlineUtils.getLogger(FrontlineSMS.class);


	/** Dump the database to an SQL string */
	public static String dumpDB(Properties props,Connection dbConn,DatabaseMetaData dbMetaData) {
		LOG.info("Initialse Export sql dumpDB method");

		try {
			StringBuffer result = new StringBuffer();
			String catalog = props.getProperty("catalog");
			String schema = props.getProperty("schemaPattern");
			String dbName=props.getProperty("dbName");
			String tables = props.getProperty("tableName");

			List<String> list = new ArrayList<String>();


			ResultSet rs = dbMetaData.getTables(catalog, schema, tables, null);
			if (! rs.next()) {
				LOG.info("Unable to find any tables matching: catalog="+catalog+" schema="+schema+" tables="+tables);
				rs.close();
			}

			else {								

				do {
					String tableName = rs.getString("TABLE_NAME");
					String tableType = rs.getString("TABLE_TYPE");			 

					if ("TABLE".equalsIgnoreCase(tableType)) {				

						ResultSet tableMetaData = dbMetaData.getColumns(null, null, tableName, "%");
						boolean firstLine = true;
						while (tableMetaData.next()) {
							if (firstLine) {
								firstLine = false;
							}

							String nullable = tableMetaData.getString("IS_NULLABLE");
							String nullString = "NULL";
							if ("NO".equalsIgnoreCase(nullable)) {
								nullString = "NOT NULL";
							}

						}
						tableMetaData.close();				

						// list of all tables in database						

						list.add(tableName);						
					}
				} while (rs.next());
				rs.close();
			}
			creaScript(dbConn, result, list,dbName);  //method to create script for export

			return result.toString();
		} catch (SQLException e) {
			e.printStackTrace();  
		}
		LOG.info("End Export sql dumpDB method");
		return null;
	}

	private static void creaScript(Connection dbConn, StringBuffer result, List<String> list,String dbName) {
		LOG.info("Initialse creaScript method");
		result.append("DECLARE @F_ID int=0;"+"\n");
		result.append("DECLARE @R_ID int=0;"+"\n");
		result.append("DECLARE @FR_ID int=0;"+"\n");
		result.append("DECLARE @FF_ID int=0;"+"\n");
		result.append("DECLARE @FREP_ID int=0;"+"\n");
		result.append("DECLARE @msg1 NVARCHAR(200);"+"\n");		
		result.append("DECLARE @msg2 NVARCHAR(200);"+"\n");	
		result.append("DECLARE @msg_value NVARCHAR(200);"+"\n");	
		result.append("DECLARE @LOG TABLE(typeOfLog int default 0, message NVARCHAR(2000))"+"\n");
		result.append("set @msg1="+"\'Import of form  \'"+"\n");
		result.append("set @msg2=\' has failed due to the absence of form definition.Please import the form  definition."+"\'"+"\n");	//Fabaris_raji		
		String  tableName;
		try {	
			System.out.println("Entro nel try");
			StringBuilder temp=new StringBuilder();	
			StringBuilder tempresult=new StringBuilder();	
			StringBuilder tempForm=new StringBuilder();	
			StringBuilder tempresp=new StringBuilder();
			List<Integer> listId=new ArrayList<Integer>();	
			List<Integer> listIdrv=new ArrayList<Integer>();
			PreparedStatement stmtForm=null;
			PreparedStatement stmtFormresponse=null;
			PreparedStatement stmtResponseValue=null;
			PreparedStatement stmtResponseValue2=null;
			PreparedStatement stmtFormField=null;
			PreparedStatement stmtResponseValueRep=null;//Fabaris_raji
			PreparedStatement stmtRepeatbleResp=null;//Fabaris_raji
			String q=null;
			String q2= new String();
			String r=null;
			int j=0;
			while(j<list.size()){ 		//iterate through list of tables
				tableName=(String) list.get(j);

				if(tableName.equalsIgnoreCase("Form")){	
					PreparedStatement stmt = dbConn.prepareStatement("SELECT * FROM "+dbName+".dbo."+tableName);	

					ResultSet rs= stmt.executeQuery();					
					//list  Form id
					while (rs.next()) {		
						//if(!rs.getBoolean("pushed")){						
						listId.add(rs.getInt("id"));//}
					}
					stmt.close();
					rs.close();		

					// List of responsevalue id
//					PreparedStatement stmtresp = dbConn.prepareStatement("SELECT * FROM "+dbName+".dbo.FormResponse");
//					ResultSet rsresp= stmtresp.executeQuery();	
//					listIdrv=new ArrayList<Integer>();
//					while (rsresp.next()) {		
//						if(!rsresp.getBoolean("pushed")){						
//							listIdrv.add(rsresp.getInt("id"));}
//					}
//					stmtresp.close();
//					rsresp.close();

					for(int i=0;i<listId.size();++i){  //iterate through Form id

						stmtForm= dbConn.prepareStatement("SELECT * FROM "+dbName+".dbo."+tableName+" where id="+listId.get(i));

						ResultSet rsform= stmtForm.executeQuery();
						ResultSetMetaData metaData = rsform.getMetaData();
						int columnCount = metaData.getColumnCount();			
						int counter=0;
						
						while(rsform.next()){		//iterate through Form resultset				
							tempForm=new StringBuilder(genQueryForm(dbConn,rsform, result, tableName,dbName,columnCount));	

							System.out.println("Entro nel while: "+(String) list.get(j)+": "+listId.get(i));
							//result.append(tempForm);//commented by raji_today
							/******commented by raji
							//FormField
							stmtFormField=dbConn.prepareStatement("SELECT * FROM "+dbName+".dbo.FormField where form_id="+listId.get(i));							
							ResultSet rsformfield= stmtFormField.executeQuery();
							ResultSetMetaData metaDatafield = rsformfield.getMetaData();
							int columnCountfield = metaData.getColumnCount();	
							
							while(rsformfield.next()){  //iterate through FormField resultset

								tempresp=new StringBuilder(genQueryFormField(dbConn,rsformfield, result, 
										"FormField",dbName,columnCountfield));	
								result.append(tempresp);

							}//end while FormField
							*****/
							//FormResponse
							stmtFormresponse=dbConn.prepareStatement("SELECT * FROM "+dbName+".dbo.FormResponse where parentForm_id="+listId.get(i)+
									" and pushed=0");
							ResultSet rsformresponse= stmtFormresponse.executeQuery();
							ResultSetMetaData metaDataresp = rsformresponse.getMetaData();
							int columnCountresp = metaDataresp.getColumnCount();	
																					
							/*
							 * 
							 * added by fabaris_raji
							 * 
							 */			
							tempresult=new StringBuilder();
							while(rsformresponse.next()){  //iterate through FormResponse resultset
								counter++;
								//added by fabaris_raji
								tempresp=new StringBuilder(genQueryFormResponse(dbConn,rsformresponse, result, 
										"FormResponse",dbName,columnCountresp));
								tempresult.append(tempresp);
								//result.append(tempresp);
								q="SELECT ResponseValue.* " +
										"FROM	FormResponse_ResponseValue inner join" +
										" ResponseValue on ResponseValue.id = FormResponse_ResponseValue.results_id " +
										"WHERE	FormResponse_ResponseValue.FormResponse_id = "+rsformresponse.getInt("id");

								stmtResponseValue=dbConn.prepareStatement(q);								
								ResultSet responseValue= stmtResponseValue.executeQuery();
								ResultSetMetaData metaresp = responseValue.getMetaData();
								int columnresp = metaresp.getColumnCount();									
								while(responseValue.next()){  //iterate through Response value resultset
									
									temp=new StringBuilder(genQueryResponseValue(dbConn,responseValue, result, 
											"ResponseValue",dbName,columnresp));	
									//result.append(temp);
									tempresult.append(temp);

									q2="SELECT	ResponseValue.* " +
											"FROM	ResponseValue_ResponseValue inner join" +
											" ResponseValue on ResponseValue.id = ResponseValue_ResponseValue.repetableResponseValues_id " +
											"WHERE	ResponseValue_ResponseValue.ResponseValue_id = "+responseValue.getInt("id");
									stmtResponseValue2=dbConn.prepareStatement(q2);								
									ResultSet responseValue2= stmtResponseValue2.executeQuery();
									ResultSetMetaData metaresp2 = responseValue2.getMetaData();
									int columnresp2 = metaresp2.getColumnCount();	

									while(responseValue2.next()){  //iterate through FormResponse_Response value resultset
										temp=new StringBuilder(genQueryResponseRepeatbles(dbConn,responseValue2, result, 
												"ResponseValue",dbName,columnresp2));	
										//result.append(temp);
										tempresult.append(temp);

									}//end while                        //added by Fabaris_raji
								}//end while of ResponseValue
//								//added by Fabaris_Raji for repeatables
//								String queryResp="select ResponseValue.* from ResponseValue  join "+
//										"ResponseValue_ResponseValue on ResponseValue_ResponseValue.repetableResponseValues_id =ResponseValue.id" ;																	
//								stmtRepeatbleResp=dbConn.prepareStatement(queryResp);								
//								ResultSet repeatableResp= stmtRepeatbleResp.executeQuery();        
//								ResultSetMetaData metaRespRep = repeatableResp.getMetaData();
//								int columnRespRep=  metaRespRep.getColumnCount();	
//
//								while(repeatableResp.next()){  //iterate through Responsevalue resultset								
//									temp=new StringBuilder(genQueryResponseRepeatbles(dbConn,repeatableResp, result, 
//											"ResponseValue",dbName,columnRespRep));	
//									//result.append(temp);	
//									tempresult.append(temp);
//									String queryRepeatable="select ResponseValue_ResponseValue.* from ResponseValue " +
//											"inner join ResponseValue_ResponseValue on "+
//											"ResponseValue.id=ResponseValue_ResponseValue.ResponseValue_id where Responsevalue_ResponseValue.repetableResponseValues_id = "+
//											repeatableResp.getInt("id") ;
//
//									stmtResponseValueRep=dbConn.prepareStatement(queryRepeatable);								
//									ResultSet repeatable= stmtResponseValueRep.executeQuery();        
//									ResultSetMetaData metarep = repeatable.getMetaData();
//									int columnrep= metarep.getColumnCount();	
//
//									while(repeatable.next()){  //iterate through Responsevalue_ResponseValue resultset								
//										temp=new StringBuilder(genQueryFormRepeatbles(dbConn,repeatable, result, 
//												"ResponseValue_ResponseValue",dbName,columnrep));	
//										//result.append(temp);
//										tempresult.append(temp);
//
//									}//end while ResponseValue_ResponseValue
//								}//end while ResponseValue								

							}//end while form response	
							
							if (counter>0){
								System.out.println("counter:"+counter);
								result.append(tempForm.toString());
								result.append(tempresult.toString());	
								result.append("END\n");	
													
							}
						} // end while form
						
					}	//end for loop of form

				}// end form table

				++j;

			}// end while
//			result.append("END\n");//have to add
//			result.append("END\n");	
			//System.out.println(result);
			result.append(new StringBuffer("SELECT * FROM @LOG"));
			LOG.info("End creaScript method");
		}//end try  
		catch (SQLException e) {
			LOG.debug("Exception"+e);
			e.printStackTrace();
		}	


	}

	//method to generate query from Responsevalue 
	private static StringBuilder genQueryResponseValue(Connection dbConn,ResultSet rs, StringBuffer result, String tableName,String dbName,int columnCount) throws SQLException {

		StringBuilder temp=new StringBuilder();
		StringBuilder col=new StringBuilder();   
		temp.append("	--ResponseValue"+"\n");
		temp.append("	IF (@FR_ID IS NOT NULL)"+"\n");
		temp.append("	BEGIN"+"\n");
		temp.append("		INSERT INTO "+dbName+".dbo."+tableName+"(");
		col=new StringBuilder();

		for (int j=1; j<columnCount; j++) {  
			if (j > 1) {  
				col.append(", ");
			}	                                 

			Object name=rs.getMetaData().getColumnLabel(j+1);
			col.append(name);  
		}//end for
		temp.append(col);
		temp.append(")VALUES (");
		for (int i=1; i<columnCount; i++) {

			if (i > 1) {
				temp.append(", ");
			}
			Object value = rs.getObject(i+1);      

			if (value == null) {
				temp.append("NULL");
			} else {
				String outputValue = value.toString();								
				outputValue = outputValue.replaceAll("'","\\'");

				outputValue = outputValue.replaceAll("'","\''");
				temp.append("N'"+outputValue+"'");
			}
		}//end for
		temp.append(");\n");
		temp.append("		SET @R_ID = @@IDENTITY; \n");		
		temp.append("		INSERT INTO @LOG(message) VALUES('IMPORTED RESPONSEVALUE '+Convert(NVarchar(10),@R_ID)+' OF FORM '+Convert(NVarchar(10),@F_ID))"+"\n");
		temp.append("		INSERT INTO "+dbName+".dbo."+"FormResponse_ResponseValue(FormResponse_id, results_id)VALUES(@FR_ID, @R_ID);\n");
		temp.append("	END"+"\n");
		return temp;
	} //end if
	//method to generate query FormResponse_ResponseValue 
	private static StringBuilder genQueryForm_FormResponse(Connection dbConn,ResultSet rs, StringBuffer result, String tableName,String dbName,int columnCount) throws SQLException {

		StringBuilder temp=new StringBuilder();
		StringBuilder col=new StringBuilder();   
		temp.append("IF (@FR_ID IS NOT NULL)"+"\n");
		temp.append("BEGIN"+"\n");
		temp.append("INSERT INTO "+dbName+".dbo."+tableName+"(");
		col=new StringBuilder();

		for (int j=0; j<columnCount; j++) {  
			if (j > 0) {  
				col.append(", ");
			}	                                 

			Object name=rs.getMetaData().getColumnLabel(j+1);
			col.append(name);  
		}//end for
		temp.append(col);
		temp.append(")VALUES (");
		for (int i=0; i<columnCount; i++) {

			if (i > 0) {
				temp.append(", ");
			}
			Object value = rs.getObject(i+1);      

			Object name=rs.getMetaData().getColumnLabel(i+1);
			if(name.equals("FormResponse_id")){
				value="@FR_ID";
			}
			if(name.equals("results_id")){
				value="@R_ID";
			}

			if (value.equals("@FR_ID")) {				
				temp.append("@FR_ID".replaceAll("\'",""));			

			} 
			else if (value.equals("@R_ID")) {				
				temp.append("@R_ID".replaceAll("\'",""));			

			} 
			else if (value == null) {
				temp.append("NULL");
			} else {
				String outputValue = value.toString();								
				outputValue = outputValue.replaceAll("'","\\'");

				outputValue = outputValue.replaceAll("'","\''");
				temp.append("'"+outputValue+"'");
			}
		}//end for
		temp.append(");\n");
		temp.append("END"+"\n");

		return temp;
	} //end if
	//method to generate query  for Form as permittedpath as NULL
	private static StringBuilder genQueryForm(Connection dbConn,ResultSet rs, StringBuffer result, String tableName,String dbName,int columnCount) throws SQLException {
		// Now we can output the actual data
		StringBuilder temp=new StringBuilder();
		StringBuilder col=new StringBuilder();   
		//StringBuilder tempFormResponse=new StringBuilder();
		temp.append("SET @F_ID = (SELECT id FROM "+dbName+".dbo."+tableName+" WHERE "+"id_flsmsId='"+rs.getString("id_flsmsId")+"')"+"\n");	
		temp.append("if(@F_ID IS NULL)"+"\n");
		//Added by Fabaris_raji
		temp.append("begin"+"\n");
		temp.append("	set @msg_value=@msg1+'"+rs.getString("id_flsmsId")+"'"+"+@msg2"+"\n");	
		temp.append("	INSERT INTO @LOG(typeOfLog, message) VALUES(1, @msg_value)"+"\n");		
		temp.append("end"+"\n");
		temp.append("else"+"\n");			
		temp.append("BEGIN"+"\n");
		//temp.append("IF (@F_ID IS NOT NULL)"+"\n");	commented today 24	
		//temp.append("BEGIN"+"\n");		commented today 24
		
		/*commented by Fabaris_raji
		  temp.append("INSERT INTO @LOG VALUES('IMPORTING NEW FORM')"+"\n");

		temp.append("INSERT INTO "+dbName+".dbo."+tableName+"(");
		col=new StringBuilder();

		for (int j=1; j<columnCount; j++) {  
			if (j > 1) {  
				col.append(", ");
			}	                                 

			Object name=rs.getMetaData().getColumnLabel(j+1);
			col.append(name);  
		}//end for
		temp.append(col);
		temp.append(")VALUES (");
		for (int i=1; i<columnCount; i++) {

			if (i > 1) {
				temp.append(", ");
			}						

			Object value = rs.getObject(i+1); 
			Object name=rs.getMetaData().getColumnLabel(i+1);			
			if(name.equals("permittedGroup_path")){
				value=null;
			}
			if (value == null) {
				temp.append("NULL");
			} else {
				String outputValue = value.toString();								
				outputValue = outputValue.replaceAll("'","\\'");

				outputValue = outputValue.replaceAll("'","\''");
				temp.append("'"+outputValue+"'");
			}
		}//end for
		temp.append(");\n");
		temp.append("SET @F_ID = @@IDENTITY; \n");
		temp.append("END"+"\n");
		temp.append("INSERT INTO @LOG VALUES('FORM ID:'+Convert(NVarchar(10),@F_ID))"+"\n"); */		
		return temp;
	} //end if
	//method to generate query  for FormField as permittedpath as NULL
	/*commented by Fabaris_raji
	  private static StringBuilder genQueryFormField(Connection dbConn,ResultSet rs, StringBuffer result, 
			String tableName,String dbName,int columnCount) throws SQLException {
	
		StringBuilder temp=new StringBuilder();
		StringBuilder col=new StringBuilder();   
	  temp.append("SET @FF_ID = (SELECT id FROM "+dbName+".dbo."+tableName+" WHERE id_flsmsId='"+rs.getString("id_flsmsId")+"')"+"\n");
		temp.append("IF (@FF_ID IS NULL)"+"\n");
		temp.append("BEGIN"+"\n");
	 
		//StringBuilder tempFormResponse=new StringBuilder();
		temp.append("INSERT INTO "+dbName+".dbo."+tableName+"(");
		col=new StringBuilder();

		for (int j=1; j<columnCount; j++) {  
			if (j > 1) {  
				col.append(", ");
			}	                                 

			Object name=rs.getMetaData().getColumnLabel(j+1);
			col.append(name);  
		}//end for
		temp.append(col);
		temp.append(")VALUES (");
		for (int i=1; i<columnCount; i++) {

			if (i > 1) {
				temp.append(", ");
			}						

			Object value = rs.getObject(i+1); 
			Object name=rs.getMetaData().getColumnLabel(i+1);			
			if(name.equals("form_id")){
				value="@F_ID";
			}
			if (value == null) {
				temp.append("NULL");			

			}
			else if (value.equals("@F_ID")) {				
				temp.append("@F_ID".replaceAll("\'",""));			

			} else {
				String outputValue = value.toString();								
				outputValue = outputValue.replaceAll("'","\\'");

				outputValue = outputValue.replaceAll("'","\''");
				temp.append("'"+outputValue+"'");
			}
		}//end for
		temp.append(");\n");
		temp.append("SET @FF_ID = @@IDENTITY;"+"\n");
		temp.append("INSERT INTO "+dbName+".dbo.Form_FormField(Form_id, fields_id) VALUES(@F_ID, @FF_ID)");
		temp.append("INSERT INTO @LOG VALUES('IMPORTED FORM FIELD '+Convert(NVarchar(10),@FF_ID)+' OF FORM '+Convert(NVarchar(10),@F_ID))"+"\n");
		temp.append("END"+"\n");		
		return temp;
	}*/	
	
	//method to generate query  for FormResponse
	private static StringBuilder genQueryFormResponse(Connection dbConn,ResultSet rs, StringBuffer result, String tableName,String dbName,int columnCount) throws SQLException {
		// Now we can output the actual data
		StringBuilder temp=new StringBuilder();
		StringBuilder col=new StringBuilder();   

		temp.append("	SET @FR_ID = (SELECT id FROM "+dbName+".dbo."+tableName+" WHERE id_flsmsId='"+rs.getString("id_flsmsId")+"')"+"\n");
		temp.append("	IF (@FR_ID IS NULL)"+"\n");
		temp.append("	BEGIN"+"\n");
		temp.append("		INSERT INTO "+dbName+".dbo."+tableName+"(");
		col=new StringBuilder();

		for (int j=1; j<columnCount; j++) {  
			if (j > 1) {  
				col.append(", ");
			}	                                 

			Object name=rs.getMetaData().getColumnLabel(j+1);
			col.append(name);  
		}//end for
		temp.append(col);
		temp.append(")VALUES (");
		for (int i=1; i<columnCount; i++) {

			if (i > 1) {
				temp.append(", "); 
			}						

			Object value = rs.getObject(i+1); 
			Object name=rs.getMetaData().getColumnLabel(i+1);			
			if(name.equals("parentForm_id")){								
				value="@F_ID";
			}			
			if (value == null) {
				temp.append("NULL");			

			}
			else if (value.equals("@F_ID")) {				
				temp.append("@F_ID".replaceAll("\'",""));			

			} 
			else {
				String outputValue = value.toString();								
				outputValue = outputValue.replaceAll("'","\\'");

				outputValue = outputValue.replaceAll("'","\''");
				temp.append("N'"+outputValue+"'");
			}
		}//end for
		temp.append(");\n");
		temp.append("		SET @FR_ID = @@IDENTITY; \n");
		temp.append("		INSERT INTO @LOG(message) VALUES('IMPORTED FORM RESPONSE '+Convert(NVarchar(10),@FR_ID)+' OF FORM '+Convert(NVarchar(10),@F_ID))"+"\n");
		temp.append("	END"+"\n");
		temp.append("	ELSE"+"\n");
		temp.append("	BEGIN"+"\n");
		temp.append("		SET @FR_ID = NULL \n");
		temp.append("	END"+"\n");
		temp.append("	--Following responseValues \n");
		return temp;
	} //end if
	//Fabaris_raji
	//method to generate query  for repeatables
	private static StringBuilder genQueryFormRepeatbles(Connection dbConn,ResultSet rs, StringBuffer result, String tableName,String dbName,int columnCount) throws SQLException {
		StringBuilder temp=new StringBuilder();
		StringBuilder col=new StringBuilder();   
		temp.append("IF (@R_ID IS NOT NULL)"+"\n");
		temp.append("BEGIN"+"\n");
		temp.append("INSERT INTO "+dbName+".dbo."+tableName+"(");
		col=new StringBuilder();

		for (int j=0; j<columnCount; j++) {  
			if (j > 0) {  
				col.append(", ");
			}	                                 

			Object name=rs.getMetaData().getColumnLabel(j+1);
			col.append(name);  
		}//end for
		temp.append(col);
		temp.append(")VALUES (");
		for (int i=0; i<columnCount; i++) {

			if (i > 0) {
				temp.append(", ");
			}
			Object value = rs.getObject(i+1);      

			Object name=rs.getMetaData().getColumnLabel(i+1);
			if(name.equals("ResponseValue_id")){
				value="@R_ID";
			}
			if(name.equals("repetableResponseValues_id")){
				value="@FREP_ID";
			}

			if (value.equals("@R_ID")) {				
				temp.append("@R_ID".replaceAll("\'",""));			

			} 
			else if (value.equals("@FREP_ID")) {				
				temp.append("@FREP_ID".replaceAll("\'",""));			

			} 
			else if (value == null) {
				temp.append("NULL");
			} 
			else {
				String outputValue = value.toString();								
				outputValue = outputValue.replaceAll("'","\\'");

				outputValue = outputValue.replaceAll("'","\''");
				temp.append("'"+outputValue+"'");
			}
		}//end for
		temp.append(");\n");	
		temp.append("INSERT INTO @LOG(message) VALUES('IMPORTED RESPONSEVALUE_RESPONSEVALUE '+Convert(NVarchar(10),@FREP_ID)+' OF RESPONSEVALUE '+Convert(NVarchar(10),@R_ID))"+"\n");
		temp.append("SET @FREP_ID = @@IDENTITY; \n");
		temp.append("END"+"\n");

		return temp;
	} 
	//Fabaris_raji 
	//method to generate query of Responsevalue for repeatbles
	private static StringBuilder genQueryResponseRepeatbles(Connection dbConn,ResultSet rs, StringBuffer result, String tableName,String dbName,int columnCount) throws SQLException {
		StringBuilder temp=new StringBuilder();
		StringBuilder col=new StringBuilder();   
		temp.append("		--RepeteableData"+"\n");
		temp.append("		IF (@FR_ID IS NOT NULL)"+"\n");
		temp.append("		BEGIN"+"\n");
		temp.append("			INSERT INTO "+dbName+".dbo."+tableName+"(");
		col=new StringBuilder();

		for (int j=1; j<columnCount; j++) {  
			if (j > 1) {  
				col.append(", ");
			}	                                 

			Object name=rs.getMetaData().getColumnLabel(j+1);
			col.append(name);  
		}//end for
		temp.append(col);
		temp.append(")VALUES (");
		for (int i=1; i<columnCount; i++) {

			if (i > 1) {
				temp.append(", ");
			}
			Object value = rs.getObject(i+1);      

			if (value == null) {
				temp.append("NULL");
			} else {
				String outputValue = value.toString();								
				outputValue = outputValue.replaceAll("'","\\'");

				outputValue = outputValue.replaceAll("'","\''");
				temp.append("N'"+outputValue+"'");
			}
		}//end for
		temp.append(");\n");
		temp.append("			SET @FREP_ID = @@IDENTITY; \n");		
		temp.append("			INSERT INTO @LOG(message) VALUES('IMPORTED RESPONSEVALUE '+Convert(NVarchar(10),@R_ID)+' OF FORM '+Convert(NVarchar(10),@F_ID))"+"\n");
		temp.append("			INSERT INTO "+dbName+".dbo."+"ResponseValue_ResponseValue(ResponseValue_id, repetableResponseValues_id)VALUES(@R_ID, @FREP_ID);\n");
		temp.append("		END"+"\n");
		return temp;
	} //end if
	//method setting pushed =1 when the table data is exported 
	public void setPushed(Connection dbConn,String dbName,String tableName){

		try{		

			PreparedStatement pstmt = null;		    
			String query = "UPDATE "+dbName+"."+"dbo."+tableName+
					" SET pushed=? WHERE pushed=? or pushed is null";
			pstmt =  dbConn.prepareStatement(query); 
			
			pstmt.setInt(1,1); 
			pstmt.setInt(2,0);
			pstmt.executeUpdate();  
			//System.out.println("numero"+ pstmt.executeUpdate());
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}	

	}
	//method to generate IdFrontlinesmsId for export
	public String getFrontlineSmsId(Properties props, Connection dbConn) throws SQLException {
		// TODO Auto-generated method stub   
		String id=null;
		PreparedStatement stmt = dbConn.prepareStatement("SELECT TOP 1 * FROM User_Credential where roles_id=3");				
		ResultSet rs= stmt.executeQuery();
		while(rs.next()){
			id=rs.getString("frontlinesms_id");
		}
		return id;
	}
	//method to get the owner of Form (email id of the supervisor)
	public String getFrontlineSmsOwner(Properties props, Connection dbConn,String id) throws SQLException {
		// TODO Auto-generated method stub   

		String owner=null;		
		PreparedStatement stmt = dbConn.prepareStatement("SELECT * FROM User_Credential " +
				"where roles_id=4");

		ResultSet rs= stmt.executeQuery();
		while(rs.next()){
			owner=rs.getString("email");
		}
		return owner;
	}
	//method to set the owner in form
	public void setFrontlineSmsOwner(Properties props, Connection dbConn,String flid,String owner,String tableName,String dbName) throws SQLException {

		Statement stmt = null;
		try {
			dbConn.setAutoCommit(false);
			stmt = dbConn.createStatement();
			String qryUpdate=null;
			qryUpdate = "UPDATE "+dbName+".dbo."+tableName+  
					" SET owner='"+owner+"'"+
					" WHERE owner is null";
			System.out.println(qryUpdate);
			stmt.execute(qryUpdate);
			stmt.close();
			dbConn.commit();
		}
		catch (SQLException e) {
			dbConn.rollback();
			e.printStackTrace();				
		}
		//return rowcount;

	}		

	public int setFrontlineSmsIdAll(Properties props, Connection dbConn,String flsmsid,String tableName,String dbName, String colID, String colDest) throws SQLException {

		int rowcount=0;
		Statement stmt = null;
		String  flsms=null;

		ArrayList<Integer> intList = new ArrayList<Integer>();		
		try {
			dbConn.setAutoCommit(false);
			stmt = dbConn.createStatement();
			String qryUpdate=null;
			qryUpdate = "UPDATE "+dbName+".dbo."+tableName+  
					" SET "+colDest+"=Convert(Varchar(10),"+colID+")+'"+"_"+flsmsid+"'"+
					" WHERE "+colDest+" is null";
			System.out.println(qryUpdate);
			stmt.execute(qryUpdate);
			stmt.close();
			dbConn.commit();
		}
		catch (SQLException e) {
			dbConn.rollback();
			e.printStackTrace();				
		}
		return rowcount;

	}	
	//method to set the IdfrontlinesmsId in the tables to export
	public int setFrontlineSmsId(Properties props, Connection dbConn,String flsmsid,String tableName,String dbName) throws SQLException {

		int rowcount=0;
		Statement stmt = null;
		String  flsms=null;

		ArrayList<Integer> intList = new ArrayList<Integer>();		
		try {
			dbConn.setAutoCommit(false);
			stmt = dbConn.createStatement();
			String qryUpdate=null;

			ResultSet rs1;
			rs1=stmt.executeQuery("SELECT * from "+dbName+"."+"dbo."+tableName);
			while(rs1.next()){
				intList.add(rs1.getInt(1));
			}
			for (int i : intList){				
				flsms=Integer.toString(i)+"_"+flsmsid;		
				qryUpdate="UPDATE "+dbName+"."+"dbo."+tableName+
						" SET id_flsmsId='"+flsms+"' WHERE id="+i +" and id_flsmsId is null";
				rowcount=stmt.executeUpdate(qryUpdate);	
			}					


			dbConn.commit();
		}
		catch (SQLException e) {
			dbConn.rollback();
			e.printStackTrace();				
		}
		return rowcount;

	}	
	//method to create id_flsms id
	public int setFrontlineSmsIdForm(Properties props, Connection dbConn,String id,String tableName,
			String dbName,String col) throws SQLException,NumberFormatException {

		int rowcount=0;
		Statement stmt = null;
		String  flsms=null;

		ArrayList<Integer> intList = new ArrayList<Integer>();		
		try {
			dbConn.setAutoCommit(false);
			stmt = dbConn.createStatement();
			String qryUpdate=null;		
			String resId=null;
			if(tableName.equalsIgnoreCase("FormResponse")){
				resId="FormResponse_id";
			}
			if(tableName.equalsIgnoreCase("ResponseValue")){
				resId="results_id";
			}
			ResultSet rs1;
			rs1=stmt.executeQuery("SELECT * from "+dbName+"."+"dbo."+tableName);
			while(rs1.next()){
				intList.add(rs1.getInt(1));

			}
			for (int i : intList){
				flsms=Integer.toString(i)+"_"+id;	

				qryUpdate="UPDATE  FormResponse_ResponseValue  SET"+
						" FormResponse_ResponseValue."+col+"="+tableName+".id_flsmsId"+					       
						" FROM FormResponse_ResponseValue"+
						" INNER JOIN "+tableName+
						" ON FormResponse_ResponseValue."+resId+"="+tableName+".id" +
						" WHERE FormResponse_ResponseValue."+resId+"="+i +" and FormResponse_ResponseValue."+resId+" is null";
				//System.out.println(qryUpdate);
				rowcount=stmt.executeUpdate(qryUpdate);

			}
			dbConn.commit();
		}
		catch (SQLException e) {
			dbConn.rollback();
			e.printStackTrace();				
		}
		return rowcount;

	}		

}

