public class Prey extends Creature {
    private Boolean inDanger;
    private Entity fleeTarget;
    private int fleeTicksRemaining = 0;
    private int lastFleeDirX = 0;
    private static final int FLEE_DURATION_TICKS = 20;
    private static final double FLEE_SPEED_MULTIPLIER = 1.8;

    public Prey(int speed, int hunger, boolean isFood, int x, int y) {
        super(speed, hunger, isFood, x, y);
        this.inDanger = false;
    }

    public boolean isInDanger(){
        return inDanger;
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

    private void flee(Entity threat, int panelWidth, int panelHeight){
        double diffX = getX() - threat.getX();
        double diffY = getY() - threat.getY();
        double distance = Math.sqrt(diffX*diffX + diffY * diffY);

        if(distance == 0){
            diffX = 1;
            diffY = 0;
            distance = 1;
        }

        final double DEAD_ZONE = 8.0;
        if (Math.abs(diffX) < DEAD_ZONE && lastFleeDirX != 0) {
            diffX = lastFleeDirX * DEAD_ZONE;
            distance = Math.sqrt(diffX*diffX + diffY*diffY);
        }

        double boostedSpeed = getSpeed() * FLEE_SPEED_MULTIPLIER;
        double stepX = (diffX/distance) * boostedSpeed;
        double stepY = (diffY/distance) * boostedSpeed;

        int moveX = (int) Math.round(stepX);
        int moveY = (int) Math.round(stepY);

        if(moveX == 0 && Math.abs(stepX) > 0.1) moveX = (int) Math.signum(stepX);
        if(moveY == 0 && Math.abs(stepY) > 0.1) moveY = (int) Math.signum(stepY);

        if (moveX != 0) {
            lastFleeDirX = (int) Math.signum(moveX);
        }

        updateFacing(moveX);
        setX(clampToWidth(getX() + moveX, panelWidth));
        setY(clampToGround(getY() + moveY, panelHeight));
    }

    @Override
    public void update(int panelWidth, int panelHeight){
        if (fleeTicksRemaining > 0 && fleeTarget != null) {
            flee(fleeTarget, panelWidth, panelHeight);
            fleeTicksRemaining--;
        } else if (target != null) {
            moveTowards(target, panelWidth, panelHeight);
        } else{
            moveWithBounce(panelWidth, panelHeight);
        }
        recordPosition();
    }

    @Override 
    public void resetState(int startHunger, int startX, int startY){
        super.resetState(startHunger, startX, startY);
        this.fleeTicksRemaining = 0;
        this.fleeTarget = null;
        this.inDanger = false;
        this.lastFleeDirX = 0;
    }
}
