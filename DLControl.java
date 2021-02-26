package rawDeepLearningClassifer;

import java.awt.Frame;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamController.PamControlledUnit;
import PamController.PamControlledUnitGUI;
import PamController.PamControlledUnitSettings;
import PamController.PamController;
import PamController.PamGUIManager;
import PamController.PamSettingManager;
import PamController.PamSettings;
import PamController.SettingsPane;
import PamView.PamSidePanel;
import PamView.WrapperControlledGUISwing;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamRawDataBlock;
import dataPlotsFX.data.TDDataProviderRegisterFX;
import detectionPlotFX.data.DDPlotRegister;
import pamViewFX.fxNodes.pamDialogFX.PamDialogFX2AWT;
import rawDeepLearningClassifer.dataPlotFX.DLDetectionPlotProvider;
import rawDeepLearningClassifer.dataPlotFX.DLPredictionProvider;
import rawDeepLearningClassifer.ddPlotFX.RawDLDDPlotProvider;
import rawDeepLearningClassifer.dlClassification.DLClassName;
import rawDeepLearningClassifer.dlClassification.DLClassNameManager;
import rawDeepLearningClassifer.dlClassification.DLClassiferModel;
import rawDeepLearningClassifer.dlClassification.DLClassifyProcess;
import rawDeepLearningClassifer.dlClassification.dummyClassifier.DummyClassifier;
import rawDeepLearningClassifer.dlClassification.genericModel.GenericDLClassifier;
//import rawDeepLearningClassifer.dlClassification.orcaSpot.OrcaSpotClassifier;
import rawDeepLearningClassifer.dlClassification.soundSpot.SoundSpotClassifier;
import rawDeepLearningClassifer.layoutFX.DLSidePanelSwing;
import rawDeepLearningClassifer.layoutFX.DLSymbolManager;
import rawDeepLearningClassifer.layoutFX.PredictionSymbolManager;
import rawDeepLearningClassifer.layoutFX.RawDLSettingsPane;
import rawDeepLearningClassifer.logging.DLResultBinarySource;
import rawDeepLearningClassifer.offline.DLOfflineProcess;
import rawDeepLearningClassifer.logging.DLAnnotationType;
import rawDeepLearningClassifer.logging.DLDataUnitDatagram;
import rawDeepLearningClassifer.logging.DLDetectionBinarySource;
import rawDeepLearningClassifer.logging.DLDetectionDatagram;
import rawDeepLearningClassifer.segmenter.SegmenterProcess;

/**
 * Module which uses an external deep learning classifier to identify any data unit
 * containing raw data or FFt data.
 * 
 * For example can be sued to analyse section of a spectrogram or perhaps analyse the spectra of clicks. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class DLControl extends PamControlledUnit implements PamSettings {

	/**
	 * Flag for processing start
	 */
	public static final int PROCESSING_START = 0;

	/**
	 * Flag to indicate a setup is required
	 */
	public static final int NEW_PARAMS = 1;

	/*
	 * Called whenever processing has ended. This allows algorithms to save currently 
	 * held click trains etc once processing has completed. 
	 */
	public static final int PROCESSING_END = 2;


	/**
	 * List of different deep learning models that are available. 
	 */
	private ArrayList<DLClassiferModel> dlModels = new ArrayList<DLClassiferModel>();

	/**
	 * The settings pane. 
	 */
	private RawDLSettingsPane settingsPane;

	/**
	 * The settings dialog
	 */
	private PamDialogFX2AWT<RawDLParams> settingsDialog;

	/**
	 * Generic parameters. 
	 */
	private RawDLParams rawDLParmas = new RawDLParams();

	/**
	 * The deep elarning process. 
	 */
	private SegmenterProcess segmenterProcess; 

	/**
	 * The deep learning classification process
	 */
	private DLClassifyProcess dlClassifyProcess;

	/**
	 * The DL side panel - holds algorithm info. 
	 */
	private DLSidePanelSwing dlSidePanel;

	/**
	 * DLControl GUI using JavaFX
	 */
	private DLControlGUI rawGUIFX;

	/**
	 * The GUI for swing. 
	 */
	private WrapperControlledGUISwing rawDLGUISwing;

	/**
	 * Binary store for the model results. 
	 */
	private DLResultBinarySource dlBinaryDataSource;

	/**
	 * The binary data source for detection data
	 */
	private DLDetectionBinarySource dlDetectionBinarySource;


	/**
	 * The DL offline process. 
	 */
	private DLOfflineProcess dlOfflineProcess;

	private DLClassNameManager dlClassNameManager; 


	/**
	 * Constructor for the DL Control. 
	 * @param unitName - the unit name.
	 */
	public DLControl(String unitName) {
		super("Deep Learning Classifier", unitName);

		PamRawDataBlock rawDataBlock = PamController.getInstance().
				getRawDataBlock(rawDLParmas.groupedSourceParams.getDataSource());

		//segment the raw sound data
		addPamProcess(segmenterProcess = new SegmenterProcess(this, rawDataBlock));

		//classify the raw data segments. 
		addPamProcess(dlClassifyProcess = new DLClassifyProcess(this, segmenterProcess.getSegmenterDataBlock()));


		dlClassNameManager = new DLClassNameManager(this); 

		//add storage options etc. 
		dlBinaryDataSource = new DLResultBinarySource(dlClassifyProcess); 
		dlClassifyProcess.getDLPredictionDataBlock().setBinaryDataSource(dlBinaryDataSource);
		dlClassifyProcess.getDLPredictionDataBlock().setDatagramProvider(new DLDataUnitDatagram(this));

		dlDetectionBinarySource = new DLDetectionBinarySource(this, dlClassifyProcess.getDLDetectionDatablock()); 
		dlClassifyProcess.getDLDetectionDatablock().setBinaryDataSource(dlDetectionBinarySource);
		dlClassifyProcess.getDLDetectionDatablock().setDatagramProvider(new DLDetectionDatagram(this));

		dlClassifyProcess.getDLDetectionDatablock().setPamSymbolManager(new DLSymbolManager(this, 	dlClassifyProcess.getDLDetectionDatablock()));
		dlClassifyProcess.getDLPredictionDataBlock().setPamSymbolManager(new PredictionSymbolManager(this, 	dlClassifyProcess.getDLDetectionDatablock()));

		/*****Add new deep learning models here****/

		dlModels.add(new GenericDLClassifier(this)); 
		dlModels.add(new SoundSpotClassifier(this)); 

		//dlModels.add(new DummyClassifier()); 
		//dlModels.add(new OrcaSpotClassifier(this)); //removed soon.

		if (this.isViewer) {
			dlOfflineProcess = new DLOfflineProcess(this);
		}; 

		//register click detector for the javafx display. 
		TDDataProviderRegisterFX.getInstance().registerDataInfo(new DLDetectionPlotProvider(this, dlClassifyProcess.getDLDetectionDatablock()));
		TDDataProviderRegisterFX.getInstance().registerDataInfo(new DLPredictionProvider(this, dlClassifyProcess.getDLDetectionDatablock()));

		//register the DD display
		DDPlotRegister.getInstance().registerDataInfo(new RawDLDDPlotProvider(this, dlClassifyProcess.getDLDetectionDatablock()));
		//load the previous settings
		PamSettingManager.getInstance().registerSettings(this);

		//because this was added after some settings classes have already been serialized
		if (rawDLParmas.classNameMap==null) rawDLParmas.classNameMap = new ArrayList<DLClassName>(); 

		//ensure everything is updated. 
		updateParams(rawDLParmas); 
	}




	/**
	 * Get the available deep learning models
	 * @return the available deep learning models. 
	 */
	public ArrayList<DLClassiferModel> getDLModels() {
		return dlModels;
	}

	/**
	 * Get the current deep learning model.
	 * @return the current deep learning model. 
	 */
	public DLClassiferModel getDLModel() {
		return dlModels.get(rawDLParmas.modelSelection);
	}



	/**
	 * Called whenever there are new params. 
	 * @param newParams - new deep learning params to implement. 
	 */
	private void updateParams(RawDLParams newParams) {
		this.rawDLParmas = newParams; 

		this.segmenterProcess.setupSegmenter(); 
		this.dlClassifyProcess.setupProcess();

		//this is a bit of a hack. Annotations are added to data units but the datablock knows nothing about them
		//unless the annotation type is set in the datablock. This is required for things like symbol choosers that 
		//may need to know a data block contains a certian type of annotation. 

		this.getParentDataBlock().addDataAnnotationType(dlClassifyProcess.getDLAnnotionType());

		if (dlSidePanel!=null) {
			dlSidePanel.setupPanel(); 
		}
	}

	/**
	 * Get  basic parameters. 
	 * @return parameters class. 
	 */
	public RawDLParams getDLParams() {
		return rawDLParmas;
	}


	@Override
	public Serializable getSettingsReference() {
		return this.rawDLParmas;
	}


	@Override
	public long getSettingsVersion() {
		return RawDLParams.serialVersionUID;
	}


	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		RawDLParams newParameters = (RawDLParams) pamControlledUnitSettings.getSettings();;
		rawDLParmas = newParameters.clone();
		return true;
	}


	/**
	 * Get the segmenter process. This breaks raw data into chunks, combines into groups and 
	 * sends to DL classifiers. 
	 * @return the segmenter process. 
	 */
	public SegmenterProcess getSegmenter() {
		return this.segmenterProcess;
	}


	/****GUI STUFF***/

	/**
	 * Get the settings pane. 
	 * @return the settings pane. 
	 */
	public RawDLSettingsPane getSettingsPane(){

		if (this.settingsPane==null){
			settingsPane= new RawDLSettingsPane(this); 
		}
		return settingsPane; 
	}

	/****----Baked in Swing stuff----*****/

	//Swing components should not be in the control class but that is way PG is at the moment. 

	/**
	 * Show settings dialog. 
	 * @param parentFrame - the frame. 
	 */
	public void showSettingsDialog(Frame parentFrame) {
		if (settingsDialog == null || parentFrame != settingsDialog.getOwner()) {
			SettingsPane<RawDLParams> setPane = (SettingsPane<RawDLParams>) getSettingsPane();
			setPane.setParams(this.rawDLParmas);
			settingsDialog = new PamDialogFX2AWT<RawDLParams>(parentFrame, setPane, false);
			settingsDialog.setResizable(true);
		}
		RawDLParams newParams = settingsDialog.showDialog(rawDLParmas);

		//if cancel button is pressed then new params will be null. 
		if (newParams!=null) {
			updateParams(newParams); 
		}
	}


	@Override
	public PamSidePanel getSidePanel() {
		if (dlSidePanel ==null) {
			dlSidePanel = new DLSidePanelSwing(this); 
		}
		return dlSidePanel;
	}

	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenuItem menu; 
		if (this.isViewer) {
			menu = new JMenu("Raw Deep Learning Classifier"); 

			JMenuItem menuItem = new JMenuItem("Settings..."); 
			menuItem.addActionListener((action)->{
				showSettingsDialog(parentFrame); 
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Reclassify detections..."); 
			menuItem.addActionListener((action)->{
				this.dlOfflineProcess.showOfflineDialog(parentFrame);
			});
			menu.add(menuItem);
		}

		else {
			menu = new JMenuItem(); 
			//no need for nested menus if there is only one option. 
			menu.setText("Raw Deep Learning Classifier...");
			menu.addActionListener((action)->{
				showSettingsDialog(parentFrame); 
			});
		}

		return menu; 
	}


	/**
	 * Get the deep learning classification process. This handles running the current
	 * deep learning model. 
	 * @return the deep learning classification process. 
	 */
	public DLClassifyProcess getDLClassifyProcess() {
		return this.dlClassifyProcess;
	}


	/**
	 * Get the GUI for the PAMControlled unit. This has multiple GUI options 
	 * which are instantiated depending on the view type. 
	 * @param flag. The GUI type flag defined in PAMGuiManager. 
	 * @return the GUI for the PamControlledUnit unit. 
	 */
	public PamControlledUnitGUI getGUI(int flag) {
		if (flag==PamGUIManager.FX) {
			if (rawGUIFX ==null) {
				rawGUIFX= new DLControlGUI(this);
			}
			return rawGUIFX;
		}
		if (flag==PamGUIManager.SWING) {
			if (rawDLGUISwing ==null) {
				rawDLGUISwing= new WrapperControlledGUISwing(this);	
			}
			return rawDLGUISwing;
		}
		return null;
	}


	public void setParams(RawDLParams newParams) {
		this.rawDLParmas=newParams;
	}



	/**
	 * Get the parent data block.   
	 * @return the parent data block. 
	 */
	@SuppressWarnings("rawtypes")
	public PamDataBlock getParentDataBlock() {
		return segmenterProcess.getParentDataBlock();
	}


	/**
	 * Get the number of classes for the current classifier.
	 * @return the number of classes. 
	 */
	public int getNumClasses() {
		return  getDLModel().getNumClasses(); 

	}

	/**
	 * Called whenever offline processing is occurring 
	 * @param processingFlag
	 */
	public void update(int processingFlag) {
		switch (processingFlag) {
		case DLControl.PROCESSING_END:
			//force the click detector to repaint. 
			break;
		}

	}

	public DLClassNameManager getClassNameManager() {
		return this.dlClassNameManager;
	}

	/**
	 * Convenience function to get the DLAnnotationType from the DLClassification process. 
	 * @return the DLAnnotationType
	 */
	public DLAnnotationType getAnnotationType() {
		return this.dlClassifyProcess.getDLAnnotionType(); 
	}

}
