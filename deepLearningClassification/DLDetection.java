package rawDeepLearningClassifer.deepLearningClassification;

import java.util.ArrayList;

import PamDetection.AbstractLocalisation;
import PamDetection.LocContents;
import PamDetection.PamDetection;
import PamUtils.PamUtils;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;
import PamguardMVC.RawDataHolder;
import annotation.DataAnnotation;
import bearinglocaliser.annotation.BearingAnnotation;

/**
 * A detected DL data unit. These data units are only ever generated from raw 
 * sound data from the segmenter. Otherwise DL results are saved as annotations on 
 * other data units. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class DLDetection extends PamDataUnit implements PamDetection, RawDataHolder {
	
	/**
	 * The abstract localisation 
	 */
	private DLLocalisation localisation; 
	
	/**
	 * The raw data unit. 
	 */
	private double[][] waveData;

	/**
	 * Model results that make up this data unit. 
	 */
	private ArrayList<ModelResult> modelResults;

	/**
	 * Create a data unit for DL which has passed binary classification.  
	 * @param timeMilliseconds - the time in milliseconds. 
	 * @param channelBitmap - the channel bit map. 
	 * @param startSample - the start sample. 
	 * @param durationSamples - the duration in samples. 
	 * @param modelResults - the model results that were used to construct the data unit. 
	 */
	public DLDetection(long timeMilliseconds, int channelBitmap, long startSample, long durationSamples,
			ArrayList<ModelResult> modelResults, double[][] waveData) {
		super(timeMilliseconds, channelBitmap, startSample, durationSamples);
		this.modelResults = modelResults; 
		this.waveData=waveData; 
	}
	
	
	/**
	 * Create a data unit for DL which has passed binary classification. Usually used for 
	 * loading data units from binary files. 
	 * @param baseData - the base binary data. 
	 * @param probdata - the probability data. 
	 * @param waveData - the wave data. 
	 */
	public DLDetection(DataUnitBaseData baseData, ArrayList<ModelResult> modelResults, double[][] waveData) {
		super(baseData);
		this.modelResults = modelResults; 
		this.waveData=waveData; 
	}

	
	
	@Override
	public void addDataAnnotation(DataAnnotation dataAnnotation) {
		super.addDataAnnotation(dataAnnotation); 
		/**
		 * This is a total hack to add bearing info from the bearing module 
		 * so that everything displays nicely on the map.
		 */
		if (dataAnnotation instanceof BearingAnnotation) {
			
			BearingAnnotation bearingAnnotation = (BearingAnnotation) dataAnnotation; 
			
			localisation = new DLLocalisation(this, LocContents.HAS_BEARING | LocContents.HAS_AMBIGUITY, 
					PamUtils.getSingleChannel(this.getChannelBitmap())); 

			localisation.setBearing(bearingAnnotation); 
		}
	}
	

	@Override
	public AbstractLocalisation getLocalisation() {
		return this.localisation; 
	}


	@Override
	public double[][] getWaveData() {
		return waveData;
	}
	
	/**
	 * Get the model results that were used to construct the data unit. 
	 * The number of results will generally be the raw data length divided by hop size. 
	 * @return the  model results. 
	 */
	public ArrayList<ModelResult> getModelResults() {
		return modelResults;
	}

	/**
	 * Set the model results. 
	 * @param modelResults - the model results. 
	 */
	public void setModelResults(ArrayList<ModelResult> modelResults) {
		this.modelResults = modelResults;
	}



}
