package Settlers.UI;

import Settlers.BasicGameApp;
import Settlers.Houses.*;
import Settlers.PathComponent;
import Settlers.Resource;
import Settlers.TileComponent;
import Settlers.Types.GameSpeed;
import Settlers.Types.HouseSize;
import Settlers.Types.HouseType;
import Settlers.Types.ResourceType;
import com.almasb.fxgl.texture.Texture;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppHeight;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class UIManager {
    public static BlendMode defaultblendmode;
    private static HouseType houseToBuild;
    public VBox root;
    VBox buildHousePane;

    static TileComponent firstClicked;
    static TileComponent secondClicked;

    static Text mouseOverTexture;
    static TilePane resourcePane;
    public static HouseComponent selectedHouse;

    public static void mouseOverTile(TileComponent tileComponent, boolean entered) {

        if (entered) {
            int scale = 3;
            String text = "X";
            if (tileComponent.canBuildFlag()) {
                text = "|";
                scale = 4;
            }
            if (tileComponent.canBuildHouse(HouseSize.HUT))
                text = "o";
            if (tileComponent.canBuildHouse(HouseSize.House)) {
                text = "O";
                scale = 5;
            }

            mouseOverTexture = new Text(text);
            mouseOverTexture.setScaleX(scale);
            mouseOverTexture.setScaleY(scale);
            tileComponent.getEntity().getViewComponent().addChild(mouseOverTexture);
        } else tileComponent.getEntity().getViewComponent().removeChild(mouseOverTexture);
    }

    public enum ClickMode {
        BUILDPATH, DESTROY, NONE, BUILD
    }

    static public ClickMode clickMode;

    public UIManager() {
        root = new VBox();
        root.setMinWidth(400);
        root.setMaxWidth(400);
        root.setMinHeight(getAppHeight());
        root.setMaxHeight(getAppHeight());
        root.setTranslateX(getAppWidth() - 400);
//        ButtonBar buttonBar = new ButtonBar();
//        buttonBar.setPrefWidth(400);
//        buttonBar.getButtons().add(new Text("Game Speed"));
////        buttonBar.setPrefHeight(100);
////        buttonBar.setMaxHeight(100);
//
//        TilePane SpeedPane = new TilePane();
////        SpeedPane.setMinWidth(400);
////        SpeedPane.setMaxWidth(400);
////        SpeedPane.setMinHeight(50);
////        SpeedPane.setMaxHeight(50);
//        SpeedPane.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
//        kotlin.Pair<GameSpeed, String>[] speedPairs = new kotlin.Pair[]{new kotlin.Pair<GameSpeed, String>(GameSpeed.NORMAL, "Normal"), new kotlin.Pair<GameSpeed, String>(GameSpeed.FAST, "3x"), new kotlin.Pair<GameSpeed, String>(GameSpeed.VERY_FAST, "5x")};
//        for (kotlin.Pair<GameSpeed, String> val : speedPairs) {
//            Button but = new Button(val.component2());
//            but.setOnMouseClicked((e) -> {
//                BasicGameApp.gameSpeed = val.component1();
//            });
//            buttonBar.getButtons().add(but);
//        }
//
//        SpeedPane.getChildren().add(buttonBar);

        HBox SpeedPane = new HBox();
        Text speedLabel = new Text("Game Speed");
        SpeedPane.getChildren().add(speedLabel);

        Slider speedSlider = new Slider(BasicGameApp.gameSpeed.val, 1, 5);
        speedSlider.setMin(1);
        speedSlider.setMax(5);
        speedSlider.setMajorTickUnit(1);
//        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                BasicGameApp.gameSpeed = new GameSpeed(new_val.intValue());
            }
        });
        SpeedPane.getChildren().add(speedSlider);

        root.getChildren().add(SpeedPane);

        buildHousePane = new VBox();
        buildHousePane.setMaxWidth(350);
        buildHousePane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        initBuildHouses();
        ScrollPane scrollPane = new ScrollPane(buildHousePane);
        scrollPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
//        scrollPane.setMinHeight(550);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setBackground(Background.EMPTY);
//        root.getChildren().add(new GUITest().init());
        resourcePane = new TilePane();
        resourcePane.setMinWidth(400);
        resourcePane.setMaxWidth(400);
        resourcePane.setOrientation(Orientation.VERTICAL);
//        resourcePane.setMinHeight(1000);
        resourcePane.setBackground(new Background(new BackgroundFill(Color.BROWN, null, null)));
        root.getChildren().add(scrollPane);
        root.getChildren().add(resourcePane);
        root.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
//        root.applyCss();
    }

    static Texture log = texture("log.png", 16, 16);
    static Texture plank = texture("plank.png", 16, 16);
    static Texture stone = texture("rock.png", 16, 16);

    static Texture getResourceTexture(ResourceType type) {
        switch (type) {
            case STONE:
                return texture("rock.png", 32, 32);
            case PLANK:
                return texture("plank.png", 32, 32);
            case LOG:
                return texture("log.png", 32, 32);

        }
        return null;
    }

    public static void repaintResourcePane() {
        resourcePane.getChildren().clear();
        System.out.println("repaint");
        if (selectedHouse != null) {
            resourcePane.getChildren().add(new Text(selectedHouse.name));
            for (ResourceType type : ResourceType.values()) {
//                resourcePane.getChildren().add(new Text(type.toString()));

                for (Resource resource : selectedHouse.getResourcesFromInventory(type)) {

                    StackPane child = new StackPane();
                    child.setPrefWidth(200);
                    child.setMaxWidth(200);
                    child.setPrefHeight(20);
                    Texture tex = getResourceTexture(type);
//                    tex.setTranslateX(-50);
                    child.getChildren().add(tex);
                    Text text = new Text(" -> " + ((resource.target == null) ? "" : resource.target.name));
                    //text.setTranslateX(-0);
                    child.getChildren().add(text);
                    child.setAlignment(tex, Pos.TOP_LEFT);
                    tex.setTranslateX(10);
                    child.setAlignment(text, Pos.TOP_LEFT);
                    text.setTranslateX(60);
                    text.setTranslateY(10);
                    resourcePane.getChildren().add(child);
                }
            }
            resourcePane.getChildren().add(new Text(selectedHouse.name + " Arriving"));
            for (ResourceType type : ResourceType.values()) {

                for (Resource resource : selectedHouse.getResourcesFromReserve(type)) {
                    StackPane child = new StackPane();
                    child.setPrefWidth(200);
                    child.setMaxWidth(200);
                    child.setPrefHeight(20);
                    Texture tex = getResourceTexture(type);
                    tex.setTranslateX(-100);
                    child.getChildren().add(tex);
                    Text text = new Text("<---" /*+ ((resource.target == null) ? "" : resource.target.toString())*/);
                    tex.setTranslateX(-80);
                    child.getChildren().add(text);
                    resourcePane.getChildren().add(child);
                    resourcePane.autosize();

                }
            }

        } else {
        }
    }

    public void initBuildHouses() {
        ObservableList<Node> children = buildHousePane.getChildren();
        buildHousePane.setMaxWidth(350);
        buildHousePane.autosize();
        Text small = new Text("Small Buildings");
        small.setFill(Color.TRANSPARENT);//new Background(new BackgroundFill(Color.TRANSPARENT,null,null))
        small.setStroke(Color.WHITE);
        children.add(small);

        for (HouseType housetype : new HouseType[]{HouseType.WOODCUTTER, HouseType.FORRESTER, HouseType.ROCKCUTTER}) {
            children.add(new HouseButton(housetype).button);
        }
        Text medium = new Text("Medium Buildings");
        medium.setStroke(Color.WHITE);
        children.add(medium);
        for (HouseType housetype : new HouseType[]{HouseType.SAWMILL, HouseType.STOREHOUSE,}) {
            children.add(new HouseButton(housetype).button);
        }
    }


    private class HouseButton {
        Button button;
        Texture texture;
        HouseType type;

        HouseButton(HouseType type) {
            switch (type) {
                case WOODCUTTER:
                    texture = WoodcutterHouseComponent.getNewTexture(64, 64);
                    this.type = type;
                    break;
                case STOREHOUSE:
                    texture = StoreHouseComponent.getNewTexture(64, 64);
                    this.type = type;
                    break;
                case SAWMILL:
                    texture = SawmillHouseComponent.getNewTexture(64, 64);
                    this.type = type;
                    break;
                case FORRESTER:
                    texture = ForresterHouseComponent.getNewTexture(64, 64);
                    this.type = type;
                    break;
                case ROCKCUTTER:
                    texture = RockCutterHouseComponent.getNewTexture(64, 64);
                    this.type = type;
                    break;
            }
            button = new Button();
            button.setOnMouseClicked(e -> {
                if (firstClicked != null) {
                    firstClicked.getEntity().getViewComponent().setOpacity(1);
                    firstClicked.getEntity().getViewComponent().getChildren().get(0).blendModeProperty().set(defaultblendmode);
                    firstClicked = null;
                }
                System.out.println(type);
                clickMode = ClickMode.BUILD;
                houseToBuild = type;
            });
            button.setMaxWidth(350);
            button.setGraphic(texture);
            button.setText(type.toString());
            button.setId("Button");
        }

    }

    public static void clickPath(TileComponent tileComponent, MouseButton button) {

        if (firstClicked == null) {
            if (clickMode == ClickMode.BUILD) {
                clickMode = ClickMode.NONE;
                tileComponent.buildHouse(houseToBuild, true);

            } else if (button.equals(MouseButton.PRIMARY)) {
                firstClicked = tileComponent;
                firstClicked.getEntity().getViewComponent().setOpacity(0.5);
                clickMode = ClickMode.BUILDPATH;

            } else if (button.equals(MouseButton.SECONDARY)) {
                firstClicked = tileComponent;
                firstClicked.getEntity().getViewComponent().getChildren().get(0).blendModeProperty().set(BlendMode.RED);
                clickMode = ClickMode.DESTROY;
            }

        } else {
            if (button.equals(MouseButton.PRIMARY)) {
                secondClicked = tileComponent;
                if (clickMode == ClickMode.BUILDPATH)
                    PathComponent.buildPath(firstClicked, secondClicked);

            } else {
                secondClicked = tileComponent;
                if (clickMode == ClickMode.DESTROY) {
                    if (firstClicked == secondClicked) {
                        if (firstClicked.pathPassingThrough != null) {
                            firstClicked.pathPassingThrough.destroyPath(true, null);
                        } else {
                            if (firstClicked.flag) {
                                firstClicked.setFlag(false);
                            }
                        }
                    }
                }
            }
            firstClicked.getEntity().getViewComponent().setOpacity(1);
            firstClicked.getEntity().getViewComponent().getChildren().get(0).blendModeProperty().set(defaultblendmode);
            firstClicked = null;
            secondClicked = null;

        }
    }

}
