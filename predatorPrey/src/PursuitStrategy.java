public class PursuitStrategy implements MovementStrategy {
    private static final double MAX_LOOK_AHEAD_TICKS = 10.0;

    @Override
    public void move(
            Creature creature,
            int panelWidth,
            int panelHeight) {

        if (!(creature.getTarget() instanceof Creature target)) {
            return;
        }

        double diffX = (double) target.getX() - creature.getX();
        double diffY = (double) target.getY() - creature.getY();
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance == 0) {
            return;
        }

        double lookAheadTicks = Math.min(
            distance / creature.getSpeed(),
            MAX_LOOK_AHEAD_TICKS
        );

        int predictedX = target.getX() + (int) (target.getVelocityX() * lookAheadTicks);
        int predictedY = target.getY() + (int) (target.getVelocityY() * lookAheadTicks);

        creature.moveInDirection(
            (double) predictedX - creature.getX(),
            (double) predictedY - creature.getY(),
            creature.getSpeed(),
            panelWidth,
            panelHeight
        );
    }
}