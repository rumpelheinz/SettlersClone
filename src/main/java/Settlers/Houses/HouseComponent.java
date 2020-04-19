package Settlers.Houses;


import Settlers.Resource;
import Settlers.TileComponent;
import com.almasb.fxgl.entity.component.Component;

public abstract class HouseComponent extends Component {

    TileComponent flagComponent;

    void addResource(Resource resource){
        this.flagComponent.signalResource(resource);
    }
    abstract public int wantResource(Resource resource);
}
