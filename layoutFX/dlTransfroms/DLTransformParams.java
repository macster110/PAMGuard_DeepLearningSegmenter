package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import java.io.Serializable;

import org.jamdev.jdl4pam.transforms.DLTransform.DLTransformType;

/**
 * Parameters that can be saved for a DL transform. 
 * @author Jamie Macaulay
 *
 */
public class DLTransformParams implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The transform type. 
	 */
	public DLTransformType flag; 
	
	
	/**
	 * The number of parameters. 
	 */
	public Number[] params; 
	

}
