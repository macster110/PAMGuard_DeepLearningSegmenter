package rawDeepLearningClassifer.layoutFX;

import PamView.GeneralProjector;
import PamView.symbol.StandardSymbolChooser;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.SymbolData;
import PamguardMVC.PamDataBlock;
import rawDeepLearningClassifer.DLControl;

public class DLSymbolManager extends StandardSymbolManager {

	/**
	 * Reference ot the click control. 
	 */
	private DLControl dlControl;
	
	/**
	 * Flag to colour clicks by their frequency. 
	 */
	public static final int COLOUR_BY_FREQ= 5;

	public DLSymbolManager(DLControl dlControl, PamDataBlock pamDataBlock) {
		super(pamDataBlock, new SymbolData());
		this.dlControl = dlControl;
		addSymbolOption(HAS_CHANNEL_OPTIONS);
		addSymbolOption(HAS_SPECIAL_COLOUR);
		addSymbolOption(HAS_SYMBOL);
		super.setSpecialColourName("by probability");
	}
	

	@Override
	protected StandardSymbolChooser createSymbolChooser(String displayName, GeneralProjector projector) {
		return new StandardSymbolChooser(this, getPamDataBlock(), displayName, getDefaultSymbol(), projector);
	}
	

	
}
