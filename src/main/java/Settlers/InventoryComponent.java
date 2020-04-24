package Settlers;

import Settlers.Types.ResourceType;
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


    public void setReserved(Resource resource, boolean reserved) {
        if (reserved&&!inventoryList.contains(resource)) {

            reservedList.add(resource);
        } else {
            reservedList.remove(resource);
        }

    }
    public int getResourceCountFromInventory(ResourceType type) {
        int ret=0;
        for (Resource resource : inventoryList) {
            if (resource.type.equals(type)) {
                ret++;
            }
        }
        return ret;
    }

    public int getResourceCountFromReserve(ResourceType type) {
        int ret=0;
        for (Resource resource : reservedList) {
            if (resource.type.equals(type)) {
                ret++;
            }
        }
        return ret;
    }

    public LinkedList<Resource> getResourcesFromInventory(ResourceType type) {
        LinkedList<Resource> ret = new LinkedList<>();
        for (Resource resource : inventoryList) {
            if (resource.type.equals(type)) {
                ret.add(resource);
            }
        }
        return ret;
    }

    public LinkedList<Resource> getResourcesFromReserve(ResourceType type) {
        LinkedList<Resource> ret = new LinkedList<>();
        for (Resource resource : reservedList) {
            if (resource.type.equals(type)) {
                ret.add(resource);
            }
        }
        return ret;
    }

    public abstract boolean pickUp(Resource resource);

}
