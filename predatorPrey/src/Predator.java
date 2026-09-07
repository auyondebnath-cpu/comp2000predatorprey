public class Predator extends Creature {
    
    public Predator(int speed, int hunger, int x, int y) {
        super(speed, hunger, false, x, y);
    }

    @Override
    public void update(){
        moveWithBounce(800, 600);
    }
}