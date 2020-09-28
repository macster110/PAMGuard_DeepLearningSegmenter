package rawDeepLearningClassifer.soundSpot;

import java.io.Serializable;

import org.jamdev.jtorch4pam.DeepLearningBats.DLParams;
/**
 * Parameters for the SoundSpot model. 
 * 
 * 
 * @author Jamie Macaulay 
 *
 */
public class SoundSpotParams extends DLParams implements Serializable, Cloneable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	
	/**
	 * The model path
	 */
	public String modelPath;

	/**
	 * True to use CUDA. 
	 */
	public boolean useCUDA = false;

	
	/**
	 * The threshold between zero and one. This is used to allow binary classificiation. 
	 */
	public double threshold; 
	
	
	@Override
	public SoundSpotParams clone() {
		SoundSpotParams newParams = null;
		try {
			newParams = (SoundSpotParams) super.clone();
//			if (newParams.spectrogramNoiseSettings == null) {
//				newParams.spectrogramNoiseSettings = new SpectrogramNoiseSettings();
//			}
//			else {
//				newParams.spectrogramNoiseSettings = this.spectrogramNoiseSettings.clone();
//			}
		}
		catch(CloneNotSupportedException Ex) {
			Ex.printStackTrace(); 
			return null;
		}
		return newParams;
	}

}
