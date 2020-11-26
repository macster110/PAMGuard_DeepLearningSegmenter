package rawDeepLearningClassifer.layoutFX.exampleSounds;

import java.net.URL;

/**
 * The example sound factory
 * @author Jamie Macaulay 
 *
 */
public class ExampleSoundFactory {
	
	/**
	 * An example sound type. 
	 * @author Jamie macaulay
	 *
	 */
	public enum ExampleSoundType {
	    BAT_CALL("Bat Call (Myotis daubentonii)");

	    private final String text;

	    /**
	     * @param text
	     */
	    ExampleSoundType(final String text) {
	        this.text = text;
	    }

	    /* (non-Javadoc)
	     * @see java.lang.Enum#toString()
	     */
	    @Override
	    public String toString() {
	        return text;
	    }
	}
	
	/**
	 * Get the example sound type. 
	 * @param exampleSoundType
	 * @return
	 */
	public ExampleSound getExampleSound(ExampleSoundType exampleSoundType) {
		ExampleSound exampleSound = null; 
		switch (exampleSoundType) {
		case BAT_CALL:
			URL path = getClass().getResource("DUB_20200623_000152_885.wav"); 
			exampleSound = new SimpleExampleSound(path.getFile()); 
			break;
		default:
			break;
		
		}
		return exampleSound; 
	}
	

}
