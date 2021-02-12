package rawDeepLearningClassifer.dlClassification.genericModel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pamViewFX.PamGuiManagerFX;
import pamViewFX.fxGlyphs.PamGlyphDude;
import pamViewFX.fxNodes.PamHBox;
import rawDeepLearningClassifer.dlClassification.soundSpot.StandardModelPane;

/**
 * 
 * Settings pane for the generic pane. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class GenericModelPane extends StandardModelPane  {
	
	/**
	 * Currently selected file.
	 */
	private File currentSettingsFile = new File(System.getProperty("user.home"));
	
	
	/**
	 * The extension filter for sound spot models. 
	 */
	private ArrayList<ExtensionFilter> extensionFilters;
	
	/**
	 * The fiel chooser. 
	 */
	private FileChooser fileChooser; 
	
	
	public GenericModelPane(GenericDLClassifier genericDLClassifier) {
		super(genericDLClassifier);
		
		//must add an additional import settings button. 
		extensionFilters = new ArrayList<ExtensionFilter>(); 
		
		//import the settings holder
		extensionFilters.add(new ExtensionFilter("TensorFlow Model", "*.pb")); 
		extensionFilters.add(new ExtensionFilter("Pytorch Model", 	"*.pk"));
		
		//import the settings holder.
		this.getVBoxHolder().getChildren().add(1, createSettingsImportPane());
	
	}
	
	public Pane createSettingsImportPane() {
		
		//import the settings holder. 
		PamHBox importSettingsHolder = new PamHBox(); 
		importSettingsHolder.setSpacing(5);
		
		
		ArrayList<ExtensionFilter> extensionFilters = new ArrayList<ExtensionFilter>(); 
		extensionFilters.add(new ExtensionFilter("Deep Learning Settings File", "*.pgtf")); 
	
		//import the settings holder.
		Button button = new Button(); 
		button.setGraphic(PamGlyphDude.createPamGlyph(MaterialDesignIcon.FILE_XML, PamGuiManagerFX.iconSize));
		button.setOnAction((action)->{
			
			//open a settings file. 
			fileChooser.getExtensionFilters().addAll(getExtensionFilters()); 

			Path path = currentSettingsFile.toPath();
			
			if(path!=null && Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				fileChooser.setInitialDirectory(new File(currentSettingsFile.getParent()));
			}
			else { 
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}

			File file = fileChooser.showOpenDialog(null); 

			if (file==null) {
				return; 
			}

			newSettingsFile(file); 
			
		});
	
		importSettingsHolder.getChildren().add(new Label("Import settings")); 
		importSettingsHolder.getChildren().add(button); 
		
		return importSettingsHolder; 
	}

	private void newSettingsFile(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<ExtensionFilter> getExtensionFilters() {
		return extensionFilters;
	}

	@Override
	public void newModelSelected(File file) {
		//the new model is selected
	}
	
}
	
	
	