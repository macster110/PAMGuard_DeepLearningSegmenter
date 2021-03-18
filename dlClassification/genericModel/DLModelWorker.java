package rawDeepLearningClassifier.dlClassification.genericModel;

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


import PamUtils.PamCalendar;
import rawDeepLearningClassifier.DLControl;
import rawDeepLearningClassifier.dlClassification.soundSpot.StandardModelParams;
import rawDeepLearningClassifier.segmenter.SegmenterProcess.GroupedRawData;


/**
 * 
 * Runs the deep learning model and performs feature extraction.
 * <p>
 *  
 * 
 * @author Jamie Macaulay 
 *
 */
public abstract class DLModelWorker<T> {

	/**
	 * The maximum allowed queue size;
	 */
	public final static int MAX_QUEUE_SIZE = 10 ; 

	/**
	 * The model transforms for the data. 
	 */
	private ArrayList<DLTransform> modelTransforms;



	/**
	 * Run the initial data feature extraction and the model
	 * @param rawDataUnit - the raw data unit. 
	 * @param iChan - the channel to run the data on. 
	 * @return the model to run. 
	 */
	public synchronized ArrayList<T> runModel(ArrayList<GroupedRawData> rawDataUnits, float sampleRate, int iChan) {

		try {
			
			//the number of chunks. 
			int numChunks = rawDataUnits.size(); 
			
			//PamCalendar.isSoundFile(); 
			//create an audio data object from the raw data chunk
			long timeStart = System.nanoTime(); 
			
			//data input into the model - a stack of spectrogram images. 
			float[][][] transformedDataStack = new float[numChunks][][]; 

			//geenrate the spectrogram stack. 
			AudioData soundData; 
			double[][] transformedData;
			for (int j=0; j<numChunks; j++) {

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
				
				//System.out.println("DLModelWorker: Input shape: " + transformedData.length + "  " + transformedData[0].length + PamCalendar.formatDBDateTime(rawDataUnits.get(j).getTimeMilliseconds(), true));
				
				transformedDataStack[j] = DLUtils.toFloatArray(transformedData); 

			}


			//run the model. The output is the 
			float[] output = null; 
			long time1 = System.currentTimeMillis();
			output = runModel(transformedDataStack); 
			long time2 = System.currentTimeMillis();


			int numclasses = (int) (output.length/transformedDataStack.length); 

//			System.out.println(PamCalendar.formatDBDateTime(rawDataUnits.get(0).getTimeMilliseconds(), true) + 
//					" Time to run model: " + (time2-time1) + " ms for spec of len: " + transformedDataStack.length + 
//					"output: " + output.length + " " + numclasses); 

			ArrayList<T> modelResults = new ArrayList<T>(); 
			float[] prob; 
			float[] classOut; 
			for (int i=0; i<transformedDataStack.length; i++) {
				

				/**
				 * This is super weird. Reading the documentation for copeOfRange the index from and index to are enclusive. So 
				 * to copy the first two elements indexfrom =0 and indexto = 1. But actually it seems that this should be indexfrom =0 and indexto =2. 
				 * So do not minus one form (i+1)*numclasses. This works but I'm confused as to why?
				 */
				classOut = Arrays.copyOfRange(output, i*numclasses, (i+1)*numclasses); 

//				System.out.println("The copyOfRange is: " + i*numclasses + " to " + ((i+1)*numclasses-1) + " class out len: " + classOut.length); 

				prob = new float[classOut.length]; 

				for (int j=0; j<classOut.length; j++) {
					//python code for this. 
					//	    	prob = torch.nn.functional.softmax(out).numpy()[n, 1]
					//                    pred = int(prob >= ARGS.threshold)		    	
					//softmax function
					prob[j] = (float) DLUtils.softmax(classOut[j], classOut); 
					//System.out.println("The probability is: " + j + ": " + prob[j]); 
				}

				//does this pass binary classification
				long timeEnd = System.nanoTime(); 

				T soundSpotResult = makeModelResult(prob, (timeEnd-timeStart)/1000/1000/1000); 
				//				soundSpotResult.setAnalysisTime((timeEnd-timeStart)/1000/1000/1000);

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

	public abstract float[] runModel(float[][][] transformedDataStack);

	public abstract T makeModelResult(float[] prob, double time);

	public abstract void prepModel(StandardModelParams soundSpotParams, DLControl dlControl);


	/**
	 * Destroy the model. 
	 */
	public abstract void closeModel();

	//	public SoundSpotResult makeModelResult(Tensor output) {
	//
	//		//grab the results. 
	//		float[] prob = new float[(int) output.shape()[1]]; 
	//
	//
	//		for (int j=0; j<output.shape()[1]; j++) {
	//			//python code for this. 
	//			//	    	prob = torch.nn.functional.softmax(out).numpy()[n, 1]
	//			//                    pred = int(prob >= ARGS.threshold)		    	
	//			//softmax function
	//			prob[j] = (float) DLUtils.softmax(output.getDataAsFloatArray()[j], output.getDataAsFloatArray()); 
	//			System.out.println("The probability is: " + prob[j]); 
	//		}
	//
	//		SoundSpotResult soundSpotResult = new SoundSpotResult(prob); 
	//
	//		return soundSpotResult; 
	//	}


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


}
