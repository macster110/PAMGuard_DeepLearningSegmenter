package rawDeepLearningClassifer.deepLearningClassification;

/**
 * Model results for the classifier. 
 * <p> Model results are dependent on the type of deep learning classifier that is being used 
 * but all must implement ModelResult. ModelResults are saved to binary files and if there is unique
 * model result data that requires saving then modifications must be made to ModelResultBinaryFactory class 
 * @author Jamie Macaulay
 *
 */
public interface ModelResult {

	/**
	 * Get the predictions for this result. The array contains the probabilities for all classes. 
	 * @return the prediction.
	 */
	public double[] getPrediction(); 

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
	
	/**
	 * String representation of the result
	 * @return a string of the result. 
	 */
	public String getResultString(); 



}
