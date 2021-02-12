package rawDeepLearningClassifer.dlClassification.soundSpot;

import java.util.ArrayList;
import java.util.Arrays;

import org.jamdev.jdl4pam.SoundSpot.SoundSpotModel;
import org.jamdev.jdl4pam.SoundSpot.SoundSpotParams;
import org.jamdev.jdl4pam.transforms.DLTransform;
import org.jamdev.jdl4pam.transforms.FreqTransform;
import org.jamdev.jdl4pam.transforms.WaveTransform;
import org.jamdev.jdl4pam.transforms.DLTransformsFactory;
import org.jamdev.jdl4pam.utils.DLUtils;
import org.jamdev.jpamutils.wavFiles.AudioData;
import org.pytorch.Module;
import org.pytorch.Tensor;

import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;


/**
 * 
 * Runs the deep learning model and performs feature extraction.
 * <p>
 *  
 * 
 * @author Jamie Macaulay 
 *
 */
public class SoundSpotWorker {

	/**
	 * The maximum allowed queue size;
	 */
	public final static int MAX_QUEUE_SIZE = 10 ; 

	/**
	 * The loaded model. 
	 */
	private Module model;

	/**
	 * The sample rate.
	 */
	private float sampleRate;

	/**
	 * The model transforms for the data. 
	 */
	private ArrayList<DLTransform> modelTransforms;

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
	public void prepModel(PamSoundSpotParams soundSpotParams, DLControl dlControl) {
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

			this.modelTransforms =  model2DLTransforms(dlParams); 
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
			e.printStackTrace();
			//WarnOnce.showWarning(null, "Model Metadata Error", "There was an error extracting the metadata from the model.", WarnOnce.OK_OPTION); 
		}
	}

	/**
	 * Run the initial data feature extraction and the model
	 * @param rawDataUnit - the raw data unit. 
	 * @param iChan - the channel to run the data on. 
	 * @return the model to run. 
	 */
	public ArrayList<SoundSpotResult> runModel(ArrayList<GroupedRawData> rawDataUnits, float sampleRate, int iChan) {

		try {
			//		PamCalendar.isSoundFile(); 
			//		// create an audio data object from the raw data chunk
			long timeStart = System.nanoTime(); 
			
			//data input into the model - a stack of spectrogram images. 
			float[][][] transformedDataStack = new float[rawDataUnits.size()][][]; 

			//geenrate the spectrogram stack. 
			AudioData soundData; 
			double[][] transformedData;
			for (int j=0; j<rawDataUnits.size(); j++) {

				soundData  = new AudioData(rawDataUnits.get(j).getRawData()[iChan], sampleRate); 

				//			for (int i=0; i<modelTransforms.size(); i++) {
				//				System.out.println("Transfrom type: " + modelTransforms.get(i).getDLTransformType()); 
				//			}

				//set the sound in the first transform. 
				((WaveTransform) modelTransforms.get(0)).setWaveData(soundData); 

				//System.out.println("Model transforms:no. " + modelTransforms.size()+ "  " + soundData.getLengthInSeconds() + " Decimate Params: " + ((WaveTransform) modelTransforms.get(0)).getParams()[0]);

				DLTransform transform = modelTransforms.get(0); 
				for (int i =0; i<modelTransforms.size(); i++) {
					transform = modelTransforms.get(i).transformData(transform); 
				}

				//the transformed data
				transformedData = ((FreqTransform) transform).getSpecTransfrom().getTransformedData(); 
				
				transformedDataStack[j] = DLUtils.toFloatArray(transformedData); 

			}
			

			//run the model. The output is the 
			float[] output = null; 
			long time1 = System.currentTimeMillis();
			output = soundSpotModel.runModel(transformedDataStack); 
			long time2 = System.currentTimeMillis();

			
			int numclasses = (int) (output.length/transformedDataStack.length); 

			//System.out.println(PamCalendar.formatDBDateTime(rawDataUnit.getTimeMilliseconds(), true) + " Time to run model: " + (time2-time1) + " ms for spec of len: " + transformedData.length); 

			ArrayList<SoundSpotResult> modelResults = new ArrayList<SoundSpotResult>(); 
			float[] prob; 
			float[] classOut; 
			for (int i=0; i<transformedDataStack.length; i++) {
				
				classOut = Arrays.copyOfRange(output, i*numclasses, (i+1)*numclasses-1); 
				
				prob = new float[classOut.length]; 
				
				for (int j=0; j<classOut.length; j++) {
					//python code for this. 
					//	    	prob = torch.nn.functional.softmax(out).numpy()[n, 1]
					//                    pred = int(prob >= ARGS.threshold)		    	
					//softmax function
					prob[j] = (float) DLUtils.softmax(classOut[j], classOut); 
					//System.out.println("The probability is: " + prob[j]); 
				}

				//does this pass binary classification
				long timeEnd = System.nanoTime(); 

				SoundSpotResult soundSpotResult =  new SoundSpotResult(prob); 
				soundSpotResult.setAnalysisTime((timeEnd-timeStart)/1000/1000/1000);
				
				modelResults.add(soundSpotResult);
			}
			
			return modelResults;

		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public SoundSpotResult makeModelResult(Tensor output) {

		//grab the results. 
		float[] prob = new float[(int) output.shape()[1]]; 


		for (int j=0; j<output.shape()[1]; j++) {
			//python code for this. 
			//	    	prob = torch.nn.functional.softmax(out).numpy()[n, 1]
			//                    pred = int(prob >= ARGS.threshold)		    	
			//softmax function
			prob[j] = (float) DLUtils.softmax(output.getDataAsFloatArray()[j], output.getDataAsFloatArray()); 
			System.out.println("The probability is: " + prob[j]); 
		}

		SoundSpotResult soundSpotResult = new SoundSpotResult(prob); 

		return soundSpotResult; 
	}

	/**
	 * Destroy the model. 
	 */
	public void closeModel() {
		model.destroy();
	}

	public ArrayList<DLTransform> getModelTransforms() {
		return modelTransforms;
	}

	public void setModelTransforms(ArrayList<DLTransform> modelTransforms) {
		this.modelTransforms = modelTransforms;
	}



	/**
	 * Convert the parameters saved in the sound spot model to DLtransform parameters. 
	 * @return the DLTransform parameters. 
	 */
	public ArrayList<DLTransform> model2DLTransforms(SoundSpotParams dlParams) {

		ArrayList<DLTransform> transforms = DLTransformsFactory.makeDLTransforms(dlParams.dlTransforms); 

		//		//waveform transforms. 
		//		transforms.add(new WaveTransform(DLTransformType.DECIMATE, dlParams.sR)); 
		//		transforms.add(new WaveTransform(DLTransformType.PREEMPHSIS, dlParams.preemphases)); 
		//		//transforms.add(new WaveTransform(soundData, DLTransformType.TRIM, samplesChunk[0], samplesChunk[1])); 
		//
		//		//frequency transforms. 
		//		transforms.add(new FreqTransform(DLTransformType.SPECTROGRAM, dlParams.n_fft, dlParams.hop_length)); 
		//		transforms.add(new FreqTransform(DLTransformType.SPECCROPINTERP, dlParams.fmin, dlParams.fmax, dlParams.n_freq_bins)); 
		//		transforms.add(new FreqTransform(DLTransformType.SPEC2DB)); 
		//		transforms.add(new FreqTransform(DLTransformType.SPECNORMALISE, dlParams.min_level_dB, dlParams.ref_level_dB)); 
		//		transforms.add(new FreqTransform(DLTransformType.SPECCLAMP, dlParams.clampMin, dlParams.clampMax)); 


		return transforms; 

	}

	/**
	 * Get the currently loaded mode. 
	 * @return - the currently loaded mode. 
	 */
	public SoundSpotModel getModel() {
		return soundSpotModel;
	}

}
