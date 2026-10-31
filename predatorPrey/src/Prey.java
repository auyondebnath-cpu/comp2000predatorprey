public class Prey extends Creature {
    private Boolean inDanger;
    private Entity fleeTarget;
    private int fleeTicksRemaining = 0;
    private static final int FLEE_DURATION_TICKS = 20;
    private final FleeStrategy fleeStrategy = new FleeStrategy();

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

    public void setFleeTarget(Entity fleeTarget) {
        this.fleeTarget = fleeTarget;
        fleeStrategy.setThreat(fleeTarget);
    }

    @Override
    protected MovementStrategy chooseMovementStrategy() {
        if (fleeTicksRemaining > 0 && fleeTarget != null) {
            fleeTicksRemaining--;
            return fleeStrategy;
        } else if (target != null) {
            return getSeekStrategy();
        }
        return getWanderStrategy();
    }

    @Override
    public boolean shouldBeRemoved() {
        return isDead() || isStarved(-4);
    }

    @Override
    public Entity reproduce(int panelWidth, int panelHeight, int groundTop, java.util.Random random) {
        if (shouldReproduce(2)) {
            int offsetX = random.nextInt(21) - 10;
            int offsetY = random.nextInt(21) - 10;
            int spawnX = Math.max(0, Math.min(getX() + offsetX, panelWidth - Simulation.SPRITE_MARGIN));
            int spawnY = Math.max(groundTop, Math.min(getY() + offsetY, panelHeight - Simulation.SPRITE_MARGIN));
            return new Prey(getSpeed(), 100, false, spawnX, spawnY);
        }
        return null;
    }

    @Override 
    public void resetState(int startHunger, int startX, int startY) {
        super.resetState(startHunger, startX, startY);
        this.fleeTicksRemaining = 0;
        this.fleeTarget = null;
        this.inDanger = false;
        fleeStrategy.reset();
    }
}
