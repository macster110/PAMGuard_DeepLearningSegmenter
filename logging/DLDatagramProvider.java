package rawDeepLearningClassifer.logging;

import PamguardMVC.PamDataUnit;
import dataGram.DatagramProvider;
import dataGram.DatagramScaleInformation;
import rawDeepLearningClassifer.DLControl;

public class DLDatagramProvider implements DatagramProvider  {
	
	public DLDatagramProvider(DLControl dlControl) {
		
	}

	@Override
	public int getNumDataGramPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addDatagramData(PamDataUnit dataUnit, float[] dataGramLine) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DatagramScaleInformation getScaleInformation() {
		// TODO Auto-generated method stub
		return null;
	}

}
