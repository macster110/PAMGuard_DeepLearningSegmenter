package rawDeepLearningClassifer.logging;

import java.util.ArrayList;

import annotation.DataAnnotation;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;

/**
 * Deep learning results annotation. 
 * @author Jamie Macaulay. 
 *
 */
public class DLAnnotation extends DataAnnotation<DLAnnotationType> {

	/**
	 * The results of the DL model. 
	 */
	private ArrayList<ModelResult> modelResults;

	public DLAnnotation(DLAnnotationType dlAnnotationType, ArrayList<ModelResult> modelResults) {
		super(dlAnnotationType);
		this.modelResults = modelResults; 
	}

	/**
	 * Get all the model results. 
	 * @return the model results. 
	 */
	public ArrayList<ModelResult> getModelResults() {
		return modelResults;
	}

}
