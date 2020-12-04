package rawDeepLearningClassifer.dlClassification.soundSpot;

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
	 * Pane containing controls to set up the OrcaSPot classifier. 
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
	public SettingsPane<PamSoundSpotParams> getSettingsPane() {
		if (soundSpotPane==null) {
			soundSpotPane = new  SoundSpotPane(soundSpotClassifier); 
		}
		return soundSpotPane;
	}

	@Override
	public void getParams() {
		PamSoundSpotParams orcaSpotParams =  getSettingsPane().getParams(soundSpotClassifier.getSoundSpotParams()); 
		soundSpotClassifier.setSoundSpotParams(orcaSpotParams.clone()); //be safe and clone.  
		
	}

	@Override
	public void setParams() {
		 getSettingsPane() .setParams(soundSpotClassifier.getSoundSpotParams());
		
	}


	@Override
	public JPanel getSidePanel() {
		// TODO Auto-generated method stub
		return null;
	}

}