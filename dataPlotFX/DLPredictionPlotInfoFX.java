package rawDeepLearningClassifer.dataPlotFX;

import PamController.PamController;
import PamUtils.Coordinate3d;
import PamView.GeneralProjector;
import PamView.HoverData;
import PamView.GeneralProjector.ParameterType;
import PamView.GeneralProjector.ParameterUnits;
import PamView.symbol.PamSymbolChooser;
import PamView.symbol.PamSymbolManager;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import dataPlotsFX.TDManagedSymbolChooserFX;
import dataPlotsFX.TDSymbolChooserFX;
import dataPlotsFX.data.TDDataInfoFX;
import dataPlotsFX.data.TDDataProviderFX;
import dataPlotsFX.data.TDScaleInfo;
import dataPlotsFX.data.generic.GenericScaleInfo;
import dataPlotsFX.layout.TDGraphFX;
import dataPlotsFX.projector.TDProjectorFX;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.dlClassification.DLDataUnit;

/**
 * Plot the raw probability information. 
 * 
 * Plots multiple probability lines. Note that the deulat TDDataInfo can handle drawing lines for different channels
 * but we are drawing for different prediction classes here. 
 * 
 * @author Jamie Macaulay
 *
 */
public class DLPredictionPlotInfoFX extends TDDataInfoFX {

	/**
	 * Scale infos to show what axis clicks can be plotted on. 
	 */
	private GenericScaleInfo probabilityScaleInfo;

	/**
	 * TRhe managed symbol chooser. 
	 */
	private TDSymbolChooserFX managedSymbolChooser;


	private Point2D[] lastUnits;

	/**
	 * The frequency information. 
	 */
	private GenericScaleInfo frequencyInfo; 


	Color color = Color.DODGERBLUE;

	private DLControl dlControl; 

	public DLPredictionPlotInfoFX(TDDataProviderFX tdDataProvider, DLControl dlContorl, TDGraphFX tdGraph, PamDataBlock pamDataBlock) {
		super(tdDataProvider, tdGraph, pamDataBlock);
		
		this.dlControl=dlContorl; 

		probabilityScaleInfo = new GenericScaleInfo(0, 1, ParameterType.PROBABILITY, ParameterUnits.PROBABILITY);

		frequencyInfo = new GenericScaleInfo(0, 1, ParameterType.FREQUENCY, ParameterUnits.HZ);
		
		addScaleInfo(probabilityScaleInfo);
		addScaleInfo(frequencyInfo);
	}


	/**
	 * Base class draws a simple frequency box. Easily overridden to draw something else, e.g. a contour. 
	 * @param plotNumber - the plot number 
	 * @param pamDataUnit - the data unit to plot. 
	 * @param g - the graphics context. 
	 * @param scrollStart - the scroll start. 
	 * @param tdProjector - the TDProjectorFX. 
	 * @param type - the type flag. 
	 * @return
	 */
	private Polygon drawFrequencyData(int plotNumber, PamDataUnit pamDataUnit, GraphicsContext g, double scrollStart,
			TDProjectorFX tdProjector, int type) {

		DLDataUnit dataUnit = (DLDataUnit) pamDataUnit; 


		g.setStroke(color);
		g.setLineDashes(null);


//		double[] f = pamDataUnit.getFrequency();
//		if (f == null) {
//			return null;
//		}
//		if (f.length == 1) {
//			System.out.println("GenericDataPlotInfo: Single frequency measure in data unit " + pamDataUnit.toString());
//		}

		if (dataUnit.getPredicitionResult().isBinaryClassification()) {
			g.setFill(Color.color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 
					(Math.min(0.7, dlControl.getDLParams().sampleHop/(double) dlControl.getDLParams().rawSampleSize)))); 
			//			float[] prediciton = dataUnit.getPredicitionResult().getPrediction(); 
			//			float max = -(Float.MAX_VALUE+1);
			//			for (int i=0; i<prediciton.length ; i++) {
			//				if (max<prediciton[i]) {
			//					max = prediciton[i]; 
			//				}
			//			}		
		}
		else {
			//not classified so just has a dashed line. 
			g.setLineDashes(new double[] {9});

		}

		// draw a frequency box. 
		double y0 = tdProjector.getYPix(0);
		double y1 = tdProjector.getYPix(dlControl.getParentDataBlock().getSampleRate()/2);
		double x0 = tdProjector.getTimePix(pamDataUnit.getTimeMilliseconds()-scrollStart);
		double x1 = tdProjector.getTimePix(pamDataUnit.getEndTimeInMilliseconds()-scrollStart);
		double y = Math.min(y0,  y1);
		double h = Math.abs(y1-y0);
		
		//System.out.println(" Frequency: " + x0 + " " + x1 + " " +  " y: " +  y0 + " " + y1); 
		g.strokeRect(x0, y, x1-x0, h);
		if (dataUnit.getPredicitionResult().isBinaryClassification()) {
			g.fillRect(x0, y, x1-x0, h);
		}

		return null;
	}


	//	@Override
	//	public Double getDataValue(PamDataUnit pamDataUnit) {
	//
	//		DLDataUnit dataUnit = (DLDataUnit) pamDataUnit; 
	//
	//		if (lastUnits==null && dataUnit.getPredicitionResult().getPrediction()!=null) {
	//			lastUnits = new Point2D[dataUnit.getPredicitionResult().getPrediction().length]; 
	//		}
	//
	//		//System.out.println("DLModelPlotInfoFX model prediciton: " + dataUnit.getPredicitionResult().getPrediction()[0] + "  " +  dataUnit.getPredicitionResult().getPrediction()[1]); 
	//		return (double) dataUnit.getPredicitionResult().getPrediction()[0];
	//	}


	/* (non-Javadoc)
	 * @see dataPlotsFX.data.TDDataInfoFX#drawDataUnit(int, PamguardMVC.PamDataUnit, javafx.scene.canvas.GraphicsContext, long, dataPlotsFX.projector.TDProjectorFX, int)
	 */
	@Override
	public Polygon drawDataUnit(int plotNumber, PamDataUnit pamDataUnit, GraphicsContext g, double scrollStart,
			TDProjectorFX tdProjector, int type) {

		//System.out.println("Get data type: " + getScaleInfo().getDataType()); 
		if (getScaleInfo().getDataType().equals(ParameterType.FREQUENCY)) { // frequency data !
			
			return drawFrequencyData(plotNumber, pamDataUnit, g, scrollStart, tdProjector, type);
		}
		else {
			return drawPredicition(plotNumber, pamDataUnit, g, scrollStart, tdProjector, type);
		}

	}


	/**
	 * Draw the prediction. 
	 * @param plotNumber - the plot number. 
	 * @param pamDataUnit - the pam data unit. 
	 * @param g - the graphics context. 
	 * @param scrollStart - the scroll start. 
	 * @param tdProjector - the TDProjectorFX. 
	 * @param type - the type flag. 
	 * @return the polygon of the shape. 
	 */
	public Polygon drawPredicition(int plotNumber, PamDataUnit pamDataUnit, GraphicsContext g, double scrollStart,
			TDProjectorFX tdProjector, int type) {

		g.setLineDashes(null);

		DLDataUnit dataUnit = (DLDataUnit) pamDataUnit; 

		if (lastUnits==null && dataUnit.getPredicitionResult().getPrediction()!=null) {
			//System.out.println("lastUnits:  " + lastUnits);
			//create the array of last units. 
			lastUnits = new Point2D[dataUnit.getPredicitionResult().getPrediction().length]; 
		}

		//use the center of the window for plotting
		double timeMillis=(pamDataUnit.getTimeMilliseconds()+pamDataUnit.getDurationInMilliseconds()/2); ;
		double tC=tdProjector.getTimePix(timeMillis-scrollStart);


		//draws lines so tc should be some slop in pixels. 
		if (tC < -1000 || tC>tdProjector.getWidth()+1000) {
			return null;
		}


		double dataPixel; 
		Coordinate3d c; 
		Color color = Color.color(this.color.getRed(), this.color.getGreen(), this.color.getBlue()); 
		for (int i=0; i<dataUnit.getPredicitionResult().getPrediction().length; i++) {

			g.setStroke(color);
			color = Color.color(color.getRed()*0.8, color.getGreen()*0.8, color.getBlue()*0.8); 

			//			System.out.println("TDDataInfoFX: tc: "+tC+ " dataUnitTime: "+PamCalendar.formatTime(timeMillis)+" scrollStart: "
			//			+PamCalendar.formatTime((long) scrollStart)+" (timeMillis-scrollStart)/1000. "+((timeMillis-scrollStart)/1000.));


			c = tdProjector.getCoord3d(timeMillis, dataUnit.getPredicitionResult().getPrediction()[i], 0);

			dataPixel = tdProjector.getYPix(dataUnit.getPredicitionResult().getPrediction()[i]);

			if (lastUnits[i]==null) {
				lastUnits[i] = new Point2D(tC, dataPixel); 
				return null; 
			}
			else {

				if (tC>lastUnits[i].getX()) {
					g.strokeLine(tC, dataPixel, lastUnits[i].getX(), lastUnits[i].getY());					
				}
				else {
					lastUnits = null; 
					return null; 
				}
				lastUnits[i] = new Point2D(tC, dataPixel); 
			}

			//getSymbolChooser().getPamSymbol(pamDataUnit,type).draw(g, new Point2D(tC, dataPixel));

			tdProjector.addHoverData(new HoverData(c , pamDataUnit, 0, plotNumber));

		}

		return null; 
	}

	@Override
	public TDSymbolChooserFX getSymbolChooser() {
		if (managedSymbolChooser == null) {
			managedSymbolChooser = createSymbolChooser();
		}
		return managedSymbolChooser;
	}

	private TDSymbolChooserFX createSymbolChooser() {
		PamSymbolManager symbolManager = getDataBlock().getPamSymbolManager();
		if (symbolManager == null) {
			return null;
		}
		GeneralProjector p = this.getTDGraph().getGraphProjector();
		PamSymbolChooser sc = symbolManager.getSymbolChooser(getTDGraph().getUniqueName(), p);
		return new TDManagedSymbolChooserFX(this, sc, TDSymbolChooserFX.DRAW_SYMBOLS);
	}

	/**
	 * Notifications from the PamController are passed to this function.
	 * 
	 * @param changeType - notification flag.
	 */
	public void notifyModelChanged(int changeType) {
		switch (changeType) {

		case PamController.CHANGED_PROCESS_SETTINGS:
			lastUnits = null; 
			break;
		}
	}


	@Override
	public Double getDataValue(PamDataUnit pamDataUnit) {
		// TODO Auto-generated method stub
		return null;
	}

}
