package Richardluk12.aio_agility;

import org.powerbot.script.Area;
import org.powerbot.script.ClientContext;
import org.powerbot.script.Tile;

/**
 * Created by Rich on 4/19/2016.
 */
public class Course {

    public String name;
    public Tile mapBase;
    public Obstacle[] actions;
    public boolean failed_ca;
    public int current_action;
    public int level;

    public Course(String _name, Tile _map, int _level, Obstacle... _obstacle){
        this.name = _name;
        this.level = _level;
        this.mapBase = _map;
        this.actions = _obstacle;
        this.current_action = 0;
        this.failed_ca = false;
    }


    public void getAction(Tile location){
        for(int i = 0; i != this.actions.length; i++) {
            if(this.failed_ca && this.actions[i].canFail.canFail) {
                if(this.actions[i].canFail.obstacle.compare(location) || this.actions[i].compare(location)) {
                    if (i + 1 == this.actions.length) {
                        this.current_action = 0;
                        break;
                    } else {
                        this.current_action = i + 1;
                        break;
                    }
                }
            }else if(this.actions[i].compare(location)){
                if(i+1 == this.actions.length) {
                    this.current_action = 0;
                    break;
                } else {
                    this.current_action = i + 1;
                    break;
                }
            }
        }
    }
}
