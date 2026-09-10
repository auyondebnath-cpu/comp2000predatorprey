public abstract class Creature extends Entity {
    private int speed;
    private int hunger;
    private int dx =1;
    private int dy =1;
    protected int daysSinceFed = 0;
    protected int fillMeter = 1;
    protected boolean fedToday = false;
    

    public Creature(int speed, int hunger, boolean isFood, int x, int y) {
        super(x, y, isFood);
        validateSpeed(speed);
        validateHunger(hunger);
        this.speed = speed;
        this.hunger = hunger;
    }

    public int getSpeed(){
        return speed;
    }

    public void setSpeed(int speed){
        validateSpeed(speed);
        this.speed = speed;
    }

    public int getHunger(){
        return hunger;
    }

    public void setHunger(int hunger){
        validateHunger(hunger);
        this.hunger = hunger;
    }

    public int getDaysSinceFed(){
        return daysSinceFed;
    }

    public int getFillMeter(){
        return fillMeter;
    }

    public void setFedToday(boolean fedToday){
        this.fedToday = fedToday;
    }

    public boolean isStarved(int maxUnfedDays){
        return daysSinceFed>=maxUnfedDays;
    }

    private void validateSpeed(int speed){
        if(speed <=0){
            throw new InvalidCreatureStateException("Speed must be positive, got: " + speed);
        }
    }

    private void validateHunger(int hunger){
        if(hunger<0){
            throw new InvalidCreatureStateException("Hunger cannot be negative, got: "+ hunger);
        }
    }

    public boolean isDead(){
        return hunger <= 0;
    }

    protected void depleteHunger(){
        hunger--;
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