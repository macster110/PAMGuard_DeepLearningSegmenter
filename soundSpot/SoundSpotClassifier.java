package rawDeepLearningClassifer.soundSpot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import PamController.PamControlledUnitSettings;
import PamController.PamSettings;
import PamUtils.PamCalendar;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.deepLearningClassification.DLClassiferModel;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;
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
	private SoundSpotParams soundSpotParmas;


	/**
	 * The deep learning model worker.
	 */
	private SoundSpotWorker orcaSpotWorker;



	public SoundSpotClassifier(DLControl dlControl) {
		this.dlControl=dlControl; 
		this.soundSpotParmas = new SoundSpotParams(); 
		this.soundSpotUI= new SoundSpotUI(this); 
	}

	
	@Override
	public ModelResult runModel(GroupedRawData groupedRawData) {
		/**
		 * If a sound file is being analysed then SoundSpot can go as slwo as it wants. if used in real time
		 * then there is a buffer with a maximum queue size. 
		 */
		if (PamCalendar.isSoundFile()) {
			//run the model 
			SoundSpotResult modelResult = getSpotWorker().runModel(groupedRawData, 0); 
			newResult(modelResult, groupedRawData);
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
	private SoundSpotWorker getSpotWorker() {
		if (orcaSpotWorker==null) {
			orcaSpotWorker.prepModel();
			orcaSpotWorker = new SoundSpotWorker(soundSpotParmas, dlControl.getSegmenter().getSampleRate()); 
			orcaSpotWorker.prepModel(); 
		}
		return orcaSpotWorker; 
	}

	
	public class TaskThread extends Thread {

		private AtomicBoolean run = new AtomicBoolean(true);
		
		TaskThread() {
			super("TaskThread");
			if (orcaSpotWorker == null) {
				//create the daemons etc...
				orcaSpotWorker = new SoundSpotWorker(soundSpotParmas, dlControl.getSegmenter().getSampleRate()); 	
			}
		}

		public void stopTaskThread() {
			run.set(false);  
			//Clean up daemon.
			if (orcaSpotWorker!=null) {
				orcaSpotWorker.closeModel();
			}
			orcaSpotWorker = null; 
		}

		public void run() {
			while (run.get()) {
				//				System.out.println("ORCASPOT THREAD while: " + "The queue size is " + queue.size()); 
				try {
					if (queue.size()>0) {
						//						System.out.println("ORCASPOT THREAD: " + "The queue size is " + queue.size()); 
						GroupedRawData groupedRawData = queue.remove(0);

						long timestart = System.currentTimeMillis(); 

						SoundSpotResult modelResult = getSpotWorker().runModel(groupedRawData, 0); 

						newResult(modelResult, groupedRawData);

						long timeEnd = System.currentTimeMillis(); 

					}
					else {
						//						System.out.println("ORCASPOT THREAD SLEEP: "); ; 
						Thread.sleep(250);
						//						System.out.println("ORCASPOT THREAD DONE: "); ; 
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	private void newResult(SoundSpotResult modelResult, GroupedRawData groupedRawData) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void prepModel() {
		//should load the model. 
		getSpotWorker(); 
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
	 * Set the sound spot parameters. 
	 * @param the params to set 
	 */
	public void setSoundSpotParams(SoundSpotParams soundSpotParmas) {
		this.soundSpotParmas=soundSpotParmas; 
		
	}

	@Override
	public int getNumClasses() {
		return this.soundSpotParmas.numClasses;
	}

}
