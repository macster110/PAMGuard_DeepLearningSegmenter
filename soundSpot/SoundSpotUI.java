package rawDeepLearningClassifer.soundSpot;

import javax.swing.JPanel;

import PamController.SettingsPane;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;


/**
 * UI components for the SoundSpot deep learning model. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class SoundSpotUI implements DLCLassiferModelUI {
	
	/**
	 * Pane containing controls to set up the OrcaSPot classiifer. 
	 */
	private SoundSpotPane soundSpotPane;
	
	/**
	 * The sound spot classifier. 
	 */
	private SoundSpotClassifier soundSpotClassifier;
	
	/**
	 * SondSpot classifier. 
	 * @param soundSpotClassifier
	 */
	public SoundSpotUI(SoundSpotClassifier soundSpotClassifier) {
		this.soundSpotClassifier=soundSpotClassifier; 
	}

	@Override
	public SettingsPane<?> getSettingsPane() {
		if (soundSpotPane==null) {
			soundSpotPane = new  SoundSpotPane(null); 
		}
		return soundSpotPane;
	}

	@Override
	public void getParams() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParams() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JPanel getSidePanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
