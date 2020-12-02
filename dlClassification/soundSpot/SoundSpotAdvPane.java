package rawDeepLearningClassifer.dlClassification.soundSpot;

import org.controlsfx.control.ToggleSwitch;

import PamController.SettingsPane;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamHBox;
import pamViewFX.fxNodes.PamVBox;
import rawDeepLearningClassifer.layoutFX.dlTransfroms.DLTransformsPane;


/**
 * Advanced settings for the SoundSpot classifier. 
 * 
 * 20:33:02|D|dataOpts: {
    "sr": 256000,
    "preemphases": 0.98,
    "n_fft": 256,
    "hop_length": 8,
    "n_freq_bins": 256,
    "fmin": 40000,
    "fmax": 100000,
    "freq_compression": "linear",
    "min_level_db": -100,
    "ref_level_db": 0
}
 * @author Jamie Macaulay 
 *
 */
public class SoundSpotAdvPane extends SettingsPane<PamSoundSpotParams> {
	
	PamBorderPane mainPane;
	
	/**
	 * The default parameters. 
	 */
	private PamSoundSpotParams defaultParams = new PamSoundSpotParams(); 
	
	/*
	 * Toggle switch for settiing defaults. 
	 */
	private ToggleSwitch toggleSwitch;

	/**
	 * The pane fwith toggle switch for defaults 
	 */
	private Pane defaultTogglePane;

	/**
	 * Pane which holds and allows users to edit dlTransforms; 
	 */
	private DLTransformsPane transfromPane;


	public SoundSpotAdvPane() {
		super(null);
		mainPane = new PamBorderPane(); 
		
		defaultTogglePane = createTogglePane(); 
		transfromPane = new DLTransformsPane(); 
		mainPane.setPadding(new Insets(5,5,5,5));
	}
	
	private Pane createTogglePane() {
		
		PamHBox holderPane = new PamHBox(); 
		holderPane.setSpacing(5);
		
		toggleSwitch = new ToggleSwitch(); 
		toggleSwitch.selectedProperty().addListener((obsval, oldval, newval)->{
			//change the other switches 
			setDefaultEnabled(newval); 
		});
		toggleSwitch.setSelected(true);
		
		holderPane.getChildren().addAll(toggleSwitch, new Label( "Use default model settings"));
		
		return holderPane; 
	}

	/**
	 * Set the controls to be enabled by default. 
	 * @param true to set the controls to enabled. 
	 */
	private void setDefaultEnabled(Boolean newval) {
		if (transfromPane!=null) transfromPane.setDisable(newval); 
	}

	@Override
	public PamSoundSpotParams getParams(PamSoundSpotParams currParams) {
		currParams.dlTransfroms = transfromPane.getDLTransforms(); 
		currParams.useDefaultTransfroms = toggleSwitch.isSelected(); 
		return currParams; 
	}

	@Override
	public void setParams(PamSoundSpotParams input) {
		if (input.dlTransfroms==null) {
			mainPane.setTop(null);
			mainPane.setCenter(new Label("A model must be loaded before \n "
					+ "advanced settings are available"));
		}
		else {
			mainPane.setTop(defaultTogglePane);
			mainPane.setCenter(transfromPane);
			
			BorderPane.setMargin(defaultTogglePane, new Insets(5));
			transfromPane.setDisable(true);
			toggleSwitch.setSelected(true);
			transfromPane.setTransforms(input.dlTransfroms);
		}
		
	}

	@Override
	public String getName() {
		return "SoundSpot Adv. Params";
	}

	@Override
	public Node getContentNode() {
		return mainPane;
	}

	@Override
	public void paneInitialized() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Get the default paramters. 
	 * @return the default paramters. 
	 */
	public PamSoundSpotParams getDefaultParams() {
		return defaultParams;
	}

	public void setDefaultParams(PamSoundSpotParams defaultParams) {
		this.defaultParams = defaultParams;
	}


}
