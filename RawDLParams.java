package rawDeepLearningClassifer;

import java.io.Serializable;

import PamView.GroupedSourceParameters;

/**
 * Basic parameters for deep learning module. 
 * @author Jamie Macaulay 
 *
 */
public class RawDLParams implements Serializable, Cloneable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 2L;

	/**
	 * The currently selected Deep Learning model. 
	 */
	public int modelSelection = 0; 
	
	/**
	 * Holds channel and grouping information 
	 */
	public GroupedSourceParameters groupedSourceParams = new GroupedSourceParameters(); 
	
	/**
	 * The number of raw samples to send to the classifier. 
	 */
	public int rawSampleSize =  192000; 
	
	/**
	 * The hop size i.e. how far to move forward in raw data before sending another chunk of data to the classifier.
	 */
	public int sampleHop =  96000;  
	
	
	
	@Override
	public RawDLParams clone() {
		RawDLParams newParams = null;
		try {
			newParams = (RawDLParams) super.clone();
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
