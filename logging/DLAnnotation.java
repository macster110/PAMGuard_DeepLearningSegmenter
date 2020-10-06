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
		//add to annotations. 
	}

	/**
	 * Get all the model results. 
	 * @return the model results. 
	 */
	public ArrayList<ModelResult> getModelResults() {
		return modelResults;
	}


	@Override
	public String toString() {
		String results = "<html><p> "; 
		for (int j=0; j<this.modelResults.get(0).getPrediction().length; j++) {
			results += "Class " + j + ": "; 
			for (int i=0; i<this.modelResults.size(); i++) {
				results += String.format(" %.2f", modelResults.get(i).getPrediction()[j]);
			}
			results += "\n"; 
		}
		results += "<html>"; 
		return results;
	}

}
