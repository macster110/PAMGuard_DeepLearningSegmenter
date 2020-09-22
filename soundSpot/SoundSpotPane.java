package rawDeepLearningClassifer.soundSpot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import PamController.SettingsPane;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pamViewFX.PamGuiManagerFX;
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

	private CheckBox useCuda; 

	public SoundSpotPane(Object ownerWindow) {
		super(ownerWindow);
		mainPane = createPane(); 
		//the directory chooser. 
		fileChooser = new FileChooser();
		fileChooser.setTitle("Classifier Model Location");
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
		Label locationLabel = new Label("No classifier file selected"); 
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

			locationLabel.setText(file.getName());

		});

		PamHBox hBox = new PamHBox(); 
		hBox.setSpacing(5);
		hBox.getChildren().addAll(locationLabel, pamButton); 
		hBox.setAlignment(Pos.CENTER_RIGHT);

		useCuda = new CheckBox("Use CUDA"); 

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
		vBox.getChildren().addAll(classiferInfoLabel, hBox, useCuda, classiferInfoLabel2, gridPane); 

		mainPane.setCenter(vBox);

		return mainPane; 
	}

	@Override
	public SoundSpotParams getParams(SoundSpotParams currParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParams(SoundSpotParams input) {
		// TODO Auto-generated method stub
		
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
