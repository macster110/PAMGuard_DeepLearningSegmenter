package rawDeepLearningClassifer.dlClassification.genericModel;

import PamController.SettingsPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.utilityPanes.PamToggleSwitch;
import rawDeepLearningClassifer.layoutFX.dlTransfroms.DLImageTransformPane;

/**
 * 
 * The advanced pane for the generic classifier. 
 * 
 * @author Jamie Macaulay
 *
 */
public class GenericAdvPane  extends SettingsPane<GenericModelParams> {
	
	/**
	 * The tab pane. 
	 */
	private TabPane tabPane;
	
	/**
	 * The shape spinners. 
	 */
	private Spinner<Long>[] shapeSpinners;

	/**
	 * 	The class number. 
	 */
	private Spinner<Integer> classNumber;

	/**
	 * The class name holder. 
	 */
	private GridPane classNameHolder; 

	/**
	 * The text field names.  
	 */
	private TextField[] classNameFields;

	/**
	 * The DL transform pane. 
	 */
	private DLImageTransformPane transfromPane;

	/**
	 * The generic dl classifier. 
	 */
	private GenericDLClassifier genericClassifier;

	/**
	 * Toggle switch
	 */
	private PamToggleSwitch toggleSwitch;

	/**
	 * The current input. 
	 */
	private GenericModelParams currentInput; 
	
	/**
	 * The file chooser. 
	 */
	private FileChooser fileChooser; 
	
	/**
	 * The import export pane. 
	 */
	private ImportExportPane importExportPane; ; 
	
	/**
	 * The main pane. 
	 */
	private PamBorderPane mainPane; 

	/**
	 * Create the generic advanced pane. 
	 */
	public GenericAdvPane() {
		super(null);
		
		tabPane = new TabPane(); 
		
		Tab tab1 = new Tab("Model Transforms"); 
		tab1.setContent(transfromPane = new DLImageTransformPane()); 
		transfromPane.setPadding(new Insets(5,5,5,5));
		tab1.setClosable(false);
	
		Pane modelSettingsPane = createModelSettingsPane();
		modelSettingsPane.setPadding(new Insets(5,5,5,5));

		Tab tab2 = new Tab("Model Settings"); 
		tab2.setContent(modelSettingsPane);
		tab2.setClosable(false);

		tabPane.getTabs().addAll(tab2, tab1);
				
		importExportPane = new ImportExportPane(); 
		importExportPane.setPadding(new Insets(5,5,5,5));

		mainPane = new PamBorderPane(); 
		mainPane.setCenter(tabPane);
		mainPane.setBottom(importExportPane);
		mainPane.setPadding(new Insets(5,5,5,5));
		
		mainPane.setPrefHeight(500);
		mainPane.setPrefWidth(500);

		PamBorderPane.setAlignment(importExportPane, Pos.CENTER_RIGHT);
	}
	
	/**
	 * Create the model settings pane.
	 * @return the model settings pane.
	 */
	public Pane createModelSettingsPane() {
		
		Label shapeLabel = new Label("Input shape"); 
		Font font= Font.font(null, FontWeight.BOLD, 11);
		shapeLabel.setFont(font);
		
		//shape
		HBox shapeHolder = new HBox(); 
		shapeHolder.setSpacing(5);
		
		shapeSpinners= new Spinner[4]; //set at  for now but could be different in future?
		
		for (int i=0; i<shapeSpinners.length; i++) {
			shapeSpinners[i] =  new Spinner<Long>(-1L, Long.MAX_VALUE, 10L,  10L); 
			shapeSpinners[i] .setPrefWidth(80);
			shapeSpinners[i] .getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
			shapeSpinners[i] .setEditable(true);
			
			shapeHolder.getChildren().add(shapeSpinners[i]); 
		}
		
		toggleSwitch = new PamToggleSwitch("Use model default shape"); 
		toggleSwitch.selectedProperty().addListener((obsval, oldval, newval)->{
			//set the correct model shape. 
			if (currentInput!=null && currentInput.defaultShape!=null) {
				for (int i=0; i<currentInput.defaultShape.length; i++) {
					shapeSpinners[i].getValueFactory().setValue(currentInput.defaultShape[i]);
				}
			}
			//disable the shapes. 
			shapeHolder.setDisable(newval);
		});
		
		//class names - list and number of names
		Label classLabel = new Label("Class labels"); 
		classLabel.setFont(font);
		
		// the class names. 
		//have a max just in-case someone wants to input 1000 in which case 1000 text fields will be create. 
		classNumber =  new Spinner<Integer>(1, 64, 2,  1);
		classNumber .setPrefWidth(80);
		classNumber .getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		classNumber.valueProperty().addListener((obsval, oldVal, newVal)->{
			populateClassNameFields(newVal); 
		});

		HBox classNumberHolder = new HBox(); 
		classNumberHolder.setAlignment(Pos.CENTER_LEFT);
		classNumberHolder.setSpacing(5);
		classNumberHolder.getChildren().addAll(new Label("Number classes"), classNumber); 
		
		classNameHolder = new GridPane(); 
		classNameHolder.setHgap(5);
		classNameHolder.setVgap(5);
		
		//the holder. 
		VBox holder = new VBox(); 
		holder.setSpacing(5);
		holder.getChildren().addAll(shapeLabel, toggleSwitch, shapeHolder, classLabel,  classNumberHolder, classNameHolder); 
		
		populateClassNameFields(classNumber.getValue()); 
		
		return holder;
	}
	
	/**
	 * Populate the class name fields. 
	 */
	public void populateClassNameFields(int nClass) {
		
		TextField[] textFields = new TextField[nClass]; 
		
		classNameHolder.getChildren().clear();
		
		for (int i = 0 ; i<nClass; i++) {
			textFields[i] = new TextField(); 
			
			if (classNameFields!=null && i<classNameFields.length) {
				textFields[i].setText(classNameFields[i].getText()); 
			}
			else {
				textFields[i].setText(("Class " + i)); 
			}
			classNameHolder.add(new Label(("Class " + i)), 0, i);
			classNameHolder.add(textFields[i], 1, i);
		}
		
	}
	
	
	@Override
	public Node getContentNode() {
		return mainPane;
	}

	@Override
	public GenericModelParams getParams(GenericModelParams currParams) {
		
		//transfromPane.setTransforms(currParams.dlTransfroms);
		
		currParams.dlTransfroms = transfromPane.getDLTransforms(); 
		
		return currParams;
	}

	@Override
	public void setParams(GenericModelParams input) {
		
		this.currentInput = input; 
		
		//System.out.println("Generic Adv Pane: " + input.dlTransfromParams); 
		
		if (input.defaultShape==null) {
			toggleSwitch.setSelected(false);
			toggleSwitch.setDisable(true);
		}
		else {
			toggleSwitch.setDisable(false);
		}
		
		transfromPane.setTransforms(input.dlTransfroms);

	}

	@Override
	public String getName() {
		return "Generic Settings Pane";
	}

	@Override
	public void paneInitialized() {
		// TODO Auto-generated method stub
		
	}
	


}
