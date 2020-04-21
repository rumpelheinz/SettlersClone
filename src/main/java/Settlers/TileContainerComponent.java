package Settlers;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.Group;

public class TileContainerComponent extends Component {
static Group tiles=new Group();

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(tiles);
        entity.setZ(-1);
    }
}
