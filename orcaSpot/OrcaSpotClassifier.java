package rawDeepLearningClassifer.orcaSpot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.deepLearningClassification.DLClassiferModel;
import rawDeepLearningClassifer.deepLearningClassification.DLDataUnit;
import rawDeepLearningClassifer.deepLearningClassification.ModelResult;
import rawDeepLearningClassifer.layoutFX.DLCLassiferModelUI;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;

/**
 * Calls python.exe to run a python script and then returns a result. 
 * 
 * @author Jamie Macaulay
 *
 */
public class OrcaSpotClassifier implements DLClassiferModel, PamSettings {

	/**
	 * The maximum allowed queue size;
	 */
	public final static int MAX_QUEUE_SIZE = 10 ; 
	/**
	 * The data model control 
	 */
	private DLControl dlControl;

	/**
	 * The OrcaSpot worker that does all the heavy lifting. 
	 */
	//	private OrcaSpotWorkerExe2 orcaSpotWorker;

	/**
	 * The parameters for the OrcaSpot classifier. 
	 */
	private OrcaSpotParams2 orcaSpotParams;

	/**
	 * User interface components for the OrcaSpot classifier. 
	 */
	private OrcaSpotClassifierUI orcaSpotUI;

	/**
	 * The last prediciton
	 */
	private OrcaSpotModelResult lastPrediction; 

	private static final int THREAD_POOL_SIZE = 1; //keep this for now

	/**
	 * Hiolds a list of segmeneted raw data units which need to be classified. 
	 */
	private List<GroupedRawData> queue = Collections.synchronizedList(new ArrayList<GroupedRawData>());

	/**
	 * The current task thread.
	 */
	private TaskThread workerThread;


	public OrcaSpotClassifier(DLControl dlControl) {
		this.dlControl = dlControl; 
		orcaSpotParams = new OrcaSpotParams2(); 
		this.orcaSpotUI = new OrcaSpotClassifierUI(this); ;
		//load the previous settings
		PamSettingManager.getInstance().registerSettings(this);	}

	@Override
	public String getName() {
		return "OrcaSpot";
	}

	@Override
	public DLCLassiferModelUI getModelUI() {
		return orcaSpotUI;
	}

	@Override
	public Serializable getDLModelSettings() {
		return orcaSpotParams;
	}

	@Override
	public ModelResult runModel(GroupedRawData rawDataUnit) {

		if (queue.size()>MAX_QUEUE_SIZE) {
			//we are not doing well - clear the buffer
			queue.clear();
		}
		queue.add(rawDataUnit);
		
		
		System.out.println("ORCASPOT CLASSIFIER: " + "Add data unit " + queue.size()); 

		this.orcaSpotUI.notifyUpdate(-1);

		return null; 
	}


	@Override
	public void prepModel() {
		//make sure the paramters have the right sequence length set up. 
		double windowSize = dlControl.getDLParams().rawSampleSize/dlControl.getSegmenter().getSampleRate(); 
		orcaSpotParams.seq_len = String.valueOf(windowSize);
		orcaSpotParams.hop_size  = String.valueOf(windowSize); 


		if (workerThread!=null) {
			workerThread.stopTaskThread();
		}

		workerThread = new TaskThread();
		workerThread.start();
	}

	/**
	 * Get OrcaSpot params
	 * @return the orca spot params. 
	 */
	public OrcaSpotParams2 getOrcaSpotParams() {
		return this.orcaSpotParams;
	}

	/**
	 * Set the OrcaSpot parameters. 
	 * @param orcaSpotParams2 - the new parameters to set. 
	 */
	public void setOrcaSpotParams(OrcaSpotParams2 orcaSpotParams2) {
		this.orcaSpotParams = orcaSpotParams2;
	}
	

	/**
	 * Called whenever there is a new result. 
	 * @param modelResult - the new model result.
	 * @param groupedRawData - the raw data from which the result was calculated.
	 */
	private void newOrcaSpotResult(OrcaSpotModelResult modelResult, GroupedRawData groupedRawData) {

		this.orcaSpotUI.notifyUpdate(-1);
		
		
		//check whether to set binary classification to true - mainly for graphics and downstream filtering of data. 
		modelResult.setBinaryClassification(modelResult.getPrediction()>Double.valueOf(this.orcaSpotParams.threshold));
		
		//the result is added later to the data block - we are dumping the raw sound data here. 
		DLDataUnit dlDataUnit = new DLDataUnit(groupedRawData.getTimeMilliseconds(), groupedRawData.getChannelBitmap(), groupedRawData.getStartSample(),
				groupedRawData.getSampleDuration(), modelResult); 
		//send the raw data unit off to be classified!
		dlDataUnit.setFrequency(new double[] {0, dlControl.getDLClassifyProcess().getSampleRate()/2});
		dlDataUnit.setDurationInMilliseconds(groupedRawData.getDurationInMilliseconds()); 
		
		dlControl.getDLClassifyProcess().getDLClassifiedDataBlock().addPamData(dlDataUnit);
		
		groupedRawData= null; //just in case 
		
	
	}


	public class TaskThread extends Thread {

		private AtomicBoolean run = new AtomicBoolean(true);
		private OrcaSpotWorkerExe2 orcaSpotWorker;

		TaskThread() {
			super("TaskThread");
		}

		public void stopTaskThread() {
			run.set(false);  
			//Clean up daemon.
			if (orcaSpotWorker!=null) {
				orcaSpotWorker.closeOrcaSpotWorker();
			}
			orcaSpotWorker = null; 
		}

		public void run() {
			while (run.get()) {
//				System.out.println("ORCASPOT THREAD while: " + "The queue size is " + queue.size()); 

				try {
					if (orcaSpotWorker == null) {
						//create the daemons etc...
						this.orcaSpotWorker = new OrcaSpotWorkerExe2(orcaSpotParams); 	
					}

					if (queue.size()>0) {
//						System.out.println("ORCASPOT THREAD: " + "The queue size is " + queue.size()); 
						GroupedRawData groupedRawData = queue.remove(0);
						double[] data = groupedRawData.getRawData()[0]; 

						long timestart = System.currentTimeMillis(); 

						OrcaSpotModelResult modelResult = orcaSpotWorker.runOrcaSpot(data);
						
						newOrcaSpotResult(modelResult, groupedRawData);
						
						lastPrediction = modelResult; 

						long timeEnd = System.currentTimeMillis(); 

						System.out.println("ORCASPOT THREAD: " + "Time to run OrcaSpot: " + (timeEnd-timestart)); 
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

	@Override
	public void closeModel() {
		if (workerThread!=null) {
			workerThread.stopTaskThread();
		}
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
		if (orcaSpotParams==null) {
			orcaSpotParams = new OrcaSpotParams2(); 
		}
		return orcaSpotParams;

	}

	@Override
	public long getSettingsVersion() {
		return OrcaSpotParams2.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		OrcaSpotParams2 newParameters = (OrcaSpotParams2) pamControlledUnitSettings.getSettings();
		if (newParameters!=null) {
			orcaSpotParams = newParameters.clone();
		}
		else orcaSpotParams = new OrcaSpotParams2(); 
		return true;
	}

	/**
	 * Get the current number of raw data units in the queue. 
	 * @return the number of dtaa units in the queue. 
	 */
	public int getRawDataQueue() {
		return this.queue.size();
	}

	/**
	 * Get the last prediction. Convenience function as this could be 
	 * acquired from the data block. 
	 * @return the last prediction. 
	 */
	public OrcaSpotModelResult getLastPrediction() {
		return lastPrediction;
	}

}