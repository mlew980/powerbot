package KaraFisher;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GeItem;

public class Fish {

	private final int id;
	private final String name;
	
	private int count;
	private GeItem item;
	
	public Fish (int id) {
		this.id = id;
		this.item = new GeItem(id);
		this.name = this.item.name;
		this.count = 0;
	}
	
	public Fish (int id, String name) {
		this.id = id;
		this.name = name;
		this.item = new GeItem(id);
		this.count = 0;
	}
	
	public int getID()      { return id;         }
	public String getName() { return name;       }
	public int getCount()   { return count;      }
	public int getPrice()   { return item.price; }
	
	public int getValue()       { return count * getPrice(); }
	public int getCachedPrice() { return item.price;         }
	public void updatePrice()   { item = new GeItem(id);     }
	
	public void updateCount(ClientContext ctx) { 
		count += ctx.backpack.select().id(id).count() + ctx.backpack.id(id + 1).count(true);
	}
}
