package ch.epfl.cs107.play.game.icwars.actor.unit;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.game.icwars.actor.players.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;

import java.util.List;
import java.util.Queue;

public class Unit extends ICWarsActor implements Interactor {

    private final String NAME;
    protected boolean played;
    protected ICWarsUnitInteractionHandler handler;
    private int defense_stars;
    protected final Sprite SPRITE;
    private Integer actionIndex;
    protected final int RADIUS;
    private int hp,damage;
    protected  List<Action> availableActions;
    private final DiscreteCoordinates spawnPosition;
    protected ICWarsRange range;

    /**
     * Three types of players (Tank,Soldier and Atomizer)
     */
    public enum PlayerType {
        TANK("Tank",7,4,10),
        ATOMIZER("Atomizer",100,10,100),
        SOLDIER("Soldier",2,2,5);
        private final String value;
        private final int damage;
        private final int radius;

        public int getMaxhp() {
            return maxhp;
        }

        private final int maxhp;
        public int getDamage() {
            return damage;
        }

        public int getRadius() {
            return radius;
        }


        PlayerType(String value,int damage,int radius, int maxhp){
            this.value=value;
            this.damage = damage;
            this.radius = radius;
            this.maxhp = maxhp;
        }
        public String getValue(){
            return value;
        }
    }

    /**
     * Unit Constructor
     * @param area (Area): area where the unit will be
     * @param position (DiscreteCoordinates): position of the unit
     * @param name (String): name of the unit
     * @param faction (Faction): faction of the unit
     * @param playerType (PlayerType): type of unit
     */
    public Unit(Area area, DiscreteCoordinates position,
                String name, Faction faction, PlayerType playerType) {
        super(area, position, faction);

        this.handler = new ICWarsUnitInteractionHandler();
        this.SPRITE = new Sprite("icwars/"+faction.value()+playerType.getValue(), 1.5f, 1.5f, this,
                null, new Vector(-0.25f, -0.25f));

        availableActions = List.of(new Action((ICWarsArea) area, this, Action.ActionType.Wait),
                new Action((ICWarsArea) area,this,Action.ActionType.Attack));

        this.NAME = name;
        this.RADIUS = playerType.getRadius();
        this.hp = playerType.getMaxhp();
        this.damage = playerType.getDamage();
        this.range = new ICWarsRange();
        spawnPosition = position;
        addNodes(position);
    }



    @Override
    public void draw(Canvas canvas) {
        SPRITE.draw(canvas);
        SPRITE.setAlpha(played?0.40f:1f);
    }

    public String getNAME() {
        return NAME;
    }

    public int getHp() {
        return hp;
    }

    public DiscreteCoordinates getSpawnPosition() {
        return spawnPosition;
    }

    public int getRADIUS(){
        return this.RADIUS;
    }

    public int getDamage(){
        return this.damage;
    }

    public void setPlayed(boolean newUsed) {
        played = newUsed;
    }

    public boolean isPlayed() {
        return played;
    }

    public boolean isInRange(DiscreteCoordinates position) {
        return (range.nodeExists(position));
    }

    /**
     * How damages affect the hp of a unit
     *A unit can be protected by a cell type (rock, tree, ...)
     * @param damage: amount of damage a unit can do
     */
    public void damage(int damage) {
        hp = hp - damage + defense_stars;
        if (hp < 0) hp = 0;
        if(defense_stars>damage) hp-=damage;
    }

    /**
     * @return true if the hp of a unit are inferior to 0
     */
    public boolean isDead() {
        return (hp <= 0.f);
    }

    /**
     * Draw the unit's range and a path from the unit position to
     * destination
     *
     * @param destination path destination
     * @param canvas      canvas
     */
    public void drawRangeAndPathTo(DiscreteCoordinates destination, Canvas canvas) {
        this.range.draw(canvas);
        Queue<Orientation> path = this.range.shortestPath(getCurrentMainCellCoordinates(),
                destination);
        //Draw path only if it exists (destination inside the range)
        if (path != null) {
            new Path(getCurrentMainCellCoordinates().toVector(),path).draw(canvas);
        }
    }

    /**
     * Changes the unit position
     * @param newPosition new unit's position
     * @return true if it can be changed
     */
    public boolean changePosition(DiscreteCoordinates newPosition) {
        if (!range.nodeExists(newPosition) || !super.changePosition(newPosition))
            return false;

        addNodes(newPosition);
        return true;
    }


    public void setActionInGUI(ICWarsPlayerGUI gui){
        gui.setActions(availableActions);
    }

    public void makeIndexAction(int index, float dt, ICWarsPlayer player, Keyboard keyboard){
        availableActions.get(index).makeAction(player, keyboard, dt);
    }

    public void autoAction(int keyboard, float dt, AIPlayer player){
        for(Action act: availableActions){
            if(keyboard==act.getKey())
                act.makeAIAction(player, dt);

        }
    }

    public Integer getActionIndex(Keyboard keyboard,ICWarsPlayer player){

        for(Action act: availableActions){
            if(keyboard.get(act.getKey()).isReleased()){
                actionIndex= availableActions.indexOf(act);
                player.setState(ICWarsPlayer.State.ACTION);
            }
        }
        return actionIndex;
    }

    public void drawAction(Canvas canvas, int index){
        availableActions.get(index).draw(canvas);
    }

    private void addNodes(DiscreteCoordinates position) {
        range = new ICWarsRange();
        for (int y = -RADIUS; y <= RADIUS; y++) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                int nodeX = position.x + x;
                int nodeY = position.y + y;

                boolean hasUpEdge = (nodeY < getOwnerArea().getHeight() - 1 && y < RADIUS);
                boolean hasDownEdge = (nodeY > 0 && y > -RADIUS);
                boolean hasRightEdge = (nodeX < getOwnerArea().getWidth() - 1 && x < RADIUS);
                boolean hasLeftEdge = (x > -RADIUS && nodeX > 0);


                boolean inX =nodeX >= 0 && nodeX < getOwnerArea().getWidth();
                boolean inY = nodeY >= 0 && nodeY < getOwnerArea().getHeight();

                assert (inX && inY);
                range.addNode(new DiscreteCoordinates(nodeX, nodeY), hasLeftEdge,
                        hasUpEdge, hasRightEdge, hasDownEdge);


            }
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ICWarsInteractionVisitor) v).interactWith(this);
    }

    @Override
    public void enterArea(Area area, DiscreteCoordinates position) {
        super.enterArea(area, position);
        addNodes(position);
    }

    @Override
    public boolean takeCellSpace() {
        return true;
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
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    private class ICWarsUnitInteractionHandler implements ICWarsInteractionVisitor{
        public void interactWith(ICWarsBehavior.ICWarsCell cell){
            defense_stars = cell.getDefenseStars();
        }
    }
}


