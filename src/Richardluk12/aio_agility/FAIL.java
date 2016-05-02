package Richardluk12.aio_agility;

import org.powerbot.script.Tile;

/**
 * Created by Rich on 4/19/2016.
 */
public class FAIL{
    public boolean canFail;
    public Obstacle obstacle;
    public Tile failLocation;
    public FAIL(boolean _fail){
        this.canFail = _fail;
        this.obstacle = null;
        this.failLocation = new Tile(0, 0, 0);
    }
    public FAIL(boolean _fail, Obstacle _backStep, Tile _failLocation){
        this.canFail = _fail;
        this.obstacle = _backStep;
        this.failLocation = _failLocation;
    }
}

