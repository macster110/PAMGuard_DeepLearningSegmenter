package rawDeepLearningClassifer.logging;

import PamguardMVC.PamDataUnit;
import annotation.DataAnnotation;
import annotation.DataAnnotationType;
import annotation.binary.AnnotationBinaryData;
import annotation.binary.AnnotationBinaryHandler;

/**
 * Saves deep learning annotations in binary files. 
 * @author Jamie Macaulay
 *
 */
public class DLAnnotationBinary extends AnnotationBinaryHandler<DLAnnotation> {

	public DLAnnotationBinary(DataAnnotationType<DLAnnotation> dataAnnotationType) {
		super(dataAnnotationType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public AnnotationBinaryData getAnnotationBinaryData(PamDataUnit pamDataUnit, DataAnnotation annotation) {
		// TODO Auto-generated method stub
		
		//if there specific types of model results then add unique binary data her.e 
		return null;
	}

	@Override
	public DLAnnotation setAnnotationBinaryData(PamDataUnit pamDataUnit,
			AnnotationBinaryData annotationBinaryData) {
		// TODO Auto-generated method stub
		return null;
	}

}
