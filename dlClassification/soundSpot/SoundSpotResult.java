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
	 * The class name IDs
	 */
	private short[]  classNameID;

	/**
	 * True if has passed binary classification. 
	 */
	private boolean binaryPass; 
	
	/**
	 * Analysis time in seconds. 
	 */
	public double analysisTime=0; 
	

	public SoundSpotResult(float[] prob, short[] classNameID, boolean isBinary) {
		this.prob=prob; 
		this.classNameID = classNameID;
		this.binaryPass= isBinary; 
	}

	
	public SoundSpotResult(float[] prob, boolean isBinary) {
		this(prob, null, isBinary); 
	}

	/**
	 * Create a result for the Sound Spot classifier. 
	 * @param prob - the probability of each class. 
	 */
	public SoundSpotResult(float[] prob) {
		this(prob, null, false); 
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
		//the classification results. 
		return PamArrayUtils.array2String(prob, 1, "/n"); 
	}

	@Override
	public short[] getClassNames() {
		return classNameID;
	}

	/**
	 * Set the IDs of the class names. Use a class name manager to retrieve the 
	 * actual String names. 
	 * @param classNameID - the class name IDs. 
	 */
	public void setClassNameID(short[] classNameID) {
		this.classNameID = classNameID; 
	}

}
