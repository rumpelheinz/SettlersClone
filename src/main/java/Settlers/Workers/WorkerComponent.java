package Settlers.Workers;

import Settlers.*;
import Settlers.Houses.HouseComponent;
import Settlers.Houses.StoreHouseComponent;
import Settlers.Types.ResourceType;
import Settlers.Types.TaskType;
import Settlers.Types.TileType;
import Settlers.Types.WorkerType;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.LinkedList;
import java.util.List;

import static Settlers.BasicGameApp.stepsBetweenRedraw;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public abstract class WorkerComponent extends Component {
    public TileComponent homeTile;
    TileComponent targetTile;
    TileComponent currentTile;
    private LinkedList<TileComponent> path;
    float speed = 1f / 60.0f;
    boolean hasGatheredResource = false;
    protected TaskType currentTaskType = TaskType.IDLE;
    public HouseComponent house;
    private int timeSinceLastRedraw = 0;
    boolean inside;
    Point2D currentPosition2D;
    Texture texture;
    private int targetPathIndex;
    private int currentPathIndex;
    private Point2D[] pathPoints2D;


    abstract String getTextureString();

    public abstract WorkerType getWorkerType();

    @Override
    public void onAdded() {
        initResourceTexture();
        texture = texture(getTextureString(), 32, 32);
        texture.setTranslateX(-32 / 2);
        texture.setTranslateY(-32 / 2);
        texture.setScaleX(2);
        texture.setScaleY(2);
        entity.setZ(10);
        entity.getViewComponent().addChild(texture);
        currentPosition2D = entity.getPosition();
        inside = true;
        texture.setVisible(false);
    }


    public void spawnAtWareHouse(StoreHouseComponent storeHouse) {
        setCurrentTile(storeHouse.flagTile);
        house.setWorker(this);
        texture.setVisible(true);
        currentTaskType = TaskType.MOVINGTOBUILDING;
        setNextPath();
    }

    PathComponent currentPathSection;

    void setNextPath() {
        if (currentTile.flag) {
            LengthPair tmpPath = currentTile.getPathsectionTo(house);
            if (tmpPath != null) {
                currentPathSection = tmpPath.pathComponent;
                setPathComponent(currentPathSection);
            }
        }
    }

    public void setPathComponent(PathComponent pathComponent) {
        int i = 0;
        List<TileComponent> tmpTileList = pathComponent.currentTileList;
        pathPoints2D = new Point2D[tmpTileList.size()];

        if (currentTile == pathComponent.a) {
            currentPathIndex = 0;
            targetPathIndex = pathPoints2D.length - 1;
        } else {
            currentPathIndex = pathComponent.currentTileList.size() - 1;
            targetPathIndex = 0;
        }
        for (TileComponent tile : tmpTileList) {
            if (i == targetPathIndex)
                targetPathTile = tile;
            pathPoints2D[i] = tile.getEntity().getPosition();
            i++;
        }

    }

    TileComponent targetPathTile;

    void moveAlongPath(double tpf) {
        if (targetPathIndex == currentPathIndex) {
            return;
        }
        if (targetPathIndex > -1) {
            int curTargetIndex = targetPathIndex > currentPathIndex ? currentPathIndex + 1 : currentPathIndex - 1;
            Point2D currTargetLoc = pathPoints2D[(curTargetIndex)];

            Point2D aLoc = pathPoints2D[(currentPathIndex)];
            Point2D bLoc = currTargetLoc;
            Vec2 dir = new Vec2(bLoc.getX() - aLoc.getX(), bLoc.getY() - aLoc.getY());
            dir = dir.mul(speed);                        //
            if ((dir.length() + 0.1 >= new Vec2(currentPosition2D.subtract(bLoc)).length())) {   // update next location when the next point is reached
                currentPosition2D = currTargetLoc;
                entity.setPosition(currTargetLoc);
                currentPathIndex = curTargetIndex;
            } else {
                currentPosition2D = dir.add(currentPosition2D).toPoint2D();
                timeSinceLastRedraw++;
                if (timeSinceLastRedraw > stepsBetweenRedraw) {
                    timeSinceLastRedraw = 0;
                    entity.setPosition(currentPosition2D);
                }
            }
        }
    }

    void move(double tpf) {
        if (path != null && path.size() > 0) {
            TileComponent curtarget = path.getFirst();
            if (curtarget == currentTile) return;
            Point2D a = currentTile.getEntity().getPosition();
            Point2D b = curtarget.getEntity().getPosition();
            Vec2 dir = new Vec2(b.getX() - a.getX(), b.getY() - a.getY());
            // System.out.println(dir.toString());
            dir = dir.mul(speed);
            if (dir.length() >= new Vec2(currentPosition2D.subtract(b)).length()) {
                currentPosition2D = curtarget.getEntity().getPosition();
                entity.setPosition(currentPosition2D);
                currentTile = curtarget;
                path.remove(0);
            } else {
                //  System.out.println(dir.toString());
                currentPosition2D = dir.add(currentPosition2D).toPoint2D();
                timeSinceLastRedraw++;
                if (timeSinceLastRedraw > stepsBetweenRedraw) {
                    timeSinceLastRedraw = 0;
                    entity.setPosition(currentPosition2D); //Uncommenting this line increases CPUusage significantly
                }
            }
        }
    }


    public abstract boolean isValidTargetTile(TileComponent compareTile);

    SearchQuery findHomeQuery = new SearchQuery() {
        @Override
        public boolean isValidTarget(TileComponent compareTile) {
            return compareTile.equals(homeTile);
        }

        @Override
        public boolean canGoThrough(TileComponent compareTile) {
            return (compareTile.type != TileType.WATER);
        }

        @Override
        public boolean canStartAt(TileComponent startTile) {
            return true;
        }


    };

    abstract void initResourceTexture();

    Texture resourceTexture;

    public void setCurrentTile(TileComponent currentTile) {
        this.currentTile = currentTile;
        entity.setX(currentTile.getEntity().getX());
        entity.setY(currentTile.getEntity().getY());
        currentPosition2D = entity.getPosition();
    }

    abstract boolean attemptGatherResource();

    private int searchRadius = 10;
    WorkerComponent worker = this;
    SearchQuery findResourceQuery = new SearchQuery() {

        @Override
        public boolean isValidTarget(TileComponent compareTile) {
            boolean ret = isValidTargetTile(compareTile) && (compareTile.targetedByGatherer() == null || (compareTile.targetedByGatherer() == worker));
            return ret;
        }

        @Override
        public boolean canGoThrough(TileComponent compareTile) {
            return (compareTile.type != TileType.WATER && compareTile.pathHere.size() < searchRadius);

        }

        @Override
        public boolean canStartAt(TileComponent startTile) {
            return true;
        }
    };

    boolean findPath(SearchQuery searchQuery) {
        SearchResult result = Map.findPath(searchQuery, currentTile, false);
        if (targetTile != null)
            if (targetTile.targetedByGather == this)
                targetTile.targetedByGather = null;
        if (result.success) {
            path = result.path;
            targetTile = result.target;
            targetTile.targetedByGather = this;
            return true;
        } else {
            return false;
        }
//
    }


    @Override
    public void onUpdate(double tpf) {

        //   System.out.println(" " + currentTask + "X " + entity.getX() + " Y " + entity.getY());
        if (!waiting()) {
            switch (currentTaskType) {
                case MOVINGTOBUILDING: {
                    moveAlongPath(tpf);
                    if (currentPathIndex == targetPathIndex) {
                        currentTile = targetPathTile;
                    }
                    if (currentTile == house.flagTile) {
                        setCurrentTile(homeTile);
                        currentTaskType = TaskType.GOINGINSIDE;
                    } else {
                        if (currentPathIndex == targetPathIndex) {
                            setNextPath();
                        }
                    }
                    break;
                }
                case GATHERING:
                    move(tpf);
                    if (targetTile == currentTile) {

                        if (attemptGatherResource()) {
                            hasGatheredResource = true;
                            if (resourceTexture != null)
                                entity.getViewComponent().addChild(resourceTexture);
                        }
                        startWaiting(2000);
                        if (findPath(findHomeQuery)) {
                            currentTaskType = TaskType.RETURNING;
                        }

                    }

                    break;
                case IDLE:
                    if (getWorkerType() == WorkerType.GATHERER) {
                        if (findPath(findResourceQuery)) {
                            if (inside) {
                                texture.setVisible(true);
                            }
                            inside = false;
                            currentTaskType = TaskType.GATHERING;
                        } else {
                            startWaiting(5000);
                        }
                    }
                    if (getWorkerType()==WorkerType.BUILDER){
                        texture.setVisible(true);
                        inside=false;
                        currentTaskType=TaskType.BUILDING;
                    }
                    break;
                case RETURNING:
                    move(tpf);
                    if (currentTile == homeTile) {
                        if (house.destroyed) {
                            entity.removeFromWorld();
                        }
                        currentTaskType = TaskType.GOINGINSIDE;
                        startWaiting(1000);
                    }
                    break;

                case GOINGINSIDE:
                    if (house.destroyed) {
                        entity.removeFromWorld();
                    } else {
                        if (hasGatheredResource && createsResource() != null) {
                            house.addResource(new Resource(createsResource()));
                            entity.getViewComponent().removeChild(resourceTexture);
                        }
                        hasGatheredResource = false;
                        texture.setVisible(false);
                        currentTaskType = TaskType.IDLE;
                        inside = true;
                        texture.setVisible(false);
                        startWaiting(5000);
                    }

                    break;
                case BUILDING:
                    if (house.finished){
                        entity.removeFromWorld();
                    }
                    if (house.build()){

                    }
                    startWaiting(5000);
                    break;
            }
        }

    }

    protected abstract ResourceType createsResource();

    long waitUntil = System.currentTimeMillis();

    boolean waiting() {
        return System.currentTimeMillis() < waitUntil;
    }


    void startWaiting(int duration) {
        waitUntil = System.currentTimeMillis() + duration;
    }


}
