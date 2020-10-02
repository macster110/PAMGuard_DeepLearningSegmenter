package rawDeepLearningClassifer.deepLearningClassification;

/**
 * A generic model result. Primarily used for datagrams etc where
 * there is no need to load the full model results form a classifier, 
 * just the probabilities. 
 * @author Jamie Macaulay 
 *
 */
public class GenericModelResult implements ModelResult {
	
	/**
	 * List of probabilities per species. 
	 */
	private double[] p;

	public GenericModelResult(double[] p) {
		this.p=p; 
	}

	@Override
	public double[] getPrediction() {
		return p;
	}

	@Override
	public boolean isBinaryClassification() {
		return false;
	}

	@Override
	public double getAnalysisTime() {
		return 0;
	}

	@Override
	public String getResultString() {
		return null;
	}

}
