package rawDeepLearningClassifer.deepLearningClassification;

import java.io.Serializable;

import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;

/**
 * The classifier model. Each classifier must satisfy this interface. 
 * 
 * @author Jamie Macaulay
 *
 */
public interface DLClassiferModel {
	
	/**
	 * Run the deep learning model and return a model result. 
	 * @return the deep learning model. 
	 */
	public ModelResult runModel(GroupedRawData rawDataUnit); 
	
	/**
	 * Prepare the model. This is called on PAMGuard start up. 
	 */
	public void prepModel(); 
	
	/**
	 * Called whenever PAMGuard stops.  
	 */
	public void closeModel(); 
	
	/**
	 * Get the name of the model. 
	 * @return the name of the model. 
	 */
	public String getName(); 
	
	/**
	 * Get any UI components for the model. Can be null. 
	 * @return UI components for the model.
	 */
	public DLCLassiferModelUI getModelUI(); 
	
	/**
	 * A settings object that can be saved. 
	 * @return the settings object. 
	 */
	public Serializable getDLModelSettings();
	
	/**
	 * Get the number of output classes. 
	 * @return the number of output classes. 
	 */
	public int getNumClasses();


}
