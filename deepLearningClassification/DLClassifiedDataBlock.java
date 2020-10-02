package rawDeepLearningClassifer.deepLearningClassification;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

/**
 * Holds classified data units from deep learning model. 
 * 
 * @author Jamie Macaulay
 *
 */
public class DLClassifiedDataBlock extends PamDataBlock<DLDetection> {

	public DLClassifiedDataBlock(String dataName, PamProcess parentProcess, int channelMap) {
		super(DLDetection.class, dataName, parentProcess, channelMap);
		
	}


}
