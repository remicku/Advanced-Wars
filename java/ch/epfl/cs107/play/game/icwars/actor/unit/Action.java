package ch.epfl.cs107.play.game.icwars.actor.unit;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.actor.players.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;

import java.util.ArrayList;
import java.util.List;


public class Action implements Graphics {

    protected ICWarsArea area;
    protected Unit unit;


    private final ImageGraphics cursor =new ImageGraphics(ResourcePath.getSprite("icwars/UIpackSheet"),
            1f, 1f,
            new RegionOfInterest(4*18, 26*18,16,16));
    private int targetIndex =0;
    private List<Integer> enemyIndexes;

    private final ActionType actionType;

    /**
     * Two types of Actions ( Attack and Wait )
     */
    public enum ActionType {
        Attack("(A)ttack"),Wait("(W)ait");
        private final String value;
        ActionType(String value){
            this.value=value;
        }
        public String getValue(){
            return value;
        }
    };

    /**
     * Action Constructor
     * @param area: area where the unit will be
     * @param unit: units selected
     * @param actionType: Type of action that the unit will do
     */
    public Action(ICWarsArea area, Unit unit, ActionType actionType) {
        this.unit = unit;
        this.area = area;
        this.actionType = actionType;
        this.enemyIndexes= new ArrayList<>();
    }



    /**
     * Action is made by real player
     * @param player (ICWarsPlayer): real player
     * @param keyboard (Keyboard): which key is pressed ?
     * @param dt: elapsed time
     */
    public void makeAction(ICWarsPlayer player, Keyboard keyboard, float dt) {
        if(actionType == ActionType.Attack){
            enemyIndexes = area.getEnemiesIndex(unit);
            if (enemyIndexes.isEmpty()
                    || keyboard.get(Keyboard.TAB).isReleased()) {
                player.centerCamera();
                player.setState(ICWarsPlayer.State.ACTION_SELECTION);

            } else {
                if (keyboard.get(Keyboard.RIGHT).isReleased())
                    targetIndex = (targetIndex >= 0)?enemyIndexes.size() - 1:targetIndex-1;

                if (keyboard.get(Keyboard.LEFT).isReleased())
                    targetIndex =(targetIndex == enemyIndexes.size() - 1 || targetIndex == 0) ? 0:targetIndex-1;

                if (keyboard.get(Keyboard.ENTER).isReleased()) {
                    area.damageUnitByIndex(enemyIndexes.get(targetIndex), unit.getDamage());
                    unit.setPlayed(true);
                    player.centerCamera();
                    player.setState(ICWarsPlayer.State.NORMAL);
                    targetIndex =0;
                }

            }
        }else{
            unit.setPlayed(true);
            player.setState(ICWarsPlayer.State.NORMAL);
        }

    }

    /**
     * Draw the cursor to choose which enemy
     * @param canvas target, not null
     */

    @Override
    public void draw(Canvas canvas) {
        if(actionType == ActionType.Attack &&
                !enemyIndexes.isEmpty() &&
                targetIndex >= 0 &&
                targetIndex < enemyIndexes.size()) {
            area.centerCamera(enemyIndexes.get(targetIndex));
            cursor.setAnchor(canvas.getPosition().add(1,0));
            cursor.draw(canvas);
        }
    }


    /**
     * Action is made by AI (If actionType is Attack it will make some damage, if not, back to normal)
     * @param player (AIPlayer)
     * @param dt: elapsed time
     */

    public void makeAIAction(AIPlayer player, float dt) {
        if(actionType == ActionType.Attack){
            enemyIndexes = area.getEnemiesIndex(unit);
            if (!enemyIndexes.isEmpty()) {
                area.damageUnitByIndex(area.getLowHpEnemy(unit), unit.getDamage());
                unit.setPlayed(true);
            }
            player.setState(ICWarsPlayer.State.NORMAL);
        }else{
            unit.setPlayed(true);
            player.setState(ICWarsPlayer.State.NORMAL);
        }

    }

    public String getName() {
        return this.actionType.getValue();
    }

    /**
     * @return the pressed key
     */
    public int getKey() {
        return (actionType == ActionType.Attack)?
                Keyboard.A:
                Keyboard.W;
    }
}


