public abstract class Creature extends Entity {
    private int speed;
    private int hunger;
    protected int energyMeter = 0;
    protected boolean fedToday = false;
    protected Entity target;
    private int lastX;
    private int lastY;
    private int velocityX = 0;
    private int velocityY = 0;
    private boolean facingRight = true;
    private MovementStrategy wanderStrategy = new WanderStrategy();
    private MovementStrategy movementStrategy = wanderStrategy;
    private final MovementStrategy seekStrategy = new SeekStrategy();

    public Creature(int speed, int hunger, boolean isFood, int x, int y) {
        super(x, y, isFood);
        validateSpeed(speed);
        validateHunger(hunger);
        this.speed = speed;
        this.hunger = hunger;
        this.lastX = x;
        this.lastY = y;
    }


    public int getSpeed(){
        return speed;
    }

    public boolean isFacingRight(){
        return facingRight;
    }

    protected void updateFacing (int moveX){
        if(moveX>0){
            facingRight = true;
        } else if(moveX<0){
            facingRight = false;
        }
    }

    public void setSpeed(int speed){
        validateSpeed(speed);
        this.speed = speed;
    }

    public void setMovementStrategy(MovementStrategy movementStrategy) {
    if (movementStrategy == null) {
        throw new IllegalArgumentException(
            "Movement strategy cannot be null"
        );
    }

    this.movementStrategy = movementStrategy;
}

    protected void performMovement(int panelWidth, int panelHeight) {
        movementStrategy.move(this, panelWidth, panelHeight);
    }

    protected MovementStrategy getWanderStrategy() {
        return wanderStrategy;
    }

    protected MovementStrategy getSeekStrategy() {
        return seekStrategy;
    }

    public Entity getTarget() {
        return target;
    }

    public int getHunger(){
        return hunger;
    }


    public void setHunger(int hunger){
        validateHunger(hunger);
        this.hunger = hunger;
    }

    public void setFedToday(boolean fedToday){
        this.fedToday = fedToday;
    }


    public boolean isStarved(int deathThreshold){
        return energyMeter <= deathThreshold;
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


    public void setTarget (Entity target){
        this.target = target;
    }


    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public void finishMovementTick() {
        velocityX = getX() - lastX;
        velocityY = getY() - lastY;

        lastX = getX();
        lastY = getY();
    }

    public boolean isFedToday(){
        return fedToday;
    }


    protected void moveWithBounce(int panelWidth, int panelHeight) {
        wanderStrategy.move(this, panelWidth, panelHeight);
    }

    public void moveInDirection(double diffX, double diffY, double movementSpeed, int panelWidth, int panelHeight) {
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance == 0) {
            return;
        }

        double moveDistance = Math.min(movementSpeed, distance);
        double stepX = (diffX / distance) * moveDistance;
        double stepY = (diffY / distance) * moveDistance;

        int moveX = (int) Math.round(stepX);
        int moveY = (int) Math.round(stepY);

        if (moveX == 0 && Math.abs(stepX) > 0.1) {
            moveX = (int) Math.signum(stepX);
        }

        if (moveY == 0 && Math.abs(stepY) > 0.1) {
            moveY = (int) Math.signum(stepY);
        }

        updateFacing(moveX);

        setX(clampToWidth(getX() + moveX, panelWidth));
        setY(clampToGround(getY() + moveY, panelHeight));
    }


    public void onDayTick(){
        if(fedToday){
            energyMeter++;
        } else {
            energyMeter--;
        }
        fedToday = false;
    }


    public boolean shouldReproduce (int reproduceThreshold){
        if(energyMeter >= reproduceThreshold){
            energyMeter = 0;
            return true;
        }
        return false;
    }

    protected int clampToGround(int y, int panelHeight){
        int groundTop = panelHeight/5;
        return Math.max(groundTop, Math.min(y, panelHeight - 65));
    }

    protected int clampToWidth(int x, int panelWidth) {
        return Math.max(0, Math.min(x, panelWidth - 65));
    }

    public void resetState(int startHunger, int startX, int startY) {
        validateHunger(startHunger);
        this.hunger = startHunger;
        this.energyMeter = 0;
        this.fedToday = false;
        this.target = null;
        this.wanderStrategy = new WanderStrategy();
        this.movementStrategy = this.wanderStrategy;
        this.lastX = startX;
        this.lastY = startY;
        this.velocityX = 0;
        this.velocityY = 0;
        this.facingRight = true;
    }   
}
