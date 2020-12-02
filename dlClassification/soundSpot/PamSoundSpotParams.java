package rawDeepLearningClassifer.dlClassification.soundSpot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamdev.jtorch4pam.transforms.DLTransform;

import rawDeepLearningClassifer.layoutFX.dlTransfroms.DLTransformPane;
import rawDeepLearningClassifer.layoutFX.dlTransfroms.DLTransformParams;

/**
 * Parameters for the SoundSpot model. 
 * 
 * 
 * @author Jamie Macaulay 
 *
 */
public class PamSoundSpotParams implements Serializable, Cloneable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 2L;
	
	/**
	 * The model path
	 */
	public String modelPath;

	/**
	 * True to use CUDA. 
	 */
	public boolean useCUDA = false;
	
	/**
	 * Use defualt transforms
	 */
	public boolean useDefaultTransfroms; 

	
	/**
	 * The threshold between zero and one. This is used to allow binary classification. 
	 */
	public double threshold = 0.9; 

	/*
	 * the number of output classes. 
	 */
	public int numClasses = 2; 
	
	/**
	 * List of transforms for the raw data e.g. filtering, spectrogram, spectrogram normalisation etc. This is only used for saving serialised settings
	 * 
	 */
	public List<DLTransformParams> dlTransfromParams = null; 
	
	/**
	 * The DL custom transforms if the default transforms for the mdoel are not being used. 
	 */
	public transient ArrayList<DLTransform> dlTransfroms = null;

	/**
	 * The default segment length of the model in microseconds. 
	 */
	public 	Double defaultSegmentLen = null;

	/**
	 * The class names. e.g. porpoise, noise, bat
	 */
	public String[] classNames; 


	
	@Override
	public PamSoundSpotParams clone() {
		PamSoundSpotParams newParams = null;
		try {
			newParams = (PamSoundSpotParams) super.clone();
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
