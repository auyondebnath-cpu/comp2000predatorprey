public class Grass extends Entity {
    private boolean isEaten;
    private int ageInDays = 0;
    public static final int LIFESPAN_DAYS = 2;
    private boolean markedForRemoval = false;

    public Grass(int x, int y, int growthTimer) {
        super(x, y, true);
        this.isEaten = false;
    }

    public boolean isEdible() {
        return !isEaten;
    }

    public boolean isEaten() {
        return isEaten;
    }

    public void setEaten(boolean isEaten) {
        this.isEaten = isEaten;
        if (isEaten) {
            markedForRemoval = true;
        }
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void onDayTick() {
        ageInDays++;
        if (ageInDays >= LIFESPAN_DAYS) {
            markedForRemoval = true;
        }
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        // No per-tick behaviour: eating triggers immediate removal, and
        // natural expiry is handled once per day via onDayTick().
    }
}