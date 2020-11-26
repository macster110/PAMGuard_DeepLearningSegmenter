package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import java.util.ArrayList;

import org.jamdev.jtorch4pam.SoundSpot.SoundSpotParams;
import org.jamdev.jtorch4pam.transforms.DLTransform;
import org.jamdev.jtorch4pam.transforms.DLTransform.DLTransformType;
import org.jamdev.jtorch4pam.transforms.FreqTransform;
import org.jamdev.jtorch4pam.transforms.WaveTransform;
import org.jamdev.jtorch4pam.utils.DLUtils;
import org.jamdev.jtorch4pam.wavFiles.AudioData;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pamViewFX.fxStyles.PamStylesManagerFX;

/**
 * Test the dl transfroms pane. 
 * 
 * @author Jamie Macaulay
 *
 */
public class DLTransformsPaneTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		//test the DL transfroms pane. 
			
		
		ArrayList<DLTransform> transforms = new ArrayList<DLTransform>(); 
		
		String wavFilePath = "/Users/au671271/Google Drive/Aarhus_research/PAMGuard_bats_2020/deep_learning/BAT/example_wav/call_393_2019_S4U05619MOL2-20180917-051012_2525_2534.wav";
	
		//create the DL params. 
		SoundSpotParams dlParams = new SoundSpotParams();
		
		AudioData soundData = null; 
		//Open wav files. 
		try {
			soundData = DLUtils.loadWavFile(wavFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//waveform transforms. 
		transforms.add(new WaveTransform(soundData, DLTransformType.DECIMATE, dlParams.sR)); 
		transforms.add(new WaveTransform(soundData, DLTransformType.PREEMPHSIS, dlParams.preemphases)); 
		//transforms.add(new WaveTransform(soundData, DLTransformType.TRIM, samplesChunk[0], samplesChunk[1])); 
		//frequency transforms. 
		transforms.add(new FreqTransform(DLTransformType.SPECTROGRAM, dlParams.n_fft, dlParams.hop_length)); 
		transforms.add(new FreqTransform(DLTransformType.SPECCROPINTERP, dlParams.fmin, dlParams.fmax, dlParams.n_freq_bins)); 
		transforms.add(new FreqTransform(DLTransformType.SPEC2DB)); 
		transforms.add(new FreqTransform(DLTransformType.SPECNORMALISE, dlParams.min_level_dB, dlParams.ref_level_dB)); 
		transforms.add(new FreqTransform(DLTransformType.SPECCLAMP, dlParams.clampMin, dlParams.clampMax)); 
		
		
		//create the 
		DLTransformsPane dlTarnsformsPane = new DLTransformsPane();
		dlTarnsformsPane.getStylesheets().add(PamStylesManagerFX.getPamStylesManagerFX().getCurStyle().getGUICSS());

		
		dlTarnsformsPane.setTransforms(transforms);
		dlTarnsformsPane.setPadding(new Insets(5,5,5,5));
		
		primaryStage.setScene(new Scene(dlTarnsformsPane, 890, 570));
		primaryStage.show();
	}
	
	
	public static void main(String args[]){           
	      launch(args);      
	} 
	

}
