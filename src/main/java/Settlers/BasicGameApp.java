package Settlers;

import Settlers.Types.GameSpeed;
import Settlers.UI.UIManager;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.CSS;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BasicGameApp extends GameApplication {
    public static int stepsBetweenRedraw = 0;
    static Random random = new Random();
    static protected Map map;

    public static GameSpeed gameSpeed=GameSpeed.FAST;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1400);
        settings.setHeight(1000);
        settings.setManualResizeEnabled(true);
//        settings.setFullScreenFromStart(true);
        settings.setFullScreenAllowed(true);
        settings.setTitle("Basic Game App");
        settings.getCSSList().add("myStyle.css");

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
            Viewport viewport = getGameScene().getViewport();
            System.out.println("height " + viewport.getHeight());
//            getGameScene().get
            if (zoom > 0) {
                double x = FXGL.getInput().getMouseXWorld();
                double y = FXGL.getInput().getMouseYWorld();

//            System.out.println(FXGL.getInput().getMouseXUI());
//            System.out.println(FXGL.getInput().getMouseYUI());
                double centerX = viewport.getX() + (viewport.getWidth() / viewport.getZoom() / 2);//+viewport.getWidth()*viewport.getZoom();
                double centerY = viewport.getY() + (viewport.getHeight() / viewport.getZoom() / 2);
                viewport.setZoom(zoom);
                viewport.setX(centerX - (viewport.getWidth() / viewport.getZoom() / 2));//-(viewport.getWidth()/2)*viewport.getZoom());
                viewport.setY(centerY - (viewport.getHeight() / viewport.getZoom() / 2));//-(viewport.getHeight()/2)*viewport.getZoom());
            }
        });
        FXGL.getInput().addAction(scrollLeft, KeyCode.A);
        FXGL.getInput().addAction(scrollDown, KeyCode.S);
        FXGL.getInput().addAction(scrollUp, KeyCode.W);
        FXGL.getInput().addAction(toggleFullScreen, KeyCode.F);
        FXGL.getInput().addAction(scrollRight, KeyCode.D);
        FXGL.getGameScene().getRoot().addEventHandler(MouseDragEvent.ANY, (e -> {
            System.out.println(e.toString());
        }));
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new HexFactory());
//        FXGL.getPrimaryStage().setX(-1920);
//        FXGL.getPrimaryStage().setY(0);
        map = loadLevel("/assets/json/map3.json");
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
//        FXGL.getAssetLoader().loadText(filename);
//        File levelFile = new File(filename);
//        Level level = null;
        MyLevelLoader loader = new MyLevelLoader();
        return loader.load(FXGL.getAssetLoader().getStream(filename));
    }

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    protected void onUpdate(double tpf) {

//        System.out.println((System.currentTimeMillis()%100000));
//        viewport.setX((System.currentTimeMillis()%100000)/(1000.0/110)%(30*110- getSettings().getWidth()));
    }

    UserAction toggleFullScreen = new UserAction("toggleFullScreen") {
        @Override
        protected void onActionBegin() {
            FXGL.getSettings().getFullScreen().set(!FXGL.getSettings().getFullScreen().get());
            if (FXGL.getSettings().getFullScreen().get()) {
                //                FXGL.getSettings().setHeight(1000);
                Viewport view = getGameScene().getViewport();
            }
//            FXGL.getGameScene().getViewport().setX(FXGL.getGameScene().getViewport().getX() - 10);
        }

        @Override
        protected void onAction() {
        }

        @Override
        protected void onActionEnd() {
            // action finished (key is released), play hitting animation based on swing power
        }
    };

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