package rawDeepLearningClassifer.ddPlotFX;

import clickDetector.ClickControl;
import detectionPlotFX.clickDDPlot.ClickDDPlotProvider;
import detectionPlotFX.clickDDPlot.ClickSpectrumPlot;
import detectionPlotFX.clickDDPlot.ClickWaveformPlot;
import detectionPlotFX.clickDDPlot.ClickWignerPlot;
import detectionPlotFX.data.DDDataInfo;
import detectionPlotFX.data.DDDataProvider;
import detectionPlotFX.layout.DetectionPlotDisplay;
import rawDeepLearningClassifer.deepLearningClassification.DLDetection;

/**
 * Data info for the raw data info. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class RawDLDDDataInfo extends DDDataInfo<DLDetection> {

	
	public RawDLDDDataInfo(ClickDDPlotProvider clickDDPlotProvider, ClickControl clickControl,
			DetectionPlotDisplay displayPlot) {
		super( clickDDPlotProvider,  displayPlot,  clickControl.getClickDataBlock());
		
		//add the various click plots
		super.addDetectionPlot(new ClickWaveformPlot(displayPlot));
		super.addDetectionPlot(new ClickWignerPlot(displayPlot));
		super.addDetectionPlot(new ClickSpectrumPlot(displayPlot));


		super.setCurrentDetectionPlot(0);
		
	}


}
