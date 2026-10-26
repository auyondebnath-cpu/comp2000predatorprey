public class SeekStrategy implements MovementStrategy {

    @Override
    public void move(
            Creature creature,
            int panelWidth,
            int panelHeight) {

        Entity target = creature.getTarget();

        if (target == null) {
            return;
        }

        creature.moveInDirection(
            (double) target.getX() - creature.getX(),
            (double) target.getY() - creature.getY(),
            creature.getSpeed(),
            panelWidth,
            panelHeight
        );
    }
}
