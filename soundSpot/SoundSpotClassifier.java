package rawDeepLearningClassifer.soundSpot;

import java.io.Serializable;

import PamController.PamControlledUnitSettings;
import PamController.PamSettings;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.deepLearningClassification.DLClassiferModel;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;
import org.pytorch.Module;

/**
 * A deep learning classifier trained using the OrcaSpot and run natively in Java.
 * <p>
 * This method has numerous advantages, it greatly simplifies the setup, which requires only that
 * the pytorch TorchScript library is installed and that the library location is added to the virtual
 * machine arguments e.g.  
 * -Djava.library.path=/Users/au671271/libtorch/lib 
 * <p>
 * It also means that np python code is called which greatly increases speed. 
 * 
 * @author JamieMacaulay 
 *
 */
public class SoundSpotClassifier implements DLClassiferModel, PamSettings {

	/**
	 * Reference to the control.
	 */
	private DLControl dlControl;

	/**
	 * Sound spot parameters. 
	 */
	private SoundSpotParams soundSpotParmas;

	/**
	 * The user interface for sound spot. 
	 */
	private SoundSpotUI soundSpotUI; 

	/**
	 * The loaded pytorch model 
	 */
	public org.pytorch.Module model; 


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
		try {
			model = Module.load(soundSpotParmas.modelPath);
			
			//TODO
			//need to load the classifier metadata here...
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void closeModel() {
		if (model!=null) {
			model.destroy();
		}

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
		return soundSpotParmas;
	}

	@Override
	public String getUnitName() {
		return dlControl.getUnitName()+"_SoundSpot"; 
	}

	@Override
	public String getUnitType() {
		return dlControl.getUnitType()+"_SoundSpot";
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
		return SoundSpotParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		SoundSpotParams newParameters = (SoundSpotParams) pamControlledUnitSettings.getSettings();
		if (newParameters!=null) {
			soundSpotParmas = newParameters.clone();
		}
		else soundSpotParmas = new SoundSpotParams(); 
		return true;
	}

	/**
	 * Get the sound spot parameters. 
	 * @return sound spot parameters. 
	 */
	public SoundSpotParams getSoundSpotParams() {
		return soundSpotParmas;
	}

	/**
	 * Set the sound spot params. 
	 * @param the params to set 
	 */
	public void setSoundSpotParams(SoundSpotParams clone) {
		this.soundSpotParmas=soundSpotParmas; 
		
	}
}
