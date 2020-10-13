package rawDeepLearningClassifer.dataPlotFX;

import clipgenerator.ClipDataBlock;
import dataPlotsFX.data.TDDataInfoFX;
import dataPlotsFX.data.TDDataProviderFX;
import dataPlotsFX.layout.TDGraphFX;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.deepLearningClassification.DLDetectionDataBlock;

/**
 * The DL detection plot provider. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class DLDetectionPlotProvider extends TDDataProviderFX {

	/**
	 * Reference DL control. 
	 */
	private DLControl dlControl;

	/**
	 * The DL detection plot provider. 
	 * @param dlControl - reference to DL control. 
	 * @param dlDetectionDataBlock - the dl detection data block. 
	 */
	public DLDetectionPlotProvider(DLControl dlControl, DLDetectionDataBlock dlDetectionDataBlock) {
		super(dlDetectionDataBlock); 
		this.dlControl = dlControl;
	}

	@Override
	public TDDataInfoFX createDataInfo(TDGraphFX tdGraph) {
		return new DLDetectionPlotInfoFX(this, dlControl, tdGraph, dlControl.getDLClassifyProcess().getDLDetectionDatablock());
	}
}
