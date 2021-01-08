package rawDeepLearningClassifer.logging;

import java.util.ArrayList;

import annotation.DataAnnotation;
import rawDeepLearningClassifer.dlClassification.ModelResult;

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
		//		System.out.println("DLAnnotation: " + modelResults.size()); 
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
		
		String results = "<html>"; 

		if (modelResults==null) {
			results += "WARNING: There are no model results associated with this data unit?"; 
		}
		else {
			for (int j=0; j<this.modelResults.get(0).getPrediction().length; j++) {
				results += "<p>"; 
				results += "Class " + j + ": "; 
				for (int i=0; i<this.modelResults.size(); i++) {

					if (i<this.modelResults.size()-1) {
						results += String.format(" %.2f,", modelResults.get(i).getPrediction()[j]);
					}
					else {
						//remove comma
						results += String.format(" %.2f", modelResults.get(i).getPrediction()[j]);
					}
				}
				results += "</p>"; 
			}
		}
		
		results += "</html>"; 

		return results;
	}

}
