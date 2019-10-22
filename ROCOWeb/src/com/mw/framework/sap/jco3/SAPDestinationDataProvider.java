package com.mw.framework.sap.jco3;
import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
   
/*
Use DestinationDataProvider to make SAP connection.
*/   
public class SAPDestinationDataProvider   
{   
    static class MyDestinationDataProvider implements DestinationDataProvider   
    {   
        private DestinationDataEventListener eL;      
        
		private HashMap<String, Properties> destinations;
		
		private static MyDestinationDataProvider provider = new MyDestinationDataProvider();
		
		private MyDestinationDataProvider(){
			if( provider == null ){
				//System.out.println("Creating MyDestinationDataProvider ... ");
				destinations = new HashMap();
			}
		}
		
		//Static method to retrieve instance
		public static MyDestinationDataProvider getInstance(){
			//System.out.println("Getting MyDestinationDataProvider ... ");
			return provider;
		}
		
        public Properties getDestinationProperties(String destinationName)   
        {   
			if( destinations.containsKey( destinationName ) ){
				return destinations.get( destinationName );
			} else {
				throw new RuntimeException("Destination " + destinationName + " is not available");   
			}
        }   
   
        public void setDestinationDataEventListener(DestinationDataEventListener eventListener)   
        {   
            this.eL = eventListener;   
        }   
   
        public boolean supportsEvents()   
        {   
            return true;   
        }   
        
		/**
		  *Add new destination
		  *@param properties holds all the required data for a destination
		 **/
        void addDestination(String destinationName, Properties properties)   
        {   
			synchronized ( destinations ){
				destinations.put(destinationName, properties);
			}
        }   
    }   
} 