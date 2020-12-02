package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import java.util.ArrayList;

import org.jamdev.jtorch4pam.transforms.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamGridPane;
import pamViewFX.fxNodes.PamHBox;
import pamViewFX.fxNodes.PamSpinner;

/**
 * Pane for a simple transform. This is a DLTransfrom which has a list of Numbers as parameters. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class SimpleTransformPane extends DLTransformPane {


	/**
	 * The parameter spinners
	 */
	private ArrayList<Spinner<Number>> paramSpinner = new ArrayList<Spinner<Number>>();

	/**
	 * The transform associated with the settings pane. 
	 */
	private SimpleTransform simpleTransfrom; 

	/**
	 * The default spinner width. 
	 */
	protected static int prefSpinnerWidth = 100; 

	int nParamCol=10; 


	/**
	 * 
	 * @param simpleTransfrom
	 * @param paramNames
	 */
	public SimpleTransformPane(SimpleTransform simpleTransfrom, String...paramNames) {
		this(simpleTransfrom, paramNames, null, 10); 
	}
	
	/**
	 * Create a settings pane for a SimpleTransform
	 * @param simpleTransfrom
	 * @param paramNames
	 * @param unitNames
	 */
	public SimpleTransformPane(SimpleTransform simpleTransfrom, String[] paramNames, String[] unitNames) {
		this(simpleTransfrom, paramNames, unitNames, 10); 

	}

	/**
	 * Create a settings pane for a SimpleTransform
	 * @param simpleTransfrom
	 * @param paramNames
	 * @param unitNames
	 */
	public SimpleTransformPane(SimpleTransform simpleTransfrom, String[] paramNames, String[] unitNames, int nColumns) {
		this.simpleTransfrom = simpleTransfrom; 
		this.setCenter(createPane(simpleTransfrom, paramNames,unitNames,nColumns) ); 
	}

	protected Node createPane(SimpleTransform simpleTransfrom, String[] paramNames, String[] unitNames, int nColumns) {

		
		PamGridPane gridPane = new PamGridPane(); 

		gridPane.setPadding(new Insets(5,5.,5.,15));


		PamSpinner<Number> spinner; 

		//			hBox.getChildren().add(new Label(simpleTransfrom.getDLTransformType().toString())); 

		int row = 0; 
		int column = 0; 
		if  (simpleTransfrom.getParams()!=null) {
			for (int i=0; i<simpleTransfrom.getParams().length; i++) {
				spinner = new PamSpinner<Number>(0.0, Integer.MAX_VALUE, 2, 0.01);
				spinner.setPrefWidth(prefSpinnerWidth);
				spinner.setEditable(true);
				spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

				gridPane.add(new Label(paramNames[i]), column , row); 
				gridPane.add(spinner, column+1, row);
				if (unitNames!=null && unitNames[i]!=null) {
					gridPane.add(new Label(unitNames[i]), column+2 , row); 
				}
				
				//System.out.println("New line: " + i + "  " + nParamCol + "  " +  i%nParamCol); 

				if (i%nParamCol == 0 ) {
					row++; 
					column=0; 
				}
			}

		}

		TitledPane titledPane = new TitledPane(simpleTransfrom.getDLTransformType().toString(), gridPane); 

		//			PamBorderPane borderPane = new PamBorderPane(); 
		//			borderPane.setTop(new Label(simpleTransfrom.getDLTransformType().toString()));
		//			borderPane.setCenter(hBox);

		titledPane.setExpanded(false);


		return titledPane;  

	}


	public SimpleTransform getParams(DLTransform currParams) {

		SimpleTransform simpleTransform = (SimpleTransform) currParams;

		//Set the new numbers
		Number[] params = new Number[simpleTransform.getParams().length]; 
		for (int i=0; i<simpleTransform.getParams().length; i++) {
			params[i] = paramSpinner.get(i).getValue(); 
		}

		simpleTransform.setParams(params);

		return simpleTransform;
	}

	/**
	 * Set the parameters
	 */
	public void setParams(DLTransform input) {

		SimpleTransform simpleTransform = (SimpleTransform) input;

		for (int i=0; i<simpleTransform.getParams().length; i++) {
			paramSpinner.get(i).getValueFactory().setValue(simpleTransform.getParams()[i]);
		}

	}

	@Override
	public DLTransform getDLTransform() {
		return this.simpleTransfrom;
	}




}
