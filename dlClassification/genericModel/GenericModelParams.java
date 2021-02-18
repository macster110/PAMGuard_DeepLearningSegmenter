package rawDeepLearningClassifer.dlClassification.genericModel;

import java.util.ArrayList;

import org.jamdev.jdl4pam.transforms.DLTransform.DLTransformType;
import org.jamdev.jdl4pam.transforms.DLTransformsFactory;
import org.jamdev.jdl4pam.transforms.DLTransfromParams;
import org.jamdev.jdl4pam.transforms.SimpleTransformParams;

import rawDeepLearningClassifer.dlClassification.soundSpot.StandardModelParams;

/**
 * Parameters for the deep learning module. 
 * @author Jamie Macaulay
 *
 */
public class GenericModelParams extends StandardModelParams implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L; 
	
	public GenericModelParams() {
		
		ArrayList<DLTransfromParams> dlTransformParamsArr = new ArrayList<DLTransfromParams>();
		//waveform transforms. 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.DECIMATE, 96000)); 
		//			dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.PREEMPHSIS, preemphases)); 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.SPECTROGRAM, 256, 100)); 
		//in the python code they have an sfft of 129xN where N is the number of chunks. They then
		//choose fft data between bin 5 and 45 in the FFT. 	This roughly between 40 and 350 Hz. 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.SPECCROPINTERP, 1000.0, 20000.0, 40)); 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.SPECNORMALISEROWSUM)); 
		
		this.dlTransfroms =	DLTransformsFactory.makeDLTransforms(dlTransformParamsArr); 
	
	}
	
	/**
	 * Path to the metadata file.
	 */
	public String metadataPath = null; 
	
	/**
	 * The input shape defined by the user. 
	 */
	public long[] shape = new long[] {-1L, 40, 40, 1L}; 
	
	/**
	 * The input shape for the model if the model has an in-built default. 
	 */
	public long[] defaultShape = null;
	
	/**
	 * The output shape for the model if the model has an in-built default. 
	 */
	public long[] defualtOuput = null;
	
	
	@Override
	public GenericModelParams clone() {
		GenericModelParams newParams = null;
		newParams = (GenericModelParams) super.clone();
//			if (newParams.spectrogramNoiseSettings == null) {
//				newParams.spectrogramNoiseSettings = new SpectrogramNoiseSettings();
//			}
//			else {
//				newParams.spectrogramNoiseSettings = this.spectrogramNoiseSettings.clone();
//			}
		return newParams;
	}
	
	

}
