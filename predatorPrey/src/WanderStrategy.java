public class WanderStrategy implements MovementStrategy {
    private int dx = 1;
    private int dy = 1;

    @Override
    public void move(Creature creature, int panelWidth, int panelHeight) {
        int speed = creature.getSpeed();
        int groundTop = panelHeight / 5;
        int maxX = panelWidth - Simulation.SPRITE_MARGIN;
        int maxY = panelHeight - Simulation.SPRITE_MARGIN;

        int newX = creature.getX() + dx * speed;
        int newY = creature.getY() + dy * speed;

        if (newX < 0 || newX > maxX) {
            dx = -dx;
            newX = creature.getX() + dx * speed;
            newX = Math.max(0, Math.min(newX, maxX));
        }

        if (newY < groundTop || newY > maxY) {
            dy = -dy;
            newY = creature.getY() + dy * speed;
            newY = Math.max(groundTop, Math.min(newY, maxY));
        }

        creature.updateFacing(newX - creature.getX());
        creature.setX(newX);
        creature.setY(newY);
    }
}
