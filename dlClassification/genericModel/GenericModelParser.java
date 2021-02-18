package rawDeepLearningClassifer.dlClassification.genericModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jamdev.jdl4pam.transforms.jsonfile.DLTransformsParser;
import org.json.JSONArray;
import org.json.JSONObject;

import rawDeepLearningClassifer.DLControl;

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


	private DLControl dlControl; 
	
	public GenericModelParser(DLControl dlControl) {
		this.dlControl=dlControl; 
	}

	/**
	 * Write the generic model parameters to a file. 
	 * @param file - the file to write to. 
	 * @param params - the parameters to write. 
	 * @return true if the write was successful
	 */
	private boolean writeGenericModelParams(File file, GenericModelParams params) {

		try {

			//this writes a new file. 
			DLTransformsParser.writeJSONFile(file, params.dlTransfromParams); 

			//Now make a second JSON object with a load of the parameters that might be required by the model. 
			//'class_info': '{num_class : 2,name_class : noise,bat}', 'seg_size': '{size_ms : 4}'}

			//set the class names. 
			JSONObject paramsObject = getJSonParamsObject( params); 

			writeJSONToFile(file, paramsObject.toString(), true); 


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
		try(FileWriter fileWriter = new FileWriter(file)) {
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
	public JSONObject getJSonParamsObject(GenericModelParams params) {
		//set the class names. 
		JSONObject classInfo = new JSONObject(); 
		classInfo.put("num_class", params.numClasses); 

		String classNames = "";
		for (int i =0; i<params.classNames.length ; i++) {
			classNames+= params.classNames[i];
			if (i!=params.classNames.length-1) {
				classNames+=",";
			}
		}
		classInfo.put("name_class", classNames); 

		JSONObject segSize = new JSONObject(); 
		segSize.put("size_ms", params.defaultSegmentLen); 

		JSONObject modelData = new JSONObject(); 
		modelData.put("input_shape", new JSONArray(params.shape)); 

		JSONObject paramsObject = new JSONObject(); 
		paramsObject.put(CLASS_STRING, classInfo.toString()); 
		paramsObject.put(SEG_SIZE_STRING, segSize.toString()); 
		paramsObject.put(MODEL_INFO_STRING, modelData.toString()); 

		return paramsObject; 
	}
	
	
	/**
	 * Set new parameters in a GenericModelParams object. 
	 * @param jsonParamsString - the json parameters string. 
	 * @param params - the parameters. 
	 * @return the parameters class with new settings set.
	 */
	public GenericModelParams parseJSOString(String jsonParamsString, GenericModelParams params) {
		
		
		GenericModelParams paramsClone = params.clone(); 
		
		
		JSONObject jsonObject = new JSONObject(jsonParamsString);

		//the segment length information 
		JSONObject segSize = new JSONObject(jsonObject.getString(SEG_SIZE_STRING)); 
		paramsClone.defaultSegmentLen = segSize.getDouble("size_ms");

		
		//the model info
		JSONObject modelInfo = new JSONObject(jsonObject.getString(MODEL_INFO_STRING)); 
		
		JSONArray shapeArray = modelInfo.getJSONArray("input_shape"); 
		long[] shape = new long[shapeArray.length()]; 
		
		for (int i=0; i<shape.length; i++) {
			shape[i] = shapeArray.getLong(i); 
		}
		paramsClone.shape = shape; 
		

		//the class names
		JSONObject classinfo = new JSONObject(jsonObject.getString(CLASS_STRING)); 
		
		//the class names
		String[] classNames = classinfo.getString("name_class").split(",");
		for (int i=0; i<classNames.length; i++) {
			classNames[i]=classNames[i].trim(); //remove whitespace

		}
		paramsClone.classNames = dlControl.getClassNameManager().makeClassNames(classNames); 

		
		return paramsClone; 
	}



}
