package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import org.jamdev.jtorch4pam.transforms.DLTransform;

import pamViewFX.fxNodes.PamBorderPane;

/**
 * Settings pane for all DLTransforms
 * 
 * @author Jamie macaulay
 *
 */
public abstract class DLTransformPane extends PamBorderPane {
	
	/**
	 * Get the DL transform
	 * @return the DL transform. 
	 */
	public abstract DLTransform getDLTransform(); 

	/**
	 * Get the parameters form the controls in the pane. 
	 * @param dlTransform - the dl transform to apply parameters to. 
	 * @return the DLTransform with new parameters. 
	 */
	public abstract DLTransform getParams(DLTransform dlTransform); 
	
	/**
	 * Set the parameters on the pane. 
	 * @param dlTransform - the dltransform containing the parameters. 
	 */
	public abstract void setParams(DLTransform dlTransform); 


}
