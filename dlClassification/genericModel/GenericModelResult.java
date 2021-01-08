package rawDeepLearningClassifer.dlClassification.genericModel;

import rawDeepLearningClassifer.dlClassification.ModelResult;

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
	private float[] p;
	private boolean isBinary;

	public GenericModelResult(float[] p) {
		this.p=p; 
	}

	public GenericModelResult(float[] data, boolean isBinary) {
		this.p=p; 
		this.isBinary = isBinary; 
	}

	@Override
	public float[] getPrediction() {
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

	@Override
	public String[] classNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
