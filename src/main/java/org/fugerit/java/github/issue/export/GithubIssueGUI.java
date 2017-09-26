/**
 * 
 */
package org.fugerit.java.github.issue.export;

import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
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
	
	private JPasswordField inputProxyPass, inputRepoPass;
	
	private JTextField inputRepoName, inputRepoOwner, inputRepoUser, inputXlsPath, inputLocale;
	
	private JComboBox<String> inputStateCombo;
	
	private String labelStateOpen, labelStateClosed, labelStateAll;
	
	private ResourceBundle lagelBundle;
	
	private JButton buttonSaveConfiguration, buttonGenerateReport;
	
	private JTextArea outputArea;
	
	private File configSavePath;
	
	private static JLabel newJLabel( String text, int hAlign ) {
		JLabel label = new JLabel( text );
		configureLayout( label );
		label.setHorizontalAlignment( hAlign );
		Font f = label.getFont();
		f = new Font( f.getFontName(), Font.BOLD, f.getSize() );
		label.setFont( f );
		return label;
	}
	
	private static JLabel newJLabel( String text ) {
		return newJLabel( text, JLabel.RIGHT );
	}
	
	private static JTextField newJTextField( String text ) {
		JTextField field = new JTextField( text );
		configureLayout( field );
		return field;
	}
	
	private static void configureLayout( Component c) {
		c.setBackground( new Color( 220, 220, 255 ) );
		c.setForeground( new Color( 0, 0, 80 ) );
	}
	
	private static void addRow( Component c1 , JPanel parent ) {
		configureLayout( c1 );
		parent.add( c1 );
	}
	
	private static JPanel newRowPanel( Component c1, Component c2, JPanel parent ) {
		JPanel panel = new JPanel( new GridLayout( 1, 2 ) );
		configureLayout( c1 );
		configureLayout( c2 );
		configureLayout( panel );
		panel.add( c1 );
		panel.add( c2 );
		parent.add( panel );
		return panel;
	}
	
	/*
	 * Configuration init method
	 */
	private void initConf() {
		this.config = new Properties();
		// handle config file
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
		// i18n
		String defaultLocale = this.config.getProperty( GithubIssueExport.ARG_LANG, Locale.getDefault().toString() );
		Locale loc = Locale.getDefault();
		if ( !StringUtils.isEmpty( defaultLocale ) ) {
			try {
				loc = Locale.forLanguageTag( defaultLocale );	
			} catch (Exception e) {
				logger.warn( "Errore overriding locale : "+defaultLocale+", using default : "+loc, e );
			}
		}
		this.lagelBundle = ResourceBundle.getBundle( "org.fugerit.java.github.issue.export.config.gui.gui-label", loc );
		// create components
		this.outputArea = new JTextArea( this.lagelBundle.getString( "label.output.area.init" ) );
		this.outputArea.setEditable( false );
		String defaultInputText = "";
		// proxy configuration
		this.inputProxyHost = newJTextField( this.config.getProperty( GithubIssueExport.ARG_PROXY_HOST, defaultInputText ) );
		this.inputProxyPort = newJTextField( this.config.getProperty( GithubIssueExport.ARG_PROXY_PORT, defaultInputText ) );
		this.inputProxyUser = newJTextField( this.config.getProperty( GithubIssueExport.ARG_PROXY_USER, defaultInputText ) );
		this.inputProxyPass = new JPasswordField();
		// repository configuration
		this.inputRepoOwner = newJTextField( this.config.getProperty( GithubIssueExport.ARG_OWNER, defaultInputText ) );
		this.inputRepoName = newJTextField( this.config.getProperty( GithubIssueExport.ARG_REPO, defaultInputText ) );
		this.inputRepoUser = newJTextField( this.config.getProperty( GithubIssueExport.ARG_GITHUB_USER, defaultInputText ) );
		this.inputRepoPass = new JPasswordField();
		this.inputXlsPath = newJTextField( this.config.getProperty( GithubIssueExport.ARG_XLSFILE, defaultInputText ) );
		this.inputLocale = newJTextField( defaultLocale );
		this.inputStateCombo = new JComboBox<String>();
		this.labelStateOpen = this.lagelBundle.getString( "label.input.state.open" );
		this.labelStateClosed = this.lagelBundle.getString( "label.input.state.closed" );
		this.labelStateAll = this.lagelBundle.getString( "label.input.state.all" );
		this.inputStateCombo.addItem( this.labelStateOpen );
		this.inputStateCombo.addItem( this.labelStateClosed );
		this.inputStateCombo.addItem( this.labelStateAll );
		String selectedState = this.config.getProperty( GithubIssueExport.ARG_STATE );
		if ( selectedState.equalsIgnoreCase( GithubIssueExport.ARG_STATE_OPEN ) ) {
			this.inputStateCombo.setSelectedItem( this.labelStateOpen );	
		} else if ( selectedState.equalsIgnoreCase( GithubIssueExport.ARG_STATE_CLOSED ) ) {
			this.inputStateCombo.setSelectedItem( this.labelStateClosed );
		} else if ( selectedState.equalsIgnoreCase( GithubIssueExport.ARG_STATE_ALL ) ) {
			this.inputStateCombo.setSelectedItem( this.labelStateAll );
		}
		// buttons
		this.buttonSaveConfiguration = new JButton( this.lagelBundle.getString( "button.input.configuration.save" ) );
		this.buttonSaveConfiguration.addActionListener( this );
		this.buttonGenerateReport = new JButton( this.lagelBundle.getString( "button.input.generate.report" ) );
		this.buttonGenerateReport.addActionListener( this );
		
	}
	
	/*
	 * Layout
	 */
	private void initLayout() {
		this.setTitle( this.lagelBundle.getString( "frame.title" ) );
		
		// configure layout
		JPanel mainPanel = new JPanel();
		GridLayout ml = new GridLayout( 13, 1 );
		mainPanel.setLayout( ml );
		configureLayout( mainPanel );
		
		// repository config
		// add row
		addRow( newJLabel( this.lagelBundle.getString( "label.input.repo.title" ), JLabel.CENTER ), mainPanel );
		// add row
		JPanel repoPanel1 = new JPanel( new GridLayout( 1 , 2 ) );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.repo.owner" ) ), this.inputRepoOwner, repoPanel1 );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.repo.name" ) ), this.inputRepoName, repoPanel1 );
		addRow( repoPanel1 , mainPanel );
		// add row
		JPanel repoPanel2 = new JPanel( new GridLayout( 1 , 2 ) );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.repo.user" ) ), this.inputRepoUser, repoPanel2 );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.repo.pass" ) ), this.inputRepoPass, repoPanel2 );
		addRow( repoPanel2 , mainPanel );
		// add row
		addRow( new JLabel( "" ), mainPanel );
		
		// report config
		// add row
		addRow( newJLabel( this.lagelBundle.getString( "label.input.report.title" ), JLabel.CENTER ), mainPanel );
		// add row
		JPanel reportPanel1 = new JPanel( new GridLayout( 1 , 2 ) );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.input.language" ) ), this.inputLocale, reportPanel1 );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.state.label" ) ), this.inputStateCombo, reportPanel1 );
		addRow( reportPanel1 , mainPanel );
		// add row
		JPanel reportPanel2 = new JPanel( new GridLayout( 1 , 2 ) );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.output.xls" ) ), this.inputXlsPath, reportPanel2 );
		reportPanel2.add( new JLabel( "" ) );
		addRow( reportPanel2 , mainPanel );
		// add row
		addRow( new JLabel( "" ), mainPanel );

		// proxy field config
		// add row		
		addRow( newJLabel( this.lagelBundle.getString( "label.input.proxy.title" ), JLabel.CENTER ), mainPanel );				
		// add row
		JPanel proxyPanel1 = new JPanel( new GridLayout( 1 , 2 ) );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.proxy.host" ) ), this.inputProxyHost, proxyPanel1 );	
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.proxy.port" ) ), this.inputProxyPort, proxyPanel1 );
		addRow( proxyPanel1 , mainPanel );
		// add row
		JPanel proxyPanel2 = new JPanel( new GridLayout( 1 , 2 ) );
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.proxy.user" ) ), this.inputProxyUser, proxyPanel2 );		
		newRowPanel( newJLabel( this.lagelBundle.getString( "label.input.proxy.pass" ) ), this.inputProxyPass, proxyPanel2 );
		addRow( proxyPanel2 , mainPanel );
		// add row
		addRow( new JLabel( "" ), mainPanel );
		
		// buttons
		// add row
		newRowPanel( this.buttonSaveConfiguration, this.buttonGenerateReport, mainPanel );									
		
		this.addWindowListener( this );
		
		this.setLayout( new GridLayout( 2 , 1 ) );
		this.add( mainPanel );
		this.add( this.outputArea );
		
		this.setSize( 640 , 480 );
		this.setVisible( true );
	}
	
	public GithubIssueGUI( Properties params ) {
		super( "GITHUB ISSUE EXPORT GUI" );
		this.initConf();
		this.initLayout();
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
		this.config.setProperty( GithubIssueExport.ARG_LANG , this.inputLocale.getText() );
		this.config.setProperty( GithubIssueExport.ARG_PROXY_PASS , new String( this.inputProxyPass.getPassword() ) );
		this.config.setProperty( GithubIssueExport.ARG_GITHUB_USER , this.inputRepoUser.getText() );
		this.config.setProperty( GithubIssueExport.ARG_GITHUB_PASS , new String( this.inputRepoPass.getPassword() ) );
		String selectedState = this.inputStateCombo.getSelectedItem().toString();
		if ( selectedState.equalsIgnoreCase( this.labelStateOpen ) ) {
			this.config.setProperty( GithubIssueExport.ARG_STATE , GithubIssueExport.ARG_STATE_OPEN );	
		} else if ( selectedState.equalsIgnoreCase( this.labelStateClosed ) ) {
			this.config.setProperty( GithubIssueExport.ARG_STATE , GithubIssueExport.ARG_STATE_CLOSED );
		} else if ( selectedState.equalsIgnoreCase( this.labelStateAll ) ) {
			this.config.setProperty( GithubIssueExport.ARG_STATE , GithubIssueExport.ARG_STATE_ALL );
		}
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
			String tempPass1 = this.config.getProperty( GithubIssueExport.ARG_GITHUB_PASS );
			String tempPass2 = this.config.getProperty( GithubIssueExport.ARG_PROXY_PASS );
			this.config.remove( GithubIssueExport.ARG_GITHUB_PASS );
			this.config.remove( GithubIssueExport.ARG_PROXY_PASS );
			try {
				if ( !this.configSavePath.getParentFile().exists() )  {
					this.configSavePath.getParentFile().mkdirs();
				}
				FileOutputStream fos = new FileOutputStream( this.configSavePath );
				this.config.store( fos , "Config saved on "+new Date() );
				fos.close();
				String baseText = this.lagelBundle.getString( "label.output.area.configuration.saved" );
				this.outputArea.setText( baseText+" "+this.configSavePath.getAbsolutePath() );
			} catch (Exception ex) {
				logger.warn( "Failed to save configuration "+this.configSavePath, ex );
			}
			this.config.setProperty( GithubIssueExport.ARG_GITHUB_PASS , tempPass1 );
			this.config.setProperty( GithubIssueExport.ARG_PROXY_PASS , tempPass2 );	
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
