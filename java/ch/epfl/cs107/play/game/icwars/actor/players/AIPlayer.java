package ch.epfl.cs107.play.game.icwars.actor.players;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class AIPlayer extends ICWarsPlayer {

    private Unit selectedUnit;
    private int unitIndex = 0;
    private boolean counting;
    private float counter;


    private final Faction faction;

    /**
     * AIPlayer Constructor
     * @param area (Area): area where the unit will be
     * @param position (DiscreteCoordinates): position of the unit in the area
     * @param faction (Faction): faction of the unit
     * @param units (Units): list
     */
    public AIPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit... units) {
        super(area, position, faction, units);
        this.faction = faction;
        if (faction.value().equals("enemy")) {
            sprite = new Sprite("icwars/" + faction.value() + "Cursor", 1f, 1f, this);
        } else {
            sprite = new Sprite("icwars/allyCursor", 1f, 1f, this);
        }

    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateState(deltaTime);
        //System.out.println(currentState);
    }

    @Override
    public void draw(Canvas canvas) {
        if (state != State.IDLE) {
            sprite.draw(canvas);
        }
    }


    /**
     * Ensures that value time elapsed before returning true
     * @param value waiting time (in seconds)
     * @param dt elapsed time
     * @return true if value seconds has elapsed , false otherwise
     */
    private boolean waitFor(float value, float dt) {
        if (counting) {
            counter += dt;
            if (counter > value) {
                counting = false;
                return true;
            }
        } else {
            counter = 0f;
            counting = true;
        }
        return false;

    }


    @Override
    public void interactWith(Interactable other) {

    }

    /**
     *
     * @param unit AI unit
     * @param coordEnemy Coords of the closest unit of RealPlayer (which is the enemy now)
     * @return new Unit position (close to the enemy)
     */
    private void moveUnit(Unit unit, DiscreteCoordinates coordEnemy) {
        DiscreteCoordinates coordAlly = unit.getCurrentCells().get(0);
        int newX, newY;
        if(!unit.isInRange(coordEnemy)) {

            if(coordAlly.x<coordEnemy.x)
                newX = (coordEnemy.x-coordAlly.x < unit.getRADIUS())? // check if in raduis
                        // add the distance(enemy,ally) to ally, then sub 1 to get the enemy closer
                        coordAlly.x + (coordEnemy.x - coordAlly.x) - 1:
                        // add the radius to ally.x if the enemy's away
                        coordAlly.x + unit.getRADIUS();
            else
                // same logic
                newX =(coordAlly.x-coordEnemy.x < unit.getRADIUS())?
                        coordAlly.x - (coordAlly.x - coordEnemy.x):
                        coordAlly.x - unit.getRADIUS();



            if(coordAlly.y<coordEnemy.y)
                newY = (coordEnemy.y-coordAlly.y < unit.getRADIUS())?
                        coordAlly.y + (coordEnemy.y - coordAlly.y) - 1:
                        coordAlly.y + unit.getRADIUS();

            else
                newY = (coordAlly.y-coordEnemy.y < unit.getRADIUS())  ?
                        coordAlly.y - (coordAlly.y - coordEnemy.y):
                        coordAlly.y - unit.getRADIUS();

            unit.changePosition(new DiscreteCoordinates(newX, newY));
        }
    }


    /**
     * Different states of a unit
     * @param deltaTime
     */
    public void updateState(float deltaTime) {

        switch (state) {

            case IDLE:
                selectedUnit = null;
                unitIndex=0;
                break;

            case NORMAL:
                centerCamera();
                if(waitFor(1.5f, deltaTime)){
                    state = State.SELECT_CELL;
                    //System.out.println("NORMAL(AI)");
                }
                break;

            case SELECT_CELL:
                centerCamera();
                if(unitIndex<units.size()){
                    selectedUnit=units.get(unitIndex++);
                    state =State.MOVE_UNIT;
                }else state =State.IDLE;


            case MOVE_UNIT:
                centerCamera();
                if(waitFor(1.5f, deltaTime)){
                    getOwnerArea().setViewCandidate(selectedUnit);
                    moveUnit(selectedUnit,((ICWarsArea)getOwnerArea()).getClosestEnemy(selectedUnit));
                    state = State.ACTION;
                }
                break;

            case ACTION:
                centerCamera();
                getOwnerArea().setViewCandidate(selectedUnit);
                if(!((ICWarsArea)getOwnerArea()).getEnemiesIndex(selectedUnit).isEmpty()){
                    selectedUnit.autoAction(Keyboard.A,deltaTime,this);
                    ///System.out.println("Action(AI)");
                }else{
                    selectedUnit.autoAction(Keyboard.W,deltaTime,this);
                    //System.out.println("WAIT(AI)");
                }

        }

    }





}
