package ch.epfl.cs107.play.game.icwars.handler;

import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;

public interface ICWarsInteractionVisitor extends AreaInteractionVisitor {

    default void interactWith(ICWarsBehavior.ICWarsCell cell){}
    default void interactWith(Unit unit) {}

}
