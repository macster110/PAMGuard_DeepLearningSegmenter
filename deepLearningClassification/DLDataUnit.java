package rawDeepLearningClassifer.deepLearningClassification;

import PamDetection.AbstractLocalisation;
import PamDetection.LocContents;
import PamDetection.PamDetection;
import PamUtils.PamUtils;
import PamguardMVC.PamDataUnit;
import annotation.DataAnnotation;
import bearinglocaliser.annotation.BearingAnnotation;

/**
 * A data unit created from classification results of DL model. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class DLDataUnit extends PamDataUnit  implements PamDetection {

	/**
	 * The result for the model. 
	 */
	private ModelResult modelResult;

	/**
	 * The abstract localisation 
	 */
	private DLLocalisation localisation; 

	/**
	 * Constructor using the original parameters that have now been moved to DataUnitBaseData
	 * @param timeMilliseconds - the time in milliseconds. 
	 * @param channelBitmap- the channel bitmap in samples
	 * @param startSample - the start sample in samples
	 * @param duration (number of samples, not milliseconds)
	 * @param modelResult - the deep learning result
	 */
	public DLDataUnit(long timeMilliseconds,
			int channelBitmap, long startSample, long durationSamples, 	ModelResult modelResult) {
		super(timeMilliseconds, channelBitmap, startSample, durationSamples); 
		this.modelResult = modelResult; 
	}
	
	@Override
	public void addDataAnnotation(DataAnnotation dataAnnotation) {
		super.addDataAnnotation(dataAnnotation); 
		
		/**
		 * 
		 * This is a total hack to add bearing info from the bearing module 
		 * so that everything displays nicely on the map.
		 */
		if (dataAnnotation instanceof BearingAnnotation && modelResult.isBinaryClassification()) {
			
			BearingAnnotation bearingAnnotation = (BearingAnnotation) dataAnnotation; 
			
			localisation = new DLLocalisation(this, LocContents.HAS_BEARING | LocContents.HAS_AMBIGUITY, 
					PamUtils.getSingleChannel(this.getChannelBitmap())); 

			localisation.setBearing(bearingAnnotation); 
		}
	}

	/**
	 * Get the model result. 
	 * @return the model result. 
	 */
	public ModelResult getModelResult() {
		return modelResult; 
	}

	@Override
	public AbstractLocalisation getLocalisation() {
		return this.localisation; 
	}

}
