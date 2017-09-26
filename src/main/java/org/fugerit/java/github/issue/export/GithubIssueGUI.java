/**
 * 
 */
package org.fugerit.java.github.issue.export;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mttfranci
 *
 */
public class GithubIssueGUI extends JFrame implements WindowListener, ActionListener {

	protected static final Logger logger = LoggerFactory.getLogger(GithubIssueGUI.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7199253866506495111L;
	
	private Properties config;
	
	private JTextField inputProxyHost, inputProxyPort, inputProxyUser;
	
	private JPasswordField inputProxyPass;
	
	private JTextField inputRepoName, inputRepoOwner, inputXlsPath;
	
	private ResourceBundle lagelBundle;
	
	private JButton buttonSaveConfiguration, buttonGenerateReport;
	
	private JTextArea outputArea;
	
	private File configSavePath;
	
	private static JLabel newJLabel( String text, int hAlign ) {
		JLabel label = new JLabel( text );
		label.setHorizontalAlignment( hAlign );
		Font f = label.getFont();
		f = new Font( f.getFontName(), Font.BOLD, f.getSize() );
		label.setFont( f );
		return label;
	}
	
	private static JLabel newJLabel( String text ) {
		return newJLabel( text, JLabel.RIGHT );
	}
	
	public GithubIssueGUI( Properties params ) {
		super( "GITHUB ISSUE EXPORT GUI" );
		
		this.config = new Properties();
		
		this.configSavePath = new File( System.getProperty( "user.home" ), ".github-issue-export"+File.separator+"saved-config.properties" );
		
		if ( this.configSavePath.exists() ) {
			try {
				FileInputStream fis = new FileInputStream( this.configSavePath );
				this.config.load( fis );
				fis.close();
				logger.info( "Config loaded : "+this.configSavePath );
			} catch (Exception e) {
				logger.warn( "Failed to load configuration "+this.configSavePath, e );
			} 
		} else {
			logger.info( "Config file does not exist : "+this.configSavePath );
		}
		
		Locale loc = Locale.getDefault();
		this.lagelBundle = ResourceBundle.getBundle( "gui.gui-label", loc );
		
		this.setTitle( this.lagelBundle.getString( "frame.title" ) );
		
		JPanel mainPanel = new JPanel( new GridLayout( 10 , 2 ) );
		this.outputArea = new JTextArea( this.lagelBundle.getString( "label.output.area.init" ) );
		this.outputArea.setEditable( false );
			
		String defaultInputText = "";
		
		// input config
		this.inputProxyHost = new JTextField( this.config.getProperty( GithubIssueExport.ARG_PROXY_HOST, defaultInputText ) );
		this.inputProxyPort = new JTextField( this.config.getProperty( GithubIssueExport.ARG_PROXY_PORT, defaultInputText ) );
		this.inputProxyUser = new JTextField( this.config.getProperty( GithubIssueExport.ARG_PROXY_USER, defaultInputText ) );
		this.inputProxyPass = new JPasswordField();
		this.inputRepoOwner = new JTextField( this.config.getProperty( GithubIssueExport.ARG_OWNER, defaultInputText ) );
		this.inputRepoName = new JTextField( this.config.getProperty( GithubIssueExport.ARG_REPO, defaultInputText ) );
		this.inputXlsPath = new JTextField( this.config.getProperty( GithubIssueExport.ARG_XLSFILE, defaultInputText ) );
		
		// buttons
		this.buttonSaveConfiguration = new JButton( this.lagelBundle.getString( "button.input.configuration.save" ) );
		this.buttonSaveConfiguration.addActionListener( this );
		this.buttonGenerateReport = new JButton( this.lagelBundle.getString( "button.input.generate.report" ) );
		this.buttonGenerateReport.addActionListener( this );
		
		// heading
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.column.prop.name" ), JLabel.CENTER ) );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.column.prop.value" ), JLabel.CENTER ) );
		
		// proxy field config
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.host" ) ) );
		mainPanel.add( this.inputProxyHost );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.port" ) ) );
		mainPanel.add( this.inputProxyPort );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.user" ) ) );
		mainPanel.add( this.inputProxyUser );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.pass" ) ) );
		mainPanel.add( this.inputProxyPass );
		
		// repo config
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.repo.owner" ) ) );
		mainPanel.add( this.inputRepoOwner );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.repo.name" ) ) );
		mainPanel.add( this.inputRepoName );
		
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.output.xls" ) ) );
		mainPanel.add( this.inputXlsPath );
		
		// spacing
		mainPanel.add( newJLabel( "", JLabel.CENTER ) );
		mainPanel.add( newJLabel( "", JLabel.CENTER ) );
		
		// buttons
		mainPanel.add( this.buttonSaveConfiguration );
		mainPanel.add( this.buttonGenerateReport );
		
		
		this.addWindowListener( this );
		
		this.setLayout( new GridLayout( 2 , 1 ) );
		this.add( mainPanel );
		this.add( this.outputArea );
		
		this.setSize( 640 , 480 );
		this.setVisible( true );
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		this.config.setProperty( GithubIssueExport.ARG_PROXY_HOST , this.inputProxyHost.getText() );
		this.config.setProperty( GithubIssueExport.ARG_PROXY_PORT , this.inputProxyPort.getText() );
		this.config.setProperty( GithubIssueExport.ARG_PROXY_USER , this.inputProxyUser.getText() );
		this.config.setProperty( GithubIssueExport.ARG_OWNER , this.inputRepoOwner.getText() );
		this.config.setProperty( GithubIssueExport.ARG_REPO , this.inputRepoName.getText() );
		this.config.setProperty( GithubIssueExport.ARG_XLSFILE , this.inputXlsPath.getText() );
		this.config.setProperty( GithubIssueExport.ARG_PROXY_PASS , new String( this.inputProxyPass.getPassword() ) );
		if ( e.getSource() == this.buttonGenerateReport ) {
			try {
				GithubIssueExport.handle( this.config );
				String baseText = this.lagelBundle.getString( "label.output.area.generate.ok" );
				this.outputArea.setText( baseText + new File( this.inputXlsPath.getText() ).getAbsolutePath() );
			}  catch (Exception ex) {
				logger.warn( "Report generation failed "+ex, ex );
				String baseText = this.lagelBundle.getString( "label.output.area.generate.ko" );
				this.outputArea.setText( baseText+ex.getMessage() );
			} 
		} else if ( e.getSource() == this.buttonSaveConfiguration ) {
			String tempPass = this.config.getProperty( GithubIssueExport.ARG_PROXY_PASS );
			this.config.remove( GithubIssueExport.ARG_PROXY_PASS );
			try {
				if ( !this.configSavePath.getParentFile().exists() )  {
					this.configSavePath.getParentFile().mkdirs();
				}
				FileOutputStream fos = new FileOutputStream( this.configSavePath );
				this.config.store( fos , "Config saved on "+new Date() );
				fos.close();
			} catch (Exception ex) {
				logger.warn( "Failed to save configuration "+this.configSavePath, ex );
			}
			this.config.setProperty( GithubIssueExport.ARG_PROXY_PASS , tempPass );	
		}
	}

	// windows event
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		if ( e.getSource() == this ) {
			this.setVisible( false );
			this.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {
	}
		
	
}
