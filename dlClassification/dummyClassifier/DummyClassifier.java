package rawDeepLearningClassifer.dlClassification.dummyClassifier;

import java.io.Serializable;
import java.util.ArrayList;

import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLClassName;
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

	@Override
	public DLClassName[] getClassNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ModelResult> runModel(ArrayList<GroupedRawData> rawDataUnit) {
		ArrayList<ModelResult> modelResults = new ArrayList<ModelResult>(); 

		for (int i=0; i<rawDataUnit.size(); i++) {
			modelResults.add(new DummyModelResult(new float[] {(float) Math.random(), (float) Math.random()}));
		}


		return modelResults;
	}

	@Override
	public DLControl getDLControl() {
		// TODO Auto-generated method stub
		return null;
	}

}
