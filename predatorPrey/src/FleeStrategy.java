public class FleeStrategy implements MovementStrategy {
    private static final double SPEED_MULTIPLIER = 1.8;
    private static final double DEAD_ZONE = 8.0;

    private Entity threat;
    private int lastFleeDirX = 0;

    public void setThreat(Entity threat) {
        this.threat = threat;
    }

    public void reset() {
        threat = null;
        lastFleeDirX = 0;
    }

    @Override
    public void move(
            Creature creature,
            int panelWidth,
            int panelHeight) {

        if (threat == null) {
            return;
        }

        double diffX = (double) creature.getX() - threat.getX();
        double diffY = (double) creature.getY() - threat.getY();

        if (diffX == 0 && diffY == 0) {
            diffX = 1;
        }

        if (Math.abs(diffX) < DEAD_ZONE && lastFleeDirX != 0) {
            diffX = lastFleeDirX * DEAD_ZONE;
        }

        int previousX = creature.getX();

        creature.moveInDirection(
            diffX,
            diffY,
            creature.getSpeed() * SPEED_MULTIPLIER,
            panelWidth,
            panelHeight
        );

        int actualMoveX = creature.getX() - previousX;

        if (actualMoveX != 0) {
            lastFleeDirX = Integer.signum(actualMoveX);
        }
    }
}