package rawDeepLearningClassifer.dlClassification.genericModel;

import java.io.Serializable;
import java.util.ArrayList;

import PamController.PamControlledUnitSettings;
import PamController.PamSettings;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLClassName;
import rawDeepLearningClassifer.dlClassification.DLClassiferModel;
import rawDeepLearningClassifer.dlClassification.ModelResult;
import rawDeepLearningClassifer.dlClassification.soundSpot.PamSoundSpotParams;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;


/**
 * A generic model - can be load any model but requires manaully setting model metadaata. 
 * @author Jamie Macaulay
 * 
 *
 */
public class GenericDLClassifier implements DLClassiferModel, PamSettings {

	
	private DLControl dlControl;
	
	private GenericModelParams genericModelParams = new GenericModelParams();

	private DLCLassiferModelUI genericModelUI; 


	public GenericDLClassifier(DLControl dlControl) {
		this.dlControl=dlControl; 
		
		genericModelUI = new GenericModelUI(this); 
	}
	

	@Override
	public void prepModel() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void closeModel() {
		// TODO Auto-generated method stub
	}


	@Override
	public int getNumClasses() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getUnitName() {
		return dlControl.getUnitName()+"_generic_model"; 
	}

	@Override
	public String getUnitType() {
		return dlControl.getUnitType()+"_generic_model";
	}


	@Override
	public DLClassName[] getClassNames() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ArrayList<? extends ModelResult> runModel(ArrayList<GroupedRawData> rawDataUnit) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DLControl getDLControl() {
		return dlControl;
	}
	
	
	@Override
	public String getName() {
		return "Generic Model";
	}

	@Override
	public DLCLassiferModelUI getModelUI() {
		return genericModelUI;
	}

	@Override
	public Serializable getDLModelSettings() {
		return genericModelParams;
	}
	
	@Override
	public Serializable getSettingsReference() {
		if (genericModelParams==null) {
			genericModelParams = new GenericModelParams(); 
		}
		System.out.println("SoundSpot have been saved. : " + genericModelParams.modelPath); 
		return genericModelParams;

	}

	@Override
	public long getSettingsVersion() {
		return PamSoundSpotParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		GenericModelParams newParameters = (GenericModelParams) pamControlledUnitSettings.getSettings();
		if (newParameters!=null) {
			genericModelParams = (GenericModelParams) newParameters.clone();
			System.out.println("SoundSpot have been restored. : " + genericModelParams.modelPath); 
		}
		else genericModelParams = new GenericModelParams(); 
		return true;
	}

	/**
	 * Get the sound spot parameters. 
	 * @return sound spot parameters. 
	 */
	public GenericModelParams getGenericDLParams() {
		return genericModelParams;
	}


	public void setSoundSpotParams(PamSoundSpotParams clone) {
		// TODO Auto-generated method stub
		
	}



}
