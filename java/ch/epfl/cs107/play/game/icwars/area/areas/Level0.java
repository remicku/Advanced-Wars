package ch.epfl.cs107.play.game.icwars.area.areas;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0 extends ICWarsArea {

    /**
     *
     * @return the title of the area
     */
    @Override
    public String getTitle() {
        return ("icwars/Level0");
    }

    /**
     * Creates the area
     */
    @Override
    protected void createArea() {

        registerActor(new Background(this));

    }

    /**
     *
     * @return the Player spawn Position
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        // (0,0)
        return new DiscreteCoordinates(0,0);
    }

    /**
     *
     * @return the Enemy spawn Position
     */
    public DiscreteCoordinates getEnemySpawnPosition() {
        return new DiscreteCoordinates(7,4);
    }

}
