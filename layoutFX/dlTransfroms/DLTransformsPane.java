package rawDeepLearningClassifer.layoutFX.dlTransfroms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jamdev.jtorch4pam.transforms.DLTransform;
import org.jamdev.jtorch4pam.transforms.DLTransform.DLTransformType;
import org.jamdev.jtorch4pam.transforms.FreqTransform;
import org.jamdev.jtorch4pam.transforms.WaveTransform;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
	
	/**
	 * The order of transforms has changed. 
	 */
	public static final short TRANSFORM_ORDER_CHANGE = 0x01; 

	/**
	 * A transform has had it's params changed. 
	 */
	public static final short TRANSFORM_SETTINGS_CHANGE = 0x02; 

	DLDraggableList draggablelistPane;

	/**
	 * the main holder pane. 
	 */
	private PamVBox mainPane;

	private Pane controlPane;

	private float sampleRate; 

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
	protected void addNewDLTransfrom(DLTransformType dlTransformType) {
		//System.out.println("DLTransformsPane: add a DL transform - TODO"); 
		draggablelistPane.addDraggablePane(DataTransformPaneFactory.getSettingsPane(dlTransformType, getSampleRate())); 
	}

	/**
	 * Get the sample rate. 
	 * @return the sample rate in samples per second. 
	 */
	private float getSampleRate() {
		return sampleRate;
	}

	/**
	 * Set the current transforms.
	 * 
	 * @param dlTransforms - the list of transforms (in order) to set
	 */
	public void setTransforms(ArrayList<DLTransform> dlTransforms) {

		ArrayList<DLTransformPane> dlTransformPanes = new ArrayList<DLTransformPane>(); 

		sampleRate=-1; 

		//create a pane for each transform
		for (int i=0; i<dlTransforms.size() ; i++) {
			//bit hackey bit try to set the sample rate...
			if ( sampleRate<0.0f && dlTransforms.get(i) instanceof WaveTransform && ((WaveTransform) dlTransforms.get(i)).getWaveData()!=null) {
				sampleRate = ((WaveTransform) dlTransforms.get(i)).getWaveData().getSampleRate(); 
			}

			dlTransformPanes.add(DataTransformPaneFactory.getSettingsPane(dlTransforms.get(i))); 
			dlTransformPanes.get(i).setParams(dlTransforms.get(i));
			dlTransformPanes.get(i).addSettingsListener(()->{
				newSettings(TRANSFORM_SETTINGS_CHANGE); 
			});
		}
		draggablelistPane = new DLDraggableList(dlTransformPanes);

		mainPane.getChildren().clear();
		mainPane.getChildren().addAll(controlPane, draggablelistPane); 

	}


	/**
	 * Called whenever there are new settings from one of the settings panes. 
	 */
	public void newSettings(int type) {
		// TODO Auto-generated method stub
	}

	/**
	 * Get parameters fror all transforms based on controls. 
	 */
	public void getParams() {
		List<DLTransformPane> transformPanes = draggablelistPane.getSortedList();
		for(int i=0; i<transformPanes.size() ; i++) {
			transformPanes.get(i).getParams(transformPanes.get(i).getDLTransform()); 
		}
	}

	/**
	 * Get the current transforms. 
	 * 
	 * @param dlTransforms - the list of transforms (in order) to set
	 */
	public ArrayList<DLTransform>  getDLTransforms() {

		if (draggablelistPane!=null) {
			List<DLTransformPane> transformPanes = draggablelistPane.getSortedList(); 

			ArrayList<DLTransform> transforms = new ArrayList<DLTransform>();
			for(int i=0; i<transformPanes.size() ; i++) {
				transforms.add(transformPanes.get(i).getDLTransform());	
			}
			return transforms;
		}

		return null; 
	}


	class DLDraggableList extends PamDraggableList<DLTransformPane>  {

		public DLDraggableList(List<DLTransformPane> panes) {
			super(panes);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void paneOrderChanged(boolean success) {
			newSettings(TRANSFORM_ORDER_CHANGE); 
		}

		@Override 
		public boolean canDrop(DLTransformPane source, int sourceIndex,  int targetIndex) {
			// if a wave transform has to be before any spectral transforms.
			
			//generate the new possible list of nodes. 
			List<DLTransformPane> newPossibleList = getTempSortedList(sourceIndex, targetIndex); 
		
			if (source.getDLTransform() instanceof WaveTransform) {
				//System.out.println("Target index: " + targetIndex); 
				//uuuurgh this is complicated because it depends on whether the transform itself is in the list or not...
	
				int i=0; 
				while (i<newPossibleList.indexOf(source)){
					//System.out.println("Wave i: " + i + " " + newPossibleList.get(i).getDLTransform().getDLTransformType()); 
					if (!(newPossibleList.get(i).getDLTransform() instanceof WaveTransform)) {
						return false; 
					}
					i++; 
				}
				return true; 
			}

			if (source.getDLTransform() instanceof FreqTransform) {
				//uuurgh this is complicated because it depends on whether the transform itself is in the list or not...
				int ii=newPossibleList.indexOf(source); 
				while (ii<newPossibleList.size()){
					//System.out.println("Freq. i: " + ii + " " + this.getSortedList().get(ii).getDLTransform().getDLTransformType()); 
					if (this.getSortedList().get(ii).getDLTransform() instanceof WaveTransform 
							|| this.getSortedList().get(ii).getDLTransform().getDLTransformType() == DLTransformType.SPECTROGRAM) {
						return false; 
					}
					ii++; 
				}
				return true; 
			}
			return true; 
		}

	}
}
