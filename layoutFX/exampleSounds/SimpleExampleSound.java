package rawDeepLearningClassifer.layoutFX.exampleSounds;

import java.io.IOException;

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

	@Override
	public double[] getWave() {
		return data.getScaledSampleAmpliudes();
	}

	@Override
	public float getSampleRate() {
		return  data.getSampleRate();
	}

}
