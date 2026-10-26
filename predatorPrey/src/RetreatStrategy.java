public class RetreatStrategy implements MovementStrategy {
    private int directionX = 1;
    private int directionY = 1;

    public void start(Creature creature, int fromX, int fromY) {
        directionX = (creature.getX() - fromX) >= 0 ? 1 : -1;
        directionY = (creature.getY() - fromY) >= 0 ? 1 : -1;
    }

    public void reset() {
        directionX = 1;
        directionY = 1;
    }

    @Override
    public void move(
            Creature creature,
            int panelWidth,
            int panelHeight) {

        int moveX = directionX * creature.getSpeed();
        int moveY = directionY * creature.getSpeed();

        creature.updateFacing(moveX);
        creature.setX(creature.clampToWidth(creature.getX() + moveX, panelWidth));
        creature.setY(creature.clampToGround(creature.getY() + moveY, panelHeight));
    }
}