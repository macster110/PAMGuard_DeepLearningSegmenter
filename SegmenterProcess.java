package rawDeepLearningClassifer;

import java.awt.Color;

import PamController.PamController;
import PamDetection.PamDetection;
import PamDetection.RawDataUnit;
import PamUtils.PamCalendar;
import PamUtils.PamUtils;
import PamView.GroupedSourceParameters;
import PamView.PamDetectionOverlayGraphics;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import PamguardMVC.PamRawDataBlock;


/**
 * Acquires raw sound data and then sends off to a deep learning classifier.
 * 
 * @author Jamie Macaulay 
 */

public class SegmenterProcess extends PamProcess {

	/**
	 * Reference to the deep learning control. 
	 */
	private DLControl dlControl;



	/**
	 * Holds data units which have been successfully classified by the deep learning algorithm.
	 */
	private DLClassifiedDataBlock classifiedOutput; 

	/**
	 * The buffer.
	 */
	private double[] buffer;

	/**
	 * The current raw data unit chunks
	 */
	private GroupedRawData[] currentRawChunks;

	/**
	 * The current raw data unit chunks
	 */
	private GroupedRawData[] nextRawChunks;


	/**
	 * Holds segments of raw sound data
	 */
	private SegmenterDataBlock segmenterDataBlock;

	PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_DIAMOND, 10, 12, false,
			Color.CYAN, Color.CYAN); 


	public SegmenterProcess(DLControl pamControlledUnit, PamDataBlock parentDataBlock) {
		super(pamControlledUnit, parentDataBlock);
		dlControl = pamControlledUnit;

		//sourceDataBlock.addObserver(this);

		setParentDataBlock(parentDataBlock);

		//			AddOutputDataBlock(outputData = new RecyclingDataBlock(PamguardMVC.DataType.FFT, "Raw FFT Data", 
		//					this, sourceDataBlock.getSourceProcess(), fftControl.fftParameters.channelMap));
		//			addOutputDataBlock(outputData = new RecyclingDataBlock<FFTDataUnit>(FFTDataUnit.class, "Raw FFT Data", 
		//					this, fftControl.fftParameters.channelMap));

		segmenterDataBlock = new SegmenterDataBlock("Segmented Raw Data", this, dlControl.getDLParams().groupedSourceParams.getChanOrSeqBitmap());
		addOutputDataBlock(segmenterDataBlock);

		setProcessName("Segmenter");  

		//allow drawing of the segmenter on the spectrogram
		PamDetectionOverlayGraphics overlayGraphics = new PamDetectionOverlayGraphics(segmenterDataBlock,  defaultSymbol);
		overlayGraphics.setDetectionData(true);
		segmenterDataBlock.setOverlayDraw(overlayGraphics);

		setupSegmenter();
	}


	@Override
	public void prepareProcess() {
		setupSegmenter();
	}



	/**
	 * called for every process once the system model has been created. 
	 * this is a good time to check out and find input data blocks and
	 * similar tasks. 
	 *
	 */
	@Override
	public void setupProcess() {
		super.setupProcess();
	}

	/**
	 * Set up the DL process. 
	 */
	void setupSegmenter() {
		
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
			currentRawChunks = new GroupedRawData[chanGroups.length]; 
			nextRawChunks = new GroupedRawData[chanGroups.length]; 
		}


		//set up connection to the parent
		PamRawDataBlock rawDataBlock;
		/*
		 * Data block used to be by number, now it's by name, but need to handle situations where
		 * name has not been set, so if there isn't a name, use the number !
		 */
		if (getSourceParams().getDataSource()  != null) {
			rawDataBlock = (PamRawDataBlock) PamController.getInstance().getDataBlock(RawDataUnit.class, getSourceParams().getDataSource());
		}
		else {
			rawDataBlock = PamController.getInstance().getRawDataBlock(getSourceParams().getDataSource());
			if (rawDataBlock != null) {
				getSourceParams().setDataSource(rawDataBlock.getDataName());
			}
		}

		if (rawDataBlock==null) return;

		setParentDataBlock(rawDataBlock);

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

		RawDataUnit rawDataUnit = (RawDataUnit) pamRawData;

		double[] rawDataChunk = rawDataUnit.getRawData();

		int iChan = PamUtils.getSingleChannel(rawDataUnit.getChannelBitmap());

		//TODO - what if the raw data lengths are larger than the segments by a long way?
		for (int i=0; i<getSourceParams().countChannelGroups(); i++) {

//			System.out.println("RawDataIn: chan: " + iChan+ "  " + PamUtils.hasChannel(getSourceParams().getGroupChannels(i), iChan) + " grouped source: " +getSourceParams().getGroupChannels(i)); 

			if (PamUtils.hasChannel(getSourceParams().getGroupChannels(i), iChan)) {

				//				System.out.println("Data holder size: " + PamUtils.getSingleChannel(pamRawData.getChannelBitmap())); 

				if (currentRawChunks[i]==null) {
					//create a new data unit - should only be called once after intial strat 
					currentRawChunks[i] = new GroupedRawData(pamRawData.getTimeMilliseconds(), getSourceParams() .getGroupChannels(i), 
							pamRawData.getStartSample(), dlControl.getDLParams().rawSampleSize, dlControl.getDLParams().rawSampleSize); 
				}

				int chanMap = getSourceParams().getGroupChannels(i); 

				int groupChan = PamUtils.getChannelPos(iChan, chanMap); 

				//how much of the chunk should we copy? 
				//				int lastPos = currentRawChunks[i].rawDataPointer[groupChan]+ 1 + rawDataChunk.length; 
				//
				//				int copyLen = rawDataChunk.length;
				//
				//
				//				if (lastPos>currentRawChunks[i].rawData[groupChan].length) {
				//					copyLen=copyLen-(lastPos-currentRawChunks[i].rawData[groupChan].length); 
				//				}
				//
				//				//update the current grouped raw data unit with new raw data. 
				//				System.arraycopy(rawDataChunk, 0, currentRawChunks[i].rawData[groupChan], currentRawChunks[i].rawDataPointer[groupChan]+1, copyLen); 
				//
				//				currentRawChunks[i].rawDataPointer[groupChan]=currentRawChunks[i].rawDataPointer[groupChan] + copyLen; 

				int overFlow = currentRawChunks[i].copyRawData(rawDataChunk, 0 , rawDataChunk.length, groupChan);

//				System.out.println("Data holder size: " + currentRawChunks[i].rawDataPointer[groupChan]); 

				//TODO - need to add something in here for very small chunks....

				if (overFlow>0) {

//					System.out.println("There has been a data overflow. " + PamUtils.getSingleChannel(pamRawData.getChannelBitmap())); 

					int copyLen = rawDataChunk.length - overFlow; //how much data was copied into the last data unit

					//now we need to populate the next data unit
					//to prevent any weird errors from accommodate sample offsets etc try to get timing information from the last pamRawDataUnit.
					if (nextRawChunks[i]== null) {
						long timeMillis = pamRawData.getTimeMilliseconds() + (long) (1000*(copyLen-dlControl.getDLParams().sampleHop)/this.getSampleRate()); 
						long startSample = pamRawData.getStartSample() + copyLen - dlControl.getDLParams().sampleHop; 

						nextRawChunks[i] = new GroupedRawData(timeMillis, getSourceParams().getGroupChannels(i), 
								startSample, dlControl.getDLParams().rawSampleSize, dlControl.getDLParams().rawSampleSize); 
					}

					//add the hop from the current grouped raw data unit to the new grouped raw data unit 
//					System.out.println("Pointer to copy from: "  + (currentRawChunks[i].rawData[groupChan].length - dlControl.getDLParams().sampleHop )); 
			
					int overFlow2 = nextRawChunks[i].copyRawData(currentRawChunks[i].rawData[groupChan], currentRawChunks[i].rawData[groupChan].length - dlControl.getDLParams().sampleHop , 
							dlControl.getDLParams().sampleHop, groupChan);

					//					System.arraycopy(currentRawChunks[i].rawData[groupChan], currentRawChunks[i].rawData[groupChan].length - dlControl.getDLParams().sampleHop,
					//							nextRawChunks[i].rawData[groupChan], 0, dlControl.getDLParams().sampleHop); 
					//					currentRawChunks[i].rawDataPointer[groupChan]=currentRawChunks[i].rawDataPointer[groupChan] + dlControl.getDLParams().sampleHop; 
					//finally copy in the slop that was not copied into the end of the last array. 
					int overFlow3 = nextRawChunks[i].copyRawData(rawDataChunk, copyLen, rawDataChunk.length-copyLen, groupChan);

					//					System.arraycopy(rawDataChunk, rawDataChunk.length-copyLen, nextRawChunks[i].rawData[groupChan], dlControl.getDLParams().sampleHop+1, rawDataChunk.length-copyLen); 
					//					currentRawChunks[i].rawDataPointer[groupChan]=currentRawChunks[i].rawDataPointer[groupChan] + copyLen; 
				}

				//now the data has been copied into the temporary unit need to check if the temporary data unit is full
				//check idf the data unit is ready to go. 
				if (isRawGroupDataFull(currentRawChunks[i])) {
					//send the raw data unit off to be classified!
					packageSegmenterDataUnit(currentRawChunks[i]); 

					this.segmenterDataBlock.addPamData(currentRawChunks[i]);

					System.out.println("Add PAM data! " + PamCalendar.formatDBDateTime(currentRawChunks[i].getTimeMilliseconds(), true) + " duration: " + currentRawChunks[i].getBasicData().getMillisecondDuration()); 

					//Need to copy a section of the old data into the 
					currentRawChunks[i] = nextRawChunks[i]; //in an unlikely situation this could be null should be picked up by the first null check. 
					nextRawChunks[i] = null; //make null until it's restarted 
				}
				//no need to carry on through the for loop
				break; 
			}; 
		}
	}
	
	/***TODO - hand small windows***/
	
	/**
	 * Check if the window size is likely smaller than the raw data. 
	 * @return
	 */
	private boolean smallWindowSize() {
		if (dlControl.getDLParams().rawSampleSize < this.getSampleRate()/100) return true;
		return false; 
	}
	
	/**
	 * There is an issue if the window length is very much smaller than the raw data chunks coming in. Instead of
	 * over complicating code, if this is the case then each GroupedRawData holds multiple raw data windows and is then 
	 * divided before being sent off to the datablock. 
	 */
	private void getWindowLength() {
		//now we want to create an integer size of array 
		
	}
	
	/***TODO **/
	
	
	/**
	 * Add all the bits and pieces to make sure the segmenter data unit
	 * has all the apprirate meta data for drawing etc. 
	 */
	private GroupedRawData packageSegmenterDataUnit(GroupedRawData groupedRawData) {
		//send the raw data unit off to be classified!
		groupedRawData.setFrequency(new double[] {0, this.getSampleRate()/2});
		groupedRawData.setDurationInMilliseconds((long) (1000*dlControl.getDLParams().rawSampleSize/this.getSampleRate()));
		
		return groupedRawData;
	}

	/**
	 * Check whether a raw data unit is full.
	 * @param groupedRawData - the grouped raw data 
	 * @return true if the grouped dtaa unit is full. 
	 */
	private boolean isRawGroupDataFull(GroupedRawData groupedRawData) {
		for (int i=0; i<groupedRawData.rawData.length; i++) {
			if ( groupedRawData.rawDataPointer[i] < groupedRawData.rawData[i].length-1) {
				return false; 
			}
		}
		return true;
	}


	/**
	 * Convenience function to get grouped source parameters. 
	 * @return the grouped source parameters. 
	 */
	private GroupedSourceParameters getSourceParams() {
		return dlControl.getDLParams().groupedSourceParams; 
	}


	/**
	 * 
	 * Temporary holder for raw data with a pre defined size. This holds one channel group of raw 
	 * sound data. 
	 * 
	 * @author Jamie Macaulay 
	 *
	 */
	public class GroupedRawData extends PamDataUnit implements PamDetection {
		//implements pam detection for plotting. 

		/*
		 * Raw data holder
		 */
		protected double[][] rawData;


		/**
		 *  Current position in the rawData;
		 */
		protected int[] rawDataPointer;


		public GroupedRawData(long timeMilliseconds, int channelBitmap, long startSample, long duration, int samplesize) {
			super(timeMilliseconds, channelBitmap, startSample, duration);
			rawData = new double[PamUtils.getNumChannels(channelBitmap)][];
			rawDataPointer = new int[PamUtils.getNumChannels(channelBitmap)];
			//			rawDataStartMillis = new long[PamUtils.getNumChannels(channelBitmap)];
								
			for (int i =0; i<rawData.length; i++) {
					rawData[i] = new double[samplesize];
			}
		}

		/**
		 * Copy raw data from an array to another. 
		 * @param src - the array to come from 
		 * @param srcPos - the raw source position
		 * @param copyLen - the copy length. 
		 * @groupChan - the channel (within the group)
		 * @return overflow. 
		 */
		public int copyRawData(Object src, int srcPos, int copyLen, int groupChan) {
			//how much of the chunk should we copy? 
		

			int lastPos = rawDataPointer[groupChan] + copyLen; 

			int dataOverflow = 0; 

			int arrayCopyLen; 
			//make sure the copy length 
			if (lastPos>=rawData[groupChan].length) {
				arrayCopyLen=copyLen-(lastPos-rawData[groupChan].length)-1; 
				dataOverflow = copyLen - arrayCopyLen; 
			}
			else {
				arrayCopyLen= copyLen; 
			}

			//update the current grouped raw data unit with new raw data. 
			System.arraycopy(src, srcPos, rawData[groupChan], rawDataPointer[groupChan], arrayCopyLen); 

			rawDataPointer[groupChan]=rawDataPointer[groupChan] + arrayCopyLen; 

			return dataOverflow; 
		}
		
		/**
		 * Get the raw data grouped by channel.
		 * @return the raw acoustic data.
		 */
		public double[][] getRawData() {
			return rawData;
		}

		/**
		 * Get the current pointer for rawData.
		 * @return the data pointer per channel. 
		 */
		public int[] getRawDataPointer() {
			return rawDataPointer;
		}

	}
	
	
	@Override
	public void pamStart() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pamStop() {
		// TODO Auto-generated method stub
	}


	public SegmenterDataBlock getSegmenterDataBlock() {
		return segmenterDataBlock;
	}

} 