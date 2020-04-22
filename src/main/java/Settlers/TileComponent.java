package Settlers;

import Settlers.Houses.HouseComponent;
import Settlers.Types.Direction;
import Settlers.Types.TileType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;


import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class TileComponent extends InventoryComponent {
    public final TileType type;
    protected int x;
    protected int y;
    TileComponent NE;
    TileComponent NW;
    TileComponent E;
    TileComponent W;
    TileComponent SW;
    TileComponent SE;
    public PathSection pathPassingThrough = null;
    public boolean flag = false;
    final public static int TILEHEIGHT = 32 * 3;
    final public static int TILEWIDTH = 28 * 3;
    static private BlendMode defaultblendmode;

    public Direction directionOfNeighbour(TileComponent neighbour) {
        for (Direction dir : Direction.values()) {
            if (getNeighbour(dir) == neighbour) {
                return dir;
            }
        }
        return null;
    }

    protected LinkedList<TileComponent> pathHere;
    boolean lookedAt = false;

    public TileComponent(int x, int y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;

        flagTexture = texture("objects/sign.png", TILEWIDTH / 3, TILEHEIGHT / 3);
        flagTexture.setTranslateY(-TILEWIDTH / 3);
        flagTexture.setTranslateX(-TILEHEIGHT / 3 / 2);
        pathSections = new LinkedList<PathSection>();
        allFlags.add(this);

        int coordx = (x * (TILEWIDTH));
        int coordy = (int) (Math.round(y * TILEWIDTH * (0.83)));
        if (y % 2 == 1) {
            coordx += (TILEWIDTH) / 2;
        }
        SpawnData data = new SpawnData(coordx, coordy);
        data.put("type", type);
        data.put("tile", this);
        data.put("name", "" + x + "," + y);

        FXGL.getGameWorld().spawn("tile", data);

//
//        if (x==5&&y==4)
//            entity.addComponent(new TreeComponent(3));
//        else if (type == TileType.GRASS)
//            entity.addComponent(new TreeComponent(trees));
    }

    Group stock;

    @Override
    public void onAdded() {
        stock = new Group();
        stock.setTranslateX(TILEWIDTH / 2 * 1);
        stock.setTranslateY(TILEHEIGHT / 2 * 0.1);
        entity.getViewComponent().addChild(stock);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("x" + x + "y" + y);
            clickpath(this, e.getButton());
            setActive();
        });
        drawStock();
        defaultblendmode = getEntity().getViewComponent().getChildren().get(0).getBlendMode();
//        entity.setUpdateEnabled(false);
    }

    static TileComponent firstClicked;
    static TileComponent secondClicked;

    private enum ClickMode {
        BUILDPATH, DESTROY
    }

    ;
    private static ClickMode clickMode;

    static void clickpath(TileComponent tileComponent, MouseButton button) {

        if (firstClicked == null) {
            if (button.equals(MouseButton.PRIMARY)) {
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
                    PathSection.buildPath(firstClicked, secondClicked);

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

    public boolean hasPath(Direction dir) {
        return (getNeighbour(dir) != null);
    }

    public TileComponent getNeighbour(@NotNull Direction dir) {
        switch (dir) {
            case NE:
                return NE;

            case E:
                return E;

            case SE:
                return SE;

            case SW:
                return SW;

            case W:
                return W;

            case NW:
                return NW;

        }
        return null;
    }

    public void clearAreaSearch() {
        pathHere = null;
        lookedAt = false;
    }

    Texture flagTexture;
    boolean active = false;
    LinkedList<PathSection> pathSections;
    public HouseComponent house;
    PathSection predecessorPath;
    LinkedList<TileComponent.LengthPair> calculatedConnections;
    public LinkedList<TileComponent.LengthPair> connections;

    void clearPathSearch() {
        //connections=null;
        predecessor = null;
        hasBeenLookedAt = false;
        predecessorPath = null;
        calculatedConnections = new LinkedList<LengthPair>();
    }

    static int searches = 0;

    public static void clearAllSearches() {
        searches++;
        //  System.out.print("Search " + searches);
        for (TileComponent flag : allFlags) {
            flag.clearPathSearch();
            //      flag.clearAreaSearch();
        }
        //    System.out.println("Enndsearch " + searches);

//        for (FlagComponent flag : allFlags) {
//            System.out.print("x");
//            flag.calculatePath();
//        }
//        for (FlagComponent flag : allFlags) {
//            if (flag.connections.size()>0){
//                System.out.println(flag.connections.size());
//            }
//        }

    }

    private TileComponent predecessor;
    private boolean hasBeenLookedAt = false;
    static LinkedList<TileComponent> allFlags = new LinkedList<TileComponent>();

    void setActive() {
        if (flag) {
            if (!active) {
                active = true;
                entity.getViewComponent().addChild(flagTexture);
            }
        } else {
            if (active) {
                active = false;
                entity.getViewComponent().removeChild(flagTexture);

            }
        }
    }

    public void setFlag(boolean flag) {
        setActive();
        if (flag) {
            if (this.flag) {
            } else {
                this.flag = true;
                setActive();
                PathSection oldpassiongThrough = pathPassingThrough;
                pathPassingThrough = null;
                if (oldpassiongThrough != null) {
                    oldpassiongThrough.split(this);
                }
            }
        } else {
            if (house!=null){
                house.destroy();
            }
            if (this.flag && connections != null) {
                LinkedList<TileComponent> connected = new LinkedList<>();
                LinkedList<LengthPair> freshConnections = new LinkedList<>();
                for (PathSection pathSection : (LinkedList<PathSection>) pathSections.clone()) {
                    connected.add(pathSection.getOtherSide(this));
                    pathSection.destroyPath(false, this);
                    removePath(pathSection);
                }
                for (TileComponent comp : connected) {
                    boolean recalculate = true;
                    for (LengthPair pair : freshConnections) {
                        if (comp == pair.component) {
                            recalculate = false;
                            break;
                        }
                    }
                    if (recalculate) {
                        comp.reCalculatePath();
                        freshConnections.addAll(comp.connections);
                    }
                }
            }
            this.flag = false;
        }
        setActive();

    }

    public void addPath(PathSection section) {
        setFlag(true);
        pathSections.add(section);
        setActive();
    }

    public void removePath(PathSection section) {
        pathSections.remove(section);
        if (pathSections.size() == 0) {
            setActive();
        }
    }

    public void signalResource(Resource resource, int maxPriority) {

        int maxDistance = Integer.MAX_VALUE;
        HouseComponent target = null;
        if (connections != null) {
            for (LengthPair location : connections) {
                if (location.type == LengthPairType.BUILDING) {
                    HouseComponent curTarget = (HouseComponent) location.component;
                    if (curTarget.wantResource(resource) > maxPriority || (location.distance < maxDistance && curTarget.wantResource(resource) >= maxPriority)) {
                        target = curTarget;
                        maxPriority = curTarget.wantResource(resource);
                        maxDistance = location.distance;
                    }
                }
            }

        }
        if (target != null) {
            resource.setTarget(target);
        }

//        addResource(resource);
    }

    void drawStock() {
        stock.getChildren().clear();
        int i = 0;
        for (Resource resource : inventoryList) {
            Texture tex = getResourceTexture(resource);
            tex.setTranslateY(-i * 15);
            stock.getChildren().add(tex);
            i++;
        }
    }

    private Texture getResourceTexture(Resource resource) {
        Texture tex = null;
        switch (resource.type) {
            case LOG:
                tex = texture("log.png", 32, 32);
                break;
            case PLANK:
                tex = texture("plank.png", 32, 32);
                break;
            case STONE:
                tex = texture("stone.png", 32, 32);
                break;
        }
        tex.setTranslateY(-32);
        tex.setTranslateX(-32);
        return tex;
    }

    public void addResource(Resource resource) {
        resource.reservedByWorker = null;
        if (resource.target == null) {
            signalResource(resource, 0);
        }
        if (resource.target != null) {
            if (this.house != null && resource.target == house) {
                house.addResource(resource);
            } else {
                if (getPathsectionTo(resource.target) != null) {
                    getPathsectionTo(resource.target).pathSection.signalResource(resource, this);
                }
                signalResource(resource, 0);
                inventoryList.add(resource);
            }
        } else {
            inventoryList.add(resource);
        }
        drawStock();

    }

    public LinkedList<LengthPair> getAllPathsectionsTo(Component target) {
        LinkedList<LengthPair> allPathSectionsToTarget = new LinkedList<>();
        if (connections != null) {
            for (LengthPair pair : connections) {
                if (pair.component == target) {
                    allPathSectionsToTarget.add(pair);
                }
            }
        }
        return allPathSectionsToTarget;
    }

    public LengthPair getPathsectionTo(Component target) {
        for (LengthPair pair : connections) {
            if (pair.component == target) {
                return pair;
            }
        }
        return null;
    }

    ;

    public boolean pickUp(Resource resource) {
        boolean ret = inventoryList.remove(resource);
        if (!ret) {
            ret = house.pickUp(resource);
        } else {
            drawStock();
        }
        return ret;
    }

    private enum LengthPairType {
        BUILDING, FLAG
    }

    public class LengthPair {
        int distance;
        public Component component;
        public PathSection pathSection;
        public LengthPairType type;

        private LengthPair(int distance, Component component, PathSection pathSection, LengthPairType type) {
            this.distance = distance;
            this.component = component;
            this.pathSection = pathSection;
            this.type = type;
        }
    }

    public void setHouse(HouseComponent houseComponent) {
        this.house = houseComponent;
        setFlag(true);
    }

    public void reCalculatePath() {
        clearAllSearches();
        calculatePath();
        if (connections != null)
            for (TileComponent.LengthPair comp : connections) {
                if (comp.component instanceof TileComponent) {
                    clearAllSearches();
                    ((TileComponent) comp.component).calculatePath();
                }
            }
    }

    public void calculatePath() {

        if (pathSections.size() == 0) {
            connections = new LinkedList<>();
            reCalculateStock();
            if (house != null) {
                house.reCalculateStock();
            }
            return;
        }
        System.out.println();
        System.out.println("Calculating path from " + entity.getComponent(TileComponent.class).x + " " + entity.getComponent(TileComponent.class).y);
        LinkedList<TileComponent> flaglist = new LinkedList<TileComponent>();
        LinkedList<TileComponent> finishedList = new LinkedList<TileComponent>();

        flaglist.add(this);
        hasBeenLookedAt = true;
        while (flaglist.size() != 0) {
            TileComponent currentFlag = flaglist.removeFirst();
            for (PathSection currentPath : currentFlag.pathSections) {
                TileComponent nextFlag;
                if (currentPath.a == currentFlag) {
                    nextFlag = currentPath.b;
                } else if (currentPath.b == currentFlag) {
                    nextFlag = currentPath.a;
                } else {
                    System.out.println("Error");
                    return;
                }
                if (!nextFlag.hasBeenLookedAt) {
                    nextFlag.predecessor = currentFlag;
                    nextFlag.predecessorPath = currentPath;
                    flaglist.addLast(nextFlag);
                    nextFlag.hasBeenLookedAt = true;
                }

            }
            finishedList.addLast(currentFlag);

        }
        int finishedlistsize = finishedList.size();
        while (finishedList.size() != 0) {
            TileComponent currentFlag = finishedList.removeLast();
            System.out.print(currentFlag.entity.getComponent(TileComponent.class).x + " " + currentFlag.entity.getComponent(TileComponent.class).y + "----->");

            if (currentFlag.predecessor != null) {
                currentFlag.predecessor.setCalculatedConnections(currentFlag.getCalculatedConnections(), currentFlag.predecessorPath);
                System.out.println(" /" + currentFlag.predecessor.entity.getComponent(TileComponent.class).x + " " + currentFlag.predecessor.entity.getComponent(TileComponent.class).y + "\\ ");

            } else {
                connections = calculatedConnections;
                if (house != null) {
                    connections.add(new LengthPair(0, house, null, LengthPairType.BUILDING));
                }
                System.out.println("null");
            }
        }
        reCalculateStock();
        if (house != null) {
            house.reCalculateStock();
        }

        System.out.println("\nFinished with " + entity.getComponent(TileComponent.class).x + " " + entity.getComponent(TileComponent.class).y + "_" + finishedlistsize + "_ " + (connections == null ? "null" : connections.size()));

    }

    private void reCalculateStock() {
        for (Resource resource : inventoryList) {
            if (resource.target != null) {
                if (getPathsectionTo(resource.target) == null && resource.target != house) {
                    resource.setTarget(null);
                }
            }
        }
    }


    private void setCalculatedConnections
            (LinkedList<TileComponent.LengthPair> predecessorCalculatedConnections, PathSection predecessorPath) {
        for (LengthPair lengthPair : predecessorCalculatedConnections) {
            calculatedConnections.add(new LengthPair(lengthPair.distance + 1, lengthPair.component, predecessorPath, lengthPair.type));
        }
    }

    private LinkedList<TileComponent.LengthPair> getCalculatedConnections() {
        if (this.house != null) {
            calculatedConnections.add(new LengthPair(0, house, null, LengthPairType.BUILDING));
        }
        calculatedConnections.add(new LengthPair(0, this, null, LengthPairType.FLAG));
        return calculatedConnections;
    }



    void destroyFlag() {
        if (house!=null){
            house.destroy();
        }
        setFlag(false);
    }

}
