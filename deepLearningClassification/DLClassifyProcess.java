package rawDeepLearningClassifer.deepLearningClassification;

import java.util.ArrayList;

import PamDetection.RawDataUnit;
import PamUtils.PamUtils;
import PamView.PamDetectionOverlayGraphics;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.layoutFX.DLDetectionGraphics;
import rawDeepLearningClassifer.layoutFX.DLGraphics;
import rawDeepLearningClassifer.logging.DLAnnotation;
import rawDeepLearningClassifer.logging.DLAnnotationType;
import rawDeepLearningClassifer.segmenter.SegmenterDataBlock;
import rawDeepLearningClassifer.segmenter.SegmenterProcess.GroupedRawData;

/**
 * The deep learning classification process. This takes a segment of raw data from the segmenter. 
 * and passes it to a deep learning model. 
 * <p>
 * All model results are added to a DLClassifiedDataBlock. This stores the probabilities for each species category. 
 * <p>
 * If a binary classification is true then the raw data from the model data block is saved. 
 * <p>
 * 
 * @author Jamie Macaulay
 *
 */
public class DLClassifyProcess extends PamProcess {

	/**
	 *  Holds all model results but no other information 
	 */
	private DLModelDataBlock dlModelResultDataBlock;

	/**
	 * Reference to the DL control
	 */
	private DLControl dlControl;

	/**
	 * Holds results which have passed a binary classification 
	 */
	private DLClassifiedDataBlock dlClassifiedDataBlock;

	/**
	 * Buffer which holds positive grouped data results to be merged into one data unit. This mirrors modeResultDataBuffer
	 */
	private ArrayList<GroupedRawData> groupDataBuffer = new ArrayList<GroupedRawData>(); 

	/**
	 * Buffer which holds positive model results to be merged into one data unit. This mirrors groupDataBuffer. 
	 */
	private ArrayList<ModelResult> modelResultDataBuffer = new ArrayList<ModelResult>(); 
	
	/**
	 * The DL annotation type. 
	 */
	private DLAnnotationType dlAnnotationType; 


	/**
	 * The last parent data for grouped data. This is used to ensure that DLDetections 
	 * correspond to the raw chunk of data from a parent detectiobn e.g. a click detection. 
	 */
	private PamDataUnit lastParentDataUnit; 


	public DLClassifyProcess(DLControl dlControl, SegmenterDataBlock parentDataBlock) {
		super(dlControl, parentDataBlock);

		this.dlControl = dlControl; 

		//all the deep learning results.
		dlModelResultDataBlock = new DLModelDataBlock("DL Model Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(dlModelResultDataBlock);
		dlModelResultDataBlock.setNaturalLifetimeMillis(600*1000); //keep this data for a while.

		//the classifier deep learning data, 
		dlClassifiedDataBlock = new DLClassifiedDataBlock("DL Classifier Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(dlClassifiedDataBlock);
		dlClassifiedDataBlock.setNaturalLifetimeMillis(600*1000); //keep this data for a while.


		//add custom graphics
		PamDetectionOverlayGraphics overlayGraphics = new DLGraphics(dlModelResultDataBlock);
		overlayGraphics.setDetectionData(true);
		dlModelResultDataBlock.setOverlayDraw(overlayGraphics);

		overlayGraphics = new DLDetectionGraphics(dlClassifiedDataBlock);
		overlayGraphics.setDetectionData(true);
		dlClassifiedDataBlock.setOverlayDraw(overlayGraphics);

		//the process name. 
		setProcessName("Deep Learning Classifier");  
		
		//create an annotations object. 
		dlAnnotationType = new DLAnnotationType(dlControl);
	}

	/**
	 * Data block which holds data units for localisation.
	 * @return the data block which holds data unit for localisation.
	 */	
	public DLClassifiedDataBlock getDlClassifiedLocBlock() {

		return dlClassifiedDataBlock;
	}

	/*
	 * Segments raw data and passes a chunk of multi-channel data to a deep learning algorithms. 
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
			//if the model is null there may be a buffer on a different thread 
			newModelResult(modelResult, rawDataUnit); 
		}
	}

	/**
	 * Create a data unit form a model result. 
	 * @param modelResult - the model result. 
	 * @param pamRawData - the raw data unit which the model result came from. 
	 */
	public void newModelResult(ModelResult modelResult, GroupedRawData pamRawData) {

		//the model result may be null if the classifier uses a new thread. 

		//create a new data unit - allways add to the model result section. 
		DLDataUnit dlDataUnit = new DLDataUnit(pamRawData.getTimeMilliseconds(), pamRawData.getChannelBitmap(), 
				pamRawData.getStartSample(), pamRawData.getSampleDuration(), modelResult); 
		dlDataUnit.setFrequency(new double[] {0, dlControl.getDLClassifyProcess().getSampleRate()/2});
		dlDataUnit.setDurationInMilliseconds(pamRawData.getDurationInMilliseconds()); 
		
		this.dlModelResultDataBlock.addPamData(dlDataUnit);


		/*** 
		 * The are two options here. 
		 * 1) Create a new data unit from the segmented data. This data unit may be made up of multiple segment that all pass binary
		 * classification. 
		 * 2) Annotated an existing data unit with a deep learning annotation. 
		 */
		if (pamRawData.getParentDataUnit() instanceof RawDataUnit) {
			if (dlDataUnit.getModelResult().isBinaryClassification()) {
				//if the model result has a binary classification then it is added to the data buffer unless the data
				//buffer has reached a maximum size. In that case the data is saved. 
				groupDataBuffer.add(pamRawData); 
				modelResultDataBuffer.add(modelResult); 
				if (groupDataBuffer.size()>=dlControl.getDLParams().maxMergeHops) {
					//need to save the data unit and clear the unit. 
					DLDetection dlDetection  = makeDLDetection(groupDataBuffer, modelResultDataBuffer); 
					clearBuffer();
					if (dlDetection!=null) {
						this.dlClassifiedDataBlock.addPamData(dlDetection);
					}
				}

			}
			else {
				if (pamRawData.getParentDataUnit() instanceof RawDataUnit) {
					//no binary classification thus the data unit is complete and buffer must be saved. 
					DLDetection dlDetection  = makeDLDetection(groupDataBuffer, modelResultDataBuffer); 
					clearBuffer() ;
					if (dlDetection!=null) {
						this.dlClassifiedDataBlock.addPamData(dlDetection);
					}
				}
			}
		}
		else {
			//need to go by the parent data unit for merging data, not the 
			if (pamRawData.getParentDataUnit()!=lastParentDataUnit) {
				//save any data
				if (groupDataBuffer.size()>0 && lastParentDataUnit!=null) {
					if (this.dlControl.getDLParams().forceSave) {
						DLDetection dlDetection = makeDLDetection(groupDataBuffer,modelResultDataBuffer);
						clearBuffer();
						if (dlDetection!=null) {
							this.dlClassifiedDataBlock.addPamData(dlDetection);
						}
					}
					else {
						addDLAnnotation(lastParentDataUnit,groupDataBuffer,modelResultDataBuffer); 
						clearBuffer(); 
					}
				}
			}
			lastParentDataUnit=pamRawData.getParentDataUnit();
			groupDataBuffer.add(pamRawData); 
			modelResultDataBuffer.add(modelResult); 
		}
	}

	/**
	 * Make a positive DL detection from a number of model results and corresponding chunks of raw sound data. 
	 * @param groupDataBuffer - the raw data chunks (these may overlap). 
	 * @param modelResult - the model results. 
	 * @return a DL detection with merged raw data. 
	 */
	private synchronized DLDetection makeDLDetection(ArrayList<GroupedRawData> groupDataBuffer, ArrayList<ModelResult> modelResult) {
		
		if (groupDataBuffer==null || groupDataBuffer.size()<=0) {
			return null; 
		}
		
		
		int chans = PamUtils.getNumChannels(groupDataBuffer.get(0).getChannelBitmap());

		//need to merge the raw data chunks- these are overlapping whihc is a bit of pain but should be OK if we only copy in the hop lengths.
		double[][] rawdata = new double[PamUtils.getNumChannels(groupDataBuffer.get(0).getChannelBitmap())][dlControl.getDLParams().sampleHop*groupDataBuffer.size()]; 
		
		//copy all data into a new data buffer making sure to compensate for hop size.  
		for (int i=0; i<groupDataBuffer.size(); i++) {
			for (int j=0; j<chans; j++) {
				System.arraycopy(groupDataBuffer.get(i).getRawData()[j], 0, rawdata[j], i*dlControl.getDLParams().sampleHop, dlControl.getDLParams().sampleHop);
			}
		}

		//create the data unit
		return new DLDetection(groupDataBuffer.get(0).getBasicData().clone(), modelResult, rawdata); 
	}
	
	/**
	 * Clear the data unit buffer. 
	 */
	private void clearBuffer() {
		groupDataBuffer.clear(); 
		modelResultDataBuffer.clear(); 
	}
	
	
	/**
	 * Add a data annotation to an existing data unit from a number of model results and corresponding chunks of raw sound data. 
	 * @param groupDataBuffer - the raw data chunks (these may overlap). 
	 * @param modelResult - the model results. 
	 * @return a DL detection with merged raw data. 
	 */
	private void addDLAnnotation(PamDataUnit parentDataUnit, ArrayList<GroupedRawData> groupDataBuffer, ArrayList<ModelResult> modelResult) {
		parentDataUnit.addDataAnnotation(new DLAnnotation(dlAnnotationType, modelResult)); 
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
	 * Get the data block which contains detectons from the deep learning output. 
	 * @return the data block which holds classified data units
	 */
	public DLModelDataBlock getDLClassifiedDataBlock() {
		return dlModelResultDataBlock; 
	}

	/**
	 * Get the number of classes the model outputs e.g. 
	 * the number of species it can recognise. 
	 * @return the number of classes. 
	 */
	public int getNumClasses() {
		return this.dlControl.getNumClasses(); 
	}

}
