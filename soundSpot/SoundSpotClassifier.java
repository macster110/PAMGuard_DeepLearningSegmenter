package rawDeepLearningClassifer.soundSpot;

import java.io.Serializable;

import PamController.PamControlledUnitSettings;
import PamController.PamSettings;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.deepLearningClassification.DLClassiferModel;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;


/**
 * A deep learning classifier trained using the OrcaSpot and run natively in Java.
 * <p>
 * This method has numerous advanatges, it greatly simplifies the setup, which requires only that
 * the pytorch TorchScript library is installed and that the library location is added to the virtual
 * machine arguments e.g.  
 * -Djava.library.path=/Users/au671271/libtorch/lib 
 * <p>
 * It also means that np python code is called which greatly increses speed. 
 * 
 * @author JamieMacaulay 
 *
 */
public class SoundSpotClassifier implements DLClassiferModel, PamSettings {

	/**
	 * Reference to the control.
	 */
	private DLControl dlControl;
		
	private SoundSpotParams soundSpotParmas;
	
	private SoundSpotUI soundSpotUI; 

	public SoundSpotClassifier(DLControl dlControl) {
		this.dlControl=dlControl; 
		this.soundSpotParmas = new SoundSpotParams(); 
		this.soundSpotUI= new SoundSpotUI(this); 
	}

	@Override
	public ModelResult runModel(GroupedRawData rawDataUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepModel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeModel() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "SoundSpot";
	}

	@Override
	public DLCLassiferModelUI getModelUI() {
		return soundSpotUI;
	}

	@Override
	public Serializable getDLModelSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnitName() {
		return dlControl.getUnitName()+"_OrcaSpot"; 
	}

	@Override
	public String getUnitType() {
		return dlControl.getUnitType()+"_OrcaSpot";
	}

	@Override
	public Serializable getSettingsReference() {
		if (soundSpotParmas==null) {
			soundSpotParmas = new SoundSpotParams(); 
		}
		return soundSpotParmas;

	}

	@Override
	public long getSettingsVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		// TODO Auto-generated method stub
		return false;
	}
}
