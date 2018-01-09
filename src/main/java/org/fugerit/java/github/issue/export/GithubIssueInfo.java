package org.fugerit.java.github.issue.export;

import java.io.IOException;
import java.util.Properties;

public class GithubIssueInfo {

	public GithubIssueInfo( Properties params ) throws IOException {
		super();
		this.params = params;
		this.cache = GithubIssueConfig.getInstance().loadCachePropForRepo( this.getOwner() , this.getRepo() );
	}

	private Properties params;
	
	private Properties cache;

	public Properties getParams() {
		return params;
	}
	
	public Properties getCache() {
		return cache;
	}

	public String getRepo() {
		return this.getParams().getProperty( GithubIssueExport.ARG_REPO );
	}
	
	public String getOwner() {
		return this.getParams().getProperty( GithubIssueExport.ARG_OWNER );
	}

	public String getProperty( String key ) {
		return params.getProperty(key);
	}

	public String getProperty( String key, String defaultValue ) {
		return params.getProperty(key, defaultValue);
	}
	
	public void addCacheEntry( String issueId, String field, String value ) {
		this.cache.setProperty( issueId+"."+field , value );
	}
	
	public String getCacheEntry( String issueId, String field ) {
		return this.cache.getProperty( issueId+"."+field );
	}
	
}
