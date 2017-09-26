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
	
	private JPasswordField inputProxyPass;
	
	private JTextField inputRepoName, inputRepoOwner, inputXlsPath, inputLocale;
	
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
		
		this.setTitle( this.lagelBundle.getString( "frame.title" ) );
		
		JPanel mainPanel = new JPanel( new GridLayout( 11 , 2 ) );
		
		configureLayout( this );
		configureLayout( mainPanel );
		
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
		
		// heading
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.column.prop.name" ), JLabel.CENTER ) );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.column.prop.value" ), JLabel.CENTER ) );
		
		// report config
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.repo.owner" ) ) );
		mainPanel.add( this.inputRepoOwner );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.repo.name" ) ) );
		mainPanel.add( this.inputRepoName );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.input.language" ) ) );
		mainPanel.add( this.inputLocale );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.output.xls" ) ) );
		mainPanel.add( this.inputXlsPath );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.state.label" ) ) );
		mainPanel.add( this.inputStateCombo );
		
		// proxy field config
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.host" ) ) );
		mainPanel.add( this.inputProxyHost );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.port" ) ) );
		mainPanel.add( this.inputProxyPort );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.user" ) ) );
		mainPanel.add( this.inputProxyUser );
		mainPanel.add( newJLabel( this.lagelBundle.getString( "label.input.proxy.pass" ) ) );
		mainPanel.add( this.inputProxyPass );
		
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
		this.config.setProperty( GithubIssueExport.ARG_LANG , this.inputLocale.getText() );
		this.config.setProperty( GithubIssueExport.ARG_PROXY_PASS , new String( this.inputProxyPass.getPassword() ) );
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
			String tempPass = this.config.getProperty( GithubIssueExport.ARG_PROXY_PASS );
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
