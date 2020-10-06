package rawDeepLearningClassifer.logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import PamguardMVC.PamDataUnit;
import annotation.DataAnnotation;
import annotation.binary.AnnotationBinaryData;
import annotation.binary.AnnotationBinaryHandler;
import binaryFileStorage.BinaryStore;
import rawDeepLearningClassifer.deepLearningClassification.DLDetection;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;

/**
 * Saves deep learning annotations in binary files. 
 * @author Jamie Macaulay
 *
 */
public class DLAnnotationBinary extends AnnotationBinaryHandler<DLAnnotation> {

	private ByteArrayOutputStream bos;
	private DataOutputStream dos;
	private DLAnnotationType dlAnnotationType;

	public DLAnnotationBinary(DLAnnotationType dataAnnotationType) {
		super(dataAnnotationType);
		this.dlAnnotationType = dataAnnotationType; 
	}

	@Override
	public AnnotationBinaryData getAnnotationBinaryData(PamDataUnit pamDataUnit, DataAnnotation annotation) {
		DLAnnotation ba = (DLAnnotation) annotation;
		DLDetection dlDetection = (DLDetection) pamDataUnit;

		//write the number of results for reading back
		try {
			if (dos == null) {
				dos = new DataOutputStream(bos = new ByteArrayOutputStream(14));
			}
			else {
				bos.reset();
			}

			dos.writeShort(dlDetection.getModelResults().size());

			for (int i=0; i<dlDetection.getModelResults().size(); i++) {
				ModelResultBinaryFactory.getPackedData(dlDetection.getModelResults().get(i), dos, 	ModelResultBinaryFactory.getType(dlDetection.getModelResults().get(i)));
			}

			AnnotationBinaryData abd = new AnnotationBinaryData(BinaryStore.CURRENT_FORMAT, (short) 1, 
					super.getDataAnnotationType(), getDataAnnotationType().getShortIdCode(), bos.toByteArray());

			return abd;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DLAnnotation setAnnotationBinaryData(PamDataUnit pamDataUnit,
			AnnotationBinaryData annotationBinaryData) {
		//System.out.println("MatchedClickAnnotationBinary: Extracting threshold from matched click: "); 
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(annotationBinaryData.data));

		int version = annotationBinaryData.annotationVersion; //1 for original single template, 2 for multi template

		//		System.out.println("Matched annot length: " + annotationBinaryData.data.length); 

		ArrayList<ModelResult> modelResults = new ArrayList<ModelResult>(); 
		try {

			int numModels  = dis.readShort(); 

			for (int i =0; i<numModels; i++) {
				modelResults.add(ModelResultBinaryFactory.sinkData(dis)); 
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return new DLAnnotation(dlAnnotationType, modelResults); 
	}

}
