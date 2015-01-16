package net.frontlinesms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;

import serial.*;

import net.frontlinesms.messaging.CommProperties;
import net.frontlinesms.resources.ResourceUtils;

import org.apache.log4j.Logger;


/**
 * Utilities class for managing bugs in javax.comm classes.
 * @author Alex
 */
public final class CommUtils {
	/** Logging object */
	private static Logger LOG = FrontlineUtils.getLogger(CommUtils.class);
	
	/** Flag indicating whether the preferred COM package has been set. */
	private static boolean setPackage;
	
	/**
	 * Gets a FRESH list of available COM ports.  This method works around a bug with Java COM API
	 * that leads to ports being cached.  For more info on the bug, see http://forum.java.sun.com/thread.jspa?threadID=575580&messageID=2986928
	 * @return an enumeration of {@link CommPortIdentifier}s, as supplied by {@link CommPortIdentifier#getPortIdentifiers()}
	 */
	public static synchronized Enumeration<CommPortIdentifier> getPortIdentifiers() {
		// Get the preferred comm drivers
		if(!setPackage) {
			SerialClassFactory.init(CommProperties.getInstance().getCommLibraryPackageName());
			setPackage = true;
		}
		
		/* This method is synchronized to prevent strange things happening when reloading the config etc. */
		if(SerialClassFactory.PACKAGE_JAVAXCOMM.equals(SerialClassFactory.getInstance().getSerialPackageName())) {
			try {
				Class<?> commPortIdentifierClass = javax.comm.CommPortIdentifier.class;
				
				Field masterIdList = commPortIdentifierClass.getDeclaredField("masterIdList");
				masterIdList.setAccessible(true);
				masterIdList.set(null, null);
	
				Method loadDriver = commPortIdentifierClass.getDeclaredMethod("loadDriver", String.class);			
				loadDriver.setAccessible(true);
				loadDriver.invoke(null, ResourceUtils.getConfigDirectoryPath() + "properties/javax.comm.properties");
			} catch(Exception ex) {
				LOG.warn("There was an error trying to reset javax.comm ports cache.", ex);
			}
		}
		return CommPortIdentifier.getPortIdentifiers();
	}

}
