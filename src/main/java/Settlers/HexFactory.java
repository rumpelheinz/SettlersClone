package Settlers;

import Settlers.Types.Hextypes;
import Settlers.Types.TileType;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.texture;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static Settlers.Types.Hextypes.TILE;


public class HexFactory implements EntityFactory {
    @Spawns("storehouse")
    public Entity newStoreHouse(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.STOREHOUSE)
                .from(data)
                .view(texture("house.png"))
             //   .with(new StoreHouseComponent())
                .build();
    }
    @Spawns("worker")
    public Entity newWorker(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.WORKER)
                .from(data)
                .with(new WorkerComponent())
                .build();
    }
    @Spawns("carrier")
    public Entity newCarrier(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.CARRIER)
                .from(data)
                .with(new CarrierComponent())
                .build();
    }

    @Spawns("tile")
    public Entity newPlatform(SpawnData data) {

//        String[] tilestrings = {"tiles/Fire.png", "tiles/Forest.png", "tiles/Grass.png", "tiles/Water.png"};
//        String tilestring = tilestrings[new Random().nextInt(tilestrings.length)];
        TileComponent tileComponent=data.get("tile");
        String tilestring="tiles/Grass.png";

//        if (data.getY()%2==0){}
        switch ((TileType) data.get("type")) {

            case GRASS:
                tilestring="tiles/Grass.png";
                break;
            case WATER:
                tilestring="tiles/Water.png";
                break;
        }

        Texture tex = texture(tilestring,110,128);
        tex.setTranslateX(-55);
        tex.setTranslateY(-64);
        Group group=new Group(tex);
        Text name=new Text((String)data.get("name"));
        name.setScaleX(3);
        name.setStroke(Color.RED);
        name.setScaleY(3);

        group.getChildren().add(name);
        Entity tile = entityBuilder()
                .type(TILE)
                .at(data.getX(), data.getY())
                .view(group)
                .with(tileComponent)
                .build();
        return tile;
    }


    public static Entity lineToo(Entity a, Entity b,Color color){
        SpawnData data=new SpawnData(a.getX(),a.getY());
        data.put("toX",b.getX());
        data.put("toY",b.getY());
        data.put("color",color);
        return  spawn("line", data);
    }
    @Spawns("line")
    public Entity line(SpawnData data) {
        Line rect = new Line(data.getX(),data.getY(),data.get("toX"),data.get("toY"));
        rect.setStroke(data.get("color"));
        rect.setStrokeWidth(2);

        return entityBuilder()
                .view(rect)
                .zIndex(5000)
                .build();
    }
    @Spawns("path")
    public Entity newPath(SpawnData data) {
        Line rect=data.get("view");
        PathComponent component=data.get("pathComponent");
        return entityBuilder()
                .view(rect)
                .with(component)
                .zIndex(5000)
                .build();
    }


}
