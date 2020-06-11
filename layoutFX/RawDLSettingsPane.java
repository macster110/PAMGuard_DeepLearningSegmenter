package rawDeepLearningClassifer.layoutFX;

import PamController.SettingsPane;
import PamDetection.RawDataUnit;
import PamguardMVC.PamDataBlock;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import pamViewFX.PamGuiManagerFX;
import pamViewFX.fxGlyphs.PamGlyphDude;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamButton;
import pamViewFX.fxNodes.PamGridPane;
import pamViewFX.fxNodes.PamSpinner;
import pamViewFX.fxNodes.PamVBox;
import pamViewFX.fxNodes.pamDialogFX.PamDialogFX;
import pamViewFX.fxNodes.utilityPanes.GroupedSourcePaneFX;
import rawDeepLearningClassifer.DLClassiferModel;
import rawDeepLearningClassifer.DLControl;
import rawDeepLearningClassifer.RawDLParams;

/**
 * The settings pane. 
 * 
 * @author Jamie Macaulay
 *
 */
public class RawDLSettingsPane  extends SettingsPane<RawDLParams>{


	/**
	 * The source for the FFT data source.  
	 */
	private GroupedSourcePaneFX sourcePane;

	/**
	 * Reference to DL control 
	 */
	private DLControl dlControl;

	/**
	 * The main pane. 
	 */
	private PamBorderPane mainPane;

	/**
	 * Combo box which allows users to select model. 
	 */
	private ComboBox<String> dlModelBox;

	/**
	 * The winodw length spinner for the segmenter process
	 */
	private PamSpinner<Integer> windowLength;

	/**
	 * The hop length for the segmenter progress. 
	 */
	private PamSpinner<Integer> hopLength;

	/**
	 * The pane in which the classiifer pane sits. 
	 */
	private PamBorderPane classifierPane;

	public RawDLSettingsPane(DLControl dlControl){
		super(null); 
		this.dlControl=dlControl; 
		//		Button newButton=new Button("Test");
		//		newButton.setOnAction((action)-> {
		//			pane.layout();
		//			pamTabbedPane.layout();
		//			Stage stage = (Stage) this.getScene().getWindow();
		//			stage.sizeToScene();
		//		});
		//		this.setTop(newButton);
		mainPane=new PamBorderPane(); 
		mainPane.setCenter(createDLPane());
		mainPane.setPadding(new Insets(5,5,5,5));
	}


	/**
	 * Create Pane for changing FFT settings. 
	 * @return pane for changing FFT settings 
	 */
	private Pane createDLPane(){

		PamVBox vBox=new PamVBox();
		vBox.setSpacing(5);

		sourcePane = new GroupedSourcePaneFX("Raw Sound Data", RawDataUnit.class, true, false, true);
		vBox.getChildren().add(sourcePane);
		sourcePane.prefWidthProperty().bind(vBox.widthProperty());

		// the segmentation params
		Label label = new Label("Segmentation"); 
		label.setFont(PamGuiManagerFX.titleFontSize2);

		vBox.getChildren().add(label);

		windowLength = new PamSpinner<Integer>(0, Integer.MAX_VALUE, 10,  10000); 
		windowLength.setPrefWidth(100);
		windowLength.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		windowLength.setEditable(true);

		hopLength =    new PamSpinner<Integer>(0, Integer.MAX_VALUE, 10,  10000); 
		hopLength.setPrefWidth(100);
		hopLength.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		windowLength.setEditable(true);
		
		//button to set default hop size
		Button defaultButton = new Button();
		defaultButton.setGraphic(PamGlyphDude.createPamGlyph(MaterialDesignIcon.REFRESH));
		defaultButton.setTooltip(new Tooltip("Set default hop size"));
		defaultButton.setOnAction((action)->{
			hopLength.getValueFactory().setValue(Math.round(windowLength.getValue()/2));
		});

		PamGridPane segmenterGridPane = new PamGridPane(); 
		segmenterGridPane.add(new Label("Window length"), 0, 0);
		segmenterGridPane.add(windowLength, 1, 0);
		segmenterGridPane.add(new Label("samples"), 2, 0);
		segmenterGridPane.add(new Label("Hop length"), 0, 1);
		segmenterGridPane.add(hopLength, 1, 1);
		segmenterGridPane.add(new Label("samples"), 2, 1);
		segmenterGridPane.add(defaultButton, 3, 1);


		vBox.getChildren().add(segmenterGridPane);

		Label label2 = new Label("Deep Learning Model"); 
		label2.setFont(PamGuiManagerFX.titleFontSize2);

		vBox.getChildren().add(label2);

		//add the possible deep learning models. 
		dlModelBox= new ComboBox<String>();
		for (int i=0; i<dlControl.getDLModels().size(); i++) {
			dlModelBox.getItems().add(dlControl.getDLModels().get(i).getName()); 
		}
		dlModelBox.prefWidthProperty().bind(vBox.widthProperty());

		dlModelBox.setOnAction((action)->{
			setClassifierPane(); 
		});

		vBox.getChildren().add(dlModelBox);

		classifierPane = new PamBorderPane(); 

		vBox.getChildren().add(classifierPane);


		return vBox; 
	}

	/**
	 * Set the classifier pane. 
	 */
	private void setClassifierPane() {
		//set the classifier Pane.class 
		DLClassiferModel classifierModel = this.dlControl.getDLModels().get(dlModelBox.getSelectionModel().getSelectedIndex()); 

		if (classifierModel.getModelUI()!=null) {
			classifierPane.setCenter(classifierModel.getModelUI().getSettingsPane().getContentNode()); 
			classifierModel.getModelUI().setParams(); 
		}
		else {
			classifierPane.setCenter(null); 
		}
	}


	@Override
	public RawDLParams getParams(RawDLParams currParams) {

		if (currParams==null ) currParams = new RawDLParams(); 
		
		PamDataBlock rawDataBlock = sourcePane.getSource();
		if (rawDataBlock == null){
			PamDialogFX.showWarning("There is no datablock set. The segmenter must have a datablock set."); 
			return null;
		}

		sourcePane.getParams(currParams.groupedSourceParams); 

		currParams.modelSelection = dlModelBox.getSelectionModel().getSelectedIndex(); 
		
		if (windowLength.getValue() == 0 || hopLength.getValue()==0){
			PamDialogFX.showWarning("Neither the hop nor window length can be zero"); 
			return null;
		}

		currParams.rawSampleSize = windowLength.getValue(); 
		currParams.sampleHop = hopLength.getValue(); 
		
		//update any changes
		this.dlControl.getDLModels().get(dlModelBox.getSelectionModel().getSelectedIndex()).getModelUI().getParams(); 

		return currParams;
	}


	@Override
	public void setParams(RawDLParams currParams) {
		
		sourcePane.setParams(currParams.groupedSourceParams);

		dlModelBox.getSelectionModel().select(currParams.modelSelection);

		windowLength.getValueFactory().setValue(currParams.rawSampleSize);

		hopLength.getValueFactory().setValue(currParams.sampleHop);
		
		setClassifierPane(); 
	}


	@Override
	public String getName() {
		return "Raw Deep Learning Parameters";
	}


	@Override
	public Node getContentNode() {
		return mainPane;
	}


	@Override
	public void paneInitialized() {
		// TODO Auto-generated method stub
	}

}
