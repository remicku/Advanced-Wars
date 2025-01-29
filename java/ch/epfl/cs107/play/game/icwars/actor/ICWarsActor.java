package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;

import java.util.List;
import java.util.Collections;

public abstract class ICWarsActor extends MovableAreaEntity {

    public enum Faction {
        FRIENDLY("friendly"),
        ENEMY("enemy");
        private final String value;
        Faction(String value){
            this.value=value;
        }
        public String value(){
            return value;
        }
    }

    private final Faction faction;

    /**
     * ICWarsActor Constructor
     * Set the actor as default orientation up when entering the area
     * @param area (Area): area where the actor will be
     * @param position (DiscreteCoordinates): position of the actor in the area
     * @param faction (Faction): faction of the actor
     */
    public ICWarsActor(Area area, DiscreteCoordinates position, Faction faction) {
        super(area, Orientation.UP,position);
        this.faction= faction;
    }

    /**
     * Register the actor in the area when entering it and set his position
     * @param area (Area): area entered by the actor
     * @param position (DiscreteCoordinates): position of the actor when he enters the area
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        this.setOwnerArea(area);
        this.setCurrentPosition(position.toVector());
    }

    /**
     * Unregister an actor when leaving the area
     */
    public void leaveArea(){
        this.getOwnerArea().unregisterActor(this);
    }

    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * @return the faction of an actor
     */
    public Faction getFaction(){
        return this.faction;
    }

}
