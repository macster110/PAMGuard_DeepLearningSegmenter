package rawDeepLearningClassifer.dlClassification.soundSpot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.controlsfx.control.PopOver;

import PamController.SettingsPane;
import PamView.dialog.warn.WarnOnce;
import ai.djl.Device;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pamViewFX.PamGuiManagerFX;
import pamViewFX.fxGlyphs.PamGlyphDude;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamButton;
import pamViewFX.fxNodes.PamGridPane;
import pamViewFX.fxNodes.PamHBox;
import pamViewFX.fxNodes.PamSpinner;
import pamViewFX.fxNodes.PamVBox;

/**
 * Settings pane for SoundSpot
 * 
 * @author Jamie Macaulay
 *
 */
public class SoundSpotPane extends SettingsPane<PamSoundSpotParams> {
	
	/**
	 * The main pane for the Soundspot sETTINGS
	 */
	private PamBorderPane mainPane;
	
	/**
	 * The directory chooser
	 */
	private FileChooser fileChooser;

	/**
	 * Currently selected file.
	 */
	private File currentSelectedFile = new File(System.getProperty("user.home"));

	/**
	 * The label showing the path to the file. 
	 */
	private Label pathLabel;

	/**
	 * Detection spinner
	 */
	private PamSpinner<Double> detectionSpinner;
//
//	/**
//	 * True to use CUDA
//	 */
//	private CheckBox useCuda;

	/**
	 * The currently held parameters. 
	 */
	private PamSoundSpotParams currentParams;

	/**
	 * A pop over to show the advanced pane. 
	 */
	private PopOver popOver;

	/**
	 * Advanced settings pane. 
	 */
	private SoundSpotAdvPane advSettingsPane;

	/**
	 * The sound spot classifier. 
	 */
	private SoundSpotClassifier soundSpotClassifier;

	private PamSoundSpotParams paramsClone; 

	public SoundSpotPane(SoundSpotClassifier soundSpotClassifier) {
		super(null);
		this.soundSpotClassifier=soundSpotClassifier; 
		mainPane = createPane(); 
		//the directory chooser. 
		fileChooser = new FileChooser();
		fileChooser.setTitle("Classifier Model Location");
		advSettingsPane= new SoundSpotAdvPane(); 
	}

	/**
	 * Create the main pane. 
	 * @return the settings pane.
	 */
	private PamBorderPane createPane() {
		PamBorderPane mainPane = new PamBorderPane(); 


		Label classiferInfoLabel = new Label("SoundSpot Classifier"); 
		PamGuiManagerFX.titleFont2style(classiferInfoLabel);

		/**Basic classifier info**/
		pathLabel = new Label("No classifier file selected"); 
		PamButton pamButton = new PamButton("", PamGlyphDude.createPamGlyph(MaterialDesignIcon.FILE, PamGuiManagerFX.iconSize-3)); 
		pamButton.setMinWidth(30);
		pamButton.setTooltip(new Tooltip("Browse to selcect a .pk model file"));

		pamButton.setOnAction((action)->{
			
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Pytorch Model", "*.pk")); 

			Path path = currentSelectedFile.toPath();
			if(path!=null && Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				fileChooser.setInitialDirectory(new File(currentSelectedFile.getParent()));
			}
			else { 
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}
			
			
			File file = fileChooser.showOpenDialog(null); 
			
			if (file==null) {
				return; 
			}

			newModelSelected(file); 
						
			updatePathLabel(); 

		});

		PamHBox hBox = new PamHBox(); 
		hBox.setSpacing(5);
		hBox.getChildren().addAll(pathLabel, pamButton); 
		hBox.setAlignment(Pos.CENTER_RIGHT);
		
				
		PamButton advButton = new PamButton("", PamGlyphDude.createPamGlyph(MaterialDesignIcon.SETTINGS, PamGuiManagerFX.iconSize-3)); 
		advButton.setMinWidth(30);
		advButton.setOnAction((action)->{
			//pop up window with adv settings.
			showAdvPane(advButton); 
			
		});
		PamHBox advSettingsBox = new PamHBox(); 
		advSettingsBox.setSpacing(5);
		advSettingsBox.getChildren().addAll(new Label("Advanced"), advButton); 
		advSettingsBox.setAlignment(Pos.CENTER);

		PamBorderPane advSettings = new PamBorderPane(); 
//		advSettings.setLeft(useCuda);
//		PamBorderPane.setAlignment(useCuda, Pos.CENTER);
		advSettings.setRight(advSettingsBox);
		
		
		/**Classification thresholds etc to set.**/
		Label classiferInfoLabel2 = new Label("Binary Classification Threshold"); 
		//PamGuiManagerFX.titleFont2style(classiferInfoLabel2);

		/**
		 * There are tow classifiers the detector and the classifier
		 */
		PamGridPane gridPane = new PamGridPane(); 
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		gridPane.add(new Label("Min. probability"), 0, 0);
		gridPane.add(detectionSpinner = new PamSpinner<Double>(0.0, 1.0, 0.9, 0.1), 1, 0);
		detectionSpinner.setPrefWidth(100);
		detectionSpinner.setEditable(true);
		detectionSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

		PamVBox vBox = new PamVBox(); 
		vBox.setSpacing(5);
		vBox.getChildren().addAll(classiferInfoLabel, hBox, advSettings, classiferInfoLabel2, gridPane); 

		mainPane.setCenter(vBox);

		return mainPane; 
	}

	/**
	 * Called whenever a new model has been selected
	 * @param file
	 */
	private void newModelSelected(File file) {
		currentSelectedFile = file; 
		this.soundSpotClassifier.newModelSelected(file); 
		
		paramsClone = new PamSoundSpotParams(); 
		//prep the model with current parameters; 
		this.soundSpotClassifier.getSoundSpotWorker().prepModel(getParams(paramsClone));
		//get the model tansforms calculated from the model by SoundSpoyWorker and apply them to our temporary params clone. 
		paramsClone.dlTransfroms = this.soundSpotClassifier.getSoundSpotWorker().getModelTransforms(); 
		///se the advanced pane parameters. 
		this.setParams(paramsClone);
		
	}

	/**
	 * Sho0w the advanced settings. 
	 * @param advSettingsButton - the advanced settings. 
	 */
	public void showAdvPane(PamButton advSettingsButton) {

			if (popOver==null) {
				popOver = new PopOver(); 
				popOver.setContentNode(advSettingsPane.getContentNode());
			}

			popOver.showingProperty().addListener((obs, old, newval)->{

			});

			popOver.show(advSettingsButton);
	}

	/**
	 * Update the path label and tool tip text; 
	 */
	private void updatePathLabel() {
		if (currentSelectedFile==null) {
			pathLabel.setText("No classifier file selected");
			pathLabel.setTooltip(new Tooltip("Use the Browse... button to select a .pk file"));

		}
		pathLabel .setText(this.currentSelectedFile.getName()); 
		pathLabel.setTooltip(new Tooltip(this.currentSelectedFile.getPath() + "\n" +" Processor " + Device.defaultDevice().toString()));
	}

	@Override
	public PamSoundSpotParams getParams(PamSoundSpotParams currParams) {

		if (currentSelectedFile==null) {
			//uuurgh need to sort this out with FX stuff
			WarnOnce.showWarningFX(null,  "No Model File",  "There is no model file selected in the path: Please select a compatible model" , AlertType.ERROR);

		}
		else {
			currParams.modelPath =  currentSelectedFile.getPath(); 
		}

		currParams.threshold = detectionSpinner.getValue(); 

//		currParams.useCUDA = useCuda.isSelected(); 

		return currParams;
	}

	@Override
	public void setParams(PamSoundSpotParams currParams) {
		this.currentParams = currParams.clone(); 

		if (currentParams.modelPath!=null) {
			currentSelectedFile = new File(currentParams.modelPath);
		}

		updatePathLabel();

		pathLabel .setText(this.currentSelectedFile.getPath()); 

		detectionSpinner.getValueFactory().setValue(Double.valueOf(currParams.threshold));
		
		//set the params on the advanced pane. 
		this.advSettingsPane.setParams(currParams);
	}
	
	@Override
	public String getName() {
		return "Sound Spot Settings";
	}

	@Override
	public Node getContentNode() {
		return this.mainPane;
	}

	@Override
	public void paneInitialized() {
		// TODO Auto-generated method stub
		
	}


}
