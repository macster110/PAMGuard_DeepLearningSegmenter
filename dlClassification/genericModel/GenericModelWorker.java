package rawDeepLearningClassifer.dlClassification.genericModel;

import org.jamdev.jdl4pam.genericmodel.GenericModel;

import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.soundSpot.GenericModelResult;
import rawDeepLearningClassifer.dlClassification.soundSpot.StandardModelParams;

/**
 * Generic model worker. 
 * 
 * @author Jamie Macaulay
 *
 */
public class GenericModelWorker extends DLModelWorker<GenericModelResult> {

	/**
	 * The generic model 
	 */
	private GenericModel genericModel;

	@Override
	public float[] runModel(float[][][] transformedDataStack) {
		return genericModel.runModel(transformedDataStack);
	}

	@Override
	public GenericModelResult makeModelResult(float[] prob, double time) {
		GenericModelResult model = new  GenericModelResult(prob);
		model.setAnalysisTime(time);
		return model;
	}

	@Override
	public void prepModel(StandardModelParams soundSpotParams, DLControl dlControl) {
		try {
			//first open the model and get the correct parameters. 
			genericModel = new GenericModel(soundSpotParams.modelPath); 
			
			GenericModelParams genericModelParams = new GenericModelParams(); 
			
			genericModelParams.defaultShape = genericModel.getInputShape().getShape(); 
			genericModelParams.defualtOuput = genericModel.getOutShape().getShape(); 

		}
		catch (Exception e) {
			e.printStackTrace();
			//WarnOnce.showWarning(null, "Model Load Error", "There was an error loading the model file.", WarnOnce.OK_OPTION); 
		}

	}

	@Override
	public void closeModel() {
		// TODO Auto-generated method stub
	}

	/**
	 * Generic model. 
	 * @return the generic model. 
	 */
	public GenericModel getModel() {
		return genericModel;
	}

}
