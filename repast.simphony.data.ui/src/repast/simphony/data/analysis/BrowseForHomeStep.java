package repast.simphony.data.analysis;

import org.pietschy.wizard.PanelWizardStep;
import repast.simphony.util.SystemConstants;
import saf.core.ui.util.FileChooserUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class BrowseForHomeStep extends PanelWizardStep {

	private String installHome;
	private String defaultLocation;
	
	public JTextField homeDirField;

	public BrowseForHomeStep(String name, String installHome, String defaultLocation) {
		super(name + " home", "<HTML>Please select " + name + "'s executable file.  " +
				"The default installation location for " + name+ " is " + defaultLocation + ".");

		this.installHome = installHome;
		this.defaultLocation = defaultLocation;
		
		setupPanel();
	}

	private void setupPanel() {
		if (installHome == null)
		  installHome = defaultLocation;
			
		homeDirField = new JTextField(installHome);
		homeDirField.setPreferredSize(new Dimension(400, 20));
		add(homeDirField);

		JButton browseButton = new JButton("Browse");
		browseButton.setMnemonic('b');
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				homeDirField.setText(browseForExecutable());
			}
		});

		add(browseButton);

		setComplete(true);
	}
	private String browseForExecutable() {
		File home = FileChooserUtilities.getOpenFile(new File(defaultLocation));

		String directory;
		if (home != null) {
			directory = home.getAbsolutePath();
		} else {
			directory = homeDirField.getText();
		}

		return directory;
	}

	public String getInstallHome() {
		return homeDirField.getText();
	}

	public String getDefaultLocation() {
		return defaultLocation;
	}
}
