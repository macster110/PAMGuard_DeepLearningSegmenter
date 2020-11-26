package rawDeepLearningClassifer.layoutFX.exampleSounds;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.jamdev.jtorch4pam.utils.DLUtils;
import org.jamdev.jtorch4pam.wavFiles.AudioData;


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
