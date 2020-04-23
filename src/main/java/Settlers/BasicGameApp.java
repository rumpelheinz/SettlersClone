package Settlers;

import Settlers.UI.UIManager;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BasicGameApp extends GameApplication {
    public static int stepsBetweenRedraw = 0;
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


    @Override
    protected void initUI() {
        UIManager ui = new UIManager();
        getGameScene().setBackgroundRepeat("background.png");
        getGameScene().addUINode(ui.root);
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
        Viewport viewport = FXGL.getGameScene().getViewport();

        viewport.setZoom(0.5);
        viewport.setX(-TileComponent.TILEWIDTH);
        viewport.setY(-TileComponent.TILEWIDTH);
        for (TileComponent tileComponent : map.tiles.values()) {
        }
        TileComponent tileComponent = map.tiles.get(new Vec2(2, 2));
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
}