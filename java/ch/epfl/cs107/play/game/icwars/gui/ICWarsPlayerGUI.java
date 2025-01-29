package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.unit.Action;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class ICWarsPlayerGUI implements Graphics {

    private final ICWarsPlayer player;
    private Unit selectedUnit;
    private final ICWarsInfoPanel infoPanel;
    private final ICWarsActionsPanel actionPanel;
    public static final float FONT_SIZE= 20.f;

    /**
     * ICWarsPlayerGUI constructor
     * @param cameraScaleFactor:
     * @param player: player selected
     */
    public ICWarsPlayerGUI(float cameraScaleFactor , ICWarsPlayer player){
        this.player=player;
        this.actionPanel=new ICWarsActionsPanel(cameraScaleFactor);
        this.infoPanel=new ICWarsInfoPanel(cameraScaleFactor);
    }

    /**
     * draws the action panel and info panel, also the path when a Unit wants to make a move
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if(selectedUnit!=null && player.getState()==ICWarsPlayer.State.MOVE_UNIT)
            this.selectedUnit.drawRangeAndPathTo(player.getCurrentCells().get(0), canvas);

        if(player.getState()== ICWarsPlayer.State.ACTION_SELECTION)
            this.actionPanel.draw(canvas);


        if(player.getState()==ICWarsPlayer.State.NORMAL||
                this.player.getState()==ICWarsPlayer.State.SELECT_CELL)
            this.infoPanel.draw(canvas);


    }

    /**
     * sets the unit that we want to show the panel to
     * @param unit: unit selected
     */
    public void setUnit(Unit unit) {
        this.infoPanel.setUnit(unit);
    }

    /**
     * sets the actions that we want to show in the panel
     * @param actions: List of actions that the units can do
     */
    public void setActions(List<Action> actions){
        this.actionPanel.setActions(actions);
    }

    /**
     *
     * @param unit: unit selected
     */
    public void setSelectedUnit(Unit unit){
        this.selectedUnit=unit;
    }

    /**
     * selects the cell where we want to show our panel
     * @param cellType: the cell type defined
     */
    public void setCurrentCell(ICWarsBehavior.ICWarsCellType cellType) {
        this.infoPanel.setCurrentCell(cellType);
    }

}