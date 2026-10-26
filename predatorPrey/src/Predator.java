public class Predator extends Creature {
    
    private int retreatTicksRemaining = 0;
    private static final int RETREAT_DURATION_TICKS = 75;
    private final MovementStrategy pursuitStrategy = new PursuitStrategy();
    private final RetreatStrategy retreatStrategy = new RetreatStrategy();

    public Predator(int speed, int hunger, int x, int y) {
        super(speed, hunger, false, x, y);
    }

    public void startRetreat(int fromX, int fromY) {
        retreatStrategy.start(this, fromX, fromY);
        retreatTicksRemaining = RETREAT_DURATION_TICKS;
    }

    @Override
    public boolean shouldBeRemoved() {
        return isStarved(-5);
    }

    @Override
    public Entity reproduce(int panelWidth, int panelHeight, int groundTop, java.util.Random random) {
        if (shouldReproduce(4)) {
            int offsetX = random.nextInt(21) - 10;
            int offsetY = random.nextInt(21) - 10;
            int spawnX = Math.max(0, Math.min(getX() + offsetX, panelWidth - Simulation.SPRITE_MARGIN));
            int spawnY = Math.max(groundTop, Math.min(getY() + offsetY, panelHeight - Simulation.SPRITE_MARGIN));
            return new Predator(getSpeed(), 100, spawnX, spawnY);
        }
        return null;
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        if (retreatTicksRemaining > 0) {
            setMovementStrategy(retreatStrategy);
            performMovement(panelWidth, panelHeight);
            retreatTicksRemaining--;
        } else if (target instanceof Prey) {
            setMovementStrategy(pursuitStrategy);
            performMovement(panelWidth, panelHeight);
        } else if (target != null) {
            setMovementStrategy(getSeekStrategy());
            performMovement(panelWidth, panelHeight);
        } else {
            setMovementStrategy(getWanderStrategy());
            performMovement(panelWidth, panelHeight);
        }
    }

    @Override
    public void resetState(int startHunger, int startX, int startY) {
        super.resetState(startHunger, startX, startY);
        this.retreatTicksRemaining = 0;
        retreatStrategy.reset();
    }
}
