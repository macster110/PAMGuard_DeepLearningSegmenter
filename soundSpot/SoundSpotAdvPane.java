package rawDeepLearningClassifer.soundSpot;

import org.jamdev.jtorch4pam.DeepLearningBats.DLParams;

import PamController.SettingsPane;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamVBox;


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
public class SoundSpotAdvPane extends SettingsPane<DLParams> {
	
	PamBorderPane mainPane; 

	public SoundSpotAdvPane(Object ownerWindow) {
		super(ownerWindow);
		mainPane = new PamBorderPane(); 
		mainPane.setCenter(new Label("Hello"));
	}
	
	private Pane createPane() {
		PamVBox holderPane = new PamVBox(); 
		holderPane.setSpacing(5)
		;
		return null; 
	}

	@Override
	public DLParams getParams(DLParams currParams) {

		
		return currParams;
	}

	@Override
	public void setParams(DLParams input) {
		// TODO Auto-generated method stub
		
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

}
