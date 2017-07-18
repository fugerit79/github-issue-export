package org.fugerit.java.github.issue.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.github.issue.export.helper.PoiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tool for generating a report of github issues on a Repo
 * 
 * parameters :
 *  
 * --owner		github repo owner (required)
 * --repo		github repo name  (required)
 * --xls-file	issue report file (required)
 * --lang		language (optional)
 * 
 * --help		print help usage
 * 
 * @author Daneel <d@fugerit.org>
 *
 */
public class GithubIssueExport {

	protected static final Logger logger = LoggerFactory.getLogger(GithubIssueExport.class);
	
	public static final String ARG_HELP = "help";
	
	public static final String ARG_REPO = "repo";
	
	public static final String ARG_OWNER = "owner";
	
	public static final String ARG_XLSFILE = "xls-file";
	
	public static final String ARG_LANG = "lang";
	
	private final static String HEADER[] = {
			"#", "Title", "State", "Labels", "Assigned", "Assigned on", "Created by", "Creation", "Update", "Closed", "# Comments", "URL", "Body" 
	};
	
	private final static String HEADER_IT[] = {
			"#", "Titolo", "Stato", "Etichette", "Assegnato", "Data assegnazione", "Creato da", "Creazione", "Aggiornamento", "Chiuso", "# Commenti", "URL", "Testo" 
	};
	
	private static final Map<String, String[]> HEADER_MAP = new HashedMap<String, String[]>();
	static {
		HEADER_MAP.put( "it" , HEADER_IT );
		HEADER_MAP.put( "en" , HEADER );
		HEADER_MAP.put( "default" , HEADER );
	}
	
	private static List<Map> parseJsonData( String data ) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getFactory();
		JsonParser jp = factory.createParser( data );
		JsonNode node = jp.readValueAsTree();
		// data mapping
		ArrayList<Map> issueList = new ArrayList<Map>();
		issueList = ( ArrayList<Map> )buildModel( String.valueOf( node ), issueList.getClass() );
		return issueList;
	}
	
	private static void handle( Properties params ) throws Exception {
		List<List<String>> lines = new ArrayList<List<String>>();
		// data read
		String data = readData(params);
		List<Map> issueList = parseJsonData( data );
		Iterator<Map> issueIt = issueList.iterator();
		while ( issueIt.hasNext() ) {
			Map issue = issueIt.next();
			Set<Object> keys = issue.keySet();
			List<String> currentLine = new ArrayList<String>();
			currentLine.add( String.valueOf( issue.get( "number" ) ) );
			currentLine.add( String.valueOf( issue.get( "title" ) ) );
			currentLine.add( String.valueOf( issue.get( "state" ) ) );
			// labels
			List labels = (List)issue.get( "labels" );
			if ( labels != null && labels.size() > 0 ) {
				Iterator itLables = labels.iterator();
				StringBuffer labelList = new StringBuffer();
				while ( itLables.hasNext() ) {
					Map currentLabel = (Map)itLables.next();
					labelList.append( currentLabel.get( "name" ) );
					labelList.append( ", " );
				}
				currentLine.add( labelList.toString() );
			} else {
				currentLine.add( "-" );
			}
			// assigned
			Map assignee = (Map)issue.get( "assignee" );
			if ( assignee != null ) {
				currentLine.add( String.valueOf( assignee.get( "login" ) ) );
				String eventUrl = String.valueOf( issue.get( "events_url" ) );
				String eventsData = readUrlData( eventUrl );
				List<Map> eventsList = parseJsonData( eventsData );
				Iterator<Map> eventsIt = eventsList.iterator();
				String assignDate = null;
				while ( eventsIt.hasNext() ) {
					Map currentEvent = eventsIt.next();
					String eventType = String.valueOf( currentEvent.get( "event" ) );
					if ( eventType.equalsIgnoreCase( "assigned" ) ) {
						assignDate = String.valueOf( currentEvent.get( "created_at" ) );
					}
				}
				currentLine.add( assignDate );
			} else {
				currentLine.add( "-" );
				currentLine.add( "-" );
			}
			Map user = (Map)issue.get( "user" );
			currentLine.add( String.valueOf( user.get( "login" ) ) );
			currentLine.add( String.valueOf( issue.get( "created_at" ) ) );
			currentLine.add( String.valueOf( issue.get( "updated_at" ) ) );
			currentLine.add( String.valueOf( issue.get( "closed_at" ) ) );
			currentLine.add( String.valueOf( issue.get( "comments" ) ) );
			currentLine.add( String.valueOf( issue.get( "html_url" ) ) );
			currentLine.add( String.valueOf( issue.get( "body" ) ) );
			lines.add( currentLine );
		}
		handleExcel(params, lines);
	}

	private static String readUrlData( String url ) throws Exception {
		URL u = new URL( url );
		HttpURLConnection conn = (HttpURLConnection)u.openConnection();
		StringBuffer buffer = new StringBuffer();
		if ( conn.getResponseCode() != 200 ) {
			throw new Exception( "HTTP exit code : "+conn.getResponseCode() );
		} else {
			BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
			String line = br.readLine();
			while ( line != null ) {
				buffer.append( line );
				line = br.readLine();
			}
			br.close();
		}
		conn.disconnect();
		return buffer.toString();
	}
	
	private static String readData( Properties params ) throws Exception {
		String repo = params.getProperty( ARG_REPO );
		String owner = params.getProperty( ARG_OWNER );
		String url = "https://api.github.com/repos/"+owner+"/"+repo+"/issues?page=1&per_page=1000";
		return readUrlData( url );
	}
	
	public static Object buildModel( String data, Class c ) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		// jackson 1.9 and before
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Object items = objectMapper.readValue( data , c );
		return items;
	}
	
	public static String getValue( Object val ) {
		String res = null;
		if ( val != null ) {
			res = String.valueOf( val );
		}
		return res;
	}
	
	private static void handleExcel( Properties params, List<List<String>> lines ) throws Exception {
		String xlsFile = params.getProperty( "xls-file" );
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet( "Report github issue" );
		CellStyle headerStyle = PoiHelper.getHeaderStyle( workbook );
		String lang = params.getProperty( ARG_LANG, "en" );
		String[] header = HEADER_MAP.get( lang );
		PoiHelper.addRow( header , 0, sheet, headerStyle );
		int count = 1;
		Iterator<List<String>> itLines = lines.iterator();
		while ( itLines.hasNext() ) {
			List<String> current = itLines.next();
			String[] currentLine = new String[current.size()];
			currentLine = current.toArray( currentLine );
			PoiHelper.addRow( currentLine , count, sheet);
			count++;
		}
		PoiHelper.resizeSheet( sheet );
		logger.info( "Writing xls to file : '"+xlsFile+"'" );
		workbook.close();
		FileOutputStream fos = new FileOutputStream( new File( xlsFile ) );
		workbook.write( fos );
		fos.flush();
		fos.close();
		
	}
		
	public static void main( String[] args ) {
		Properties params = ArgUtils.getArgs( args );
		try {
			handle( params );
		} catch (Exception e) {
			logger.error( e.getMessage(), e );
		}
	}
	
}
