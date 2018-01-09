package org.fugerit.java.github.issue.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GithubIssueConfig {

	public static final String CONFIG_FOLDER = ".github-issue-export";
	
	public static final String MAIN_CONFIG_FILE = "saved-config.properties";
		
	private static final GithubIssueConfig INSTANCE = new GithubIssueConfig();
	
	public static final String FIELD_ASSIGN_DATE = "assign_date"; 
	
	public static GithubIssueConfig getInstance() {
		return INSTANCE;
	}
	
	public File getBaseConfigPath() {
		File file = new File( System.getProperty( "user.home" ), CONFIG_FOLDER );
		return file;
	}
	
	public File getMainConfigFile() {
		return new File( getBaseConfigPath(), MAIN_CONFIG_FILE );
	}
	
	public File getCacheFileForRepo( String owner, String repo ) {
		String baseName = "cache-"+owner+"-"+repo+".properties";
		return new File( getBaseConfigPath(), baseName );
	}
	
	public Properties loadCachePropForRepo( String owner, String repo ) throws IOException {
		Properties cache = new Properties();
		File file = this.getCacheFileForRepo(owner, repo);
		if ( file.exists() ) {
			FileInputStream fis = new FileInputStream( file );
			cache.load( fis );
			fis.close();	
		}
		return cache;
	}
	
	public void saveCachePropForRepo( Properties cache, String owner, String repo ) throws IOException {
		FileOutputStream fos = new FileOutputStream( this.getCacheFileForRepo(owner, repo) );
		cache.store( fos , "cache file for repo : "+owner+"/"+repo );
		fos.close();
	}
	
}
