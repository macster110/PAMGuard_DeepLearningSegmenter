package rawDeepLearningClassifer.dlClassification.genericModel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLClassName;
import rawDeepLearningClassifer.dlClassification.DLClassiferModel;
import rawDeepLearningClassifer.dlClassification.ModelResult;
import rawDeepLearningClassifer.dlClassification.soundSpot.StandardModelParams;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;
import warnings.PamWarning;
import warnings.WarningSystem;


/**
 * A generic model - can be load any model but requires manually setting model 
. 
 * @author Jamie Macaulay
 * 
 *
 */
public class GenericDLClassifier implements DLClassiferModel, PamSettings {

	/**
	 * The DL control. 
	 */
	private DLControl dlControl;
	
	/**
	 * The generic model parameters. 
	 */
	private GenericModelParams genericModelParams = new GenericModelParams();

	/**
	 * The generic model UI,
	 */
	private DLCLassiferModelUI genericModelUI; 
	
	/**
	 * The generic model worker. 
	 */
	private GenericModelWorker genericModelWorker;
	
	/**
	 * Sound spot warning. 
	 */
	PamWarning soundSpotWarning = new PamWarning("Generic deep learning classifier", "",2); 


	public GenericDLClassifier(DLControl dlControl) {
		this.dlControl=dlControl; 
		
		genericModelUI = new GenericModelUI(this); 
		
		//the generic model worker...erm...does the work. 
		genericModelWorker = new GenericModelWorker(); 
		
		//load the previous settings
		PamSettingManager.getInstance().registerSettings(this);
	}
	

	@Override
	public void prepModel() {
		//System.out.println("PrepModel! !!!");
		genericModelWorker.prepModel(genericModelParams, dlControl);
			//set cusotm transforms in the model. 
		genericModelWorker.setModelTransforms(genericModelParams.dlTransfroms);
	

		if (	genericModelWorker.getModel()==null) {
			soundSpotWarning.setWarningMessage("There is no loaded classifier model. SoundSpot disabled.");
			WarningSystem.getWarningSystem().addWarning(soundSpotWarning);
		}
	}
	
	@Override
	public ArrayList<? extends ModelResult> runModel(ArrayList<GroupedRawData> rawDataUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void closeModel() {
		// TODO Auto-generated method stub
	}
	

	@Override
	public int getNumClasses() {
		return genericModelParams.numClasses;
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
		return genericModelParams.classNames;
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
		return StandardModelParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		GenericModelParams newParameters = (GenericModelParams) pamControlledUnitSettings.getSettings();
		if (newParameters!=null) {
			genericModelParams = (GenericModelParams) newParameters.clone();
			System.out.println("Generic settings have been restored. : " + genericModelParams.modelPath); 
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
	
	/**
	 * Get the generic model worker. 
	 * @return the generic model worker. 
	 */
	public GenericModelWorker getGenericDLWorker() {
		return genericModelWorker;
	}


	/**
	 * Set the generic model params. 
	 * @param clone - the params to set. 
	 */
	public void setGenericModelParams(StandardModelParams clone) {
		this.genericModelParams=(GenericModelParams) clone;	
	}


	public void newModelSelected(File file) {
		// TODO Auto-generated method stub
		
	}



}
