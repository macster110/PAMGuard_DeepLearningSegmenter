package rawDeepLearningClassifer.orcaSpot;

import rawDeepLearningClassifer.deepLearningClassification.ModelResult;

/**
 * Stores results from an OrcaSpot classification 
 * 
 * @author Jamie Macaulay
 *
 */
public class OrcaSpotModelResult implements ModelResult {
	
	/**
	 * The time in seconds. 
	 */
	public double timeSeconds = 0; 

	/**
	 * The detection confidence 
	 */
	public double detectionConfidence = 0; 
	
	/**
	 * The call type confidence
	 */
	public double calltypeConfidence = 0;

	/**
	 * Do we call this a yes/no classification
	 */
	public boolean binaryClassification = false; 

	/**
	 * Constructor for an OrcaSpot result if only a detection has occurred. 
	 * @param detConf - the confidence. 
	 * @param time - the time in seconds. 
	 */
	public OrcaSpotModelResult(Double detConf, Double time) {
		this.detectionConfidence = detConf;
		this.timeSeconds = time; 
	}

	/**
	 * Get the detection confidence. 
	 * @return the detection confidence
	 */
	public double getPrediction() {
		return detectionConfidence;
	}


	@Override
	public boolean isClassification() {
		return binaryClassification;
	}
	
	
	/**
	 * Set whether the binary classification has passed. 
	 * @param binaryClassification - true if the binary classification has passed. 
	 */
	public void setBinaryClassification(boolean binaryClassification) {
		this.binaryClassification = binaryClassification;
	}

	@Override
	public double getAnalysisTime() {
		return timeSeconds;
	}

	/**
	 * Set the analysis time in seconds. 
	 * @param timeSeconds - the analysis time in seconds. 
	 */
	public void setAnlaysisTime(double timeSeconds) {
		this.timeSeconds=timeSeconds; 
	}


}

