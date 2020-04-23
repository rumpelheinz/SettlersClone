package Settlers.Houses;


import Settlers.InventoryComponent;
import Settlers.Resource;
import Settlers.TileComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;

public abstract class HouseComponent extends InventoryComponent{

    public boolean destroyed = false;
    TileComponent flagTile;
    TileComponent location;
    Texture texture;

    abstract public int wantResource(Resource resource);

    public abstract void addResource(Resource resource);

    public void reCalculateStock() {
        for (Resource resource : inventoryList) {
            if (resource.target != null) {
                if (flagTile.getPathsectionTo(resource.target) == null && resource.target != this) {
                    resource.setTarget(null);
                }
            }
        }
    }

    public void destroy() {
        this.flagTile.house = null;
        this.location.occupied = false;
        for (Resource resource : (LinkedList<Resource>) reservedList.clone()) {
            resource.setTarget(null);
        }
        for (Resource resource : inventoryList) {
            resource.setTarget(null);
        }
        destroyed = true;
        entity.removeFromWorld();
    }

}
