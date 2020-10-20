package rawDeepLearningClassifer.ddPlotFX;

import PamUtils.PamUtils;
import clickDetector.ClickDetection;
import detectionPlotFX.layout.DetectionPlotDisplay;
import detectionPlotFX.plots.WaveformPlot;
import rawDeepLearningClassifer.deepLearningClassification.DLDetection;

/**
 * Plot a click waveform. 
 * @author Jamie Macaulay
 *
 */
public class DLWaveformPlot extends WaveformPlot<DLDetection>{

	public DLWaveformPlot(DetectionPlotDisplay detectionPlotDisplay) {
		super(detectionPlotDisplay);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double[][] getWaveform(DLDetection pamDetection) {
		if (pamDetection==null) return null; 
		if (super.getWaveformPlotParams().showFilteredWaveform) {
//			System.out.println("Get filterred waveform. " + super.getWaveformPlotParams().waveformFilterParams.highPassFreq + 
//					" " + super.getWaveformPlotParams().waveformFilterParams.lowPassFreq);
			//seems crazy but have to clone here. The getFilteredWaceData function comapres the waveformfilterparams to
			//see if it was the same as the last filter params. As the reference is the same it always is?...so have 
			//to clone. 
			return pamDetection.getWaveData();		
		}
		else {
//			System.out.println("Get normal waveform. ");
			return pamDetection.getWaveData();		
		}	
	}

	@Override
	public String getName() {
		return "Click Waveform";
	}
	
	@Override
	public double[][] getEnvelope(DLDetection pamDetection) {
		
		if (pamDetection == null) {
			return null;
		}
		
		int nchan=PamUtils.getNumChannels(pamDetection.getChannelBitmap());
		double[][] hilbertTransformAll=new double[nchan][];
		for (int i=0; i<PamUtils.getNumChannels(pamDetection.getChannelBitmap()); i++){
			double[] hilbertTransform;
			if (super.getWaveformPlotParams().showFilteredWaveform) {
				hilbertTransform= pamDetection.getRawDataTransforms().getFilteredAnalyticWaveform(super.getWaveformPlotParams().waveformFilterParams, i);
			}
			else {
				hilbertTransform=pamDetection.getRawDataTransforms().getAnalyticWaveform(i);
			}
			hilbertTransformAll[i]=hilbertTransform;
		}
		return hilbertTransformAll;
	}

}