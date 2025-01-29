package ch.epfl.cs107.play.game.icwars.area.areas;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level1 extends ICWarsArea {

    /**
     *
     * @return the title of the area
     */
    @Override
    public String getTitle() {
        return("icwars/Level1");
    }


    /**
     *
     * @return the Player spawn Position
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(2,6);//
    }

    /**
     * creates the area
     */
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        //registerActor(new SimpleGhost(new Vector(20, 10), "ghost.2"));
    }


    /**
     *
     * @return the Enemy spawn Position
     */
    public DiscreteCoordinates getEnemySpawnPosition() {
        return new DiscreteCoordinates(3,5);
    }
}
