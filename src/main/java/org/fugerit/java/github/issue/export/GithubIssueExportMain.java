/**
 * 
 */
package org.fugerit.java.github.issue.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.io.StreamIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubIssueExportMain {

	protected static final Logger logger = LoggerFactory.getLogger(GithubIssueExportMain.class);
	
	public static final String ARG_GUI = "gui";

	public static final String ARG_GUI_LOCALE = "gui_locale";
	
	public static final String ARG_GUI_PRESET_OWNER = "gui_preset-"+GithubIssueExport.ARG_OWNER;
	public static final String ARG_GUI_PRESET_REPO = "gui_preset-"+GithubIssueExport.ARG_REPO;
	public static final String ARG_GUI_PRESET_PROXY_HOST = "gui_preset-"+GithubIssueExport.ARG_PROXY_HOST;
	public static final String ARG_GUI_PRESET_PROXY_PORT = "gui_preset-"+GithubIssueExport.ARG_PROXY_PORT;
	
	public static final String ARG_COPY_RES = "copy-res";
	
	public static void main( String[] args ) {
		Properties params = ArgUtils.getArgs( args );
		// copy res start
		try {
			String copyRes = params.getProperty( ARG_COPY_RES );
			File basePath = GithubIssueConfig.getInstance().getBaseConfigPath();
			File dest = new File( basePath, copyRes );
			if ( !dest.exists() ) {
				InputStream is = GithubIssueExportMain.class.getResourceAsStream( "/"+copyRes );
				FileOutputStream fos = new FileOutputStream( dest );
				StreamIO.pipeStream( is , fos , StreamIO.MODE_CLOSE_OUT_ONLY );
			}
		} catch (Exception e) {
			logger.error( e.getMessage(), e );
		}
		// copy res end
		try {
			String gui = params.getProperty( ARG_GUI, "1" );
			if ( "1".equalsIgnoreCase( gui ) ) {
				String guiLocale = params.getProperty( ARG_GUI_LOCALE );
				if (guiLocale != null) {
					logger.info( "gui locale : "+guiLocale );
					Locale.setDefault( Locale.forLanguageTag( guiLocale ) );
				}
				logger.info( "gui mode : "+gui+" (default if gui mode, if no gui add --gui 0" );
				new GithubIssueGUI( params ); 
			} else {
				logger.info( "no gui mode : "+gui );
				GithubIssueExport.handle( params );	
			}
		} catch (Exception e) {
			logger.error( e.getMessage(), e );
		}
	}
	
}
