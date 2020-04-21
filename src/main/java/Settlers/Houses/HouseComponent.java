package Settlers.Houses;


import Settlers.InventoryComponent;
import Settlers.Resource;
import Settlers.TileComponent;
import com.almasb.fxgl.entity.component.Component;

public abstract class HouseComponent extends InventoryComponent {

    TileComponent flagComponent;

    abstract public int wantResource(Resource resource);

    public abstract void addResource(Resource resource);

    public void reCalculateStock(){
        for (Resource resource: inventoryList){
            if (resource.target!=null){
                if (flagComponent.getPathsectionTo(resource.target)==null&&resource.target!=this){
                    resource.setTarget(null);
                }
            }
        }
    }
}
