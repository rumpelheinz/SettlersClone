package Settlers;

import Settlers.Houses.HouseComponent;
import Settlers.Types.HouseType;
import Settlers.Types.ResourceType;
import Settlers.Types.TaskType;
import com.almasb.fxgl.texture.Texture;

import java.awt.*;

public class Resource {
    public ResourceType type;
    public HouseComponent target;
    public CarrierComponent reservedByWorker;

    public Resource(ResourceType type) {
        this.type = type;
    }

    public void setTarget(HouseComponent newTarget) {
        if (target != null) {
            target.setReserved(this, false);
        }
        if (reservedByWorker != null) {
            reservedByWorker.clearTargetResource();
            reservedByWorker = null;
        }

        if (newTarget == null) {
            target = null;
        } else {
            if (!newTarget.inventoryList.contains(this)) {
                target = newTarget;
                target.setReserved(this, true);
            } else target = null;
        }
    }
}
