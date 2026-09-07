public abstract class Creature extends Entity {
    private int speed;
    private int hunger;
    private int dx =1;
    private int dy =1;
    

    public Creature(int speed, int hunger, boolean isFood, int x, int y) {
        super(x, y, isFood);
        this.speed = speed;
        this.hunger = hunger;
    }

    public int getSpeed(){
        return speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public int getHunger(){
        return hunger;
    }

    public void setHunger(int hunger){
        this.hunger = hunger;
    }

    protected void moveWithBounce(int panelWidth, int panelHeight){
        int newX = getX() + dx*speed;
        int newY = getY() + dy*speed;

        if(newX<0 || newX>panelWidth){
            dx = -dx;
            newX = getX() + dx*speed;
        }

        if(newY<0 || newY>panelHeight){
            dy = -dy;
            newY = getY() + dy*speed;
        }

        setX(newX);
        setY(newY);
    }
}