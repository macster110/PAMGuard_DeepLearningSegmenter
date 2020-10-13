package rawDeepLearningClassifer.deepLearningClassification;

import java.util.ArrayList;
import java.util.Arrays;

import PamDetection.AbstractLocalisation;
import PamDetection.LocContents;
import PamDetection.PamDetection;
import PamUtils.PamUtils;
import PamUtils.complex.ComplexArray;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;
import PamguardMVC.RawDataHolder;
import annotation.DataAnnotation;
import bearinglocaliser.annotation.BearingAnnotation;
import clickDetector.ClickDetection;
import clickDetector.ClickSpectrogram;
import clipgenerator.ClipSpectrogram;
import fftManager.FastFFT;
import rawDeepLearningClassifer.logging.DLAnnotation;

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

	private double[][] powerSpectra;

	private int currentSpecLen;

	private ComplexArray[] complexSpectrum;

	private ClipSpectrogram dlSpectrogram;


	/**
	 * Create a data unit for DL which has passed binary classification.  
	 * @param timeMilliseconds - the time in milliseconds. 
	 * @param channelBitmap - the channel bit map. 
	 * @param startSample - the start sample. 
	 * @param durationSamples - the duration in samples. 
	 * @param modelResults - the model results that were used to construct the data unit. 
	 */
	@Deprecated
	public DLDetection(long timeMilliseconds, int channelBitmap, long startSample, long durationSamples,
			ArrayList<ModelResult> modelResults, double[][] waveData) {
		super(timeMilliseconds, channelBitmap, startSample, durationSamples);
		DLAnnotation annotation = new DLAnnotation(null, modelResults); 
		this.addDataAnnotation(annotation);
		this.waveData=waveData; 
	}
	
	
	/**
	 * Create a data unit for DL which has passed binary classification. Usually used for 
	 * loading data units from binary files. 
	 * @param baseData - the base binary data. 
	 * @param probdata - the probability data. 
	 * @param waveData - the wave data. 
	 */
	public DLDetection(DataUnitBaseData baseData, double[][] waveData) {
		super(baseData);
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
		DLAnnotation annotation = (DLAnnotation) this. findDataAnnotation(DLAnnotation.class) ;
		if (annotation!=null) return annotation.getModelResults(); 
		else return null; 
	}



	public double[] getPrediction() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Returns the power spectrum for a given channel (square of magnitude of
	 * complex spectrum)
	 * 
	 * @param channel channel number
	 * @param fftLength
	 * @return Power spectrum
	 */
	public synchronized double[] getPowerSpectrum(int channel, int fftLength) {
		if (powerSpectra == null) {
			powerSpectra = new double[PamUtils.getNumChannels(this.getChannelBitmap())][];
		}
		if (fftLength == 0) {
			fftLength = getCurrentSpectrumLength();
		}
		if (fftLength == 0) {
		}
		if (powerSpectra[channel] == null
				|| powerSpectra[channel].length != fftLength / 2) {
			ComplexArray cData = getComplexSpectrumHann(channel, fftLength);
			currentSpecLen = fftLength;
			powerSpectra[channel] = cData.magsq();
			if (powerSpectra==null){
				System.err.println("DLDetection: could not calculate power spectra");
				return null;

			}
			if (powerSpectra[channel].length != fftLength/2) {
				powerSpectra[channel] = Arrays.copyOf(powerSpectra[channel], fftLength/2);
			}
		}
		return powerSpectra[channel];
	}
	
	
	/**
	 * 
	 * Returns the complex spectrum for a given channel using a set FFT length as
	 * getComplexSpectrum, but applies a Hanning window to the raw data first 
	 * 
	 * @param channel - the channel to calculate
	 * @param fftLength - the FFT length to use. 
	 * @return the complex spectrum - the comnplex spectrum of the wave data from the specified channel. 
	 */
	public synchronized ComplexArray getComplexSpectrumHann(int channel, int fftLength) {
		complexSpectrum = new ComplexArray[PamUtils.getNumChannels(this.getChannelBitmap())];
		if (complexSpectrum[channel] == null
				|| complexSpectrum.length != fftLength / 2) {
			
			complexSpectrum[channel] =  getComplexSpectrumHann(getWaveData()[channel], fftLength); 
			currentSpecLen = fftLength;
		}
		return complexSpectrum[channel];
	}
	
	/**
	 * Get the complex spectrum of a waveform. 
	 * @param waveData - the wave data. 
	 * @param fftLength
	 * @return
	 */
	public static ComplexArray getComplexSpectrumHann(double[] waveData, int fftLength) {
		double[] paddedRawData;
		double[] rawData;
		int i, mn;
		ComplexArray complexSpectrum; 
			paddedRawData = new double[fftLength];
			//messy this Hann window function should be in a utility class. 
			rawData = ClickDetection.applyHanningWindow(waveData);
			mn = Math.min(fftLength, waveData.length);
			for (i = 0; i < mn; i++) {
				paddedRawData[i] = rawData[i];
			}
			for (i = mn; i < fftLength; i++) {
				paddedRawData[i] = 0;
			}
			
			FastFFT fastFFT = new FastFFT();

			complexSpectrum= fastFFT.rfft(paddedRawData, fftLength);
		return complexSpectrum;		
	}
	

	/**
	 * Get the wave data fro a specified channel. 
	 * @param channel
	 * @return
	 */
	private double[] getWaveData(int channel) {
		return this.getWaveData()[channel];
	}


	/**
	 * Get the spectrum length
	 * @return the spectrogram length. 
	 */
	private int getCurrentSpectrumLength() {
		if (currentSpecLen<=0) {
			currentSpecLen = PamUtils.getMinFftLength(getSampleDuration());
		}
		return currentSpecLen; 
	}


	
	/**
	 * Get a spectrogram image of the wave clip. The clip is null until called. It is recalculated if the 
	 * FFT length and/or hop size are different. 
	 * @param fftSize - the FFT size in samples
	 * @param fftHop - the FFT hop in samples
	 * @return a spectrogram clip (dB/Hz ).
	 */
	public ClipSpectrogram getSpectrogram(int fftSize, int fftHop) {
		if (dlSpectrogram==null || dlSpectrogram.getFFTHop()!=fftHop || dlSpectrogram.getFFTSize()!=fftSize) {
			dlSpectrogram = new ClipSpectrogram(this); 
			dlSpectrogram.calcSpectrogram(this.getWaveData(), fftSize, fftHop); 
		}
		return dlSpectrogram;
	}



}
