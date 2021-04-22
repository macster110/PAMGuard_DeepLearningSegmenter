package rawDeepLearningClassifier.dlClassification.genericModel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;

import org.controlsfx.control.PopOver;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pamViewFX.PamGuiManagerFX;
import pamViewFX.fxGlyphs.PamGlyphDude;
import pamViewFX.fxNodes.PamButton;
import pamViewFX.fxNodes.PamHBox;
import rawDeepLearningClassifier.dlClassification.animalSpot.StandardAdvModelPane;
import rawDeepLearningClassifier.dlClassification.animalSpot.StandardModelPane;
import rawDeepLearningClassifier.dlClassification.animalSpot.StandardModelParams;

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
	
		//this.getVBoxHolder().getChildren().add(2, new Label("Classifier Settings"));
		usedefaultSeg.setDisable(true); 
		defaultSegBox.setVisible(false);

		setAdvSettingsPane(advPane = new GenericAdvPane(genericDLClassifier)); 
		
		advPane.setParams(genericDLClassifier.getGenericDLParams());
	}
	
	
	@Override
	public void showAdvPane(PamButton advSettingsButton) {
		//need to set this because the params are shared between two settings. 
		//a little messy but meh
		
		//System.out.println("GenericModelPane - showAdvPane getParamsClone(): 1 " + getParamsClone());
		//System.out.println("GenericModelPane - showAdvPane getParamsClone(): 2 " + getParams(getParamsClone()));

		this.setParamsClone(getParams(getParamsClone())); 
		
		super.showAdvPane(advSettingsButton);
		
	}
	
	@Override
	public void setParams(StandardModelParams currParams) {
		super.setParams(currParams);
		//System.out.println("advSettingsButton - showAdvPane setParams(): 1 " + getParamsClone());
	}

	
	@Override
	public void newModelSelected(File file) {
		this.setCurrentSelectedFile(file);
		this.genericDLClassifier.newModelSelected(file); 

		//this.setParamsClone(new GenericModelParams()); 
		//prep the model with current parameters; 
		genericDLClassifier.getGenericDLWorker().prepModel(getParams(getParamsClone()), genericDLClassifier.getDLControl());
		//get the model transforms calculated from the model by SoundSpoyWorker and apply them to our temporary paramters clone. 
		//getParamsClone().dlTransfroms = this.genericDLClassifier.getGenericDLWorker().getModelTransforms(); 
		///set the advanced pane parameters. 
		
		
		//now new paramters have been set in the prepModel functions so need to set new params now. 
		
		getAdvSettingsPane().setParams(getParamsClone());
	}

	
	@Override
	public ArrayList<ExtensionFilter> getExtensionFilters() {
		return extensionFilters;
	}
}
	
	
	