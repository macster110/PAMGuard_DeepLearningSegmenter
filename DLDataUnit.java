package rawDeepLearningClassifer;

import PamDetection.PamDetection;
import PamguardMVC.PamDataUnit;

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
	
	/**
	 * Get the model result. 
	 * @return the model result. 
	 */
	public ModelResult getModelResult() {
		return modelResult; 
	}

}
