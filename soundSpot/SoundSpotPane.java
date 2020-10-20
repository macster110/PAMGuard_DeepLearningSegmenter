package rawDeepLearningClassifer.soundSpot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.controlsfx.control.PopOver;

import PamController.PamController;
import PamController.SettingsPane;
import PamView.dialog.PamDialog;
import PamView.dialog.warn.WarnOnce;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
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
import rawDeepLearningClassifer.orcaSpot.OrcaSpotParams2;

/**
 * Settings pane for SoundSpot
 * 
 * @author Jamie Macaulay
 *
 */
public class SoundSpotPane extends SettingsPane<SoundSpotParams> {
	
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

	/**
	 * True to use CUDA
	 */
	private CheckBox useCuda;

	/**
	 * The currently held parameters. 
	 */
	private SoundSpotParams currentParams;

	/**
	 * A pop over to show the advanced pane. 
	 */
	private PopOver popOver;

	/**
	 * Advanced settings pane. 
	 */
	private SoundSpotAdvPane advSettingsPane; 

	public SoundSpotPane(Object ownerWindow) {
		super(ownerWindow);
		mainPane = createPane(); 
		//the directory chooser. 
		fileChooser = new FileChooser();
		fileChooser.setTitle("Classifier Model Location");
		advSettingsPane= new SoundSpotAdvPane(null); 
	}

	/**
	 * Create the main pane. 
	 * @return the settings pane.
	 */
	private PamBorderPane createPane() {
		PamBorderPane mainPane = new PamBorderPane(); 


		Label classiferInfoLabel = new Label("SoundSpot Classifier"); 
		classiferInfoLabel.setFont(PamGuiManagerFX.titleFontSize2);

		/**Basic classifier info**/
		pathLabel = new Label("No classifier file selected"); 
		PamButton pamButton = new PamButton("Browse..."); 

		pamButton.setOnAction((action)->{
			
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Pytorch Model", "*.pk")); 

			Path path = currentSelectedFile.toPath();
			if(path!=null && Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				fileChooser.setInitialDirectory(currentSelectedFile);
			}
			else { 
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}
			
			
			File file = fileChooser.showOpenDialog(null); 
			
			if (file==null) {
				return; 
			}

			currentSelectedFile = file; 
			
			updatePathLabel(); 

		});

		PamHBox hBox = new PamHBox(); 
		hBox.setSpacing(5);
		hBox.getChildren().addAll(pathLabel, pamButton); 
		hBox.setAlignment(Pos.CENTER_RIGHT);
		
		
		useCuda = new CheckBox("Use CUDA"); 
		
		PamButton advButton = new PamButton("", PamGlyphDude.createPamGlyph(MaterialDesignIcon.SETTINGS, PamGuiManagerFX.iconSize-3)); 
		advButton.setOnAction((action)->{
			//pop up window with adv settings.
			showAdvPane(advButton); 
			
		});
		PamHBox advSettingsBox = new PamHBox(); 
		advSettingsBox.setSpacing(5);
		advSettingsBox.getChildren().addAll(new Label("Advanced"), advButton); 
		advSettingsBox.setAlignment(Pos.CENTER);

		PamBorderPane advSettings = new PamBorderPane(); 
		advSettings.setLeft(useCuda);
		PamBorderPane.setAlignment(useCuda, Pos.CENTER);
		advSettings.setRight(advSettingsBox);
		
		
		/**Classification thresholds etc to set.**/
		Label classiferInfoLabel2 = new Label("Binary Classification Threshold"); 
		classiferInfoLabel2.setFont(PamGuiManagerFX.titleFontSize2);

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
		pathLabel.setTooltip(new Tooltip(this.currentSelectedFile.getPath()));
	}

	@Override
	public SoundSpotParams getParams(SoundSpotParams currParams) {

		if (currentSelectedFile==null) {
			//uuurgh need to sort this out with FX stuff
			WarnOnce.showWarningFX(null,  "No Model File",  "There is no model file selected in the path: Please select a compatible model" , AlertType.ERROR);

		}
		else {
			currParams.modelPath =  currentSelectedFile.getPath(); 
		}

		currParams.threshold = detectionSpinner.getValue(); 

		currParams.useCUDA = useCuda.isSelected(); 

		return currParams;
	}

	@Override
	public void setParams(SoundSpotParams currParams) {
		this.currentParams = currParams.clone(); 

		if (currentParams.modelPath!=null) {
		currentSelectedFile = new File(currentParams.modelPath);
		}

		updatePathLabel();

		pathLabel .setText(this.currentSelectedFile.getPath()); 

		detectionSpinner.getValueFactory().setValue(Double.valueOf(currParams.threshold));

		useCuda.setSelected(currentParams.useCUDA);

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
