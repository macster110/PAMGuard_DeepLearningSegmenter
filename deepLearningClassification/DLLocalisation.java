package rawDeepLearningClassifer.deepLearningClassification;

import PamDetection.AbstractLocalisation;
import PamDetection.LocContents;
import PamguardMVC.PamDataUnit;
import bearinglocaliser.annotation.BearingAnnotation;

/**
 * The localisation for a DL data unit. 
 * 
 * @author Jamie Macaulay
 *
 */
public class DLLocalisation extends AbstractLocalisation {

	private double[] angles;

	public DLLocalisation(PamDataUnit pamDataUnit, int locContents, int referenceHydrophones) {
		super(pamDataUnit, locContents, referenceHydrophones);
		// TODO Auto-generated constructor stub
	}

	public void setBearing(BearingAnnotation bearingAnnotation) {
		this.setLocContents(bearingAnnotation.getBearingLocalisation().getLocContents());
		
		System.out.println("Loc content!: " + this.getLocContents().hasLocContent(LocContents.HAS_AMBIGUITY) + " angles: " + angles.length); 
		
		this.angles = bearingAnnotation.getBearingLocalisation().getAngles(); 
		
	}
	
	@Override
	public double[] getAngles() {
		return angles;
	}
	

}
