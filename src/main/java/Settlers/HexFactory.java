package Settlers;

import Settlers.Types.Hextypes;
import Settlers.Types.TileType;
import Settlers.Workers.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

import static Settlers.TileComponent.TILEHEIGHT;
import static Settlers.TileComponent.TILEWIDTH;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.texture;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static Settlers.Types.Hextypes.TILE;


public class HexFactory implements EntityFactory {
    @Spawns("house")
    public Entity newHouse(SpawnData data) {

        return entityBuilder()
                .from(data)
                .with((Component) data.get("house"))
                .view((Line) data.get("path"))
                .view((Texture) data.get("view"))
                .build();
    }

    @Spawns("worker")
    public Entity newWorker(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.WORKER)
                .from(data)
                .with(new WoodCutterComponent())
                .build();
    }

    @Spawns("rockcutter")
    public Entity newRockCutter(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.WORKER)
                .from(data)
                .with(new RockCutter())
                .build();
    }


    @Spawns("forrester")
    public Entity forrester(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.WORKER)
                .from(data)
                .with(new ForresterComponent())
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
    @Spawns("builder")
    public Entity newBuilder(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.WORKER)
                .from(data)
                .with(new HouseBuilderComponent())
                .build();
    }

    @Spawns("SawmillWorker")
    public Entity newSawmillWorker(SpawnData data) {
        return entityBuilder()
                .type(Hextypes.WORKER)
                .from(data)
                .with(new SawmillWorker())
                .build();
    }

    @Spawns("tileContainer")
    public Entity newTileContainer(SpawnData data) {

        Entity tile = entityBuilder()
                .at(0, 0)
//                .view(TileContainerComponent.tiles)
                .with(new TileContainerComponent())
                .build();
        return tile;
    }

    @Spawns("pathSection")
    public Entity newPathSection(SpawnData data) {

        Entity tile = entityBuilder()
                .from(data)
                .view((Polyline) data.get("view"))
                .with((PathComponent) data.get("component"))
                .build();
        return tile;
    }


    @Spawns("tile")
    public Entity newPlatform(SpawnData data) {

//        String[] tilestrings = {"tiles/Fire.png", "tiles/Forest.png", "tiles/Grass.png", "tiles/Water.png"};
//        String tilestring = tilestrings[new Random().nextInt(tilestrings.length)];
        TileComponent tileComponent = data.get("tile");
        String tilestring = "tiles/Grass.png";

//        if (data.getY()%2==0){}
        Paint color = Color.BLUE;
        Paint fillColor = Color.BLUE;
        switch ((TileType) data.get("type")) {

            case GRASS:
                tilestring = "tiles/Grass.png";
                color = Color.rgb(85, 137, 61);
                fillColor = Color.rgb(136, 189, 64);
                break;
            case WATER:
                tilestring = "tiles/Water.png";
                fillColor = Color.rgb(28, 163, 236);
                color = Color.rgb(15, 94, 156)
                ;
                break;
        }
        double size = TileComponent.TILEHEIGHT / 2;
        double v = Math.sqrt(3) / 2.0;
        Polygon hexagon = new Polygon();
        hexagon.getPoints().addAll(0.0, -size,
                Math.sqrt(3) / 2.0 * size, -Math.sqrt(3) / 4.0 * size,
                Math.sqrt(3) / 2.0 * size, Math.sqrt(3) / 4.0 * size,

                0.0, size,
                -Math.sqrt(3) / 2.0 * size, Math.sqrt(3) / 4.0 * size,

                -Math.sqrt(3) / 2.0 * size, -Math.sqrt(3) / 4.0 * size
//                        - Math.sqrt(3) / 2.0 * size, Math.sqrt(3) / 4.0 * size
        );
//        hexagon.maxWidth(TileComponent.TILEWIDTH);
//        hexagon.maxHeight(TileComponent.TILEHEIGHT);
//        hexagon.setScaleX(0.1);
//        hexagon.setScaleY(0.1);
//        hexagon.setFill(color);
        hexagon.setStrokeWidth(6);
        hexagon.setFill(fillColor);
        hexagon.setStroke(color);
//        hexagon.setTranslateX(data.getX());
//        hexagon.setTranslateY(data.getY());
        TileContainerComponent.tiles.getChildren().add(hexagon);

//        Texture tex = texture(tilestring, TileComponent.TILEWIDTH, TileComponent.TILEHEIGHT);
//        tex.setTranslateX(-TileComponent.TILEWIDTH / 2);
//        tex.setTranslateY(-TileComponent.TILEHEIGHT / 2);
//        tex.setSmooth(false);
        Group group = new Group(hexagon);
        Text name = new Text((String) data.get("name"));
        name.setScaleX(3);
        name.setStroke(Color.RED);
        name.setScaleY(3);

//        group.getChildren().add(name);
        Entity tile = entityBuilder()
                .type(TILE)
                .at(data.getX(), data.getY())
                .view(group)
                .with(tileComponent)
                .build();
        return tile;
    }


    public static Entity lineToo(Entity a, Entity b, Color color) {
        SpawnData data = new SpawnData(a.getX(), a.getY());
        data.put("toX", b.getX());
        data.put("toY", b.getY());
        data.put("color", color);
        return spawn("line", data);
    }


    @Spawns("line")
    public Entity line(SpawnData data) {
        Line rect = new Line(data.getX(), data.getY(), data.get("toX"), data.get("toY"));
        rect.setStroke(data.get("color"));
        rect.setStrokeWidth(2);

        return entityBuilder()
                .view(rect)
                .zIndex(5000)
                .build();
    }

}
