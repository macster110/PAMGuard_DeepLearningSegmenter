package rawDeepLearningClassifer.ddPlotFX;

import PamUtils.PamUtils;
import detectionPlotFX.layout.DetectionPlotDisplay;
import detectionPlotFX.plots.FFTPlot;
import detectionPlotFX.projector.DetectionPlotProjector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import rawDeepLearningClassifer.dlClassification.DLDetection;

public class DLFFTPlot extends FFTPlot<DLDetection> {

	public DLFFTPlot(DetectionPlotDisplay displayPlot, DetectionPlotProjector projector) {
		super(displayPlot, projector);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void paintDetections(DLDetection detection, GraphicsContext graphicsContext, Rectangle windowRect,
			DetectionPlotProjector projector) {		
	}


	/**
	 * Load the raw data. This can be overridden if necessary. 
	 * @param dataUnit - the data unit to load
	 * @param padding - the padding. 
	 * @param plotChannel - the plot channel. 
	 */
	public void loadRawData(DLDetection dataUnit, double padding, int plotChannel) {
		//force set the raw data instead of loading from raw wav files. 
		int channelPos = PamUtils.getChannelPos(plotChannel, dataUnit.getChannelBitmap()); 
		
		getRawDataOrder().setRawData(dataUnit.getWaveData()[channelPos], dataUnit.getParentDataBlock().getSampleRate(), 
				plotChannel, dataUnit.getTimeMilliseconds());
	}
}
