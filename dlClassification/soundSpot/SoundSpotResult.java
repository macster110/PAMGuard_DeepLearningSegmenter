package rawDeepLearningClassifer.dlClassification.soundSpot;

/**
 * Result from the SoundSpotClassifier. 
 * @author Jamie Macaulay
 *
 */
public class SoundSpotResult extends GenericModelResult {

	public SoundSpotResult(float[] prob, boolean isBinary) {
		super(prob, isBinary);
		// TODO Auto-generated constructor stub
	}

	public SoundSpotResult(float[] prob) {
		super(prob);
		// TODO Auto-generated constructor stub
	}

	public SoundSpotResult(float[] prob, short[] classNameID, boolean isBinary) {
		super(prob, classNameID, isBinary);
		// TODO Auto-generated constructor stub
	}

}
