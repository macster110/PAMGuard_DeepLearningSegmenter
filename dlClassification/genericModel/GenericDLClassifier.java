package rawDeepLearningClassifer.dlClassification.genericModel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jamdev.jdl4pam.transforms.DLTransform;
import org.jamdev.jdl4pam.transforms.DLTransformsFactory;
import org.jamdev.jdl4pam.transforms.DLTransfromParams;
import org.jamdev.jdl4pam.transforms.SimpleTransform;
import org.jamdev.jdl4pam.transforms.SimpleTransformParams;

import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;
import PamUtils.PamCalendar;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLClassName;
import rawDeepLearningClassifer.dlClassification.DLClassiferModel;
import rawDeepLearningClassifer.dlClassification.DLTaskThread;
import rawDeepLearningClassifer.dlClassification.PredictionResult;
import rawDeepLearningClassifer.dlClassification.soundSpot.SoundSpotResult;
import rawDeepLearningClassifer.dlClassification.soundSpot.SoundSpotWorker;
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
	 * Holds a list of segmented raw data units which need to be classified. 
	 */
	private List<ArrayList<GroupedRawData>> queue = Collections.synchronizedList(new ArrayList<ArrayList<GroupedRawData>>());

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
	private GenericModelUI genericModelUI; 

	/**
	 * The generic model worker. 
	 */
	private GenericModelWorker genericModelWorker;

	/**
	 * Sound spot warning. 
	 */
	PamWarning genericModelWarning = new PamWarning("Generic deep learning classifier", "",2);

	private boolean forceQueue; 


	public GenericDLClassifier(DLControl dlControl) {
		this.dlControl=dlControl; 

		genericModelUI = new GenericModelUI(this); 

		//the generic model worker...erm...does the work. 
		genericModelWorker = new GenericModelWorker(); 

		//load the previous settings
		PamSettingManager.getInstance().registerSettings(this);

		if (genericModelParams.dlTransfromParams!=null) {
			//important to remkae transforms from params
			genericModelParams.dlTransfroms = DLTransformsFactory.makeDLTransforms((ArrayList<DLTransfromParams>)genericModelParams.dlTransfromParams); 
		}
	}


	@Override
	public void prepModel() {
		//System.out.println("PrepModel! !!!");
		genericModelWorker.prepModel(genericModelParams, dlControl);
		//set cusotm transforms in the model. 
		genericModelWorker.setModelTransforms(genericModelParams.dlTransfroms);


		if (genericModelWorker.getModel()==null) {
			genericModelWarning.setWarningMessage("There is no loaded deep learning model. Generic model classifier disabled.");
			WarningSystem.getWarningSystem().addWarning(genericModelWarning);
		}
	}

	@Override
	public ArrayList<? extends PredictionResult> runModel(ArrayList<GroupedRawData> groupedRawData) {

		if (genericModelWorker.getModel()==null) return null; 

		//		System.out.println("SoundSpotClassifier: PamCalendar.isSoundFile(): " 
		//		+ PamCalendar.isSoundFile() + "   " + (PamCalendar.isSoundFile() && !forceQueue));
		/**
		 * If a sound file is being analysed then SoundSpot can go as slow as it wants. if used in real time
		 * then there is a buffer with a maximum queue size. 
		 */
		if ((PamCalendar.isSoundFile() && !forceQueue) || dlControl.isViewer()) {
			//run the model 
			ArrayList<GenericPrediction> modelResult = getGenericDLWorker().runModel(groupedRawData, 
					groupedRawData.get(0).getParentDataBlock().getSampleRate(), 0); 
			
			if (modelResult==null) {
				genericModelWarning.setWarningMessage("Generic deep learning model returned null");
				WarningSystem.getWarningSystem().addWarning(genericModelWarning);
				return null;
			}
			
			for (int i =0; i<modelResult.size(); i++) {
				modelResult.get(i).setClassNameID(getClassNameIDs()); 
				modelResult.get(i).setBinaryClassification(isBinaryResult(modelResult.get(i))); 
				modelResult.get(i).setTimeMillis(groupedRawData.get(i).getTimeMilliseconds());

			}

			return modelResult; //returns to the classifier. 
		}
		else {
			//add to a buffer if in real time. 
			if (queue.size()>DLModelWorker.MAX_QUEUE_SIZE) {
				//we are not doing well - clear the buffer
				queue.clear();
			}
			queue.add(groupedRawData);
		}
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
	public GenericModelUI getModelUI() {
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
		
		ArrayList<DLTransfromParams> dlTransformParams = getDLTransformParams(genericModelParams.dlTransfroms);
		
		genericModelParams.dlTransfromParams=dlTransformParams; 
		
		if (genericModelParams.dlTransfromParams!=null) {
			System.out.println("Generic settings have been saved. : " + genericModelParams.dlTransfromParams.size()); 
		}		
		else {
			System.out.println("Generic settings have been saved. : " + null); 

		}
		
		return genericModelParams;
	}
	
	/**
	 * Get the parameters which can be serialized  from  transforms. 
	 * @param dlTransfroms- the dl transforms. 
	 */
	public ArrayList<DLTransfromParams> getDLTransformParams(ArrayList<DLTransform> dlTransfroms) {
		ArrayList<DLTransfromParams> dlTransformParams = new ArrayList<DLTransfromParams>(); 
		//need to set the generic model params. 
		for (int i=0; i<genericModelParams.dlTransfroms.size(); i++) {
			dlTransformParams.add(new SimpleTransformParams(dlTransfroms.get(i).getDLTransformType(), ((SimpleTransform) dlTransfroms.get(i)).getParams())); 
		}
		return dlTransformParams;
	}

	@Override
	public long getSettingsVersion() {
		return GenericModelParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		GenericModelParams newParameters = (GenericModelParams) pamControlledUnitSettings.getSettings();
		if (newParameters!=null) {
			genericModelParams = (GenericModelParams) newParameters.clone();
			if (genericModelParams.dlTransfromParams!=null) {
				System.out.println("Generic settings have been restored. : " + genericModelParams.dlTransfromParams.size()); 
			}
			else {
				System.out.println("Generic settings have been saved. : " + null); 
			}
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


	@Override
	public boolean checkModelOK() {
		return genericModelWorker.getModel()!=null;
	}

	/**
	 * The task thread. 
	 * @author Jamie Macaulay 
	 *
	 */
	public class GenericTaskThread extends DLTaskThread {

		GenericTaskThread(DLModelWorker soundSpotWorker) {
			super(soundSpotWorker);
		}

		@Override
		public void newResult(GenericPrediction soundSpotResult, GroupedRawData groupedRawData) {
			soundSpotResult.setClassNameID(getClassNameIDs()); 
			soundSpotResult.setBinaryClassification(isBinaryResult(soundSpotResult)); 
			newResult(soundSpotResult, groupedRawData);
		}

	}

	/**
	 * Get the class name IDs
	 * @return an array of class name IDs
	 */
	private short[] getClassNameIDs() {
		if (genericModelParams.classNames==null || genericModelParams.classNames.length<=0) return null; 
		short[] nameIDs = new short[genericModelParams.classNames.length]; 
		for (int i = 0 ; i<genericModelParams.classNames.length; i++) {
			nameIDs[i] = genericModelParams.classNames[i].ID; 
		}
		return nameIDs; 
	}



	/**
	 * Check whether a model passes a binary test...
	 * @param modelResult - the model results
	 * @return the model results. 
	 */
	private boolean isBinaryResult(GenericPrediction modelResult) {
		for (int i=0; i<modelResult.getPrediction().length; i++) {
			if (modelResult.getPrediction()[i]>genericModelParams.threshold && genericModelParams.binaryClassification[i]) {
				//				System.out.println("SoundSpotClassifier: prediciton: " + i + " passed threshold with val: " + modelResult.getPrediction()[i]); 
				return true; 
			}
		}
		return  false;
	}


}
