package rawDeepLearningClassifer;

import java.awt.Frame;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamController;
import PamController.PamSettingManager;
import PamController.PamSettings;
import PamController.SettingsPane;
import PamView.PamSidePanel;
import PamguardMVC.PamRawDataBlock;
import pamViewFX.fxNodes.pamDialogFX.PamDialogFX2AWT;
import rawDeepLearningClassifer.deepLearningClassification.DLClassiferModel;
import rawDeepLearningClassifer.deepLearningClassification.DLClassifyProcess;
import rawDeepLearningClassifer.layoutFX.DLSidePanelSwing;
import rawDeepLearningClassifer.layoutFX.RawDLSettingsPane;
import rawDeepLearningClassifer.orcaSpot.OrcaSpotClassifier;
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

		/*****Add new deep learning models here****/

		dlModels.add(new OrcaSpotClassifier(this)); 

		//load the previous settings
		PamSettingManager.getInstance().registerSettings(this);

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


	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenu menu = new JMenu("Raw Deep Learning Classifier"); 

		JMenuItem menuItem = new JMenuItem("Settings..."); 
		menuItem.addActionListener((action)->{
			showSettingsDialog(parentFrame); 
		});
		menu.add(menuItem);

		//		if (this.isViewer) {
		//			menuItem = new JMenuItem("Reclassy clicks..."); 
		//			menuItem.addActionListener((action)->{
		//				this.mtOfflineProcess.showOfflineDialog(parentFrame);
		//			});
		//			menu.add(menuItem);
		//		}
		//		
		return menu; 
	}



	@Override
	public PamSidePanel getSidePanel() {
		if (dlSidePanel ==null) {
			dlSidePanel = new DLSidePanelSwing(this); 
		}
		return dlSidePanel;
	}

	/**
	 * Show settings dialog. 
	 * @param parentFrame - the frame. 
	 */
	public void showSettingsDialog(Frame parentFrame) {
		if (settingsDialog == null || parentFrame != settingsDialog.getOwner()) {
			SettingsPane<RawDLParams> setPane = (SettingsPane<RawDLParams>) getSettingsPane();
			setPane.setParams(this.rawDLParmas);
			settingsDialog = new PamDialogFX2AWT<RawDLParams>(parentFrame, setPane, false);
			settingsDialog.setResizable(false);
		}
		RawDLParams newParams = settingsDialog.showDialog(rawDLParmas);

		//if cancel button is pressed then new params will be null. 
		if (newParams!=null) {
			updateParams(newParams); 
		}
	}


	/**
	 * Get the deep learning classification process. This handles running the current
	 * deep learning model. 
	 * @return the deep learning classification process. 
	 */
	public DLClassifyProcess getDLClassifyProcess() {
		return this.dlClassifyProcess;
	}

}
