package rawDeepLearningClassifer.dlClassification.genericModel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pamViewFX.PamGuiManagerFX;
import pamViewFX.fxGlyphs.PamGlyphDude;
import pamViewFX.fxNodes.PamHBox;
import rawDeepLearningClassifer.dlClassification.soundSpot.StandardAdvModelPane;
import rawDeepLearningClassifer.dlClassification.soundSpot.StandardModelPane;
import rawDeepLearningClassifer.dlClassification.soundSpot.StandardModelParams;

/**
 * 
 * Settings pane for the generic pane. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class GenericModelPane extends StandardModelPane  {
	
	/**
	 * The extension filter for sound spot models. 
	 */
	private ArrayList<ExtensionFilter> extensionFilters;
	
	
	private GenericAdvPane advPane;


	private GenericDLClassifier genericDLClassifier;
	
	
	
	public GenericModelPane(GenericDLClassifier genericDLClassifier) {
		super(genericDLClassifier);
		
		this.genericDLClassifier = genericDLClassifier;
		
		//must add an additional import settings button. 
		extensionFilters = new ArrayList<ExtensionFilter>(); 
		
		//import the settings holder
		extensionFilters.add(new ExtensionFilter("TensorFlow Model", "*.pb")); 
		extensionFilters.add(new ExtensionFilter("Pytorch Model", 	"*.pk"));
	
		//this.getVBoxHolder().getChildren().add(2, new Label("Classifer Settings"));
		usedefaultSeg.setDisable(true); 
		defaultSegBox.setVisible(false);

		setAdvSettingsPane(advPane = new GenericAdvPane()); 
		
		advPane.setParams(genericDLClassifier.getGenericDLParams());
		
		
	}
	
	
	@Override
	public void newModelSelected(File file) {
		this.setCurrentSelectedFile(file);
		this.genericDLClassifier.newModelSelected(file); 

		this.setParamsClone(new StandardModelParams()); 
		//prep the model with current parameters; 
		genericDLClassifier.getGenericDLWorker().prepModel(getParams(getParamsClone()), genericDLClassifier.getDLControl());
		//get the model tansforms calculated from the model by SoundSpoyWorker and apply them to our temporary params clone. 
		//getParamsClone().dlTransfroms = this.genericDLClassifier.getGenericDLWorker().getModelTransforms(); 
		///set the advanced pane parameters. 
		getAdvSettingsPane().setParams(getParamsClone());
	}

	@Override
	public ArrayList<ExtensionFilter> getExtensionFilters() {
		return extensionFilters;
	}
}
	
	
	