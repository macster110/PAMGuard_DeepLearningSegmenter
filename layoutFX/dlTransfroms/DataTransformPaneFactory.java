package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import org.jamdev.jtorch4pam.transforms.DLTransform;
import org.jamdev.jtorch4pam.transforms.SimpleTransform;
import org.jamdev.jtorch4pam.transforms.DLTransform.DLTransformType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import pamViewFX.fxNodes.PamGridPane;
import pamViewFX.fxNodes.PamHBox;
import pamViewFX.fxNodes.PamSpinner;

/**
 * 
 * Generates settings panes for different data transforms. 
 * 
 * @author Jamie Macaulay
 *
 */
public class DataTransformPaneFactory {

	/**
	 * Get the settings pane for a DLTransfrom
	 * @return the DlTransfrom Settings Pane. 
	 */
	public static DLTransformPane getSettingsPane(DLTransform dlTransfrom) {

		DLTransformPane settingsPane = null;
		switch (dlTransfrom.getDLTransformType()) {
		case DECIMATE:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Freq. "}, new String[]{"Hz. "}); 
			break;
		case PREEMPHSIS:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Factor "}); 
			break;
		case SPEC2DB:
			settingsPane = new LabelTransfromPane(dlTransfrom, DLTransformType.SPEC2DB.toString()); 
			settingsPane.setPadding(new Insets(0,0,0,25));

			break;
		case SPECCLAMP:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Min. ", "Max. "}); 
			break;
		case SPECCROPINTERP:
			settingsPane = new TiledTransformPane((SimpleTransform) dlTransfrom, new String[]{"Min Freq. ", "Max Freq. ", " No. bins "},  new String[]{"Hz", "Hz", ""}, 2); 
			break;
		case SPECNORMALISE:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Min. dB ", "Reference dB"}); 
			break;
		case SPECTROGRAM:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"FFT Length ", "FFT Hop"},  new String[]{"samples ", "samples"}); 
			break;
		case TRIM:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Start", "End"},  new String[]{"samples ", "samples"}); 
			break;
		default:
			break;
		}
		return settingsPane;	

	}

	


}
