package Richardluk12.aio_agility;

import org.powerbot.script.*;

/**
 * Created by Rich on 4/19/2016.
 */
public class Obstacle {

    public String action;
    public String oname;
    public int oid;
    public int[] bounds;
    public Tile[] location;
    public FAIL canFail;

    public Obstacle(String _action, String _name, int _id, int[] _bounds, FAIL _canFail, Tile... _location){
        this.action = _action;
        this.oname = _name;
        this.oid = _id;
        this.bounds = _bounds;
        this.canFail = _canFail;
        this.location = _location;
    }

    public boolean compare(Tile tile){
        for(Tile otile : this.location){
            if(otile.equals(tile)){
                return true;
            }
        }
        return false;
    }

    public String getLocations(){
        String olocation = "";
        for(Tile otile : this.location){
            olocation += otile.toString() + "\n";
        }
        return olocation;
    }
}
