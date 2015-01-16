package net.frontlinesms.ui.settings;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;



public class GraspContext
{
	private static GraspContext instance = null; 
    private Map<String, Object > param = null; 
          
    static
    { 
        if ( instance == null ) 
        { 
            instance = new GraspContext(); 
        } 
    } 
      
    private GraspContext()  
    { 
        param = new HashMap<String, Object>(); 
    } 
      
    public static GraspContext getInstance() 
    { 
        return instance; 
    } 
      
    public Object getAttribute( String key ) 
    { 
        return param.get(key); 
    } 
      
    public void setAttribute( String key, Object obj )
    { 
    	param.put( key, obj );
    } 
      
    public void removeAttribute( String key ) 
    { 
        param.remove( key ); 
    }
}

