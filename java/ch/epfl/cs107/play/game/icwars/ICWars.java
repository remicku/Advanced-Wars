package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.players.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.players.RealPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.areas.Level0;
import ch.epfl.cs107.play.game.icwars.area.areas.Level1;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.LinkedList;
import java.util.List;

public class ICWars extends AreaGame {

    private final String[] areas = {"icwars/Level0", "icwars/Level1"};
    private List<ICWarsPlayer> players;
    private List<ICWarsPlayer> currentWaitingPlayers;
    private List<ICWarsPlayer> nextWaitingPlayers;
    private ICWarsPlayer selectedPlayer;
    private int currentArea;
    private State state;


    private enum State {
        INIT,
        CHOOSE_PLAYER,
        START_PLAYER_TURN,
        PLAYER_TURN,
        END_PLAYER_TURN,
        END_TURN,
        END,
    }

    /**
     * Create and area for each level
     */
    private void createAreas(){
        addArea(new Level0());
        addArea(new Level1());
    }

    /**
     *
     * @param window (Window):
     * @param fileSystem (FileSystem):
     * @return true if the game begins (area just created and level 0)
     */
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            currentArea =0;
            initArea(areas[currentArea]);
            return true;
        }
        return false;
    }

    /**
     * Initialize the area
     * Create players, units, ally, enemy and center the camera on the real player
     * @param areaKey (String)
     */
    private void initArea(String areaKey) {
        players = new LinkedList<>();
        currentWaitingPlayers = new LinkedList<>();
        nextWaitingPlayers = new LinkedList<>();
        state = State.INIT;
        ICWarsArea area = (ICWarsArea) setCurrentArea(areaKey, true);

        DiscreteCoordinates allyCoords = area.getPlayerSpawnPosition();
        players.add(new RealPlayer(area,allyCoords, ICWarsActor.Faction.FRIENDLY,
                new Unit(area,new DiscreteCoordinates(2,5), "Tank",
                        ICWarsActor.Faction.FRIENDLY,Unit.PlayerType.TANK),
                new Unit(area,new DiscreteCoordinates(2,6), "Atomizer",
                        ICWarsActor.Faction.FRIENDLY,Unit.PlayerType.ATOMIZER),
                new Unit(area,new DiscreteCoordinates(3,5),"Soldier",
                        ICWarsActor.Faction.FRIENDLY, Unit.PlayerType.SOLDIER)));
        players.get(0).enterArea(area, allyCoords);


        DiscreteCoordinates enemyCoords = area.getEnemySpawnPosition();
        players.add(new AIPlayer(area,enemyCoords, ICWarsActor.Faction.ENEMY,
                new Unit(area,new DiscreteCoordinates(6,5),"Tank", ICWarsActor.Faction.ENEMY, Unit.PlayerType.TANK),
                new Unit(area,new DiscreteCoordinates(9,5),"Soldier" ,ICWarsActor.Faction.ENEMY, Unit.PlayerType.SOLDIER)));
        players.get(1).enterArea(area, enemyCoords);


        players.get(0).centerCamera();

    }

    /**
     * Clear the area by making the player leave
     * Clear the list of players
     */
    private void clearArea(){
        this.currentWaitingPlayers.clear();
        this.nextWaitingPlayers.clear();
        if(!this.players.isEmpty()){
            for(ICWarsPlayer player: players){
                player.leaveArea();
            }
            this.players.clear();
        }
    }

    /**
     * Clear the current area and set it to next level area
     * End game if area already at level 1 (no next level)
     */
    public void nextLevel(){

        if(this.currentArea ==0){
            this.clearArea();
            this.currentArea =1;
            this.initArea(this.areas[this.currentArea]);
        }
        if(this.currentArea ==1)
            this.end();

    }

    /**
     * Reset the game by clearing the players and reinitializing the area to level 0
     */
    public void reset(){

        this.currentWaitingPlayers.clear();
        this.nextWaitingPlayers.clear();
        this.players.get(1).leaveArea();
        this.players.get(0).leaveArea();
        this.players.clear();
        this.currentArea =0;
        this.initArea(this.areas[this.currentArea]);
    }

    /**
     * Update the game state
     */
    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);
        this.updateGameState();
        Keyboard keyboard=this.getWindow().getKeyboard();
        if(keyboard.get(Keyboard.R).isReleased()) this.reset();
        if(keyboard.get(Keyboard.N).isReleased()) this.nextLevel();


        //Attempt to put a condition to add the atomizer
        //if(keyboard.get(Keyboard.I).isReleased()){
            /*this.players.get(0).addUnit(
                    new Unit(area,new DiscreteCoordinates(6,5),"Atomizer", ICWarsActor.Faction.FRIENDLY, Unit.PlayerType.ATOMIZER),
                    area,
                    this.players.get(0).getCurrentCells().get(0)
            );*/

        //System.out.println("Added :D");

    }

    /**
     * Different steps of a game round
     */
    private void updateGameState(){

        switch(state) {

            case INIT:
                this.currentWaitingPlayers.addAll(players);
                this.state = State.CHOOSE_PLAYER;
                break;

            case CHOOSE_PLAYER:
                if(this.currentWaitingPlayers.isEmpty()) {
                    this.state = State.END_TURN;
                } else {
                    this.selectedPlayer = this.currentWaitingPlayers.get(0);
                    this.currentWaitingPlayers.remove(0);
                    this.state = State.START_PLAYER_TURN;
                }
                break;

            case START_PLAYER_TURN:
                this.selectedPlayer.startTurn();
                this.state = State.PLAYER_TURN;
                break;

            case PLAYER_TURN:
                if(this.selectedPlayer.getState()==ICWarsPlayer.State.IDLE)
                    this.state = State.END_PLAYER_TURN;

                break;

            case END_PLAYER_TURN:
                if(this.selectedPlayer.isDefeated()) {
                    this.selectedPlayer.leaveArea();
                    this.state = State.CHOOSE_PLAYER;
                } else {
                    this.nextWaitingPlayers.add(selectedPlayer);
                    this.selectedPlayer.resetUnits();
                    this.selectedPlayer = null;
                    this.state = State.CHOOSE_PLAYER;
                }
                break;

            case END_TURN:
                this.nextWaitingPlayers.removeIf(ICWarsPlayer::isDefeated);
                this.players.removeIf(ICWarsPlayer::isDefeated);
                if(this.nextWaitingPlayers.size()<=1)
                    this.state = State.END;
                else {
                    this.currentWaitingPlayers.addAll(nextWaitingPlayers);
                    this.nextWaitingPlayers.clear();
                    this.state = State.CHOOSE_PLAYER;
                }

                break;

            case END:
                this.nextLevel();
                break;
        }
    }

    /**
     * @return the title of the area
     */
    @Override
    public String getTitle() {
        return "ICWars";
    }

    /**
     * Send a message when game is over
     */
    @Override
    public void end(){
        super.end();
        System.out.println("Game over :D");
    }

}