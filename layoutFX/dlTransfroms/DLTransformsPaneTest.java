package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import java.util.ArrayList;

import org.jamdev.jtorch4pam.SoundSpot.SoundSpotParams;
import org.jamdev.jtorch4pam.transforms.DLTransform;
import org.jamdev.jtorch4pam.transforms.DLTransformsFactory;
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
		
		ArrayList<DLTransform> transforms  = DLTransformsFactory.makeDLTransforms(dlParams.dlTransforms);
		
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
