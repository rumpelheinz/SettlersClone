package Settlers;

import Settlers.Houses.HouseComponent;
import Settlers.Types.Direction;
import Settlers.Types.TileType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class TileComponent extends Component {
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

        texture = texture("objects/sign.png");
        texture.setTranslateY(-30);
        texture.setTranslateX(-10);
        pathSections = new LinkedList<PathSection>();
        allFlags.add(this);

        int coordx = x * 110;
        int coordy = y * 95;
        if (y % 2 == 1) {
            coordx += 55;
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

    @Override
    public void onAdded() {
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("x" + x + "y" + y);
            clickpath(this);
            setActive();
        });
    }

    static TileComponent firstClicked;
    static TileComponent secondClicked;

    static void clickpath(TileComponent tileComponent) {
        if (firstClicked == null) {
            firstClicked = tileComponent;
            firstClicked.getEntity().getViewComponent().setOpacity(0.5);
        } else {
            secondClicked = tileComponent;
            PathSection.buildPath(firstClicked,secondClicked);

            firstClicked.getEntity().getViewComponent().setOpacity(1);
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

    Texture texture;
    boolean active = false;
    LinkedList<PathSection> pathSections;
    HouseComponent house;
    PathSection predecessorPath;
    LinkedList<TileComponent.LengthPair> calculatedConnections;
    public LinkedList<TileComponent.LengthPair> connections;

    void clearPathSearch() {
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
                entity.getViewComponent().addChild(texture);
            }
        } else {
            if (active) {
                active = false;
                entity.getViewComponent().removeChild(texture);

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
        }
        else{
            this.flag=false;
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

    public void signalResource(Resource resource) {
        int maxPriority=0;
        int maxDistance=Integer.MAX_VALUE;
        HouseComponent target=null;
        for (LengthPair location:connections){
            if (location.type== LengthPairType.BUILDING){
                HouseComponent curTarget= (HouseComponent) location.component;
                if (target==null||curTarget.wantResource(resource)>maxPriority||(location.distance<maxDistance&&curTarget.wantResource(resource)>=maxPriority)){
                    target=curTarget;
                    maxPriority=curTarget.wantResource(resource);
                    maxDistance=maxDistance;
                }
            }
        }
        if (target!=null){
            addResource(resource);
            resource.target=target;
        }
    }
    LinkedList<Resource> resources= new LinkedList<Resource>();
    private void addResource(Resource resource) {
        resources.addFirst(resource);
        getPathsectionTo(resource.target).signalResource(resource,this);
    }
    public PathSection getPathsectionTo(Component target){
      for  (LengthPair pair:connections){
          if (pair.component==target){
              return pair.pathSection;
          }
      }
      return null;
    };

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

    public void calculatePath() {
        if (pathSections.size()==0){
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
                System.out.println("null");
            }
        }

        System.out.println("\nFinished with " + entity.getComponent(TileComponent.class).x + " " + entity.getComponent(TileComponent.class).y + "_" + finishedlistsize + "_ " + (connections == null ? "null" : connections.size()));

    }


    private void setCalculatedConnections(LinkedList<TileComponent.LengthPair> predecessorCalculatedConnections, PathSection predecessorPath) {
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

}
