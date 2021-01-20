package rawDeepLearningClassifer;

import java.io.Serializable;
import java.util.ArrayList;

import PamView.GroupedSourceParameters;
import rawDeepLearningClassifer.dlClassification.DLClassName;

/**
 * Basic parameters for deep learning module. 
 * @author Jamie Macaulay 
 *
 */
public class RawDLParams implements Serializable, Cloneable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 3L;

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
	
	/**
	 * The maximum number of samples a merged classified data unit can be. 
	 * if this is the same as rawSampleSize then data units are never merged. It must be
	 * a  multiple of rawSampleSize. 
	 */
	public int maxMergeHops = 5;

	/**
	 * The deep learning classifier can accept multiple types of data unit that contain a raw data chunk
	 * e.g. raw data, clicks, clips etc. By default the classifier saves new data units if the source is raw data. However, if 
	 * the data unit is an already processed data unit, e.g. a click detection, then the results are saved as an annotation 
	 * to that unit. If forceSave is st true then new data units are created no matter what the source data is. 
	 */
	public boolean forceSave = false; 
	
	
	/**
	 * Uuuruggh. Spent a lot of time thinking about this. Different models have
	 * different class names. If we change model then the class names may change.
	 * Previously annotated data will then be messed up. But, in a giant dataset
	 * that may be an issue. Perhaps users wish to run a new model on some chunk of
	 * data without messing up all the other classified detection which have used
	 * that module. So store the data in binary files? That is super inefficient as
	 * the same string is stored many times. So instead store a short which
	 * identifies the string that sits in this table. Everytime a new model is added
	 * add new classnames. Note that this means we have a maximum of 32k names - we
	 * will come to that issue when it arises...
	 */
	public ArrayList<DLClassName> classNameMap = new ArrayList<DLClassName>(); 
	

	/**
	 * The class name index. i.e. the number of unique class names that have been added. 
	 */
	public short classNameIndex = 0; 

	
	
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
