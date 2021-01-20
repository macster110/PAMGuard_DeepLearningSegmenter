package rawDeepLearningClassifer.dlClassification.genericModel;

import java.io.Serializable;

import PamController.PamControlledUnitSettings;
import PamController.PamSettings;
import rawDeepLearningClassifer.dlClassification.DLClassName;
import rawDeepLearningClassifer.dlClassification.DLClassiferModel;
import rawDeepLearningClassifer.dlClassification.ModelResult;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;


/**
 * A generic model - can be called with any classifier...
 * @author Jamie Macaulay
 * 
 *
 */
public class GenericDLClassifier implements DLClassiferModel, PamSettings {

	
	@Override
	public ModelResult runModel(GroupedRawData rawDataUnit) {
		// TODO Auto-generated method stub
		return null;
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
	public String getName() {
		return "Generic Model";
	}

	@Override
	public DLCLassiferModelUI getModelUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getDLModelSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumClasses() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getUnitName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnitType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getSettingsReference() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSettingsVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DLClassName[] getClassNames() {
		// TODO Auto-generated method stub
		return null;
	}



}
