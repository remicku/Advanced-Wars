package ch.epfl.cs107.play.game.icwars.actor.players;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class ICWarsPlayer extends ICWarsActor implements Interactor {

    protected List<Unit> units;
    protected Sprite sprite;
    protected State state;


    public enum State{
        IDLE,
        NORMAL,
        SELECT_CELL,
        MOVE_UNIT,
        ACTION_SELECTION,
        ACTION,
    }


    /**
     * ICWarsPlayer Constructor
     * @param area (Area): area where the player will be
     * @param position (DiscreteCoordinates): position of the player
     * @param faction (Faction): enemy or ally
     * @param units (Unit): units owned by the player
     */
    public ICWarsPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit ... units) {
        super(area, position, faction);
        this.units= new LinkedList<>();
        this.units.addAll(Arrays.asList(units));
        this.state =State.IDLE;

    }


     /*public void addUnit(Unit u,Area area, DiscreteCoordinates coords){
        this.units.add(u);
        //enterAreaIndividual(area, coords,u);
    }*/

    /**
     * Set the current state of the player
     * @param newState (State): new state of the player
     */
    public void setState(State newState){
        state =newState;
    }

    /**
     * @param deltaTime time to make the update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        unregisterUnits();
    }

    /**
     * Leave the area when no more units
     */
    @Override
    public void leaveArea() {
        super.leaveArea();
        assert (!units.isEmpty());
            for(Unit u:units){
                ((ICWarsArea)getOwnerArea()).removeFromUnitList(u);
                u.leaveArea();
            }



    }

    /**
     * Initialize units in the area
     * @param area (Area): area where the units will spawn
     * @param position (DiscreteCoordinates): units position
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        super.enterArea(area,position);
        for (Unit u:units){
            ((ICWarsArea)getOwnerArea()).addToUnitList(u);
            u.enterArea(area, u.getSpawnPosition());
        }
    }
    //Attempt to  create a function that adds a unique unit in the game
    /*
    public void enterAreaIndividual(Area area, DiscreteCoordinates position,Unit u){
        super.enterArea(area,position);
        ((ICWarsArea)getOwnerArea()).addToUnitList(u);
        u.enterArea(area, u.getSpawnPosition());
    }
*/
    /**
     * Unregister units of the area when dead
     */
    private void unregisterUnits(){
        for(Unit u : units){
            if(u.isDead()){
                ((ICWarsArea)getOwnerArea()).removeFromUnitList(u);
                units.remove(u);
                u.leaveArea();
            }
        }
        if(units.size() == 0){
            System.out.println("No more units :D");

        }



    }
    /*
    private void registerUnits(){
        for(Unit u : units){
            getOwnerArea().registerActor(u);
        }
    }
    */

    /**
     * The cursor becomes receptive to the keyboard when it selects a unit
     * @param coordinates (DiscreteCoordinates): cursor's coordinates
     */
    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        if(state ==State.SELECT_CELL)
            state =State.NORMAL;
    }

    /**
     * Reset units as not played
     */
    public void resetUnits(){
        for(Unit u: units)
            u.setPlayed(false);

    }

    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

    public void startTurn(){
        this.state = State.NORMAL;
    }

    /**
     * @return the state of the player
     */
    public State getState(){
        return this.state;
    }

    /**
     * The player is defeated when he has no more units
     * @return true if no more units
     */
    public boolean isDefeated(){
        return units.isEmpty();
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        v.interactWith(this);
    }




}
