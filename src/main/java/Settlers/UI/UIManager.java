package Settlers.UI;

import Settlers.Houses.*;
import Settlers.PathComponent;
import Settlers.Resource;
import Settlers.TileComponent;
import Settlers.Types.HouseSize;
import Settlers.Types.HouseType;
import Settlers.Types.ResourceType;
import com.almasb.fxgl.core.View;
import com.almasb.fxgl.texture.Texture;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
    public Pane root;
    TilePane buildHousePane;

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
        root = new TilePane();
        root.setMinWidth(400);
        root.setMaxWidth(400);
        root.setMinHeight(getAppHeight());
        root.setMaxHeight(getAppHeight());
        root.setTranslateX(getAppWidth() - 400);
        buildHousePane = new TilePane();
        buildHousePane.setMaxWidth(350);
        buildHousePane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        initBuildHouses();
        ScrollPane pane = new ScrollPane(buildHousePane);
        pane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        pane.setMinHeight(550);
        // pane.setBackground(Background.EMPTY);
        resourcePane = new TilePane();

        resourcePane.setMinWidth(400);
        resourcePane.setMaxWidth(400);
        resourcePane.setOrientation(Orientation.VERTICAL);
//        resourcePane.setMinHeight(1000);
        resourcePane.setBackground(new Background(new BackgroundFill(Color.BROWN, null, null)));
        root.getChildren().add(pane);
        root.getChildren().add(resourcePane);
        root.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

    }
    static Texture log = texture("log.png", 16, 16);
    static Texture plank = texture("plank.png", 16, 16);
    static Texture stone = texture("rock.png", 16, 16);
    static Texture getResourceTexture(ResourceType type){
        switch (type){
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

                    StackPane child=new StackPane();
                    child.setPrefWidth(200);
                    child.setPrefHeight(20);
                    Texture tex=getResourceTexture(type);
//                    tex.setTranslateX(-50);
                    child.getChildren().add(tex);
                    Text text=new Text( " -> " + ((resource.target == null) ? "" : resource.target.name));
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
                    StackPane child=new StackPane();
                    child.setPrefWidth(200);
                    child.setPrefHeight(20);
                    Texture tex=getResourceTexture(type);
                    tex.setTranslateX(-100);
                    child.getChildren().add(tex);
                    Text text=new Text( " <--- " /*+ ((resource.target == null) ? "" : resource.target.toString())*/);
                    tex.setTranslateX(-80);
                    child.getChildren().add(text);
                    resourcePane.getChildren().add(child);
                }
            }

        } else {
        }
    }

    public void initBuildHouses() {
        ObservableList<Node> children = buildHousePane.getChildren();
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
