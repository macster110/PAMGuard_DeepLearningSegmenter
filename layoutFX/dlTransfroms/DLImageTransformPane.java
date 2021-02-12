package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import java.util.ArrayList;

import org.jamdev.jdl4pam.transforms.DLTransform;
import org.jamdev.jdl4pam.transforms.DLTransform.DLTransformType;

import pamViewFX.fxNodes.PamBorderPane;

/**
 * Contains both a list of transforms and preview image of the waveform, spectrogram or other visualisation of 
 * the transform. 
 * @author Jamie Macaulay 
 *
 */
public class DLImageTransformPane  extends PamBorderPane {

	/**
	 * The DL transform pane. 
	 */
	private DLTransformsPane dlTransformPane;

	/**
	 * Create the DL image pane. 
	 */
	private DLTransfromImagePane dlTransfromImagePane;

	/**
	 * Constructor of the DL transform pane. 
	 */
	public DLImageTransformPane () {
		dlTransformPane = new DynamicDLTransformsPane(); 

		dlTransfromImagePane = new DLTransfromImagePane(dlTransformPane); 

		this.setTop(dlTransformPane);
		this.setCenter(dlTransfromImagePane);
	}

	/**
	 * Set the transforms in the pane. 
	 * @param dlTransforms - the transforms to set. 
	 */
	public void setTransforms(ArrayList<DLTransform> dlTransforms) {
		dlTransformPane.setTransforms(dlTransforms);
		dlTransfromImagePane.newSettings();
	}

	/**
	 * Get the DL transforms. 
	 * @return the DLTransform's. 
	 */
	public ArrayList<DLTransform> getDLTransforms() {
		return dlTransformPane.getDLTransforms();
	}

	/**
	 * Get the DL transform pane. 
	 * @return the DL transform pane. 
	 */
	public DLTransformsPane getDLTransformPane() {
		return dlTransformPane;
	}


	public class DynamicDLTransformsPane extends DLTransformsPane {

		@Override
		public void newSettings(int type) {
			switch (type) {
			case DLTransformsPane.TRANSFORM_SETTINGS_CHANGE:
				//called whenever a control is updated. 
				dlTransformPane.getParams();
				//System.out.println("Update the transform image"); 
				dlTransfromImagePane.updateTransformImage(); 
				break;
			case DLTransformsPane.TRANSFORM_ORDER_CHANGE:
				dlTransfromImagePane.newSettings();
				break;
			}
		}


		@Override
		protected void addNewDLTransfrom(DLTransformType dlTransformType) {
			//TODO - need to add frequency and wave transforms in appropriate places in the list.
			super.addNewDLTransfrom(dlTransformType);
			dlTransfromImagePane.newSettings();
		}
	}


	public class DLTransfromImagePane extends DLTransformImage {

		private DLTransformsPane dLTransformsPane;

		public DLTransfromImagePane(DLTransformsPane dLTransformsPane) {
			this.dLTransformsPane=dLTransformsPane; 
			this.newSettings();
		}

		@Override
		public ArrayList<DLTransform> getDLTransforms() {
			return dLTransformsPane.getDLTransforms();
		}
	}




}
