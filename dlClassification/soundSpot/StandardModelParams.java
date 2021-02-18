package rawDeepLearningClassifer.dlClassification.soundSpot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamdev.jdl4pam.transforms.DLTransform;
import org.jamdev.jdl4pam.transforms.DLTransfromParams;

import rawDeepLearningClassifer.dlClassification.DLClassName;

/**
 * Parameters for the SoundSpot model. 
 * 
 * 
 * @author Jamie Macaulay 
 *
 */
public class StandardModelParams implements Serializable, Cloneable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 4L;
	
	/**
	 * The model path
	 */
	public String modelPath;

	/**
	 * True to use CUDA. 
	 */
	public boolean useCUDA = false;
	
	/**
	 * Use default transforms
	 */
	public boolean useDefaultTransfroms = false; 

	
	/**
	 * The threshold between zero and one. This is used to allow binary classification. 
	 */
	public double threshold = 0.9; 

	/*
	 * the number of output classes. 
	 */
	public int numClasses = 0; 
	
	/**
	 * List of transforms for the raw data e.g. filtering, spectrogram, spectrogram normalisation etc. 
	 * This is only used for saving serialised settings
	 * 
	 */
	public List<DLTransfromParams> dlTransfromParams = null; 
	
	/**
	 * The DL custom transforms if the default transforms for the mdoel are not being used. 
	 */
	public transient ArrayList<DLTransform> dlTransfroms = null;

	/**
	 * The default segment length of the model in milliseconds. 
	 */
	public 	Double defaultSegmentLen = null;

	/**
	 * The class names. e.g. porpoise, noise, bat
	 */
	public DLClassName[] classNames; 

	/**
	 * Which classes to apply binary classification to. 
	 */
	public boolean[] binaryClassification; 
	
	@Override
	public StandardModelParams clone() {
		StandardModelParams newParams = null;
		try {
			newParams = (StandardModelParams) super.clone();
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
