package rawDeepLearningClassifer.dlClassification.genericModel;

import javax.swing.JPanel;

import PamController.SettingsPane;
import rawDeepLearningClassifer.dlClassification.soundSpot.PamSoundSpotParams;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;


/**
 * UI components for the generic model UI. 
 * 
 * @author Jamie Macaulay
 *
 */
public class GenericModelUI  implements DLCLassiferModelUI {
	
	/**
	 * Pane containing controls to set up the OrcaSPot classifier. 
	 */
	private GenericModelPane soundSpotPane;
	
	/**
	 * The sound spot classifier. 
	 */
	private GenericDLClassifier genericModelClassifier;
	
	/**
	 * SondSpot classifier. 
	 * @param soundSpotClassifier
	 */
	public GenericModelUI(GenericDLClassifier soundSpotClassifier) {
		this.genericModelClassifier=soundSpotClassifier; 
	}

	@Override
	public SettingsPane<PamSoundSpotParams> getSettingsPane() {
		if (soundSpotPane==null) {
			soundSpotPane = new  GenericModelPane(genericModelClassifier); 
		}
		return soundSpotPane;
	}

	@Override
	public void getParams() {
		PamSoundSpotParams orcaSpotParams =  getSettingsPane().getParams(genericModelClassifier.getGenericDLParams()); 
		genericModelClassifier.setSoundSpotParams(orcaSpotParams.clone()); //be safe and clone.  	
	}

	
	@Override
	public void setParams() {
		 getSettingsPane().setParams(genericModelClassifier.getGenericDLParams());
	}
	

	@Override
	public JPanel getSidePanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
