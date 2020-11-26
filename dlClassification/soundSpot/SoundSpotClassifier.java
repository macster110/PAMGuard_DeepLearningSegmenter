package rawDeepLearningClassifer.dlClassification.soundSpot;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jamdev.jtorch4pam.SoundSpot.SoundSpotParams;
import org.jamdev.jtorch4pam.transforms.DLTransform;
import org.jamdev.jtorch4pam.transforms.DLTransform.DLTransformType;
import org.jamdev.jtorch4pam.transforms.FreqTransform;
import org.jamdev.jtorch4pam.transforms.WaveTransform;

import PamController.PamControlledUnitSettings;
import PamController.PamSettings;
import PamUtils.PamCalendar;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLClassiferModel;
import rawDeepLearningClassifer.dlClassification.ModelResult;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;

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
	 * The maximum queue size. 
	 */
	private int MAX_QUEUE_SIZE = 10; 

	/**
	 * Reference to the control.
	 */
	private DLControl dlControl;


	/**
	 * The user interface for sound spot. 
	 */
	private SoundSpotUI soundSpotUI; 

	
	/**
	 * Holds a list of segmented raw data units which need to be classified. 
	 */
	private List<GroupedRawData> queue = Collections.synchronizedList(new ArrayList<GroupedRawData>());

	/**
	 * Sound spot parameters. 
	 */
	private PamSoundSpotParams soundSpotParmas;


	/**
	 * The deep learning model worker.
	 */
	private SoundSpotWorker soundSpotWorker;
	
	/**
	 * True to force the classifier to use a queue - used for simulating real time operation. 
	 */
	private boolean forceQueue = false; 




	public SoundSpotClassifier(DLControl dlControl) {
		this.dlControl=dlControl; 
		this.soundSpotParmas = new PamSoundSpotParams(); 
		this.soundSpotUI= new SoundSpotUI(this); 
	}

	
	@Override
	public ModelResult runModel(GroupedRawData groupedRawData) {
		/**
		 * If a sound file is being analysed then SoundSpot can go as slow as it wants. if used in real time
		 * then there is a buffer with a maximum queue size. 
		 */
		if (PamCalendar.isSoundFile() && !forceQueue) {
			//run the model 
			SoundSpotResult modelResult = getSoundSpotWorker().runModel(groupedRawData, 0); 
			return modelResult; //returns to the classifier. 
		}
		else {
			//add to a buffer if in real time. 
			if (queue.size()>MAX_QUEUE_SIZE) {
				//we are not doing well - clear the buffer
				queue.clear();
			}
			queue.add(groupedRawData);
		}
		return null;
	}
	
	
	/**
	 * Get the sound spot worker. 
	 * @returnthe sound spot worker. 
	 */
	SoundSpotWorker getSoundSpotWorker() {
		if (soundSpotWorker==null) {
			soundSpotWorker = new SoundSpotWorker(); 
		}
		return soundSpotWorker; 
	}

	
	public class TaskThread extends Thread {

		private AtomicBoolean run = new AtomicBoolean(true);
		
		TaskThread() {
			super("TaskThread");
			if (soundSpotWorker == null) {
				//create the daemons etc...
				soundSpotWorker = new SoundSpotWorker(); 	
			}
		}

		public void stopTaskThread() {
			run.set(false);  
			//Clean up daemon.
			if (soundSpotWorker!=null) {
				soundSpotWorker.closeModel();
			}
			soundSpotWorker = null; 
		}

		public void run() {
			while (run.get()) {
				//				System.out.println("ORCASPOT THREAD while: " + "The queue size is " + queue.size()); 
				try {
					if (queue.size()>0) {
						//						System.out.println("ORCASPOT THREAD: " + "The queue size is " + queue.size()); 
						GroupedRawData groupedRawData = queue.remove(0);


						SoundSpotResult modelResult = getSoundSpotWorker().runModel(groupedRawData, 0); 

						newResult(modelResult, groupedRawData);

					}
					else {
						//						System.out.println("ORCASPOT THREAD SLEEP: "); ; 
						Thread.sleep(10);
						//						System.out.println("ORCASPOT THREAD DONE: "); ; 
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	/**
	 * Send a new result form the thread queue to the process. 
	 * @param modelResult - the model result;
	 * @param groupedRawData - the grouped raw data. 
	 */
	private void newResult(SoundSpotResult modelResult, GroupedRawData groupedRawData) {
		this.dlControl.getDLClassifyProcess().newModelResult(modelResult, groupedRawData);
		
	}
	

	@Override
	public void prepModel() {
		getSoundSpotWorker().prepModel(soundSpotParmas);
		if (!soundSpotParmas.useDefaultTransfroms) {
			
		}
	}

	@Override
	public void closeModel() {
		

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
			soundSpotParmas = new PamSoundSpotParams(); 
		}
		return soundSpotParmas;

	}

	@Override
	public long getSettingsVersion() {
		return PamSoundSpotParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		PamSoundSpotParams newParameters = (PamSoundSpotParams) pamControlledUnitSettings.getSettings();
		if (newParameters!=null) {
			soundSpotParmas = newParameters.clone();
		}
		else soundSpotParmas = new PamSoundSpotParams(); 
		return true;
	}

	/**
	 * Get the sound spot parameters. 
	 * @return sound spot parameters. 
	 */
	public PamSoundSpotParams getSoundSpotParams() {
		return soundSpotParmas;
	}

	/**
	 * Set the sound spot parameters. 
	 * @param the params to set 
	 */
	public void setSoundSpotParams(PamSoundSpotParams soundSpotParmas) {
		this.soundSpotParmas=soundSpotParmas; 
		
	}

	@Override
	public int getNumClasses() {
		return this.soundSpotParmas.numClasses;
	}


	public void newModelSelected(File file) {
		// TODO Auto-generated method stub
		
	}

}
