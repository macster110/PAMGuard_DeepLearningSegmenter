package rawDeepLearningClassifer.ddPlotFX;

import detectionPlotFX.layout.DetectionPlotDisplay;
import detectionPlotFX.plots.SpectrumPlot;
import rawDeepLearningClassifer.deepLearningClassification.DLDetection;

/**
 * Spectrum plot for raw data holders. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class DLSpectrumPlot extends SpectrumPlot<DLDetection> {

	public DLSpectrumPlot(DetectionPlotDisplay detectionPlotDisplay) {
		super(detectionPlotDisplay);
	}

	@Override
	public double[][] getPowerSpectrum(DLDetection data) {
		return data.getPowerSpectrum(data.getWaveData()[0].length); 
	}

	@Override
	public double[][] getCepstrum(DLDetection data) {
		return null; 
		//return data.getRawDataTransforms().getCepstrum(channel, cepLength)
	}


}
