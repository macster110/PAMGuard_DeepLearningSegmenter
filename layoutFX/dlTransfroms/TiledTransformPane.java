package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import org.jamdev.jtorch4pam.transforms.SimpleTransform;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import pamViewFX.fxNodes.PamGridPane;
import pamViewFX.fxNodes.PamSpinner;

public class TiledTransformPane extends SimpleTransformPane {

	/**
	 * The number of paramters for each row. 
	 */
	private int nParamCol = 2; 

	
	public TiledTransformPane(SimpleTransform simpleTransfrom, String[] paramNames, int nParamCol) {
		super(simpleTransfrom, paramNames, null);
		//this.nParamCol= nParamCol; 
	}

	/**
	 * The spectrum interp pane
	 * @param simpleTransfrom
	 * @param paramNames
	 * @param unitNames
	 */
	public TiledTransformPane(SimpleTransform simpleTransfrom, String[] paramNames, String[] unitNames, int nParamCol) {
		super(simpleTransfrom, paramNames, unitNames);
		//this.nParamCol= nParamCol; 
	}

	@Override 
	protected Node createPane(SimpleTransform simpleTransfrom, String[] paramNames, String[] unitNames) {

		nParamCol=2; 
		
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
				
				System.out.println("New line: " + i + "  " + nParamCol + "  " +  i%nParamCol); 

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

}