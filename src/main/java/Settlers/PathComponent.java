package Settlers;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.LinkedList;

public class PathComponent extends Component {
    final PathSection pathSection;
    TileComponent a;
    TileComponent b;
    public PathComponent(TileComponent a, TileComponent b, Color rgb, PathSection pathSection){
        this.a=a;
        this.b=b;
        Line rect = new Line(a.getEntity().getX(), a.getEntity().getY(), b.getEntity().getX(), b.getEntity().getY());
        rect.setStroke(rgb);
        rect.setStrokeWidth(8);
        SpawnData spawnData=new SpawnData(0,0);
        spawnData.put("view",rect);
        spawnData.put("pathComponent",this);
        this.pathSection=pathSection;
        FXGL.spawn("path", spawnData);
    };

    public void delete() {
        this.entity.removeFromWorld();
    }
}
