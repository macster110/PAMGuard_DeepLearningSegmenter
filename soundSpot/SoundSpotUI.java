package rawDeepLearningClassifer.soundSpot;

import javax.swing.JPanel;

import PamController.SettingsPane;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.orcaSpot.OrcaSpotParams2;


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
	public SettingsPane<SoundSpotParams> getSettingsPane() {
		if (soundSpotPane==null) {
			soundSpotPane = new  SoundSpotPane(null); 
		}
		return soundSpotPane;
	}

	@Override
	public void getParams() {
		SoundSpotParams orcaSpotParams =  getSettingsPane().getParams(soundSpotClassifier.getSoundSpotParams()); 
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
