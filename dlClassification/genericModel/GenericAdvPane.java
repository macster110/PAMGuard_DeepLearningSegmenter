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
import pamViewFX.fxNodes.pamDialogFX.PamDialogFX;
import pamViewFX.fxNodes.utilityPanes.PamToggleSwitch;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.layoutFX.dlTransfroms.DLImageTransformPane;

/**
 * 
 * The advanced pane for the generic classifier. 
 * 
 * @author Jamie Macaulay
 *
 */
public class GenericAdvPane extends SettingsPane<GenericModelParams> {

	/**
	 * The tab pane. 
	 */
	private TabPane tabPane;

	/**
	 * The shape spinners. 
	 */
	private Spinner<Integer>[] shapeSpinners;

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
	private PamToggleSwitch defualtShapeSwitch;

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
	 * Text fields for class names. 
	 */
	private TextField[] textFields;

	/**
	 * Spinners to define the output shape
	 */
	private Spinner<Integer>[] outShapeSpinners;

	/**
	 * The default out switch. 
	 */
	private PamToggleSwitch defualtOutSwitch;

//	private TextField textFieldTest; 
	
	/**
	 * The shape label. 
	 */
	private Label shapeLabel;

	/**
	 * Create the generic advanced pane. 
	 */
	public GenericAdvPane(GenericDLClassifier genericClassifier) {
		super(null);
		this.genericClassifier=genericClassifier; 

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

		importExportPane = new GenericImportExportPane(this); 
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
			shapeSpinners[i] =  new Spinner<Integer>(-1, Integer.MAX_VALUE, 10,  10); 
			shapeSpinners[i] .setPrefWidth(80);
			shapeSpinners[i] .getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
			shapeSpinners[i] .setEditable(true);

			shapeHolder.getChildren().add(shapeSpinners[i]); 
		}
		
		HBox outShapeHolder = new HBox(); 
		outShapeHolder.setSpacing(5);
		

		defualtShapeSwitch = new PamToggleSwitch("Use model default shape"); 
		defualtShapeSwitch.selectedProperty().addListener((obsval, oldval, newval)->{
			//set the correct model shape. 
			if (currentInput!=null && currentInput.defaultShape!=null) {
				for (int i=0; i<currentInput.defaultShape.length; i++) {
					shapeSpinners[i].getValueFactory().setValue(currentInput.defaultShape[i].intValue());
				}
			}
			//disable the shapes. 
			shapeHolder.setDisable(newval);
		});

		
		Label outShapeLabel = new Label("Output shape"); 
		outShapeLabel.setFont(font);

		
		outShapeSpinners= new Spinner[2]; //set at  for now but could be different in future?

		for (int i=0; i<outShapeSpinners.length; i++) {
			outShapeSpinners[i] =  new Spinner<Integer>(-1, Integer.MAX_VALUE, 10,  10); 
			outShapeSpinners[i] .setPrefWidth(80);
			outShapeSpinners[i] .getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
			outShapeSpinners[i] .setEditable(true);

			outShapeHolder.getChildren().add(outShapeSpinners[i]); 
		}
		
		defualtOutSwitch = new PamToggleSwitch("Use model default out"); 
		defualtOutSwitch.selectedProperty().addListener((obsval, oldval, newval)->{
			
			if (!newval) {
				PamDialogFX.showWarning("This is generally a very bad idea. If the output shape is not that specified by the model then PAMGuard may through an index out of bounds exception");
			}
			//set the correct model shape. 
			if (currentInput!=null && currentInput.defualtOuput!=null) {
				for (int i=0; i<currentInput.defualtOuput.length; i++) {
					outShapeSpinners[i].getValueFactory().setValue(currentInput.defualtOuput[i].intValue());
				}
			}
			//disable the shapes. 
			outShapeHolder.setDisable(newval);
		});
		//defualtOutSwitch.setSelected(true);


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
			newSettings();
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
		holder.getChildren().addAll(shapeLabel, defualtShapeSwitch, shapeHolder, outShapeLabel, defualtOutSwitch, 
				outShapeHolder, classLabel,  classNumberHolder, classNameHolder); 

		populateClassNameFields(classNumber.getValue()); 

		return holder;
	}

	/**
	 * New settings. 
	 */
	private void newSettings() {
		if (setParams) return; 
		genericClassifier.getModelUI().getSettingsPane().setParams(this.getParams(currentInput));
	}

	/**
	 * Populate the class name fields. 
	 */
	public void populateClassNameFields(int nClass) {

		textFields = new TextField[nClass]; 

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
		
		if (setParams) return null; 

		//transfromPane.setTransforms(currParams.dlTransfroms);
		currParams.dlTransfroms = transfromPane.getDLTransforms(); 

		//System.out.println("Generic Adv Pane SET PARAMS: " + classNumber.getValue()); 

		currParams.numClasses = classNumber.getValue(); 

		String[] classNames= new String[textFields.length]; 
		boolean[] binaryClassification = new boolean[currParams.numClasses]; 
		
		for (int i=0; i<textFields.length; i++) {
			classNames[i]=textFields[i].getText(); 
			if (currParams.binaryClassification!=null && i<currParams.binaryClassification.length) {
				binaryClassification[i] = currParams.binaryClassification[i];
			}
			else {
				binaryClassification[i] = true; //default to true. 
			}
		}
		
		currParams.classNames = this.getDLControl().getClassNameManager().makeClassNames(classNames); 
		currParams.binaryClassification = binaryClassification; 

		return currParams;
	}
	
	boolean setParams = false; 

	@Override
	public void setParams(GenericModelParams input) {
		
		setParams= true; 

		this.currentInput = input.clone(); 

		//System.out.println("Generic Adv Pane SET PARAMS: " ); 

		if (input.defaultShape==null) {
			defualtShapeSwitch.setSelected(false);
			defualtShapeSwitch.setDisable(true);
		}
		else {
			defualtShapeSwitch.setDisable(false);
		}
		
		//
		classNumber.getValueFactory().setValue(currentInput.numClasses);
		populateClassNameFields(currentInput.numClasses); 
		
		//set the model shape
		if (currentInput.shape!=null) {
			for (int i=0; i<currentInput.shape.length; i++) {
				//System.out.println("Input shape: " + currentInput.shape[i].intValue()); 			
				shapeSpinners[i].getValueFactory().setValue(currentInput.shape[i].intValue());
				//System.out.println("Input shape: V " + shapeSpinners[i].getValue().intValue()); 

			}
		}
		
		if (currentInput.outputShape!=null) {
			for (int i=0; i<currentInput.outputShape.length; i++) {
				//System.out.println("Output shape: " + currentInput.outputShape[i].intValue()); 
				outShapeSpinners[i].getValueFactory().setValue(currentInput.outputShape[i].intValue());
			}
		}
		
		
		//textFieldTest.setText(currentInput.shape[1].toString());
		//System.out.println("Set transforms: " + currentInput.dlTransfroms.size()); 


		transfromPane.setTransforms(currentInput.dlTransfroms);
		
		setParams=false; 

	}

	@Override
	public String getName() {
		return "Generic Settings Pane";
	}

	@Override
	public void paneInitialized() {
		// TODO Auto-generated method stub

	}

	public GenericModelParams getCurrentParams() {
		return currentInput;
	}

	/**
	 * Get the DL control. 
	 * @return the DL control. 
	 */
	public DLControl getDLControl() {
		return genericClassifier.getDLControl();
	}



}
