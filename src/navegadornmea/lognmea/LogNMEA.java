package navegadornmea.lognmea;


/**
 * 
 * 
 * Data Transmitted NMEA
Header
Apparent Wind Angle MWV, VWR
Apparent Wind Speed MWV, VWR
Bearing to Waypoint BWC, APB
Course Over Ground (M) VTG
Cross Track Error APB, XTE
Date ZDA
Depth DBT
Distance (Log) VLW
Distance (Trip) VLW
GPS Fix/No Fix GGA, GLL
GPS HDOP GSA, GGA
GPS PDOP GSA
GPS Satellite Azimuth GSV
GPS Satellite Elevation GSV
GPS Satellite PR Number GSV
GPS Satellite SNR GSV
GPS Differential Station ID GGA
GPS Differential AGE GGA
GPS Number of Satellites GGA
GPS Antenna Height GGA
Operation/Wiring 9
Data NMEA Header
GPS Quality Indicator GGA
Heading (Magnetic or True) HDM, HDG, VHW, HDT
Latitude & Longitude GGA, GLL*
Locked Heading HSC
MOB (Cancel) PNATA
Rudder Angle RSA
Speed Over Ground VTG
Speed Through water VHW
Temperature, Water MTW
Time ZDA
Time Offset ZTG
True Wind Angle MWV, VWT
True Wind Direction MWD
True Wind Speed MWV, VWT, MWD
Variation HDG
Waypoint Capture WPL
Velocity Made Good to Wind VPW
Waypoint Capture WPL
Waypoint Destination No. APB, BWC
Waypoint Arrival Alarm APB, AAM
Waypoint Distance BWC
Waypoint Time To Go ZTG
 GLL version 1.5 is transmitted if version 1.5 is received via
N
 * 
 * 
 * 
 * 
 * Data Received NMEA
Header
Apparent Wind Angle MWV, VWR
Apparent Wind Speed MWV, VWR
Bearing to Waypoint APB, BWR, BWC,
RMB, BER, BEC
Course Over Ground (M) VTG, VTA, RMC, RMA
Cross Track Error APB, XTE, APA, RMB,
XTR
Date ZDA, RMC
Depth DBT, DPT
Distance (Log) VLW
Distance (Trip) VLW
GPS Fix/No Fix GGA, GSA
GPS HDOP GGA, GSA
GPS PDOP GSA
GPS Satellite Azimuth GSV
GPS Satellite Elevation GSV
GPS Satellite PR Number GSV
12 Operation/Wiring
GPS Satellite SNR GSV
GPS Differential Station ID GGA
Data NMEA Header
GPS Differential AGE GGA
GPS Number of Satellites GGA
GPS Antenna Height GGA
GPS Quality Indicator GGA
Heading (Magnetic or True) HDM, HDG, VHW, HDT
Latitude & Longitude GGA, GLL (inc. Version
1.5)
RMC, RMA,
IMA, GLP, GOP, GXP,
GDP
MOB (Cancel) PNATA
Route WPL
Speed Over Ground VTG, VTA, RMC, RMA
Speed Through water VHW
Temperature, Water MTW
Time ZDA, GLL, ZFO, ZTG
True Wind Angle MWV
Variation HDG, HVM, RMC,
RMA,
HVD
Waypoint Destination No. APB, BWR,
BWC, RMB,
BOD, WCV, BER, BEC
Waypoint Arrival Alarm APB, APA, AAM
Waypoint Distance BWC, BWR
Operation/Wiring 13
RMB, BER, BEC
Waypoint Lat & Lon BWC, BWR, BEC, BER
Waypoint Capture WPL
Waypoints, last one & next 4 PNATA
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.net.*;
import java.util.*;
import PTMF.*;



import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.*;

/**
 * Simple example application that takes a filename as command-line argument and
 * prints Position from received GGA sentences.
 * 
 * @author Kimmo Tuukkanen
 */
public class LogNMEA implements SentenceListener {

    //Construye una clase Log NMEA
	public LogNMEA(boolean isNet, Address addressWifi, File filelog) {
		super();
		this.addressWifi = addressWifi;
		this.filelog = filelog;
		this.isNet = isNet;
		
	}

	//Reader NMEA
	private SentenceReader readerNMEA;

    final static public  String VERSION_LOGNMEA = "LogNMEA v1.0"; 
    /** socket de conexión TCP al emisor wifi/rs232 */
    private java.net.Socket socket = null;
    
    //URL de prueba en tiempo real: http://olaje.dvrdns.org:950/
    private Address addressWifi = null;
    
    //Log file
    private File filelog = null; 
    
    private  InputStream netstream = null;
    
    private FileOutputStream fout = null;
    private FileInputStream fin = null;
    
    private boolean isNet = false;
    
    /** Inicio de actividad */
    public void start()
    {
    	
    	if (this.isNet)
    	{
    	//Conectar
    	try {
			this.socket = new Socket(this.addressWifi.getInetAddress(), this.addressWifi.getPort());
			this.netstream = this.socket.getInputStream();
			
			 readerNMEA = new SentenceReader(netstream);

			 if(this.filelog!=null)
			 {
				 fout = new FileOutputStream(filelog,true);
				 
			 }
			String s = "\n";
			 fout.write(s.getBytes());
			 fout.write(s.getBytes());
			 fout.write(new java.util.Date(System.currentTimeMillis()).toString().getBytes());
			 
		     //register self as a listener for all NMEA sentences
		     readerNMEA.addSentenceListener(this);
		     readerNMEA.start();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	}
    	else
    	{
    	try
    	{
    		// Lectura de fichero en disco; el log n ose escribe, se utiliza el fichero
    		 fin = new FileInputStream(filelog);
    		 
    		 readerNMEA = new SentenceReader(fin);
			 
		     //register self as a listener for all NMEA sentences
		     readerNMEA.addSentenceListener(this);
		     readerNMEA.start();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
    		
    	}
    }
    
    /**
     * Creates a new instance of FileExample
     * 
     * @param f File from which to read Checksum data
     */
    public void FileExample(File file) throws IOException {

        // create sentence reader and provide input stream
       /* InputStream stream = new FileInputStream(file);
        reader = new SentenceReader(stream);

        // register self as a listener for GGA sentences
        reader.addSentenceListener(this, SentenceId.GGA);
        reader.start();
        */
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.event.SentenceListener#readingPaused()
     */
    public void readingPaused() {
        System.out.println("-- Paused --");
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.event.SentenceListener#readingStarted()
     */
    public void readingStarted() {
        System.out.println("-- Started --");
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.event.SentenceListener#readingStopped()
     */
    public void readingStopped() {
        System.out.println("-- Stopped --");
    }

    /**
     * Implements SentenceReader interface for receiving NMEA updates from
     * SentenceReader.
     */
    public void sentenceRead(SentenceEvent event) {

        // Safe to cast as we are registered only for GGA updates, could
        // also cast to PositionSentence if interested only in position.
        // If you receiving all sentences without filtering, check the sentence
        // type before casting (e.g. with Sentence.getSentenceId()).
        
    	//Imprimir evento a fichero de log si existe, sino a pantalla
    	if(this.fout==null)
    		System.out.println(event.getSentence());
    	else
    	{
    		try {
				this.fout.write(event.getSentence().toString().getBytes());
				this.fout.write("\n".getBytes());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	
    	
    	//Convertir evento a su clase
    	
    	switch( event.getSentence().getSentenceId()) 
    	{
    	
    	case "BOD":
    		System.out.println("Bearing Origin to Destination");
        	break;
    	
    	case "DBT":
    		System.out.println("Depth of water below transducer; in meters, feet and fathoms");
    		printDBT(event);
        	break;
    	case "DPT":
    		System.out.println("Depth of water below transducer; in meters.");
        	break;
    	case "GGA":
    		System.out.println("Global Positioning System fix data");
        	break;	
    	case "GLL":
    		System.out.println("Geographic position (latitude/longitude)");
    		printGLL(event);
        	break;		
        	
    	case "GSA":
    		System.out.println(" Dilution of precision (DOP) of GPS fix and active satellites");
        	break;	
    	case "GSV":
    		System.out.println(" Detailed satellite data");
        	break;	
    	case "RMB":
    		System.out.println(" Recommended minimum navigation information");
        	break;	
    	case "RMC":
    		System.out.println(" Recommended minimum specific GPS/TRANSIT data");
        	break;	
    	case "RTE":
    		System.out.println(" Route data and waypoint list");
        	break;	
    	case "WPL":
    		System.out.println(" Waypoint location (latitude/longitude)");
        	break;	
    	case "VTG":
    		System.out.println(" Track made good and ground speed");
        	break;	
    	case "ZDA":
    		System.out.println(" UTC time and date with local time zone offset");
        	break;		
    	default:
    		System.out.println("*****COMANDO DESCONOCIDO ******");
    		break;
 
    	}
    	
    	
    	//GGASentence s = (GGASentence) event.getSentence();

        // Sentencias  (las de abajo)
        //     BODSentence, DateSentence, DBTSentence, DepthSentence, 
        //	DPTSentence, GGASentence, GLLSentence, GSASentence, GSVSentence, 
        // PositionSentence, RMBSentence, RMCSentence, RTESentence, TimeSentence, 
        // WPLSentence, VTGSentence, ZDASentence
        //
        //
        //
        
        
        // do something with sentence data..
       // System.out.println(s.getPosition());
    }

    private void printDBT(SentenceEvent event) {
		// TODO Auto-generated method stub
    	String s = null;
    	
	   	DBTSentence sentence = (DBTSentence) event.getSentence();
    	
    	s="Profundidad: "+sentence.getDepth();
    	System.out.println(s);
	}

	private void printGLL(SentenceEvent event) {
		// TODO Auto-generated method stub
    	String s = null;
    	    	        	   	
    	GLLSentence sentence = (GLLSentence) event.getSentence();
    	Position p = sentence.getPosition();
    	s="Latitud: "+p.getLatitude();
    	s=s+" Longitud: "+p.getLongitude();
    	
    	System.out.println(s);
	}

    
    
	/**
     * Main method takes one command-line argument, the name of the file to
     * read.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
    	 
    	 
    	Address dirWifi = null;
    	LogNMEA lognmea = null;
    	File file = null;
    	boolean isNet = true;
    	
    	System.out.println(""+LogNMEA.VERSION_LOGNMEA);
    	
    	
        if (args.length <= 1) {
            System.out.println("Example usage: java LogNMEA <net/file> <IP> <port> <log file>");
            System.exit(1);
            
                   }

        try {
        	
        	
        	if( args[0] == "net")
        	{
        		isNet = true;
        	}
        	else
        		isNet = false;
        	
        	//	IP + PORT = Address
        	dirWifi = new Address(args[1],Integer.parseInt(args[2]));
        	System.out.println("Direccion TCP/RS232 >> "+dirWifi);
        	//Port
        	
        	     	
        	       	
        	//Log NMEA
        	try{
        	
        		//Log file
            	System.out.println("Logfile to: "+args[3]);
            	
            	file = new File(args[3]);
        	}
        	catch(java.lang.ArrayIndexOutOfBoundsException e){;}
        	
        	
        	
        	lognmea = new LogNMEA(isNet,dirWifi,file);
        	lognmea.start();
        	
        	
        	
        	
        	
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
