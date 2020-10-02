package rawDeepLearningClassifer.dummyClassifier;

import java.io.Serializable;

import rawDeepLearningClassifer.deepLearningClassification.DLClassiferModel;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;
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
		return new DummyModelResult(Math.random());
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
	
	
	
	class DummyModelResult implements ModelResult {
		
		
		private double probability;

		public DummyModelResult(double probability) {
			this.probability = probability;  
		}

		@Override
		public double[] getPrediction() {
			return new double[] {probability};
		}

		@Override
		public boolean isBinaryClassification() {
			return probability>0.7;
		}

		@Override
		public double getAnalysisTime() {
			// TODO Auto-generated method stub
			return 0.0001;
		}

		@Override
		public String getResultString() {
			return "Dummy result: " + probability;
		}
		
	}



	@Override
	public int getNumClasses() {
		return 1;
	}

}
