import java.util.List;

public abstract class Creature extends Entity {
    private int speed;
    private int hunger;
    private int dx = 1;
    private int dy = 1;
    protected int daysSinceFed = 0;
    protected int fillMeter = 1;
    protected boolean fedToday = false;
    protected boolean inDanger = false;
    protected Entity target;
    protected Entity threat;
    private int lastX;
    private int lastY;

    public Creature(int speed, int hunger, boolean isFood, int x, int y) {
        super(x, y, isFood);
        validateSpeed(speed);
        validateHunger(hunger);
        this.speed = speed;
        this.hunger = hunger;
        this.lastX = x;
        this.lastY = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        validateSpeed(speed);
        this.speed = speed;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        validateHunger(hunger);
        this.hunger = hunger;
    }

    public int getDaysSinceFed() {
        return daysSinceFed;
    }

    public int getFillMeter() {
        return fillMeter;
    }

    public void setFedToday(boolean fedToday) {
        this.fedToday = fedToday;
    }

    public boolean isStarved(int maxUnfedDays) {
        return daysSinceFed >= maxUnfedDays;
    }

    private void validateSpeed(int speed) {
        if (speed <= 0) {
            throw new InvalidCreatureStateException("Speed must be positive, got: " + speed);
        }
    }

    private void validateHunger(int hunger) {
        if (hunger < 0) {
            throw new InvalidCreatureStateException("Hunger cannot be negative, got: " + hunger);
        }
    }

    public boolean isDead() {
        return hunger <= 0;
    }

    protected void depleteHunger() {
        hunger--;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public void setThreat(Entity threat) {
        this.threat = threat;
        this.inDanger = (threat != null);
    }

    public boolean isInDanger() {
        return inDanger;
    }

    public int getVelocityX() {
        return getX() - lastX;
    }

    public int getVelocityY() {
        return getY() - lastY;
    }

    protected void recordPosition() {
        lastX = getX();
        lastY = getY();
    }

    protected void moveWithBounce(int panelWidth, int panelHeight) {
        int newX = getX() + dx * speed;
        int newY = getY() + dy * speed;

        if (newX < 0 || newX > panelWidth) {
            dx = -dx;
            newX = getX() + dx * speed;
        }

        if (newY < 0 || newY > panelHeight) {
            dy = -dy;
            newY = getY() + dy * speed;
        }

        setX(newX);
        setY(newY);
    }

    protected void faceAwayFrom(Entity source) {
        dx = getX() >= source.getX() ? 1 : -1;
        dy = getY() >= source.getY() ? 1 : -1;
    }

    public void onDayTick() {
        if (fedToday) {
            daysSinceFed = 0;
            fillMeter++;
        } else {
            daysSinceFed++;
            fillMeter = 1;
        }
        fedToday = false;
    }

    public boolean shouldReproduce(int maxFillMeter) {
        if (fillMeter >= maxFillMeter) {
            fillMeter = 1;
            return true;
        }
        return false;
    }

    protected void moveTowards(Entity target) {
        double diffX = target.getX() - getX();
        double diffY = target.getY() - getY();
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance == 0) {
            return;
        }

        double moveDistance = Math.min(getSpeed(), distance);
        double stepX = (diffX / distance) * moveDistance;
        double stepY = (diffY / distance) * moveDistance;

        setX((int) (getX() + stepX));
        setY((int) (getY() + stepY));
    }

    protected void moveAwayFrom(Entity threat, int panelWidth, int panelHeight) {
        double diffX = getX() - threat.getX();
        double diffY = getY() - threat.getY();
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance == 0) {
            diffX = 1;
            diffY = 0;
            distance = 1;
        }

        double stepX = (diffX / distance) * getSpeed();
        double stepY = (diffY / distance) * getSpeed();

        int newX = (int) Math.max(0, Math.min(panelWidth, getX() + stepX));
        int newY = (int) Math.max(0, Math.min(panelHeight, getY() + stepY));

        setX(newX);
        setY(newY);
    }

    protected void pursue(Creature target) {
        final double LOOKAHEAD_TICKS = 8.0;

        int velX = target.getVelocityX();
        int velY = target.getVelocityY();

        double predictedX = target.getX() + velX * LOOKAHEAD_TICKS;
        double predictedY = target.getY() + velY * LOOKAHEAD_TICKS;

        double diffX = predictedX - getX();
        double diffY = predictedY - getY();
        double dist = Math.sqrt(diffX * diffX + diffY * diffY);

        if (dist == 0) return;

        double moveDistance = Math.min(getSpeed(), dist);
        double stepX = (diffX / dist) * moveDistance;
        double stepY = (diffY / dist) * moveDistance;

        setX((int) (getX() + stepX));
        setY((int) (getY() + stepY));
    }

    protected void separateFrom(List<? extends Creature> sameType, int minDist) {
        double pushX = 0;
        double pushY = 0;
        int count = 0;

        for (Creature other : sameType) {
            if (other == this) continue;
            double dx0 = getX() - other.getX();
            double dy0 = getY() - other.getY();
            double d = Math.sqrt(dx0 * dx0 + dy0 * dy0);
            if (d > 0 && d < minDist) {
                pushX += (dx0 / d) * (minDist - d);
                pushY += (dy0 / d) * (minDist - d);
                count++;
            } else if (d == 0) {
                pushX += (Math.random() - 0.5) * minDist;
                pushY += (Math.random() - 0.5) * minDist;
                count++;
            }
        }

        if (count > 0) {
            setX((int) (getX() + pushX));
            setY((int) (getY() + pushY));
        }
    }
}