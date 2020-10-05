package rawDeepLearningClassifer.soundSpot;

import rawDeepLearningClassifer.deepLearningClassification.ModelResult;

/**
 * Result from the SoundSpot classifier.
 * 
 * @author Jamie Macaulay 
 *
 */
public class SoundSpotResult implements ModelResult {

	public SoundSpotResult(double[] data, boolean isBinary) {
		// TODO Auto-generated constructor stub
	}

	public SoundSpotResult(double[] prob) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double[] getPrediction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBinaryClassification() {
		return false;
	}

	@Override
	public double getAnalysisTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getResultString() {
		// TODO Auto-generated method stub
		return null;
	}

}
