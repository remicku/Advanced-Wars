package ch.epfl.cs107.play.game.icwars.actor.players;

import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.window.Keyboard;


import java.util.List;

public class RealPlayer extends ICWarsPlayer {

    private final static int MOVE_DURATION = 8;
    protected Unit selectedUnit;
    private final ICWarsPlayerGUI gui;
    private final Keyboard keyboard;

    private Integer actionIndex;
    private final Faction faction;
    private final ICWarsPlayerInteractionHandler handler;

    /**
     * RealPlayer Constructor
     * @param area (Area): define the area where is the player
     * @param position (DiscreteCoordinates): define the position of the player
     * @param faction (Faction): enemy or ally
     * @param units (Unit): units of the player
     */
    public RealPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit... units) {
        super(area, position, faction, units);
        this.keyboard = getOwnerArea().getKeyboard();
        this.handler = new ICWarsPlayerInteractionHandler();
        this.gui =new ICWarsPlayerGUI(10f,this);
        String spriteName = "icwars/"+
                ((faction.value().equals("enemy"))?
                        faction.value(): "ally")+"Cursor";
        this.sprite = new Sprite(spriteName,1f,1f,this);
        this.faction=faction;

    }

    /**
     * @return : the faction of the player
     */
    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        changeState(deltaTime);

    }

    /**
     * @param orientation: the orientation of the player
     * @param b : the button selected
     */
    private void moveIfPressed(Orientation orientation, Button b){
        if(b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        gui.setUnit(null);
    }


    public void interactWith (Interactable o){
        if(!isDisplacementOccurs())
            o.acceptInteraction(handler);
    }

    @Override
    public void draw(Canvas canvas) {
        if(state != State.IDLE){
            sprite.draw(canvas);
            gui.draw(canvas);
        }
        if(state==State.ACTION && actionIndex!=null)
            selectedUnit.drawAction(canvas,actionIndex);

    }

    /**
     * moves the cursor by the assigned buttons
     */
    private void move(){
        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
    }

    /**
     * what the player must do according to his states
     * @param deltaTime
     */
    public void changeState(float deltaTime) {

        switch (this.state) {
            // Case that the player is in IDLE
            case IDLE :
                selectedUnit = null;
                actionIndex = null;
                break;
            // Case the player is in NORMAL
            case NORMAL :
                move();
                centerCamera();

                if (keyboard.get(Keyboard.TAB).isReleased())
                    state = State.IDLE;

                if (keyboard.get(Keyboard.ENTER).isReleased())
                    state = State.SELECT_CELL;


                //Case the player is in SELECT_CELL
                break;
            case SELECT_CELL :
                move();
                centerCamera();
                if (selectedUnit != null && !selectedUnit.isPlayed())
                    state = State.MOVE_UNIT;
                // Case the player is in MOVE_UNIT
                break;
            case MOVE_UNIT :
                move();
                centerCamera();
                if (keyboard.get(Keyboard.TAB).isReleased())
                    state = State.NORMAL;
                if (keyboard.get(Keyboard.ENTER).isReleased()) {
                    if (selectedUnit.changePosition(getCurrentMainCellCoordinates())) {
                        state = State.ACTION_SELECTION;
                        selectedUnit.setActionInGUI(gui);
                    }
                }

                //Case the player is in ACTION_SELECTION
                break;
            case ACTION_SELECTION:
                actionIndex = selectedUnit.getActionIndex(keyboard, this);
                break;
            case ACTION:
                selectedUnit.makeIndexAction(actionIndex, deltaTime, this, keyboard);
                break;
        }

    }


    private class ICWarsPlayerInteractionHandler implements ICWarsInteractionVisitor{
        /**
         * units agrees or not to interact with the actor
         * @param unit: the unit who'll interact
         */
        public void interactWith(Unit unit){
            if(state==State.SELECT_CELL && unit.getFaction()==faction){
                gui.setSelectedUnit(unit);
                selectedUnit=unit;
            }
            gui.setUnit(unit);
        }

        /**
         * Interaction with the cell
         * @param cell:
         */
        public void interactWith(ICWarsBehavior.ICWarsCell cell){
            gui.setCurrentCell(cell.getType());
        }

    }


}
