package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.window.Window;


public class ICWarsBehavior extends AreaBehavior {

    public enum ICWarsCellType{
        NONE(0,0),     // Should never be used except
        // in the toType method
        ROAD(-16777216, 0), // the second value is the number
        // of defense stars
        PLAIN(-14112955, 1),
        WOOD(-65536, 3),
        RIVER(-16776961, 0),
        MOUNTAIN(-256, 4),
        CITY(-1,2);


        final int type;
        final int defenseStars;

        /**
         * define the type of the cell
         * @param type: the int referred to the type
         * @param defenseStars: the number of stars referred to the type
         */
        ICWarsCellType(int type, int defenseStars){
            this.type= type;
            this.defenseStars= defenseStars;
        }
        public String typeToString(){
            return toType(type).toString()   ;
        }

        /**
         *
         * @return: the int defense Stars of the cell
         */
        public  int getDefenseStar(){
            return defenseStars;
        }


        /**
         *
         * @param type: the int type of the cell
         * @return: prints the type of the cell if she has one
         */
        public static ICWarsCellType toType(int type){
            for(ICWarsCellType ict: ICWarsCellType.values()){
                if(ict.type ==type) return ict;
            }
            System.out.println(type);
            return NONE;
        }




    }

    /**
     * Default IcWarsBehavior Constructor
     * @param window (Window), not null
     * @param name (String): Name of the Behavior, not null
     */
    public ICWarsBehavior(Window window, String name) {
        super(window, name);

        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth();x++){
                ICWarsCellType type= ICWarsCellType.toType(getRGB(getHeight()-1-y,x));
                setCell(x,y,new ICWarsCell(x,y,type));
            }
        }

    }

    public class ICWarsCell extends AreaBehavior.Cell{

        private final ICWarsCellType type;

        /**
         * Default ICWarsCell Constructor
         * @param x (int): x coordinate of the cell
         * @param y (int): y coordinate of the cell
         * @param type (ICWarsCellType), not null
         */
        public ICWarsCell(int x, int y, ICWarsCellType type) {
            super(x, y);
            this.type=type;
        }

        public ICWarsCellType getType() {
            return type;
        }

        public int getDefenseStars(){
            return(type.defenseStars);
        }



        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable e) {

            if(e.takeCellSpace()) {
                for (Interactable interactable : entities) {
                    if (interactable.takeCellSpace())
                        return false;

                }
            }

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
        public void acceptInteraction(AreaInteractionVisitor v) {
            ((ICWarsInteractionVisitor)v).interactWith(this);
        }
    }
}
