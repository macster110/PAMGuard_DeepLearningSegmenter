package rawDeepLearningClassifer.logging;

import PamguardMVC.PamDataUnit;
import dataGram.DatagramProvider;
import dataGram.DatagramScaleInformation;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLDataUnit;

/**
 * Datagram showing the raw model outputs form the classifier. 
 * @author Jamie Macaulay 
 *
 */
public class DLDataUnitDatagram implements DatagramProvider {

	private DLControl dlControl;

	private DatagramScaleInformation scaleInfo;

	public DLDataUnitDatagram(DLControl dlContorl) {
		this.dlControl=dlContorl; 
		scaleInfo = new DatagramScaleInformation(Double.NaN, Double.NaN, "Probability", false, DatagramScaleInformation.PLOT_3D);
	}

	@Override
	public int getNumDataGramPoints() {
		return dlControl.getDLClassifyProcess().getNumClasses(); 
	}

	@Override
	public int addDatagramData(PamDataUnit dataUnit, float[] dataGramLine) {
		DLDataUnit dlDataUnit = (DLDataUnit) dataUnit;
		
		if (dlDataUnit.getModelResult().getPrediction()!=null) {
			for (int i=0; i<dlDataUnit.getModelResult().getPrediction().length; i++) {
				dataGramLine[i] += (float) dlDataUnit.getModelResult().getPrediction()[i]; 
			}
		}

		return 1;
	}

	@Override
	public DatagramScaleInformation getScaleInformation() {
		return scaleInfo;
	}

}
