package rawDeepLearningClassifer.dlClassification.dummyClassifier;

import rawDeepLearningClassifer.dlClassification.ModelResult;

public class DummyModelResult implements ModelResult {
	
	
	private float[] probability;

	public DummyModelResult(float probability) {
		this.probability = new float[] {probability};  
	}
	
	public DummyModelResult(float[] probability) {
		this.probability = probability;  
	}



	@Override
	public float[] getPrediction() {
		return probability;
	}

	@Override
	public boolean isBinaryClassification() {
		return probability[0]>0.95;
	}

	@Override
	public double getAnalysisTime() {
		return 0.0001;
	}

	@Override
	public String getResultString() {
		return "Dummy result: " + probability;
	}

	@Override
	public String[] classNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
}