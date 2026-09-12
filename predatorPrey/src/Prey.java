public class Prey extends Creature {

    public Prey(int speed, int hunger, boolean isFood, int x, int y) {
        super(speed, hunger, isFood, x, y);
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        if (threat != null) {
            // Fleeing takes priority over foraging.
            faceAwayFrom(threat);
            moveAwayFrom(threat, panelWidth, panelHeight);
        } else if (target != null) {
            moveTowards(target);
        } else {
            moveWithBounce(panelWidth, panelHeight);
        }
        recordPosition();
    }
}