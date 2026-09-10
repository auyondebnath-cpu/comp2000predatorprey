public class Grass extends Entity{
    private int growthTimer;
    private boolean isEaten;
    public static final int REGROW_TIME = 100;

    public Grass (int x, int y, int growthTimer){
        super(x, y, true);
        this.growthTimer = growthTimer;
        this.isEaten = false;
    }

    public boolean isEdible(){
        return !isEaten;
    }

    public int getGrowthTimer(){
        return growthTimer;
    }

    public void setGrowthTimer(int growthTimer){
        this.growthTimer = growthTimer;
    }

    public boolean isEaten(){
        return isEaten;
    }

    public void setEaten(boolean isEaten){
        this.isEaten = isEaten;
    }

    @Override
    public void update(int panelWidth, int panelHeight){
        if(isEaten){
            growthTimer--;
            if(growthTimer <=0){
                isEaten = false;
                growthTimer = REGROW_TIME;
            }
        }
    }
}