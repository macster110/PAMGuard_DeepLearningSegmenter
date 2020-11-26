package rawDeepLearningClassifer.ddPlotFX;


import detectionPlotFX.data.DDDataInfo;
import detectionPlotFX.layout.DetectionPlotDisplay;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLDetection;

/**
 * Data info for the raw data info. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class RawDLDDDataInfo extends DDDataInfo<DLDetection> {

	
	public RawDLDDDataInfo(RawDLDDPlotProvider rawDLPlotProvider, DLControl dlControl,
			DetectionPlotDisplay displayPlot) {
		super(rawDLPlotProvider,  displayPlot,  dlControl.getDLClassifyProcess().getDLDetectionDatablock());
		
		//add the various click plots
		super.addDetectionPlot(new DLWaveformPlot(displayPlot));
		super.addDetectionPlot(new DLSpectrumPlot(displayPlot));
		super.addDetectionPlot(new DLFFTPlot(displayPlot, displayPlot.getDetectionPlotProjector()));


		super.setCurrentDetectionPlot(0);
	}


}
