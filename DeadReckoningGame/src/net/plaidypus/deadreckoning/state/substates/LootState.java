package net.plaidypus.deadreckoning.state.substates;

import java.util.ArrayList;

import net.plaidypus.deadreckoning.hudelements.HudElement;
import net.plaidypus.deadreckoning.hudelements.game.substates.ItemGridElement;
import net.plaidypus.deadreckoning.hudelements.game.substates.ReturnToGameElement;
import net.plaidypus.deadreckoning.items.Item;
import net.plaidypus.deadreckoning.state.HudLayersState;
import net.plaidypus.deadreckoning.state.PrebakedHudLayersState;
import net.plaidypus.deadreckoning.utilities.KeyConfig;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class LootState extends PrebakedHudLayersState{
	
	public ItemGridElement gridA, gridB;
	
	public LootState(int stateID, ArrayList<HudElement> background)
			throws SlickException {
		super(stateID, background);
	}
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException{
		super.update(container, game, delta);
		
		if(HudLayersState.doubleClick ||container.getInput().isKeyPressed(Input.KEY_ENTER)){
			if(gridA.getSelected()!=null){
				gridB.addItem(gridA.removeItem(gridA.getSelected()));
				gridB.contents = Item.collapseItemArray(gridB.contents);
			}
			else if(gridB.getClicked()!=null){
				gridA.addItem(gridB.removeItem(gridB.getSelected()));
				gridA.contents = Item.collapseItemArray(gridA.contents);
			}
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_A)) {
			gridA.contents.addAll(gridB.contents);
			gridB.contents.clear();
			gridA.contents = Item.collapseItemArray(gridA.contents);
		}
	}
	
	@Override
	protected ArrayList<HudElement> makeContents() throws SlickException {
		ArrayList<HudElement> elements = new ArrayList<HudElement>(0);
		gridA= new ItemGridElement(-241, -132,HudElement.CENTER_CENTER);
		gridB = new ItemGridElement(50, -132, HudElement.CENTER_CENTER);

		elements.add(gridA);
		elements.add(gridB);
		elements.add(new ReturnToGameElement(KeyConfig.LOOT));
		
		return elements;
	}

}
