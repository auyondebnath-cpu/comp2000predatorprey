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

    private void flee(Entity threat, int panelHeight){
        double diffX = getX() - threat.getX();
        double diffY = getY() - threat.getY();
        double distance = Math.sqrt(diffX*diffX + diffY * diffY);

        if(distance ==0){
            diffX = 1;
            diffY = 0;
            distance = 1;
        }

        double boostedSpeed = getSpeed() * FLEE_SPEED_MULTIPLIER;
        double stepX = (diffX/distance) * boostedSpeed;
        double stepY = (diffY/distance) * boostedSpeed;

        int moveX = (int) Math.round(stepX);
        int moveY = (int) Math.round(stepY);

        if(moveX ==0 && Math.abs(stepX) > 0.1) moveX = (int) Math.signum(stepX);
        if(moveY ==0 && Math.abs(stepY) > 0.1) moveY = (int) Math.signum(stepY);

        updateFacing(moveX);
        setX(getX() + moveX);
        setY(clampToGround(getY() + moveY, panelHeight));
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
