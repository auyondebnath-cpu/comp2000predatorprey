public class Prey extends Creature {
    private Boolean inDanger;
    private Entity fleeTarget;
    private int fleeTicksRemaining = 0;
    private static final int FLEE_DURATION_TICKS = 20;
    private static final double FLEE_SPEED_MULTIPLIER = 1.8;

    public Prey(int speed, int hunger, boolean isFood, int x, int y) {
        super(speed, hunger, isFood, x, y);
        this.inDanger = false;
    }

    public boolean isInDanger(){
        return inDanger;
    }
   
    public void movement() {

    }

    public void setInDanger(boolean inDanger){
        this.inDanger = inDanger;
        if(inDanger){
            fleeTicksRemaining = FLEE_DURATION_TICKS;
        }
    }

    public void setFleeTarget(Entity fleeTarget){
        this.fleeTarget = fleeTarget;
    }

    @Override
    public void update(int panelWidth, int panelHeight){
        if (target != null) {
            moveTowards(target, panelHeight);
        } else{
            moveWithBounce(panelWidth, panelHeight);
        }
        recordPosition();
    }
}
