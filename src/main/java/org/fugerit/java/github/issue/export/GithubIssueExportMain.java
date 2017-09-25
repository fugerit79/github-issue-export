/**
 * 
 */
package org.fugerit.java.github.issue.export;

import java.util.Properties;

import org.fugerit.java.core.cli.ArgUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mttfranci
 *
 */
public class GithubIssueExportMain {

	protected static final Logger logger = LoggerFactory.getLogger(GithubIssueExportMain.class);
	
	public static final String ARG_GUI = "gui";
	
	public static void main( String[] args ) {
		Properties params = ArgUtils.getArgs( args );
		try {
			String gui = params.getProperty( ARG_GUI, "1" );
			if ( "1".equalsIgnoreCase( gui ) ) {
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
