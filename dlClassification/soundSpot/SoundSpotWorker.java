package rawDeepLearningClassifer.dlClassification.soundSpot;

import org.jamdev.jdl4pam.SoundSpot.SoundSpotModel;
import org.jamdev.jdl4pam.SoundSpot.SoundSpotParams;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.genericModel.DLModelWorker;


/**
 * 
 * Runs the deep learning model and performs feature extraction.
 * <p>
 *  
 * 
 * @author Jamie Macaulay 
 *
 */
public class SoundSpotWorker extends DLModelWorker<SoundSpotResult> {


	/**
	 * Sound spot model. 
	 */
	private SoundSpotModel soundSpotModel; 


	/**
	 * SoundSpotWorker constructor. 
	 */
	public SoundSpotWorker() {

	}

	/**
	 * Prepare the model 
	 */
	public void prepModel(StandardModelParams soundSpotParams, DLControl dlControl) {
		try {
			//first open the model and get the correct parameters. 
			soundSpotModel = new SoundSpotModel(soundSpotParams.modelPath); 
		}
		catch (Exception e) {
			e.printStackTrace();
			//WarnOnce.showWarning(null, "Model Load Error", "There was an error loading the model file.", WarnOnce.OK_OPTION); 
		}

		try {
			//create the DL parameters.
			SoundSpotParams dlParams = new SoundSpotParams(soundSpotModel.getTransformsString());

			setModelTransforms(model2DLTransforms(dlParams)); 
			soundSpotParams.defaultSegmentLen = dlParams.seglen; //the segment length in microseconds. 
			soundSpotParams.numClasses = dlParams.classNames.length; 

			//ok 0 the other values are not user selectable but this is. If we relaod the same model we probably want to keep it....
			//So this is a little bt of a hack but will probably be OK in most cases. 
			if (soundSpotParams.binaryClassification==null || soundSpotParams.binaryClassification.length!=soundSpotParams.numClasses) {
				soundSpotParams.binaryClassification = new boolean[soundSpotParams.numClasses]; 
				for (int i=0; i<soundSpotParams.binaryClassification.length; i++) {
					soundSpotParams.binaryClassification[i] = true; //set default to true. 
				}
			}


			//			if (dlParams.classNames!=null) {
			//				for (int i = 0; i<dlParams.classNames.length; i++) {
			//					System.out.println("Class name " + i + "  "  + dlParams.classNames[i]); 
			//				}
			//			}
			soundSpotParams.classNames = dlControl.getClassNameManager().makeClassNames(dlParams.classNames); 

			//			if (dlParams.classNames!=null) {
			//				for (int i = 0; i<soundSpotParams.classNames.length; i++) {
			//					System.out.println("Class name " + i + "  "  + soundSpotParams.classNames[i].className + " ID " + soundSpotParams.classNames[i].ID ); 
			//				}
			//			}
			//TODO
			//need to load the classifier metadata here...
			//System.out.println("Model transforms: " + this.modelTransforms.size());
		}
		catch (Exception e) {
			soundSpotModel=null; 
			e.printStackTrace();
			//WarnOnce.showWarning(null, "Model Metadata Error", "There was an error extracting the metadata from the model.", WarnOnce.OK_OPTION); 
		}
	}



	@Override
	public float[] runModel(float[][][] transformedDataStack) {
		return soundSpotModel.runModel(transformedDataStack);
	}
	
	
	@Override
	public SoundSpotResult makeModelResult(float[]  prob, double time) {
		SoundSpotResult soundSpotResult =  new SoundSpotResult(prob); 
		soundSpotResult.setAnalysisTime(time);
		return soundSpotResult;
	}


	/**
	 * Destroy the model. 
	 */
	public void closeModel() {
		//TODO
	}


	/**
	 * Get the currently loaded mode. 
	 * @return - the currently loaded mode. 
	 */
	public SoundSpotModel getModel() {
		return soundSpotModel;
	}


}
