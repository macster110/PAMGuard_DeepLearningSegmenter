package rawDeepLearningClassifer.offline;

import PamController.PamController;
import PamguardMVC.PamDataUnit;
import dataMap.OfflineDataMapPoint;
import matchedTemplateClassifer.MTClassifierControl;
import offlineProcessing.OfflineTask;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.segmenter.SegmenterProcess;

public class DLOfflineTask extends OfflineTask<PamDataUnit<?,?>>{

	/**
	 * The DL control. 
	 */
	private DLControl dlControl;

	/**
	 * Keep a track of the number of data units processed. 
	 */
	private int count =0; 

	public DLOfflineTask(DLControl dlControl) {
		super(dlControl.getParentDataBlock()); 
		this.dlControl= dlControl; 
		this.dlControl.getDLClassifyProcess().clearOldData();	

		super.addAffectedDataBlock(this.dlControl.getDLClassifyProcess().getDLDetectionDatablock());


	}

	@Override
	public String getName() {
		return "Deep Learning Classification";
	}

	@Override
	public boolean processDataUnit(PamDataUnit<?, ?> dataUnit) {
		//Process a data unit
		dlControl.getSegmenter().newData(dataUnit); 
		return true;
	}

	/**
	 * Called at the start of the thread which executes this task. 
	 */
	@Override
	public void prepareTask() {	
		count=0; 
		prepProcess(); 
		this.setParentDataBlock(dlControl.getParentDataBlock());
		//dlControl.setNotifyProcesses(true);
		this.dlControl.getDLModel().prepModel(); 
		dlControl.update(MTClassifierControl.PROCESSING_START);
		//		System.out.println("Waveform match: " + mtClassifierControl.getMTParams().classifiers.get(0).waveformMatch.toString());
		//		System.out.println("Waveform reject: " + mtClassifierControl.getMTParams().classifiers.get(0).waveformReject.toString());
	}


	/**
	 * Called at the end of the thread which executes this task. 
	 */
	@Override
	public void completeTask() {
		//dlControl.setNotifyProcesses(false);
		this.dlControl.getDLModel().closeModel();                                                                                                            
		dlControl.update(MTClassifierControl.PROCESSING_END);
	}

	@Override
	public void newDataLoad(long startTime, long endTime, OfflineDataMapPoint mapPoint) {
		dlControl.update(MTClassifierControl.PROCESSING_START);
		// called whenever new data is loaded. 
	}

	@Override
	public void loadedDataComplete() {
		// TODO Auto-generated method stub

	}

	/**
	 * task has settings which can be called
	 * @return true or false
	 */
	public boolean hasSettings() {
		return true;
	}

	@Override
	public boolean callSettings() {
		dlControl.showSettingsDialog(PamController.getInstance().getGuiFrameManager().getFrame(0));
		return true;
	}

	/**
	 * Prepare required processes. 
	 */
	private void prepProcess() {
		dlControl.getSegmenter().prepareProcess();
		dlControl.getDLClassifyProcess().prepareProcess();
	}

	/**
	 * can the task be run ? This will generally 
	 * be true, but may be false if the task is dependent on 
	 * some other module which may not be present.  
	 * @return true if it's possible to run the task. 
	 */
	public boolean canRun() {
		prepProcess();
		//had to put this in here for some reason??
		this.setParentDataBlock(dlControl.getParentDataBlock());
		//System.out.println("Datablock: " + getDataBlock() + " Control datablock" +  dlControl.getParentDataBlock()); 
		boolean can = getDataBlock() != null; 
		return can;
	}


}
