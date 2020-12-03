package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import org.jamdev.jtorch4pam.transforms.DLTransform;
import org.jamdev.jtorch4pam.transforms.SimpleTransform;

import fftManager.layoutFX.FFTPaneFX;

import org.jamdev.jtorch4pam.transforms.DLTransform.DLTransformType;
import org.jamdev.jtorch4pam.transforms.FreqTransform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
	 * Create a step list of FFTlength sizes for a spinner
	 * @return the step list. 
	 */
	public static ObservableList<Number> createStepList() {
		ObservableList<Number> stepSizeListLength=FXCollections.observableArrayList();
		for (int i=2; i<15; i++){
			stepSizeListLength.add((int) Math.pow(2,i));
			
		}
		return stepSizeListLength;
	}


	/**
	 * Get the settings pane for a DLTransfrom
	 * @return the DlTransfrom Settings Pane. 
	 */
	public static DLTransformPane getSettingsPane(DLTransform dlTransfrom) {

		DLTransformPane settingsPane = null;
		switch (dlTransfrom.getDLTransformType()) {
		case DECIMATE:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Sample rate "}, new String[]{"Hz. "}); 
			double sR = ((SimpleTransform) dlTransfrom).getParams()[0].doubleValue(); 
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(0, 0.0, Integer.MAX_VALUE,   sR>10000 ? 1000.0 : 100.0);
			break;
		case PREEMPHSIS:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Factor "}); 
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(0, 0.0, 1.0,   0.01);
			break;
		case SPEC2DB:
			settingsPane = new LabelTransfromPane(dlTransfrom, DLTransformType.SPEC2DB.toString()); 
			settingsPane.setPadding(new Insets(0,0,0,25));

			break;
		case SPECCLAMP:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Min. ", "Max. "}); 
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(0, -1000.0, 1000.0,   0.1);
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(1, -1000.0, 1000.0,   0.1);
			break;
		case SPECCROPINTERP:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Min. Freq. ", "Max. Freq. ", " No. bins "},  new String[]{"Hz", "Hz", ""}, 2); 
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(0, 0.0, 500000.0,   100.); //hmmmm would be nce to have the sample rate here...
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(1, 0.0, 500000.0,   100.);
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(2, 0, Integer.MAX_VALUE,   10);
			break;
		case SPECNORMALISE:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Min. dB ", "Reference dB"}); 
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(0, -300.0, 300.0,   1.);
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(1, -300.0, 500000.0,   1.);

			break;
		case SPECTROGRAM:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"FFT Length ", "FFT Hop"},  new String[]{"samples ", "samples"}); 
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(0, 4, Integer.MAX_VALUE,   4);
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(1, 4, Integer.MAX_VALUE,   4);
//			//make an FFT spinner here with doubling FFT lengths - DOES NOT WORK FOR SOME REASON...
//			((SimpleTransformPane) settingsPane).getSpinners().get(0).setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(createStepList()));
//			((SimpleTransformPane) settingsPane).getSpinners().get(0).getValueFactory().setValue(4);
//			((SimpleTransformPane) settingsPane).getSpinners().get(1).setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(createStepList()));
			break;
		case TRIM:
			settingsPane = new SimpleTransformPane((SimpleTransform) dlTransfrom, new String[]{"Start", "End"},  new String[]{"samples ", "samples"}); 
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(0, 0, Integer.MAX_VALUE,   500);
			((SimpleTransformPane) settingsPane).setSpinnerMinMaxValues(1, 0,Integer.MAX_VALUE,   500);
			break;
		default:
			break;
		}
		return settingsPane;	

	}

	


}
