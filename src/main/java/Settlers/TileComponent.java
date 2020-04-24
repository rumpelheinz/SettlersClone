package Settlers;

import Settlers.Houses.*;
import Settlers.Types.Direction;
import Settlers.Types.HouseSize;
import Settlers.Types.HouseType;
import Settlers.Types.TileType;
import Settlers.UI.UIManager;
import Settlers.Workers.WorkerComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;


import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class TileComponent extends InventoryComponent {
    public final TileType type;
    public boolean occupied = false;
    protected int x;
    protected int y;
    TileComponent NE;
    TileComponent NW;
    TileComponent E;
    TileComponent W;
    TileComponent SW;
    TileComponent SE;
    public PathComponent pathPassingThrough = null;
    public boolean flag = false;
    final public static int TILEHEIGHT = 32 * 3;
    final public static int TILEWIDTH = 28 * 3;

    public Direction directionOfNeighbour(TileComponent neighbour) {
        for (Direction dir : Direction.values()) {
            if (getNeighbour(dir) == neighbour) {
                return dir;
            }
        }
        return null;
    }

    public LinkedList<TileComponent> pathHere;
    boolean lookedAt = false;

    public TileComponent(int x, int y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;

        flagTexture = texture("objects/sign.png", TILEWIDTH / 3, TILEHEIGHT / 3);
        flagTexture.setTranslateY(-TILEWIDTH / 3);
        flagTexture.setTranslateX(-TILEHEIGHT / 3 / 2);
        pathComponents = new LinkedList<PathComponent>();
        allFlags.add(this);
        connections = new LinkedList<>();

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
    TileComponent externalthis=this;
    @Override
    public void onAdded() {
        stock = new Group();
        stock.setTranslateX(TILEWIDTH / 2 * 1);
        stock.setTranslateY(TILEHEIGHT / 2 * 0.1);
        entity.getViewComponent().addChild(stock);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("x" + x + "y" + y);
            UIManager.clickPath(this, e.getButton());
            setActive();
        });
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            UIManager.mouseOverTile(this,true);
        });
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            UIManager.mouseOverTile(this,false);
        });
        drawStock();
        UIManager.defaultblendmode = getEntity().getViewComponent().getChildren().get(0).getBlendMode();
//        entity.setUpdateEnabled(false);
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
    LinkedList<PathComponent> pathComponents;
    public HouseComponent house;
    PathComponent predecessorPath;
    LinkedList<LengthPair> calculatedConnections;
    public LinkedList<LengthPair> connections;

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
                PathComponent oldpassiongThrough = pathPassingThrough;
                pathPassingThrough = null;
                if (oldpassiongThrough != null) {
                    oldpassiongThrough.split(this);
                }
            }
        } else {
            if (house != null) {
                house.destroy();
            }
            if (this.flag && connections != null) {
                LinkedList<TileComponent> connected = new LinkedList<>();
                LinkedList<LengthPair> freshConnections = new LinkedList<>();
                for (PathComponent pathComponent : (LinkedList<PathComponent>) pathComponents.clone()) {
                    connected.add(pathComponent.getOtherSide(this));
                    pathComponent.destroyPath(false, this);
                    removePath(pathComponent);
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

    public void addPath(PathComponent section) {
        setFlag(true);
        pathComponents.add(section);
        setActive();
    }

    public void removePath(PathComponent section) {
        pathComponents.remove(section);
        if (pathComponents.size() == 0) {
            setActive();
        }
    }

    public boolean hasResourceComponent() {
        return (entity.hasComponent(RockComponent.class) || entity.hasComponent(TreeComponent.class));
    }

    public WorkerComponent targetedByGather;

    public WorkerComponent targetedByGatherer() {
        return targetedByGather;
    }

    public void signalResource(Resource resource, int maxPriority) {

        int maxDistance = Integer.MAX_VALUE;
        HouseComponent target = null;
        if (connections != null) {
            for (LengthPair location : connections) {
                if (location.type == LengthPair.LengthPairType.BUILDING) {
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
                tex = texture("rock.png", 32, 32);
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
                    getPathsectionTo(resource.target).pathComponent.signalResource(resource, this);
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


    public void setHouse(HouseComponent houseComponent) {
        this.house = houseComponent;
        setFlag(true);
    }

    public void reCalculatePath() {
        clearAllSearches();
        calculatePath();
        if (connections != null)
            for (LengthPair comp : connections) {
                if (comp.component instanceof TileComponent) {
                    clearAllSearches();
                    ((TileComponent) comp.component).calculatePath();
                }
            }
    }

    public void calculatePath() {

        if (pathComponents.size() == 0) {
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
            for (PathComponent currentPath : currentFlag.pathComponents) {
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
                    connections.add(new LengthPair(0, house, null, LengthPair.LengthPairType.BUILDING));
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
            (LinkedList<LengthPair> predecessorCalculatedConnections, PathComponent predecessorPath) {
        for (LengthPair lengthPair : predecessorCalculatedConnections) {
            calculatedConnections.add(new LengthPair(lengthPair.distance + 1, lengthPair.component, predecessorPath, lengthPair.type));
        }
    }

    private LinkedList<LengthPair> getCalculatedConnections() {
        if (this.house != null) {
            calculatedConnections.add(new LengthPair(0, house, null, LengthPair.LengthPairType.BUILDING));
        }
        calculatedConnections.add(new LengthPair(0, this, null, LengthPair.LengthPairType.FLAG));
        return calculatedConnections;
    }

    public boolean canBuildFlag() {
        if (flag || occupied || type == TileType.WATER || entity.hasComponent(TreeComponent.class))
            return false;
        for (Direction dir : Direction.values()) {
            TileComponent comp = getNeighbour(dir);
            if (comp != null && (comp.flag)) {
                return false;
            }
        }
        return true;
    }

    public boolean canBuildHouse(HouseSize size) {
        if (flag || occupied || pathPassingThrough != null || (hasResourceComponent())) {
            return false;
        }
        if (getNeighbour(Direction.SE) == null || ((!getNeighbour(Direction.SE).flag) && (!getNeighbour(Direction.SE).canBuildFlag())))
            return false;
        if (!(getNeighbour(Direction.NE) == null) && getNeighbour(Direction.NE).occupied) {
            return false;
        }
        if (!(getNeighbour(Direction.NW) == null) &&
                getNeighbour(Direction.NW).occupied) {
            return false;
        }
        if (!(getNeighbour(Direction.E) == null) &&
                getNeighbour(Direction.E).occupied) {
            return false;
        }
        if (!(getNeighbour(Direction.W) == null) &&
                getNeighbour(Direction.W).occupied) {
            return false;
        }
        if (!(getNeighbour(Direction.SW) == null) &&
                getNeighbour(Direction.SW).occupied) {
            return false;
        }
        if (size == HouseSize.House || size == HouseSize.CASTLE) {
            for (Direction dir : new Direction[]{Direction.W, Direction.NW, Direction.NE}) {
                TileComponent tile = getNeighbour(dir);
                if (tile == null || tile.flag || tile.occupied || tile.pathPassingThrough != null || (tile.hasResourceComponent()))
                    return false;
            }
        }
//        if (!(getNeighbour(Direction.SE) == null &&
//                getNeighbour(Direction.SE).flag && !
//                getNeighbour(Direction.SE).occupied)) {
//            return false;
//        }
        return true;
    }


    public void buildHouse(HouseType type, boolean instant) {
        System.out.println("Building " + type.toString());
        if (!canBuildHouse(HouseSize.HUT)) {
            return;
        }

        switch (type) {
            case WOODCUTTER:
                new WoodcutterHouseComponent(this, getNeighbour(Direction.SE),false);
                break;
            case STOREHOUSE:
                new StoreHouseComponent(this, getNeighbour(Direction.SE),false);
                break;
            case SAWMILL:
                new SawmillHouseComponent(this, getNeighbour(Direction.SE),false);
                break;
            case FORRESTER:
                new ForresterHouseComponent(this, getNeighbour(Direction.SE),false);
                break;
            case ROCKCUTTER:
                new RockCutterHouseComponent(this, getNeighbour(Direction.SE),false);
                break;
        }
        System.out.println("Built " + type.toString());

    }

}
