package rawDeepLearningClassifer.logging;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import PamUtils.PamArrayUtils;
import rawDeepLearningClassifer.dlClassification.ModelResult;
import rawDeepLearningClassifer.dlClassification.dummyClassifier.DummyModelResult;
import rawDeepLearningClassifer.dlClassification.soundSpot.GenericModelResult;
import rawDeepLearningClassifer.dlClassification.soundSpot.SoundSpotResult;

/**
 * Handles the saving and loading of Model results from binary files. 
 * <p>
 * ModelResults are generated by a classifier and may have classifier-specific fields that need saved. 
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
	 * Flag for model res
	 */
	public static final int DUMMY_RESULT = 2; 


	/**
	 * Write data to a binary output stream 
	 * @param modelResult - the model result to write. 
	 * @param dos
	 */
	public static void getPackedData(ModelResult modelResult, DataOutputStream dos, int type) {

		float[] probabilities = modelResult.getPrediction(); 
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

			if (modelResult.getClassNames()==null) {
				dos.writeShort(0);
			}
			else {
				dos.writeShort(modelResult.getClassNames().length);
				for (int i = 0; i < modelResult.getClassNames().length; i++) {
					dos.writeShort((short) modelResult.getClassNames()[i]);
				}
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
	public static ModelResult sinkData(DataInputStream dis) {
		try {

			//System.out.println("Make model result: "); 

			int type = dis.readByte(); 
			boolean isBinary = dis.readBoolean(); 
			double scale = dis.readFloat();
			short nSpecies = dis.readShort(); 
			float[] data = new float[nSpecies];
			for (int i = 0; i < nSpecies; i++) {
				data[i] = (float) (dis.readShort() / scale);
			}
			
			//the class names. 
			int nClass =  dis.readShort(); 
			short[] classID = new short[nClass];
			for (int i = 0; i < nClass; i++) {
				classID[i] =  dis.readShort(); 
			}			
			//System.out.println("ModelResultBinaryFactory Type: " + type); 

			ModelResult result; 
			//specific settings for different modules 
			switch (type) {
			case SOUND_SPOT:
				result = new SoundSpotResult(data, classID, isBinary);  
				break; 
			case DUMMY_RESULT:
				result = new DummyModelResult(data);  
				break; 
			default:
				//ideally should never be used. 
				result = new GenericModelResult(data, isBinary); 
				break; 
			}

			//System.out.println("New model result: "+ type); 

			return result; 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null; 
		}
	}

	/**
	 * Get the type flag for a model result. this is based on the class type. 
	 * @param modelResult - the model result 
	 * @return the type flag for the subclass of the result. 
	 */
	public static int getType(ModelResult modelResult) {
		int type=0; 
		if (modelResult instanceof GenericModelResult) {
			type=SOUND_SPOT; 
		}
		if (modelResult instanceof GenericModelResult) {
			type=GENERIC; 
		}
		if (modelResult instanceof DummyModelResult) {
			type=DUMMY_RESULT; 
		}
		return type;
	}

}
