package rawDeepLearningClassifer;

import PamDetection.RawDataUnit;
import PamView.PamDetectionOverlayGraphics;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import rawDeepLearningClassifer.SegmenterProcess.GroupedRawData;
import rawDeepLearningClassifer.layoutFX.DLGraphics;

/**
 * The deep learning classification process
 * @author Jamie Macaulay
 *
 */
public class DLClassifyProcess extends PamProcess {

	/**
	 * The classified data block. 
	 */
	private DLClassifiedDataBlock dlClassifiedDataBlock;
	
	/**
	 * Reference to the DL control
	 */
	private DLControl dlControl;

	
	public DLClassifyProcess(DLControl dlControl, SegmenterDataBlock parentDataBlock) {
		super(dlControl, parentDataBlock);
		
		this.dlControl = dlControl; 

		//the deep learning results.
		dlClassifiedDataBlock = new DLClassifiedDataBlock("DL Classified Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(dlClassifiedDataBlock);
		dlClassifiedDataBlock.setNaturalLifetimeMillis(600*1000); //keep this data for a while.

		
		//add custom graphics
		PamDetectionOverlayGraphics overlayGraphics = new DLGraphics(dlClassifiedDataBlock);
		overlayGraphics.setDetectionData(true);
		dlClassifiedDataBlock.setOverlayDraw(overlayGraphics);

		//the process name. 
		setProcessName("Deep Learning Classifier");  
		
	
	}
	
	/*
	 * Segments raw data and passes a chunk of multi channle data to a depp leanring algorithms. 
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 *      Gets blocks of raw audio data (single channel), blocks it up into
	 *      fftSize blocks dealing with overlaps as appropriate. fft's each
	 *      complete block and sends it off to the output PamDataBlock, which
	 *      will in turn notify any subscribing processes and views
	 */
	@Override
	public void newData(PamObservable obs, PamDataUnit pamRawData) {

		//the raw data units should appear in sequential channel order  
		//		System.out.println("New raw data in: chan: " + PamUtils.getSingleChannel(pamRawData.getChannelBitmap()) + " Size: " +  pamRawData.getSampleDuration()); 

		GroupedRawData rawDataUnit = (GroupedRawData) pamRawData;

		//run the deep learning algorithm 
		ModelResult modelResult = this.dlControl.getDLModel().runModel(rawDataUnit); 

		if (modelResult!=null) {
			//the model result may be null if the classifier uses a new thread. 

			//create a new data unit
			DLDataUnit dlDataUnit = new DLDataUnit(pamRawData.getTimeMilliseconds(), pamRawData.getChannelBitmap(), pamRawData.getStartSample(),
					pamRawData.getSampleDuration(), modelResult); 
			this.dlClassifiedDataBlock.addPamData(dlDataUnit);
		}
	}


	@Override
	public void pamStart() {
		// TODO Auto-generated method stub
		this.dlControl.getDLModel().prepModel(); 
	}

	@Override
	public void pamStop() {
		this.dlControl.getDLModel().closeModel(); 
		
	}

	/**
	 * The deep leanring classifed data block
	 * @return the data block which holds classified data units
	 */
	public DLClassifiedDataBlock getDLClassifiedDataBlock() {
		return dlClassifiedDataBlock; 
	}

}
