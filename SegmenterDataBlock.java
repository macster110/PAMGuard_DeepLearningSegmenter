package rawDeepLearningClassifer;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import rawDeepLearningClassifer.SegmenterProcess.GroupedRawData;

/**
 * Holds raw data segments which will be classified. 
 * 
 * @author Jamie Macaulay
 *
 */
public class SegmenterDataBlock extends PamDataBlock<GroupedRawData> {

	public SegmenterDataBlock(String dataName, PamProcess parentProcess, int channelMap) {
		super(ModelResultDataUnit.class, dataName, parentProcess, channelMap);
		this.setNaturalLifetimeMillis(5000); //do not want to keep the data for very long  - it's raw data segmnents so memory intensive
	}


}
