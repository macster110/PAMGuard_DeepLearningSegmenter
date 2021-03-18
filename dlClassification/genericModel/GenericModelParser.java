package rawDeepLearningClassifier.dlClassification.genericModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jamdev.jdl4pam.transforms.DLTransform.DLTransformType;
import org.jamdev.jdl4pam.transforms.DLTransfromParams;
import org.jamdev.jdl4pam.transforms.SimpleTransformParams;
import org.jamdev.jdl4pam.transforms.jsonfile.DLTransformsParser;
import org.json.JSONArray;
import org.json.JSONObject;

import rawDeepLearningClassifier.dlClassification.DLClassName;
import rawDeepLearningClassifier.dlClassification.DLClassNameManager;

/**
 * Functions for saving and loading generic model metadata information. Note this 
 * does not load the model. 
 * 
 * @author Jamie Macaulay
 *
 */
public class GenericModelParser {

	public static final String CLASS_STRING = "class_info"; 


	public static final String SEG_SIZE_STRING = "seg_size"; 


	public static final String MODEL_INFO_STRING = "model_info";

	/**
	 * Write the generic model parameters to a file. 
	 * @param file - the file to write to. 
	 * @param params - the parameters to write. 
	 * @return true if the write was successful
	 */
	public static boolean writeGenericModelParams(File file, GenericModelParams params) {

		try {

			//this writes a new string - with the transforms in order. . 
			String jsOnObject = DLTransformsParser.writeJSONString(params.dlTransfromParams); 
			
			//Now make a second JSON object with a load of the parameters that might be required by the model. 
			//'class_info': '{num_class : 2,name_class : noise,bat}', 'seg_size': '{size_ms : 4}'}

			//set the class names. 
			JSONObject paramsObject = getJSonParamsObject(params); 
			
			String jsonString = paramsObject.toString().substring(0, paramsObject.toString().length()-1) 
					+ "," +  jsOnObject.toString().substring(1, jsOnObject.toString().length()) ;
			
		
			System.out.println(jsonString); 

			writeJSONToFile(file, jsonString, false); 
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false; 
	}

	/**
	 * Write a JSON string to a JSON file. 
	 * @param file - the file to write to. 
	 * @param jsonString - the jsonString. 
	 * @param append - append to the file. 
	 * @return true if the file writing was successful. 
	 */
	public static boolean writeJSONToFile(File file, String jsonString, boolean append){// Write the content in file 
		try(FileWriter fileWriter = new FileWriter(file, append)) {
			fileWriter.write(jsonString);
			fileWriter.close();
			return true; 
		} catch (IOException e) {
			// Exception handling
			e.printStackTrace();
			return false;
		}

	}
	
	

	/**
	 * Get the JSON parameters object. This contains all parameters from generic parameters except the
	 * transform parameters. The transform parameters are written on a different line. 
	 * @param params - the parameters object. 
	 * @return the  JSONObject. 
	 */
	public static JSONObject getJSonParamsObject(GenericModelParams params) {
		return getJSonParamsObject( params, new  JSONObject()); 
	}
	


	/**
	 * Get the JSON parameters object. This contains all parameters from generic parameters except the
	 * transform parameters. The transform parameters are written on a different line. 
	 * @param params - the parameters object. 
	 * @param paramsobject - jsonObject to add params to.
	 * @return the  JSONObject. 
	 */
	public static JSONObject getJSonParamsObject(GenericModelParams params, JSONObject paramsObject) {
		//set the class names. 
		JSONObject classInfo = new JSONObject(); 
		classInfo.put("num_class", params.numClasses); 

		String classNames = "";
		for (int i =0; i<params.numClasses ; i++) {

			if ( params.classNames!=null) {
				classNames+= params.classNames[i];
			}
			else {
				classNames+= "Class "+i;

			}
			
			
			if (i!=params.numClasses -1) {
				classNames+=",";
			}
		}
		classInfo.put("name_class", classNames); 

		JSONObject segSize = new JSONObject(); 
		segSize.put("size_ms", params.defaultSegmentLen); 

		JSONObject modelData = new JSONObject(); 
		modelData.put("input_shape", new JSONArray(params.shape)); 
		modelData.put("output_shape", new JSONArray(params.outputShape)); 


		paramsObject.put(CLASS_STRING, classInfo.toString()); 
		paramsObject.put(SEG_SIZE_STRING, segSize.toString()); 
		paramsObject.put(MODEL_INFO_STRING, modelData.toString()); 

		return paramsObject; 
	}


	/**
	 * Read the generic model paramters. 
	 * @param file - the file. 
	 * @param params
	 * @return
	 */
	public static GenericModelParams readGenericModelParams(File file, GenericModelParams params, DLClassNameManager classNameManager) {

		try {

			// Read the content from file
			try(FileReader fileReader = new FileReader(file)) {
				int ch =0; 
				String jsonData = ""; 
				while(ch != -1) {
					ch = fileReader.read();
					jsonData+=(char)ch; 

					//System.out.print((char)ch);

					//have the string data. now make a json object. 
				}
				fileReader.close();
				
				System.out.println(jsonData); 

				//DL transforms
				ArrayList<DLTransfromParams> dlTransforms = DLTransformsParser.parseTransfromParams(jsonData); 
				
				//System.out.println("No. parsed transforms: " + dlTransforms.size()); 

				//DL transforms 
				params.dlTransfromParams = dlTransforms;

				//parse the data
				params = parseJSOString(jsonData,  params, classNameManager);

				return params;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				// Exception handling
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null; 
				// Exception handling
			}
			catch (Exception e) {
				e.printStackTrace(); 
				return null; 
			}


		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null; 
	}


	/**
	 * Set new parameters in a GenericModelParams object. 
	 * @param jsonParamsString - the json parameters string. 
	 * @param params - the parameters. 
	 * @return the parameters class with new settings set.
	 */
	public static GenericModelParams parseJSOString(String jsonParamsString, GenericModelParams paramsClone, 
			DLClassNameManager classNameManager) {		

		JSONObject jsonObject = new JSONObject(jsonParamsString);

		//the segment length information 
		System.out.println(jsonParamsString); 

		//System.out.println("SEG SIZE: " + jsonObject.getString(SEG_SIZE_STRING) + "  " + jsonObject.isNull("size_ms")); 
		
		JSONObject segSize = new JSONObject(jsonObject.getString(SEG_SIZE_STRING)); 
		if (!segSize.isNull("size_ms")) {
			paramsClone.defaultSegmentLen = segSize.getDouble("size_ms");
		}
		else {
			paramsClone.defaultSegmentLen = null; 
		}


		//System.out.println("MODEL_INFO_STRING: " + jsonObject.getString(MODEL_INFO_STRING) + "  " + jsonObject.isNull("input_shape")); 

		//the model info
		JSONObject modelInfo = new JSONObject(jsonObject.getString(MODEL_INFO_STRING)); 

		//the input shape
		Long[] shape = null; 
		if (!modelInfo.isNull("input_shape")) {
			JSONArray shapeArray = modelInfo.getJSONArray("input_shape"); 
			shape = new Long[shapeArray.length()]; 
			
			for (int i=0; i<shape.length; i++) {
				shape[i] = shapeArray.getLong(i); 
			}
		}

		//make both the shape and default shape the same when imported. 
		paramsClone.defaultShape = shape; 
		paramsClone.shape = shape; 

		//System.out.println("MODEL_INFO_STRING: " + jsonObject.getString(MODEL_INFO_STRING) + "  " + jsonObject.isNull("output_shape")); 

		Long[] outShape = null; 
		if (!modelInfo.isNull("output_shape")) {
			JSONArray outShapeArray = modelInfo.getJSONArray("output_shape");
			outShape = new Long[outShapeArray.length()]; 

			for (int i=0; i<outShape.length; i++) {
				outShape[i] = outShapeArray.getLong(i); 
			}
		}

		//make both the shape and default shape the same when imported. 
		paramsClone.defualtOuput = outShape; 
		paramsClone.outputShape = outShape; 

		//the class names
		JSONObject classinfo = new JSONObject(jsonObject.getString(CLASS_STRING)); 

		//the class names
		String[] classNames = classinfo.getString("name_class").split(",");
		for (int i=0; i<classNames.length; i++) {
			classNames[i]=classNames[i].trim(); //remove whitespace

		}

		
		if (classNameManager!=null) {
			paramsClone.classNames = classNameManager.makeClassNames(classNames); 
		}
		else {
			paramsClone.classNames = new DLClassName[classNames.length];
			for (int i=0; i<classNames.length; i++) {
				paramsClone.classNames[i]=new DLClassName(classNames[i], (short) i); 
			}
		}


		return paramsClone; 
	}



	public static void main(String[] args) {


		File testFile = new File("/Users/au671271/Desktop/genericparamstest.pdtf"); 

		GenericModelParams genericModelParams = new GenericModelParams(); 

		float sr = 2000; 

		//create the transforms. 
		ArrayList<DLTransfromParams> dlTransformParamsArr = new ArrayList<DLTransfromParams>();

		//waveform transforms. 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.DECIMATE, sr)); 
		//			dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.PREEMPHSIS, pre-emphases)); 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.SPECTROGRAM, 256, 100)); 
		//in the python code they have an sfft of 129xN where N is the number of chunks. They then
		//choose fft data between bin 5 and 45 in the FFT. 	This roughly between 40 and 350 Hz. 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.SPECCROPINTERP, 47.0, 357.0, 40)); 
		dlTransformParamsArr.add(new SimpleTransformParams(DLTransformType.SPECNORMALISEROWSUM)); 


		genericModelParams.dlTransfromParams = dlTransformParamsArr; 

		genericModelParams.defaultShape = new Long[] {-1L, 40L, 40L, 1L}; 
		genericModelParams.shape = genericModelParams.defaultShape; 

		//default the output. 
		genericModelParams.defualtOuput = new Long[] {-1L, 2L}; 
		
		//the number of classes. 
		genericModelParams.numClasses = 2; 
		
		//the default segment length in millis. 
		genericModelParams.defaultSegmentLen = 2000.; //milliseconds. 


		//write the parameters
		writeGenericModelParams(testFile, genericModelParams); 
		
		System.out.println("====BEFORE EXPORT=====");
		System.out.println(genericModelParams.toString());
		System.out.println("---------------------------------------------------------");


		GenericModelParams genericmodelParmas = new GenericModelParams();

		//read the generic model parameters
		genericmodelParmas = readGenericModelParams(testFile, genericModelParams, null);
		
		System.out.println("---------------------------------------------------------");
		System.out.println("====AFTER EXPORT=====");
		System.out.println(genericModelParams.toString());


	}
	
	




}
