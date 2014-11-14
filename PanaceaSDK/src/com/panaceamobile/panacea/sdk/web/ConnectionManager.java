/*
 * ConnectionManager.java
 *
 * © Research In Motion Limited, 2006
 * Confidential and proprietary.
 */

package com.panaceamobile.panacea.sdk.web;

import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import com.panaceamobile.panacea.sdk.push.Log;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.CoverageStatusListener;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;


/**
 * The ConnectionManager provides the capability of parsing through
 * all of the service books (and service records) on the handheld.  
 * Once the parsing operation is complete, the ConnectionManager provides
 * two main methods for accessing information around which service books
 * should be used for connectivity.
 */
public class ConnectionManager implements /*GlobalEventListener,*/ CoverageStatusListener
{
    public static final String TAG = "ConnectionManager";
    
    private static final long ID = 0x1431cf6271d3b1edL; // ConnectionManager.ID
    private static String IPPP = "IPPP";                // Static instance of the IPPP string so we don't create it every time.
    
    private static ConnectionManager _manager;      // Static instance of the ConnectionManager.
    
    private boolean _wifiSupport;					// Boolean representing whether WiFi is supported.
    private boolean _mdsSupport;                    // Boolean representing whether MDS is supported.
    private boolean _bisSupport;                    // Boolean representing whether BIS-B is supported.
    private boolean _wapSupport;                    // Boolean representing whether WAP is supported.
    
    /**
     * The constructor for this class which simply parses the service books.
     */
    private ConnectionManager() 
    {
    	// if platform < 4.2.0 we should use this:
    	// parseServiceBooks();
    	Log.d(TAG, "Creating new Connection Manager");
   		setCoverage();
    	CoverageInfo.addListener(this);
    }
    
    /**
     * Returns an instance of the ConnectionManager.  This currently
     * only leverages providing a static instance (one per process) but could 
     * easily be changed to provide a singleton instance for the whole system.
     * @return an instance of the ConnectionManager.
     */
    public static ConnectionManager getInstance()
    {
        if( _manager == null ) {
            _manager = new ConnectionManager();
        }
        return _manager;
    }
    
    
    
    /**
     * Returns the Connection object specified by the name (e.g. HttpConnection) using the
     * appropriate transport mechanism (MDS, BIS-B, TCP) depending on what service books
     * are currently supported on the handheld and using a priority scale in the following order:
     * <code> 
     *      MDS
     *      BIS-B
     *      WAP - To be supported in the future
     *      HTTP over Direct TCP
     * </code>
     * This method does NOT check for the name to ensure that HTTP is being requested and as such
     * it may not work if you request a socket connection over the BIS-B transport protocol.  
     */
    public Connection open( String name, int mode, boolean timeouts ) throws IOException
    {
    	
    	setCoverage();
Log.d(TAG, "open() opening connection using:" );
    	
    	if ( !DeviceInfo.isSimulator() ) {
    		if ( _wifiSupport ) {
    	Log.d(TAG, "WIFI" );
    			
	            name = name.concat( ";deviceside=true;interface=wifi" );    			    			
    		} else if ( _mdsSupport ) {
	            // MDS Transport
    	Log.d(TAG, "MDS" );
	            name = name.concat( ";deviceside=false" );
	        } else if( _bisSupport ) {
	            // BIS-B Transport
    Log.d(TAG, "BIS" );
	            name = name.concat( ";deviceside=false;ConnectionType=mds-public" );
	            
	            /*
				My application makes HTTPS connections. Are there any known issues to be
				aware of?
				BIS-B does not support proxy SSL/TLS due to the fact that data exchanged between the
				handheld and the BIS-B proxy is not 3DES or AES encrypted like it would be with
				BES/MDS, and thus this channel is not secure. Therefore, all HTTPS connections must
				use end-to-end SSL/TLS and append the “EndToEndRequired” parameter at the end of
				the URL. For example:
				
				Connector.open("https://www.foo.com;deviceside=false;ConnectionType=mds-public")
				
				will fail because the handheld will try to use Proxy SSL.
				
				Connector.open("https://www.foo.com;deviceside=false;ConnectionType=mds-public
				;EndToEndRequired") 
				
				will work because the handheld forces end-to-end SSL. Note that
				this will fail if end-to-end TLS (also known as handheld TLS) is not installed on the user's
				device. To determine if handheld TLS is installed, go to Options > TLS and check the
				TLS Default entry. If it is already set to, or can be changed to “handheld”, then handheld
				TLS is installed on the BlackBerry. Otherwise, you can load the handheld TLS library on
				the device by installing handheld software on your PC and using application loader.
				Please note that with handhelds running 4.0 OS and higher, handheld TLS is installed by
				default.
	             */
	            	            
	            if ( name.indexOf("https") == 0 )
	            	name.concat( ";EndToEndRequired");
	            
	        } else if( _wapSupport ) {
	            // TODO
	        } else {
	            // HTTP over Direct TCP
    	Log.d(TAG, "Direct TCP" );
	            name = name.concat( ";deviceside=true" );
	        }
    	} else {
	  Log.d(TAG, "Direct TCP : Simulator" );
    		name = name.concat( ";deviceside=true" );
    	}
        
        return Connector.open( name, mode, timeouts );
    }

    public Connection open( String name, int mode, boolean timeouts, int[] preferredTransportTypes ) throws IOException
    {
    Log.d(TAG, "open() opening connection using preferred connection types" );
    	
		if ( DeviceInfo.isSimulator() ) {
	Log.d(TAG, "Opening connection for simulator");
			return open(name, mode, timeouts);
		}
		
    	// Create ConnectionFactory
    	// make a list of transport types ordered according to preference (they will be tried in succession)
//    	int[] preferredTransportTypes = {TransportInfo.TRANSPORT_TCP_WIFI, 
//   									 TransportInfo.TRANSPORT_BIS_B, 
//    									 TransportInfo.TRANSPORT_MDS,
//    									 TransportInfo.TRANSPORT_TCP_CELLULAR, 
//    									 TransportInfo.TRANSPORT_WAP2, 
//    									 TransportInfo.TRANSPORT_WAP};

    	// Create ConnectionFactory
    	ConnectionFactory factory = new ConnectionFactory();

    	// Configure the factory
    	factory.setPreferredTransportTypes( preferredTransportTypes );

    	// use the factory to get a connection
    	ConnectionDescriptor conDescriptor = factory.getConnection(name);
    	if ( conDescriptor != null ) {
    	   // connection succeeded
    	   int transportUsed = conDescriptor.getTransportDescriptor().getTransportType();
    	Log.d(TAG, "transport available: "+transportUsed);
    	   // using the connection
    	   return conDescriptor.getConnection();
    	} else {
    	Log.d(TAG, "Failed to open connection!");
    	}
    	
    	// unable to open a connection
    	return null;
    }

    /**
     * Close manager
     * Removes listener and deletes instance
     */
    public void close() {
    	CoverageInfo.removeListener(this);
    	_manager = null;
    }
    
    /**
     * Returns a string representing the type of connection that would be chosen when using getConnection.
     * @return a string representing the type of connection that would be chosen when using getConnection.
     */
    public String getConnectionType()
    {
    	if ( _wifiSupport ) {
            // WIFI Transport
    		return "WIFI";
    	} else if( _mdsSupport ) {
            // MDS Transport
            return "MDS";
        } else if( _bisSupport ) {
            // BIS-B Transport
            return "BIS-B";
        } else if( _wapSupport ) {
            // WAP Transport
            return "WAP";
        } else {
            // HTTP over Direct TCP
            return "Direct TCP";
        }
    }
    
    /**
     * This method uses the CoverageInfo API to determine what coverage is available on the device.
     * CoverageInfo is available as of 4.2.0, but until 4.2.2, Coverage_MDS is shown as available 
     * when only BIS_B Coverage is actually available on the device.   
     */
    private void setCoverage() {
    	
  Log.d(TAG, "DETERMINING COVERAGE" );
    	
    	if ( ( RadioInfo.getActiveWAFs() & RadioInfo.WAF_WLAN ) != 0 )
        {           
            if(CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT, RadioInfo.WAF_WLAN, true))
            {
Log.d(TAG, "wifi enabled" );
            	_wifiSupport = true;
            }
        }
    	
    	if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS)) {
 Log.d(TAG, "mds enabled" );
       		_mdsSupport = true;
    	}
    	if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B)) {
 Log.d(TAG, "bis enabled" );
    		_bisSupport = true;
    	}
    	
    	if(!_bisSupport && !_wifiSupport && !_mdsSupport)
    	{
    		Log.d(TAG, "NO COVERAGE WAS FOUND!!");
    	}
    }
    
    /**
     * This method handles changes in Coverage through the CoverageStatusListener interface.
     * CoverageStatusListener works with CoverageInfo and is available with 4.2.0 
     */
    
    public void coverageStatusChanged(int newCoverage) {
  Log.d(TAG, "CONNECTION STATUS CHANGED" );
    	
    	_wifiSupport = false;
    	_mdsSupport = false;
    	_bisSupport = false;
    	
    	if ((newCoverage & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT) {
            if(CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT, RadioInfo.WAF_WLAN, true))
            {
Log.d(TAG, "wifi enabled" );
            	_wifiSupport = true;
            }
    	}
    	
    	if ((newCoverage & CoverageInfo.COVERAGE_MDS) == CoverageInfo.COVERAGE_MDS) {
Log.d(TAG, "mds enabled" );
        	_mdsSupport = true;
    	}
    	if ((newCoverage & CoverageInfo.COVERAGE_BIS_B) == CoverageInfo.COVERAGE_BIS_B) {
Log.d(TAG, "bis enabled" );
    		_bisSupport = true;
    	}
    }
    

    /**
     * This method provides the functionality of actually parsing 
     * through the service books on the handheld and determining
     * which traffic routes are available based on that information.
     * Before 4.2.0, this method is necessary to determine coverage.
     */
    /*
    private void parseServiceBooks()
    {
        // Add in our new items by scrolling through the ServiceBook API.
        ServiceBook sb = ServiceBook.getSB();
        ServiceRecord[] records = sb.findRecordsByCid( IPPP );      // The IPPP service represents the data channel for MDS and BIS-B
        if( records == null ) {
            return;
        }
        
        int numRecords = records.length;
        for( int i = 0; i < numRecords; i++ ) {
            ServiceRecord myRecord = records[i];
            String name = myRecord.getName();       // Technically, not needed but nice for debugging.
            String uid = myRecord.getUid();         // Technically, not needed but nice for debugging.

            // First of all, the CID itself should be equal to IPPP if this is going to be an IPPP service book.
            if( myRecord.isValid() && !myRecord.isDisabled() ) {
                // Now we need to determine if the service book is Desktop or BIS.  One could check against the
                // name but that is unreliable.  The best mechanism is to leverage the security of the service
                // book to determine the security of the channel.
                int encryptionMode = myRecord.getEncryptionMode();
                if( encryptionMode == ServiceRecord.ENCRYPT_RIM ) {
                    _mdsSupport = true;
                } else {
                    _bisSupport = true;
                }
            }
        }
    }
    
    ////////////////////////////////////////////////////////////
    /// GlobalEventListener Interface Implementation         ///
    ////////////////////////////////////////////////////////////
    
    /**
     * Invoked when the specified global event occured. 
     * The eventOccurred method provides two object parameters and two integer parameters for supplying details about the event itself. The developer determines how the parameters will be used. 
     * 
     * For example, if the event corresponded to sending or receiving a mail message, the object0 parameter might specify the mail message itself, while the data0 parameter might specify the identification details of the message, such as an address value.
     * 
     * @param guid - The GUID of the event.
     * @param data0 - Integer value specifying information associated with the event.
     * @param data1 - Integer value specifying information associated with the event.
     * @param object0 - Object specifying information associated with the event.
     * @param object1 - Object specifying information associated with the event.
     */
    /*
    public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1)
    {
        if( guid == ServiceBook.GUID_SB_ADDED ||
            guid == ServiceBook.GUID_SB_CHANGED ||
            guid == ServiceBook.GUID_SB_OTA_SWITCH ||
            guid == ServiceBook.GUID_SB_OTA_UPDATE ||
            // This item was added to the JDE in v4.1.  If compiling in that version uncomment this line
            // and otherwise leave it out.
            // guid == ServiceBook.GUID_SB_POLICY_CHANGED ||
            guid == ServiceBook.GUID_SB_REMOVED ) {
                Dialog.inform( "Service Book Global Event Received" );
                parseServiceBooks();
        }
    }
    */
    
}
