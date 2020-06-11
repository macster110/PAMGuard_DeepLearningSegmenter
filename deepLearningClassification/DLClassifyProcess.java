package rawDeepLearningClassifer.deepLearningClassification;

import PamView.PamDetectionOverlayGraphics;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.layoutFX.DLGraphics;
import rawDeepLearningClassifer.segmenter.SegmenterDataBlock;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;

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

	private DLClassifiedDataBlock dlClassifiedLocBlock;

	
	public DLClassifyProcess(DLControl dlControl, SegmenterDataBlock parentDataBlock) {
		super(dlControl, parentDataBlock);
		
		this.dlControl = dlControl; 

		//the deep learning results.
		dlClassifiedDataBlock = new DLClassifiedDataBlock("DL Classified Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(dlClassifiedDataBlock);
		dlClassifiedDataBlock.setNaturalLifetimeMillis(600*1000); //keep this data for a while.
		
		
		dlClassifiedLocBlock = new DLClassifiedDataBlock("DL Localised Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(dlClassifiedLocBlock);
		dlClassifiedLocBlock.setNaturalLifetimeMillis(600*1000); //keep this data for a while.

		
		//add custom graphics
		PamDetectionOverlayGraphics overlayGraphics = new DLGraphics(dlClassifiedDataBlock);
		overlayGraphics.setDetectionData(true);
		dlClassifiedDataBlock.setOverlayDraw(overlayGraphics);
		
		overlayGraphics = new DLGraphics(dlClassifiedLocBlock);
		overlayGraphics.setDetectionData(true);
		dlClassifiedLocBlock.setOverlayDraw(overlayGraphics);

		//the process name. 
		setProcessName("Deep Learning Classifier");  
	}
	
	/**
	 * Datablock which holds data units for loclaisation
	 * @return the datablock which holds data unit for loclaisation
	 */	public DLClassifiedDataBlock getDlClassifiedLocBlock() {

		return dlClassifiedLocBlock;
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
			
			if (dlDataUnit.getModelResult().isBinaryClassification()) {
				//send off to localised datablock 
				this.dlClassifiedLocBlock.addPamData(dlDataUnit);
			}
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
