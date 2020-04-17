import java.awt.image.BufferedImage;

public final class TreasureTile extends Tile_tm{

    private int treasure;

    public TreasureTile(Tile_tm[] neighbors, BufferedImage texture, int entranceDist) {
        super(neighbors, texture, 0,0, 0, entranceDist);
        treasure = 0;
    }

    public TreasureTile(Tile_tm[] neighbors, BufferedImage texture, int entranceDist, int treasure){
        super(neighbors, texture, 0, 0, 0, entranceDist);
        this.treasure = treasure;
    }

    public void loseTreasure(int amount){
        treasure -= amount;
        if(treasure < 0) treasure = 0;
    }

    public void addTreasure(int amount){
        treasure += amount;
    }

    public boolean hasTreasure(){
        return treasure != 0;
    }

    public int getTreasure(){
        return treasure;
    }
}
