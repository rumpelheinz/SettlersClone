package Settlers;

import Settlers.Houses.HouseComponent;
import Settlers.Types.HouseType;
import Settlers.Types.ResourceType;

public class Resource {
    public ResourceType type;
    public HouseComponent target;
    public boolean busy;
    public Resource(ResourceType type){
        this.type=type;
    }

}
