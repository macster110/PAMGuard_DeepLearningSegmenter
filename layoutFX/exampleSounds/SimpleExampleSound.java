package rawDeepLearningClassifier.layoutFX.exampleSounds;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.jamdev.jdl4pam.utils.DLUtils;
import org.jamdev.jpamutils.wavFiles.AudioData;


/**
 * Simple example sound loaded from a file. 
 * @author Jamie Macaulay 
 *
 */
public class SimpleExampleSound implements ExampleSound{

	/***
	 * The data
	 */
	private AudioData data;

	public SimpleExampleSound(String file) {
		try {
			data = DLUtils.loadWavFile(file);
		} catch (IOException | UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			System.out.println(file); 
			e.printStackTrace();
		} 
	}

	public SimpleExampleSound(URL path) {
		try {
			data = DLUtils.loadWavFile(path);
		} catch (IOException | UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public double[] getWave() {
		return data.getScaledSampleAmpliudes();
	}

	@Override
	public float getSampleRate() {
		return  data.getSampleRate();
	}

}
