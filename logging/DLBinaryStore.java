package rawDeepLearningClassifer.logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import Acquisition.AcquisitionProcess;
import PamUtils.PamArrayUtils;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamProcess;
import binaryFileStorage.BinaryDataSource;
import binaryFileStorage.BinaryHeader;
import binaryFileStorage.BinaryObjectData;
import binaryFileStorage.ModuleFooter;
import binaryFileStorage.ModuleHeader;
import rawDeepLearningClassifer.deepLearningClassification.DLClassifyProcess;
import rawDeepLearningClassifer.deepLearningClassification.DLDataUnit;
import rawDeepLearningClassifer.deepLearningClassification.DLModelDataBlock;

/**
 * Binary storage for the all the model results, i.e. all the returned probabilities. 
 * @author Jamie Macaulay 
 *
 */
public class DLBinaryStore extends BinaryDataSource {
	
	private DLModelDataBlock dlDataBlock;
	private ByteArrayOutputStream bos;
	private DataOutputStream dos;
	
	/**
	 * The dl classifier process. 
	 */
	private DLClassifyProcess dlClassifierProcess;

	public DLBinaryStore(DLClassifyProcess dlClassifierProcess) {
		super(dlClassifierProcess.getDLClassifiedDataBlock());
		this.dlDataBlock = dlClassifierProcess.getDLClassifiedDataBlock();
		this.dlClassifierProcess=dlClassifierProcess; 
	}

	@Override
	public String getStreamName() {
		return dlDataBlock.getDataName();
	}

	@Override
	public int getStreamVersion() {
		return 0;
	}

	@Override
	public int getModuleVersion() {
		return 0;
	}


	/* (non-Javadoc)
	 * @see binaryFileStorage.BinaryDataSource#getPackedData(PamguardMVC.PamDataUnit)
	 */
	@Override
	public BinaryObjectData getPackedData(PamDataUnit pamDataUnit) {
		DLDataUnit dlDataUnit = (DLDataUnit) pamDataUnit;
		if (dos == null || bos == null) {
			dos = new DataOutputStream(bos = new ByteArrayOutputStream());
		}
		else {
			bos.reset();
		}
		double[] probabilities = dlDataUnit.getModelResult().getPrediction(); 
		double maxVal = PamArrayUtils.max(probabilities); 
		double scale;
		if (maxVal > 0) {
			scale = (float) (32767./maxVal);			
		}
		else {
			scale = 1.;
		}
		/*
		 * Pretty minimilist write since channel map will already be stored in the
		 * standard header and data.length must match the channel map. 
		 */
		try {
			dos.writeFloat((float) scale);
			dos.writeShort(probabilities.length);
			for (int i = 0; i < probabilities.length; i++) {
				dos.writeShort((short) (scale*probabilities[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		BinaryObjectData packedData = new BinaryObjectData(0, bos.toByteArray());
		return packedData;
	}

	/* (non-Javadoc)
	 * @see binaryFileStorage.BinaryDataSource#sinkData(binaryFileStorage.BinaryObjectData, binaryFileStorage.BinaryHeader, int)
	 */
	@Override
	public PamDataUnit sinkData(BinaryObjectData binaryObjectData, BinaryHeader bh, int moduleVersion) {
		DataUnitBaseData baseData = binaryObjectData.getDataUnitBaseData();
		
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(binaryObjectData.getData()));
		try {
			double scale = dis.readFloat();
			short nSpecies = dis.readShort(); 
			double[] data = new double[nSpecies];
			for (int i = 0; i < nSpecies; i++) {
				data[i] = (double) dis.readShort() / scale;
			}
			return new DLDataUnit(baseData, data);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	
	}

	/* (non-Javadoc)
	 * @see binaryFileStorage.BinaryDataSource#sinkModuleHeader(binaryFileStorage.BinaryObjectData, binaryFileStorage.BinaryHeader)
	 */
	@Override
	public ModuleHeader sinkModuleHeader(BinaryObjectData binaryObjectData, BinaryHeader bh) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see binaryFileStorage.BinaryDataSource#sinkModuleFooter(binaryFileStorage.BinaryObjectData, binaryFileStorage.BinaryHeader, binaryFileStorage.ModuleHeader)
	 */
	@Override
	public ModuleFooter sinkModuleFooter(BinaryObjectData binaryObjectData, BinaryHeader bh,
			ModuleHeader moduleHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void newFileOpened(File outputFile) {
		
	}

	@Override
	public byte[] getModuleHeaderData() {
		// TODO Auto-generated method stub
		return null;
	}

}
