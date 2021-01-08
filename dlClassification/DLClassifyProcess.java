package rawDeepLearningClassifer.dlClassification;

import java.util.ArrayList;

import PamDetection.RawDataUnit;
import PamUtils.PamUtils;
import PamView.GroupedSourceParameters;
import PamView.PamDetectionOverlayGraphics;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamInstantProcess;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import annotation.DataAnnotationType;
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
	private DLDetectionDataBlock dlDetectionDataBlock;

	/**
	 * Buffer which holds positive grouped data results to be merged into one data unit. This mirrors modeResultDataBuffer
	 */
	private ArrayList<GroupedRawData>[] groupDataBuffer; 

	/**
	 * Buffer which holds positive model results to be merged into one data unit. This mirrors groupDataBuffer. 
	 */
	private ArrayList<ModelResult>[] modelResultDataBuffer; 

	/**
	 * The DL annotation type. 
	 */
	private DLAnnotationType dlAnnotationType; 


	/**
	 * The last parent data for grouped data. This is used to ensure that DLDetections 
	 * correspond to the raw chunk of data from a parent detectiobn e.g. a click detection. 
	 */
	private PamDataUnit[] lastParentDataUnit; 


	public DLClassifyProcess(DLControl dlControl, SegmenterDataBlock parentDataBlock) {
		super(dlControl, parentDataBlock);
		
//		this.setParentDataBlock(parentDataBlock);

		this.dlControl = dlControl; 

		//all the deep learning results.
		dlModelResultDataBlock = new DLModelDataBlock("DL Model Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(dlModelResultDataBlock);
		dlModelResultDataBlock.setNaturalLifetimeMillis(600*1000); //keep this data for a while.

		//the classifier deep learning data, 
		dlDetectionDataBlock = new DLDetectionDataBlock("DL Classifier Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(dlDetectionDataBlock);
		dlDetectionDataBlock.setNaturalLifetimeMillis(600*1000); //keep this data for a while.


		//add custom graphics
		PamDetectionOverlayGraphics overlayGraphics = new DLGraphics(dlModelResultDataBlock);
		overlayGraphics.setDetectionData(true);
		dlModelResultDataBlock.setOverlayDraw(overlayGraphics);

		overlayGraphics = new DLDetectionGraphics(dlDetectionDataBlock);
		overlayGraphics.setDetectionData(true);
		dlDetectionDataBlock.setOverlayDraw(overlayGraphics);

		//the process name. 
		setProcessName("Deep Learning Classifier");  

		//create an annotations object. 
		dlAnnotationType = new DLAnnotationType(dlControl);
	}

	/**
	 * Setup the classification process. 
	 */
	private void setupClassifierProcess() {

		if (dlControl.getDLParams()==null) {
			System.err.println("SegmenterProcess.setupSegmenter: The DLParams are null???");
		}

		if (dlControl.getDLParams().groupedSourceParams==null) {
			dlControl.getDLParams().groupedSourceParams = new GroupedSourceParameters(); 
			System.err.println("Raw Deep Learning Classifier: The grouped source parameters were null. A new instance has been created: Possible de-serialization error.");
		}

		int[] chanGroups = dlControl.getDLParams().groupedSourceParams.getChannelGroups();

		//initialise an array of nulls. 
		if (chanGroups!=null) {
			groupDataBuffer = new  ArrayList[chanGroups.length] ; 
			modelResultDataBuffer = new  ArrayList[chanGroups.length] ; 
			lastParentDataUnit = new PamDataUnit[chanGroups.length]; 
			for (int i =0; i<chanGroups.length; i++) {
				groupDataBuffer[i] = new ArrayList<GroupedRawData>();
				modelResultDataBuffer[i] = new ArrayList<ModelResult>(); 
			}
		}

	}


	@Override
	public void prepareProcess() {
		setupClassifierProcess();
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
	
		GroupedRawData rawDataUnit = (GroupedRawData) pamRawData;

//		System.out.println("New raw data in: chan: " + PamUtils.getSingleChannel(pamRawData.getChannelBitmap()) + 
//				" Size: " +  pamRawData.getSampleDuration() + " first sample: " + rawDataUnit.getRawData()[0][0]); 
		
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

		this.dlModelResultDataBlock.addPamData(dlDataUnit); //here

		//need to implement multiple groups. 
		for (int i=0; i<getSourceParams().countChannelGroups(); i++) {

			//			System.out.println("RawDataIn: chan: " + pamRawData.getChannelBitmap()+ "  " +
			//			PamUtils.hasChannel(getSourceParams().getGroupChannels(i), pamRawData.getChannelBitmap()) + 
			//			" grouped source: " +getSourceParams().getGroupChannels(i)); 

			if (PamUtils.hasChannel(getSourceParams().getGroupChannels(i), PamUtils.getSingleChannel(pamRawData.getChannelBitmap()))) {

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
						groupDataBuffer[i].add(pamRawData); 
						modelResultDataBuffer[i].add(modelResult); 
						if (groupDataBuffer[i].size()>=dlControl.getDLParams().maxMergeHops) {
							//need to save the data unit and clear the unit. 
							DLDetection dlDetection  = makeDLDetection(groupDataBuffer[i], modelResultDataBuffer[i]); 
							clearBuffer(i);
							if (dlDetection!=null) {
								this.dlDetectionDataBlock.addPamData(dlDetection);

							}
						}

					}
					else {
						//no binary classification thus the data unit is complete and buffer must be saved. 
						DLDetection dlDetection  = makeDLDetection(groupDataBuffer[i], modelResultDataBuffer[i]); 
						clearBuffer(i) ;
						if (dlDetection!=null) {
							this.dlDetectionDataBlock.addPamData(dlDetection);
							System.out.println("Amplitude: " + dlDetection.getAmplitudeDB()  + "  " + dlDetection.getMeasuredAmplitudeType());
						}

					}
				}
				else {
					//need to go by the parent data unit for merging data not the segmentds. 
					//System.out.println("Save click annotation 0 "); 

					if (pamRawData.getParentDataUnit()!=lastParentDataUnit[i]) {
						//save any data
						if (groupDataBuffer[i].size()>0 && lastParentDataUnit[i]!=null) {
							//System.out.println("Save click annotation 1 "); 
							if (this.dlControl.getDLParams().forceSave) {
								DLDetection dlDetection = makeDLDetection(groupDataBuffer[i],modelResultDataBuffer[i]);
								clearBuffer(i);
								if (dlDetection!=null) {
									this.dlDetectionDataBlock.addPamData(dlDetection);
									
								}
							}
							else {
								addDLAnnotation(lastParentDataUnit[i],groupDataBuffer[i],modelResultDataBuffer[i]); 
								clearBuffer(i); 
							}
						}
					}
					lastParentDataUnit[i]=pamRawData.getParentDataUnit();
					groupDataBuffer[i].add(pamRawData); 
					modelResultDataBuffer[i].add(modelResult); 
				}
			}
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

		DataUnitBaseData basicData  = groupDataBuffer.get(0).getBasicData().clone(); 
		basicData.setMillisecondDuration(1000.*groupDataBuffer.size()*dlControl.getDLParams().sampleHop/this.sampleRate);
		basicData.setSampleDuration((long) (groupDataBuffer.size()*dlControl.getDLParams().sampleHop));
		
		//		System.out.println("Model result: " + modelResult.size()); 
		DLDetection dlDetection = new DLDetection(basicData, rawdata); 
		addDLAnnotation(dlDetection, groupDataBuffer,modelResult); 

		//create the data unit
		return dlDetection; 
	}


	/**
	 * Clear the data unit buffer. 
	 */
	private void clearBuffer(int group) {
		//do not clear because the arrays are referenced by other data. Prevent
		//the need to clone the arrays
		groupDataBuffer[group] = new ArrayList<GroupedRawData>();
		modelResultDataBuffer[group] = new ArrayList<ModelResult>(); 
		//		groupDataBuffer[group].clear(); 
		//		modelResultDataBuffer[group].clear(); 
	}


	/**
	 * Add a data annotation to an existing data unit from a number of model results and corresponding chunks of raw sound data. 
	 * @param groupDataBuffer - the raw data chunks (these may overlap). 
	 * @param modelResult - the model results. 
	 * @return a DL detection with merged raw data. 
	 */
	private void addDLAnnotation(PamDataUnit parentDataUnit, ArrayList<GroupedRawData> groupDataBuffer, ArrayList<ModelResult> modelResult) {
		
		//System.out.println("DLClassifyProces: Add annnotation to  " + parentDataUnit); 

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
	 * Get the data block which contains all results from the deep learning output. 
	 * @return the data block which holds results output form the deep learning classifier. 
	 */
	public DLModelDataBlock getDLResultDataBlock() {
		return dlModelResultDataBlock; 
	}

	/**
	 * Get the data block which contains detections from the deep learning output. 
	 * @return the data block which holds classified data units
	 */
	public DLDetectionDataBlock getDLDetectionDatablock() {
		return this.dlDetectionDataBlock; 
	}

	/**
	 * Get the number of classes the model outputs e.g. 
	 * the number of species it can recognise. 
	 * @return the number of classes. 
	 */
	public int getNumClasses() {
		return this.dlControl.getNumClasses(); 
	}


	/**
	 * Convenience function to get grouped source parameters. 
	 * @return the grouped source parameters. 
	 */
	private GroupedSourceParameters getSourceParams() {
		return dlControl.getDLParams().groupedSourceParams; 
	}

	/**
	 * Get the DL annotation type. This handles adding annotations to data units 
	 * with the deep learning results. 
	 * @return the annotation type. 
	 */
	public DLAnnotationType getDLAnnotionType() {
		return dlAnnotationType;
	}

}
