package rawDeepLearningClassifer.logging;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import PamUtils.PamArrayUtils;
import binaryFileStorage.BinaryHeader;
import rawDeepLearningClassifer.deepLearningClassification.GenericModelResult;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;
import rawDeepLearningClassifer.soundSpot.SoundSpotResult;

/**
 * Handles the saving and loading of Model results from binary files. 
 * <p>
 * ModelResults are generated by a classifier and may have classifier-specfic fields that need saved. 
 * The model results factory allows unique subclasses of ModelResult to save and load data different
 * data fields to binary fields. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class ModelResultBinaryFactory {
	

	/**
	 * Flag for model res
	 */
	public static final int GENERIC = 0; 

	/**
	 * Flag for model res
	 */
	public static final int SOUND_SPOT = 1; 


	/**
	 * Write data to a binary output stream 
	 * @param modelResult - the model result to write. 
	 * @param dos
	 */
	public void getPackedData(ModelResult modelResult, DataOutputStream dos, int type) {

		double[] probabilities = modelResult.getPrediction(); 
		double maxVal = PamArrayUtils.max(probabilities); 
		double scale;
		if (maxVal > 0) {
			scale = (float) (32767./maxVal);			
		}
		else {
			scale = 1.;
		}
		/*
		 * Pretty minimilst write since channel map will already be stored in the
		 * standard header and data.length must match the channel map. 
		 */
		try {
			dos.writeByte(type);
			dos.writeBoolean(modelResult.isBinaryClassification());
			dos.writeFloat((float) scale);
			dos.writeShort(probabilities.length);
			for (int i = 0; i < probabilities.length; i++) {
				dos.writeShort((short) (scale*probabilities[i]));
			}

			//specific settings for different modules 
			switch (type) {
			case SOUND_SPOT:
				
				break; 
			default:
				//no extra information. 
				break; 
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Read binary data and make a model result
	 * @param binaryObjectData
	 * @param bh
	 * @param moduleVersion
	 * @return
	 */
	public ModelResult sinkData(DataInputStream dis , BinaryHeader bh, int moduleVersion) {
		try {

			int type = dis.readByte(); 
			boolean isBinary = dis.readBoolean(); 
			double scale = dis.readFloat();
			short nSpecies = dis.readShort(); 
			double[] data = new double[nSpecies];
			for (int i = 0; i < nSpecies; i++) {
				data[i] = (double) dis.readShort() / scale;

			}

			ModelResult result; 
			//specific settings for different modules 
			switch (type) {
			case SOUND_SPOT:
				result = new SoundSpotResult(data, isBinary);  
				break; 
			default:
				//ideally should not be used. 
				result = new GenericModelResult(data, isBinary); 
				break; 
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null; 
	}

}