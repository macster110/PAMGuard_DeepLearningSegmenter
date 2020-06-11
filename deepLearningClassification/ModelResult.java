package rawDeepLearningClassifer.deepLearningClassification;

/**
 * Model results for the classifier. 
 * @author Jamie Macaulay
 *
 */
public interface ModelResult {

	/**
	 * Get the prediction for this result. This is one number that
	 * represents the overall probability. 
	 * @return the prediction.
	 */
	public double getPrediction(); 

	/**
	 * Check whether binary classification has passed. 
	 * @return true if binary classification has passed
	 */
	public boolean isBinaryClassification();

	/**
	 * Get the analysis time.
	 * @return the analysis time. 
	 */
	public double getAnalysisTime(); 


}
