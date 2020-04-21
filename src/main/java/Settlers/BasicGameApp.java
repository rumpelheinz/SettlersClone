package Settlers;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import java.io.File;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getPrimaryStage;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BasicGameApp extends GameApplication {
    public static int stepsBetweenRedraw=0;
    private Entity player;
    static Random random = new Random();
    static protected Map map;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1400);
        settings.setHeight(1000);
//        settings.setFullScreenFromStart(true);
//        settings.setFullScreenAllowed(true);
        settings.setTitle("Basic Game App");
    }

    UserAction scrollLeft = new UserAction("scrollLeft") {
        @Override
        protected void onActionBegin() {
            // action just started (key has just been pressed), play swinging animation
        }

        @Override
        protected void onAction() {
            FXGL.getGameScene().getViewport().setX(FXGL.getGameScene().getViewport().getX() - 10);

        }

        @Override
        protected void onActionEnd() {
            // action finished (key is released), play hitting animation based on swing power
        }
    };
    UserAction scrollUp = new UserAction("scrollUp") {
        @Override
        protected void onActionBegin() {
            // action just started (key has just been pressed), play swinging animation
        }

        @Override
        protected void onAction() {
            FXGL.getGameScene().getViewport().setY(FXGL.getGameScene().getViewport().getY() - 10);

        }

        @Override
        protected void onActionEnd() {
            // action finished (key is released), play hitting animation based on swing power
        }
    };
    UserAction scrollDown = new UserAction("scrollDown") {
        @Override
        protected void onActionBegin() {
            // action just started (key has just been pressed), play swinging animation
        }

        @Override
        protected void onAction() {
            FXGL.getGameScene().getViewport().setY(FXGL.getGameScene().getViewport().getY() + 10);

        }

        @Override
        protected void onActionEnd() {
            // action finished (key is released), play hitting animation based on swing power
        }
    };
    UserAction scrollRight = new UserAction("scrollRight") {
        @Override
        protected void onActionBegin() {
            // action just started (key has just been pressed), play swinging animation
        }

        @Override
        protected void onAction() {
            FXGL.getGameScene().getViewport().setX(FXGL.getGameScene().getViewport().getX() + 10);

        }

        @Override
        protected void onActionEnd() {
            // action finished (key is released), play hitting animation based on swing power
        }
    };

    @Override
    protected void initUI() {
        Group ui = new Group();
//        ui.setTranslateX(getAppWidth() - 200);
//        Rectangle uiBG = new Rectangle(10, 200);
        //     uiBG.setTranslateY(getAppWidth()-200);
//        ui.getChildren().add(ui);
        getGameScene().setBackgroundRepeat("background.png");
//        getGameScene().addUINode(ui);
//
//        for (int i = 0; i < 4; i++) {
//            int index = i + 1;
//
//            Color color = FXGLMath.randomColor();
//            Rectangle icon = new Rectangle(50, 50, color);
//            icon.setTranslateY(10 + i * 100);
//            icon.setTranslateX(100);
////            icon.setOnMouseClicked(e -> {
////                selectedColor = color;
////                selectedIndex = index;
////            });
//            uiBG.addUINode(icon);

    }

    double zoom = 0.5f;

    @Override
    protected void initInput() {
        FXGL.getGameScene().getRoot().addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
            zoom = Math.max(0.1, zoom + (event.getDeltaY() / 400));
            System.out.println(zoom);
            FXGL.getGameScene().getViewport().setZoom(zoom);
        });
        FXGL.getInput().addAction(scrollLeft, KeyCode.LEFT);
        FXGL.getInput().addAction(scrollDown, KeyCode.DOWN);
        FXGL.getInput().addAction(scrollUp, KeyCode.UP);
        FXGL.getInput().addAction(scrollRight, KeyCode.RIGHT);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new HexFactory());
//        FXGL.getPrimaryStage().setX(-1920);
//        FXGL.getPrimaryStage().setY(0);
        map = loadLevel("target/classes/assets/json/map3.json");
//        map=  loadLevel("assets/json/mapnone.json");
//        getGameScene().getRoot().setScaleX(0.5);
//        getGameScene().getRoot().setScaleY(0.5);
        Viewport viewport = FXGL.getGameScene().getViewport();
//        getGameScene().getRoot().autosize();
////
//        getGameScene().getRoot().setScaleX(        (1f*getSettings().getWidth())/((map.width+1)*110));
//        getGameScene().getRoot().setScaleY(        (1f*getSettings().getHeight())/((map.height+1)*95));
////        getGameScene().getRoot().setScaleY(0.5);
//        getGameScene().getRoot().getWidth();
//        getGameScene().getRoot().getHeight();
//        viewport.setX(map.get(map.width/2,map.height/2).getEntity().getX());
////        viewport.setY(map.get(map.width/2,map.height/2).getEntity().getY());
//        viewport.setX(getGameScene().getRoot().getWidth()/2*getGameScene().getRoot().getScaleX());
//        viewport.setY(getGameScene().getRoot().getHeight()/2*getGameScene().getRoot().getScaleY());
//view
//        viewport.setBounds(0,0,map.width*110,(map.height*95));

        viewport.setZoom(0.5);
        viewport.setX(-TileComponent.TILEWIDTH);
        viewport.setY(-TileComponent.TILEWIDTH);
//        viewport.bindToEntity(map.get(map.width/2,map.height/2).getEntity(),1200,1050);
//        viewport.bindToEntity(map.get(map.width/2,map.height/2).getEntity(),map.width*110/2,map.height*95/2);
//
//        map = new Map(6, 5);
//
        for (TileComponent tileComponent : map.tiles.values()) {

//            if (tileComponent.NE != null) {
//                var data = new SpawnData(tileComponent.getEntity().getX(), tileComponent.getEntity().getY());
//                data.put("color", Color.BLUE);
//                data.put("toX", tileComponent.NE.getEntity().getX());
//                data.put("toY", tileComponent.NE.getEntity().getY());
//                Entity line = spawn("catapultLineIndicator", data);
//            }
//            if (tileComponent.E != null) {
//                var data = new SpawnData(tileComponent.getEntity().getX(), tileComponent.getEntity().getY());
//                data.put("color", Color.RED);
//                data.put("toX", tileComponent.E.getEntity().getX());
//                data.put("toY", tileComponent.E.getEntity().getY()+5);
//                Entity line = spawn("catapultLineIndicator", data);
//            }
//            if (tileComponent.NW != null) {
//                var data = new SpawnData(tileComponent.getEntity().getX(), tileComponent.getEntity().getY());
//                data.put("color", Color.GREEN);
//                data.put("toX", tileComponent.NW.getEntity().getX());
//                data.put("toY", tileComponent.NW.getEntity().getY());
//                Entity line = spawn("catapultLineIndicator", data);
//            }
//            if (tileComponent.SW != null) {
//                var data = new SpawnData(tileComponent.getEntity().getX(), tileComponent.getEntity().getY() + 5);
//                data.put("color", Color.CORAL);
//                data.put("toX", tileComponent.SW.getEntity().getX());
//                data.put("toY", tileComponent.SW.getEntity().getY() + 5);
//                Entity line = spawn("catapultLineIndicator", data);
//            }
//            if (tileComponent.SE != null) {
//                var data = new SpawnData(tileComponent.getEntity().getX(), tileComponent.getEntity().getY() + 5);
//                data.put("color", Color.BLACK);
//                data.put("toX", tileComponent.SE.getEntity().getX());
//                data.put("toY", tileComponent.SE.getEntity().getY() + 5);
//                Entity line = spawn("catapultLineIndicator", data);
//            }
//            if (tileComponent.W != null) {
//                var data = new SpawnData(tileComponent.getEntity().getX(), tileComponent.getEntity().getY() + 5);
//                data.put("color", Color.YELLOW);
//                data.put("toX", tileComponent.W.getEntity().getX());
//                data.put("toY", tileComponent.W.getEntity().getY() + 5);
//                Entity line = spawn("catapultLineIndicator", data);
////            }
//            if (tileComponent.W != null) {
//                HexFactory.lineToo(tileComponent.getEntity(),tileComponent.W.getEntity(),Color.YELLOW);
//            }


            //   line.translate(tile.getEntity().getX(),tile.getEntity().getY());
            //            //    line.rotateToVector();

        }
        TileComponent tileComponent = map.tiles.get(new Vec2(2, 2));
        //   tile.Storehouse=add("storehouse",tile.getEntity().getX()-30,tile.getEntity().getY()-64);
//        tileComponent.getEntity().addComponent(new StoreHouseComponent());//=spawn("storehouse",tile.getEntity().getX()-30,tile.getEntity().getY()-64);
//        WorkerComponent worker=spawn("worker").getComponent(WorkerComponent.class);
//        worker.setCurrentTile(tileComponent);
//        worker.home=(map.get(2,2));
//
//        TileComponent tileComponent2 = map.tiles.get(new Vec2(5,0));
//        //   tile.Storehouse=add("storehouse",tile.getEntity().getX()-30,tile.getEntity().getY()-64);
//        tileComponent2.getEntity().addComponent(new StoreHouseComponent());//=spawn("storehouse",tile.getEntity().getX()-30,tile.getEntity().getY()-64);
//        WorkerComponent worker2=spawn("worker").getComponent(WorkerComponent.class);
//        worker2.setCurrentTile(tileComponent);
//        worker2.home=(map.get(5,0));
//
//        new PathComponent(map.tiles.get(new Vec2(2,2)),map.tiles.get(new Vec2(3,2)));
//        new PathComponent(map.tiles.get(new Vec2(3,2)),map.tiles.get(new Vec2(3,3)));
//        new PathComponent(map.tiles.get(new Vec2(3,3)),map.tiles.get(new Vec2(4,3)));

    }

    private Map loadLevel(String filename) {
        File levelFile = new File(filename);
//        Level level = null;
        MyLevelLoader loader = new MyLevelLoader();
        return loader.load(levelFile);
    }

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    protected void onUpdate(double tpf) {

//        System.out.println((System.currentTimeMillis()%100000));
//        viewport.setX((System.currentTimeMillis()%100000)/(1000.0/110)%(30*110- getSettings().getWidth()));
    }
}