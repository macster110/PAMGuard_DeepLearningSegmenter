package rawDeepLearningClassifer.dlClassification.genericModel;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.jamdev.jdl4pam.transforms.DLTransformsFactory;
import org.jamdev.jdl4pam.transforms.DLTransfromParams;

import PamController.SettingsPane;
import rawDeepLearningClassifer.dlClassification.soundSpot.StandardModelParams;
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
	public SettingsPane<StandardModelParams> getSettingsPane() {
		if (soundSpotPane==null) {
			soundSpotPane = new  GenericModelPane(genericModelClassifier); 
		}
		return soundSpotPane;
	}

	@Override
	public void getParams() {
		GenericModelParams orcaSpotParams =  (GenericModelParams) getSettingsPane().getParams(genericModelClassifier.getGenericDLParams()); 
		
		
		//System.out.println("Get generic model params: " +  orcaSpotParams.dlTransfromParams + "  transforms: " + orcaSpotParams.dlTransfroms); 
		genericModelClassifier.setGenericModelParams(orcaSpotParams.clone()); //be safe and clone.  	
	}

	
	@Override
	public void setParams() {
//		System.out.println("Set model params: " + genericModelClassifier.getGenericDLParams().dlTransfromParams.size()); 
		getSettingsPane().setParams(genericModelClassifier.getGenericDLParams());
	}
	

	@Override
	public JPanel getSidePanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
