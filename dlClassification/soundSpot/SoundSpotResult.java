package rawDeepLearningClassifer.dlClassification.soundSpot;

import PamUtils.PamArrayUtils;
import rawDeepLearningClassifer.dlClassification.ModelResult;

/**
 * Result from the SoundSpot classifier.
 * 
 * @author Jamie Macaulay 
 *
 */
public class SoundSpotResult implements ModelResult {

	/**
	 * Create a result for the Sound Spot classifier. 
	 * @param prob - the probability of each class. 
	 */
	private float[]  prob;
	
	/**
	 * True if has passed binary classification. 
	 */
	private boolean binaryPass; 
	
	/**
	 * Anlyysis time in seconds. 
	 */
	public double analysisTime=0; 

	public SoundSpotResult(float[] prob, boolean isBinary) {
		this.prob=prob; 
	}

	/**
	 * Create a result for the Sound Spot classifier. 
	 * @param prob - the probability of each class. 
	 */
	public SoundSpotResult(float[] prob) {
		this.prob=prob; 
	}

	@Override
	public float[] getPrediction() {
		return prob;
	}

	@Override
	public boolean isBinaryClassification() {
		return binaryPass;
	}

	@Override
	public double getAnalysisTime() {
		return analysisTime;
	}

	public void setAnalysisTime(double analysisTime) {
		this.analysisTime = analysisTime;
	}

	@Override
	public String getResultString() {
		return PamArrayUtils.array2String(prob, 1, "/n"); 
	}

	@Override
	public String[] classNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
