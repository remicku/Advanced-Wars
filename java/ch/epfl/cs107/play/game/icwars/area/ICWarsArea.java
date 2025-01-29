package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class ICWarsArea extends Area {

    public static final float CAMERA_SCALE_FACTOR=10.f;
    protected List<Unit> units = new LinkedList<>();

    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void  createArea();

    public float getCameraScaleFactor() {
        return CAMERA_SCALE_FACTOR;
    }

    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    public abstract DiscreteCoordinates getEnemySpawnPosition();

    /// Demo2Area implements Playable



    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            ICWarsBehavior behavior = new ICWarsBehavior(window, getTitle());
            setBehavior(behavior);
            createArea();
            return true;
        }
        return false;
    }

    public void addToUnitList(Unit unit){
        units.add(unit);
    }
    public void removeFromUnitList(Unit unit){
        units.remove(unit);
    }

    public void damageUnitByIndex(int index, int damage){
        Unit selectedUnit= units.get(index);
        selectedUnit.damage(damage);
    }

    /**
     * @return the closest DiscreteCoordinates of an enemy
     */

    public DiscreteCoordinates getClosestEnemy(Unit unit){



        DiscreteCoordinates unitCoords= unit.getCurrentCells().get(0);
        // https://cs.stackexchange.com/questions/124828/write-an-algorithm-for-finding-the-minimum-element
        DiscreteCoordinates selectedEnemyCoords= unitCoords;
        float distance= Float.MAX_VALUE;
        for(Unit u : units){
            if(u.getFaction()!=unit.getFaction()){

                DiscreteCoordinates enemyCoords = u.getCurrentCells().get(0);
                float newDistance = DiscreteCoordinates.distanceBetween(unitCoords,enemyCoords);
                if(newDistance<=distance){

                    selectedEnemyCoords=enemyCoords;
                    distance=newDistance;
                }

            }
        }
        //System.out.println(selectedEnemyCoords);
        return selectedEnemyCoords;
    }

    public void centerCamera(int index){
        if(units.size() > index)
            this.setViewCandidate(units.get(index));
    }

    /**
     *
     * @param unit : the unit selected
     * @return : the enemy unit which is dead
     */
    public int getLowHpEnemy(Unit unit){

        int unitIndex=0;

        int MinHp=Integer.MAX_VALUE;
        for(Unit u : units) {
            DiscreteCoordinates enemyCoords= u.getCurrentCells().get(0);
            if((unit.getFaction()!=u.getFaction())&&(unit.isInRange(enemyCoords)))
                if(u.getHp()<=MinHp){
                    unitIndex= units.indexOf(u);
                    MinHp= u.getHp();
                }

        }
        return unitIndex;
    }

    /**
     *
     * @param unit
     * @return: The enemy List
     */
    public ArrayList<Integer> getEnemiesIndex(Unit unit){
        ArrayList<Integer> indexList = new ArrayList<>();
        for(Unit u : units) {

            DiscreteCoordinates enemyCoords= u.getCurrentCells().get(0);
            if((unit.isInRange(enemyCoords))&&(unit.getFaction()!=u.getFaction()))
                indexList.add(units.indexOf(u));

        }
        return indexList;
    }




}

