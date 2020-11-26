package rawDeepLearningClassifer.dlClassification.dummyClassifier;

import java.io.Serializable;

import rawDeepLearningClassifer.dlClassification.DLClassiferModel;
import rawDeepLearningClassifer.dlClassification.ModelResult;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;

/**
 * Classifier which returns a random results. Used for debugging and testing. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class DummyClassifier implements DLClassiferModel{

	@Override
	public ModelResult runModel(GroupedRawData rawDataUnit) {
		return new DummyModelResult(new float[] {(float) Math.random(), (float) Math.random()});
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
		return "Random Classifier";
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
		return 2;
	}

}
