package rawDeepLearningClassifer.deepLearningClassification;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

/**
 * Holds classified data units from deep learning model. 
 * 
 * @author Jamie Macaulay
 *
 */
public class DLClassifiedDataBlock extends PamDataBlock<DLDataUnit> {

	public DLClassifiedDataBlock(String dataName, PamProcess parentProcess, int channelMap) {
		super(DLDataUnit.class, dataName, parentProcess, channelMap);
		
	}

}
