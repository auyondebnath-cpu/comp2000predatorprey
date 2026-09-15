public class Predator extends Creature {
   
    public Predator(int speed, int hunger, int x, int y) {
        super(speed, hunger, false, x, y);
    }

    protected void pursue(Creature target, int panelWidth, int panelHeight) {
        int velX = target.getVelocityX();
        int velY = target.getVelocityY();

        double distance = Math.sqrt(Math.pow(target.getX() - getX(), 2) + Math.pow(target.getY() - getY(), 2));

        if (distance == 0) return;

        double lookAheadTicks = Math.min(distance / getSpeed(), 10);

        int predictedX = target.getX() + (int) (velX * lookAheadTicks);
        int predictedY = target.getY() + (int) (velY * lookAheadTicks);

        double diffX = predictedX - getX();
        double diffY = predictedY - getY();
        double dist = Math.sqrt(diffX * diffX + diffY * diffY);

        if (dist == 0) return;

        double moveDistance = Math.min(getSpeed(), dist);
        double stepX = (diffX / dist) * moveDistance;
        double stepY = (diffY / dist) * moveDistance;

        int moveX = (int) Math.round(stepX);
        int moveY = (int) Math.round(stepY);

        if (moveX == 0 && Math.abs(stepX) > 0.1) moveX = (int) Math.signum(stepX);
        if (moveY == 0 && Math.abs(stepY) > 0.1) moveY = (int) Math.signum(stepY);

        updateFacing(moveX);
        setX(clampToWidth(getX() + moveX, panelWidth));
        setY(clampToGround(getY()+moveY, panelHeight));
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
    public void update(int panelWidth, int panelHeight){
        if (target instanceof Prey c) {
            pursue(c, panelWidth, panelHeight);
        } else if (target != null) {
            moveTowards(target, panelWidth, panelHeight);
        } else {
            moveWithBounce(panelWidth, panelHeight);
        }
        recordPosition();
    }
}
