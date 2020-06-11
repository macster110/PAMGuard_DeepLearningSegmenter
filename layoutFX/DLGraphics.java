package rawDeepLearningClassifer.layoutFX;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import PamUtils.Coordinate3d;
import PamView.GeneralProjector;
import PamView.PamDetectionOverlayGraphics;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import Spectrogram.SpectrogramProjector;
import rawDeepLearningClassifer.DLDataUnit;

/**
 * The detection graphics
 * @author Jamie Macaulay
 *
 */
public class DLGraphics extends PamDetectionOverlayGraphics {

	public Stroke dashed = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	
//	public Stroke dashed = new BasicStroke(4); 

	public Stroke normal = new BasicStroke(3); 

	public int alpha = 127; // 50% transparent

	public static Color detColor = Color.CYAN; 

	private PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_DIAMOND, 10, 12, false,
			detColor, detColor); 

	
	public DLGraphics(PamDataBlock parentDataBlock) {
		super(parentDataBlock, getDLDefaultSymbol(detColor));
		// TODO Auto-generated constructor stub
	}
	
	private static PamSymbol getDLDefaultSymbol(Color detColor) {
			return  new PamSymbol(PamSymbolType.SYMBOL_DIAMOND, 10, 12, false,
					detColor, detColor); 
	}


	@Override
	protected Rectangle drawOnSpectrogram(Graphics g, PamDataUnit pamDataUnit, GeneralProjector generalProjector) {
		// draw a rectangle with time and frequency bounds of detection.
		// spectrogram projector is now updated to use Hz instead of bins. 
		DLDataUnit pamDetection = (DLDataUnit) pamDataUnit;	// originally cast pamDataUnit to PamDetection class


		double[] frequency = pamDetection.getFrequency();
		Coordinate3d topLeft = generalProjector.getCoord3d(pamDetection.getTimeMilliseconds(), 
				frequency[1], 0);
		Coordinate3d botRight = generalProjector.getCoord3d(pamDetection.getTimeMilliseconds() + 
				pamDetection.getSampleDuration() * 1000./getParentDataBlock().getSampleRate(),
				frequency[0], 0);

		if (botRight.x < topLeft.x){
			botRight.x = g.getClipBounds().width;
		}
		if (generalProjector.isViewer()) {
			Coordinate3d middle = new Coordinate3d();
			middle.x = (topLeft.x + botRight.x)/2;
			middle.y = (topLeft.y + botRight.y)/2;
			middle.z = (topLeft.z + botRight.z)/2;
			generalProjector.addHoverData(middle, pamDataUnit);
		}


		//creates a copy of the Graphics instance
		Graphics2D g2d = (Graphics2D) g.create();
		
//		System.out.println("New OrcaSpotDatauNit draw: " + topLeft.x + "  " + botRight.x 
//				+ " " + topLeft.y + "  " + botRight.y + " Frequency: " + frequency[0] + " " + frequency[1]); 

		//do not paint unles sit's passed binary classiifcation 
		if (pamDetection.getModelResult().isClassification()) {
			//set the stroke of the copy, not the original 
			g2d.setStroke(normal);
		}
		else {
			g2d.setStroke(dashed);
		}

		g.setColor(detColor);

		g.drawRect((int) topLeft.x, (int) topLeft.y, 
				(int) botRight.x - (int) topLeft.x, (int) botRight.y - (int) topLeft.y);

		
		if (pamDetection.getModelResult().isClassification()) {

			//set the alpha so that better results are more opaque 
			int alphaDet = (int) (1 - pamDetection.getModelResult().getPrediction())*alpha; 
			
			Color detColorAlpha = new Color(detColor.getRed(), detColor.getGreen(), detColor.getBlue(), alphaDet);
			g.setColor(detColorAlpha);
			g.fillRect((int) topLeft.x, (int) topLeft.y, 
					(int) botRight.x - (int) topLeft.x, (int) botRight.y - (int) topLeft.y);

		}

		return new Rectangle((int) topLeft.x, (int) topLeft.y, 
				(int) botRight.x - (int) topLeft.x, (int) botRight.y - (int) topLeft.y);
	}

}
