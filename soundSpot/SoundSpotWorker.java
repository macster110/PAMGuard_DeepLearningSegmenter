package rawDeepLearningClassifer.soundSpot;

import java.util.Arrays;

import org.jamdev.jtorch4pam.spectrogram.SpecTransform;
import org.jamdev.jtorch4pam.spectrogram.Spectrogram;
import org.jamdev.jtorch4pam.utils.DLUtils;
import org.jamdev.jtorch4pam.wavFiles.AudioData;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import PamUtils.PamCalendar;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;


/**
 * Runs the deep learning model and performs feature extraction. 
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
	 * Sound spot parameters. 
	 */
	private SoundSpotParams dlParams;
	
	/**
	 * The loaded model. 
	 */
	private Module model;
	
	private float sampleRate; 
	
	public SoundSpotWorker(SoundSpotParams dlParams, float sampleRate) {
		this.dlParams=dlParams; 
		this.sampleRate = sampleRate; 
	}
	
	/**
	 * Prepare the model 
	 */
	public void prepModel() {
		try {
			model = Module.load(dlParams.modelPath);
			
			//TODO
			//need to load the classifier metadata here...
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Run the initial data feature extraction and the model
	 * @param rawDataUnit - the raw data unit. 
	 * @param iChan - the channel to run the data on. 
	 * @return the model to run. 
	 */
	public SoundSpotResult runModel(GroupedRawData rawDataUnit, int iChan) {
		
		PamCalendar.isSoundFile(); 
		
		// create an audio data object from the raw data chunk
		AudioData soundData  = new AudioData(rawDataUnit.getRawData()[iChan], sampleRate); 
		
		soundData = soundData.interpolate(dlParams.sR).preEmphasis(dlParams.preemphases); 

		System.out.println( "Open wav file: No. samples:"+ soundData.samples.length + " sample rate: " + soundData.sampleRate);

		//make a spectrogram 
		Spectrogram spectrogram = new Spectrogram(soundData, dlParams.n_fft, dlParams.hop_length); 
		
		//apply transforms to the spectrogram 
		SpecTransform spectransform = new SpecTransform(spectrogram)
				.interpolate(dlParams.fmin, dlParams.fmax, dlParams.n_freq_bins)
				.dBSpec()
				.normalise(dlParams.min_level_dB, dlParams.ref_level_dB)
				.clamp(dlParams.clampMin, dlParams.clampMax);
		
//		//export to a file for checking
//		DLMatFile.exportSpecSurface(spectransform, new File(outputMatfile)); 
//		//export to a file for checking
//		DLMatFile.exportSpecArray(spectrogram.getAbsoluteSpectrogram(), spectrogram.getSampleRate(), new File(outputMatfile)); 

		//now must flatten the spectrogram and create a tensor.			
		float[] specgramFlat = DLUtils.flattenDoubleArrayF(DLUtils.toFloatArray(spectransform.getTransformedData())); 
		int[] arrayShape = 	DLUtils.arrayShape(spectransform.getTransformedData());
		
		//convert the array shape to a long instead of int. 
		long[] arrayShaleL = new long[arrayShape.length]; 
		for (int i=0; i<arrayShaleL.length; i++) {
			arrayShaleL[i] = arrayShape[i]; 
//			System.out.println(arrayShaleL[i]); 
		}
		
		//create the shape for the tensor.
		long[] shape = {1L, 1L, arrayShaleL[0], arrayShaleL[1]}; 
		
//		DLUtils.printArray(specGram); 
		
		//create the tensor 
		Tensor data = Tensor.fromBlob(specgramFlat, shape); 
		
	    System.out.println("Input shape: " + Arrays.toString(data.shape()));
	    System.out.println("Input data [0]: " +data.getDataAsFloatArray()[0]);

		//run the model on the acoustic data. 
		IValue result = model.forward(IValue.from(data));
		
		//convert the output to a tensor
		Tensor output = result.toTensor();
		
		return makeModelResult(output); 
	}
	
	public SoundSpotResult makeModelResult(Tensor output) {
		
		//grab the results. 
	    double[] prob = new double[(int) output.shape()[1]]; 
	    
	    
	    for (int j=0; j<output.shape()[1]; j++) {
	    	//python code for this. 
//	    	prob = torch.nn.functional.softmax(out).numpy()[n, 1]
//                    pred = int(prob >= ARGS.threshold)		    	
	    	//softmax function
	    	prob[j] = DLUtils.softmax(output.getDataAsFloatArray()[j], output.getDataAsFloatArray()); 
	    	System.out.println("The probability is: " + prob[j]); 
	    }
	    
	    SoundSpotResult soundSpotResult = new SoundSpotResult(); 
	    
	    return soundSpotResult; 
	}

	/**
	 * Destroy the model. 
	 */
	public void closeModel() {
		model.destroy();
	}
	

}
