public class Predator extends Creature {
    
    private int retreatTicksRemaining = 0;
    private int retreatDirX = 1;
    private int retreatDirY = 1;
    private static final int RETREAT_DURATION_TICKS = 75;

    public Predator(int speed, int hunger, int x, int y) {
        super(speed, hunger, false, x, y);
    }

    public void startRetreat(int fromX, int fromY) {
        retreatDirX = (getX() - fromX) >= 0 ? 1 : -1;
        retreatDirY = (getY() - fromY) >= 0 ? 1 : -1;
        if (getX() == fromX) retreatDirX = 1;
        if (getY() == fromY) retreatDirY = 1;
        retreatTicksRemaining = RETREAT_DURATION_TICKS;
    }

    private void retreat(int panelWidth, int panelHeight) {
        int moveX = retreatDirX * getSpeed();
        int moveY = retreatDirY * getSpeed();
        updateFacing(moveX);
        setX(clampToWidth(getX() + moveX, panelWidth));
        setY(clampToGround(getY() + moveY, panelHeight));
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
        if (retreatTicksRemaining > 0) {
            retreat(panelWidth, panelHeight);
            retreatTicksRemaining--;
        } else if (target instanceof Prey c) {
            pursue(c, panelWidth, panelHeight);
        } else if (target != null) {
            moveTowards(target, panelWidth, panelHeight);
        } else {
            moveWithBounce(panelWidth, panelHeight);
        }
        recordPosition();
    }

    @Override
    public void resetState(int startHunger, int startX, int startY) {
        super.resetState(startHunger, startX, startY);
        this.retreatTicksRemaining = 0;
    }
}
