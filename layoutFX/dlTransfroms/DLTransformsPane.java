package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import java.util.ArrayList;
import java.util.List;

import org.jamdev.jtorch4pam.transforms.DLTransform;
import org.jamdev.jtorch4pam.transforms.DLTransform.DLTransformType;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.Pane;
import pamViewFX.fxGlyphs.PamGlyphDude;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamHBox;
import pamViewFX.fxNodes.PamVBox;
import pamViewFX.fxNodes.orderedList.PamDraggableList;

/**
 * Pane which allows users to create a series of transforms for 
 * raw data before inputting into a deep learning model. 
 * 
 * @author Jamie Macaulay 
 *
 */
public class DLTransformsPane extends PamBorderPane {
	
	PamDraggableList<DLTransformPane> draggablelistPane;
	
	/**
	 * the main holder pane. 
	 */
	private PamVBox mainPane;

	private Pane controlPane; 
	
	public DLTransformsPane() {
		this.mainPane = new PamVBox(); 
		
		this.setCenter(mainPane);
		controlPane = createControlPane(); 
	}
	
	/**
	 * Create the control pane that allows users to add a new transform. 
	 * @return the main control pane. 
	 */
	private Pane createControlPane() {
		
		Label label = new Label("Add Transform"); 
		
		MenuButton splitButton = new MenuButton(); 
		splitButton.setGraphic(PamGlyphDude.createPamGlyph(MaterialDesignIcon.PLUS));
		
		DLTransformType[]  dlTransformTypes = DLTransformType.values(); 
				
		MenuItem menuItem; 
 		for (int i=0; i<dlTransformTypes.length; i++) {
 			menuItem = new MenuItem(dlTransformTypes[i].toString()); 
 			final int k=i; 
 			menuItem.setOnAction((action)->{
 				addNewDLTransfrom(dlTransformTypes[k]); 
 			});
 			
 			splitButton.getItems().add(menuItem); 
		}
			
		PamHBox holder = new PamHBox(); 
		holder.setSpacing(10);
		holder.getChildren().addAll(label, splitButton);
		holder.setAlignment(Pos.CENTER_RIGHT);
		
		return holder; 
	}
	
	/**
	 * Add the new transform to the pane. 
	 * @param dlTransformType
	 */
	private void addNewDLTransfrom(DLTransformType dlTransformType) {
		// TODO Auto-generated method stub
	}

	/**
	 * Set the current transforms.
	 * 
	 * @param dlTransforms - the list of transforms (in order) to set
	 */
	public void setTransforms(ArrayList<DLTransform> dlTransforms) {
		
		ArrayList<DLTransformPane> dlTransformPanes = new ArrayList<DLTransformPane>(); 
		
		//create a pane for each transform
		for (int i=0; i<dlTransforms.size() ; i++) {
			dlTransformPanes.add(DataTransformPaneFactory.getSettingsPane(dlTransforms.get(i))); 
		}
		draggablelistPane = new PamDraggableList<DLTransformPane> (dlTransformPanes);
		
		mainPane.getChildren().clear();
		mainPane.getChildren().addAll(controlPane, draggablelistPane); 
		 
	}
	
	
	
	/**
	 * Get the current transforms. 
	 * 
	 * @param dlTransforms - the list of transforms (in order) to set
	 */
	public List<DLTransformPane>  getDLTransforms() {
		
		List<DLTransformPane> transforms = draggablelistPane.getSortedList(); 
		
		return transforms; 
	}


}
