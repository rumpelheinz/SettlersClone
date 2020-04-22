package Settlers;

import com.almasb.fxgl.entity.component.Component;

import java.util.LinkedList;

public abstract class InventoryComponent extends Component {
    protected LinkedList<Resource> inventoryList = new LinkedList<>();
    protected LinkedList<Resource> reservedList = new LinkedList<>();

    public LinkedList<Resource> getResourcesWithTargets() {
        LinkedList<Resource> resourcesWithTargets = new LinkedList<>();
        for (Resource resource : inventoryList) {
            if (resource.target != null && resource.target != this) {
                resourcesWithTargets.add(resource);
            }
        }
        return resourcesWithTargets;
    }


    public void setReserved(Resource resource,boolean reserved) {
        if (reserved){
            this.reservedList.add(resource);
        }
        else {
            this.reservedList.remove(resource);
        }

    }

    public abstract boolean pickUp(Resource resource);

}
