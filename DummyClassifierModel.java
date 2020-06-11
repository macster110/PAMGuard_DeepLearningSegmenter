package rawDeepLearningClassifer;

import java.io.Serializable;

import PamguardMVC.RawDataHolder;
import rawDeepLearningClassifer.SegmenterProcess.GroupedRawData;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;

/**
 * A model which assigns a random probability to the segmented sound data. For testing 
 * purposes only
 * @author Jamie Macaulay 
 *
 */
public class DummyClassifierModel implements DLClassiferModel {


	@Override
	public String getName() {
		return "Dummy Model - for testing";
	}

	@Override
	public DLCLassiferModelUI getModelUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getDLModelSettings() {
		return null;
	}

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

}
