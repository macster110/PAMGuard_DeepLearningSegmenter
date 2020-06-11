package rawDeepLearningClassifer.orcaSpot;

import java.io.File;
import java.io.Serializable;

/**
 * Stored settings for the OrcaSpot module. 
 * 
 * @author Jamie Macaulay
 *
 */
public class OrcaSpotParams2 implements Serializable, Cloneable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 2L;

	
//	//TODO Decision and Counter
////	public static int Threads;
//	public static int OrcaCounter = 0;
//	
//	public static int NoiseCounter = 0;
	
		
	/**
	 * The master path to the segmentor python folder. 
	 */
	public String segmenterMasterPath =  "E:/DeepLearning_OrcaSpot/Segmenter/";
	
	/***Python Scripts***/
	
	/**
	 * Location of the python.exe file.
	 */
	public String pythonExeFile = segmenterMasterPath +"pytorch/my-venv/Scripts/python.exe";
	
//	public String pythonExeFile = "C:/Users/macst/PycharmProjects/python_test/venv/Scripts/python.exe";
	
	/**
	 * Python script for running the initial prediction stage
	 */
	public String predict_script  = segmenterMasterPath + "orca-spot/src/predict.py";
	
	/**
	 * Python script for running the later classification stage. 
	 */
	public String predict2_script = segmenterMasterPath + "r18_m2_a1_c7_n_run1/orca_spot_call_type/src/predict.py"; 
	
	/***Classifier Models****/
	
	/**
	 * The initial prediction models
	 */
	public String model = segmenterMasterPath + "r18_m2_a1_c7_n_run1/classifier.pk"; 
	
	/**
	 * The classifier model
	 */
	public String classifier_model = segmenterMasterPath + "r18_m2_a1_c7_n_run1/call_type.pk"; 
	
	/**
	 * A Logfile for current Debbuging.
	 */
	public String log = segmenterMasterPath;
	
	/**
	 * Use the classifier 
	 */
	public boolean useClassifier = true;
	
	/**
	 * The threshold to apply to the initial prediction stage
	 */
	public String threshold = Double.toString(0.92); 
	
	/**
	 * The threshold to apply to the later classification stage. 
	 */
	public String threshold2 = Double.toString(0.8); 

	/****Graphics card***/
	
	/**
	 * Use the graphics card.
	 */
	public boolean cuda = false; 
	
	/**
	 * Number of workers?
	 */
	public String num_workers = Integer.toString(0); 
	
	
	/***Non user changeable***/
	
	public File audio_file; 
	
	/**
	 * The length of audio to analyse in seconds (This should be set the same as length of input file and is not user changeable);
	 */
	public String seq_len = "2"; 
	
	/**
	 * The hop size to use in seconds (This should be set the same as length of input file and is not use changeable)
	 */
	public String hop_size = "2";


	/**
	 * The location of .exe file that runs the Deep Learning classifier. 
	 */
	public String classiferFile = ""; 
	
	
	public String getThreshold() {
		return threshold;
	}
	
	public String getThreshold2() {
		return threshold2;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getPredict_script() {
		return predict_script;
	}
	public String getClass_script() {
		return predict2_script;
	}

	public void setPredict_script(String predict_script) {
		this.predict_script = predict_script;
	}

	public String getAudio_file() {
		return audio_file.getAbsolutePath();
	}
	
	public String getLog_Path() {
		return log;
	}
	
	public void setAudio_file(File audio_file) {
		this.audio_file = audio_file;
	}

	public String getSeq_len() {
		return seq_len;
	}

	public void setSeq_len(String seq_len) {
		this.seq_len = seq_len;
	}

	public String getHop_size() {
		return hop_size;
	}

	public void setHop_size(String hop_size) {
		this.hop_size = hop_size;
	}

	public Boolean getcuda() {
		return cuda;
	}

	public void setcuda(boolean cuda) {
		this.cuda = cuda;
	}

	public String getModel() {
		return model;
	}
	public String getclassifierModel() {
		return classifier_model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public String getNum_workers() {
		return num_workers;
	}

	public void setNum_workers(String num_workers) {
		this.num_workers = num_workers;
	}
	
	@Override
	public OrcaSpotParams2 clone() {
		OrcaSpotParams2 newParams = null;
		try {
			newParams = (OrcaSpotParams2) super.clone();
//			if (newParams.spectrogramNoiseSettings == null) {
//				newParams.spectrogramNoiseSettings = new SpectrogramNoiseSettings();
//			}
//			else {
//				newParams.spectrogramNoiseSettings = this.spectrogramNoiseSettings.clone();
//			}
		}
		catch(CloneNotSupportedException Ex) {
			Ex.printStackTrace(); 
			return null;
		}
		return newParams;
	}



	
}
