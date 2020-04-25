package Settlers;

import Settlers.Houses.HouseComponent;
import Settlers.Types.TaskType;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.LinkedList;

import static Settlers.BasicGameApp.stepsBetweenRedraw;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class CarrierComponent extends Component {

    public PathComponent pathComponent;
    protected TaskType currentTaskType = TaskType.IDLE;
    private Point2D currentPosition2D;
    private TileComponent aTile;
    private TileComponent bTile;
    Point2D[] pathPoints2D;
    int currentIndex;
    private int targetIndex;
    private int timeSinceLastRedraw;
    double speed = 1 / 60.0;  //Move 1 Tile per second

    void move(double tpf) {
        if (targetIndex == currentIndex) {
            return;
        }
        if (targetIndex > -1) {
            int curTargetIndex = targetIndex > currentIndex ? currentIndex + 1 : currentIndex - 1;
            Point2D currTargetLoc = pathPoints2D[(curTargetIndex)];

            Point2D aLoc = pathPoints2D[(currentIndex)];
            Point2D bLoc = currTargetLoc;
            Vec2 dir = new Vec2(bLoc.getX() - aLoc.getX(), bLoc.getY() - aLoc.getY());
            dir = dir.mul(speed);                        //
            if ((dir.length() + 0.1 >= new Vec2(currentPosition2D.subtract(bLoc)).length())) {   // update next location when the next point is reached
                currentPosition2D = currTargetLoc;
                entity.setPosition(currTargetLoc);
                currentIndex = curTargetIndex;
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

    long waitUntil = System.currentTimeMillis();

    static Texture texture;
    Texture log = texture("log.png", 64, 64);
    Texture plank = texture("plank.png", 64, 64);
    Texture stone = texture("rock.png", 64, 64);
    ;

    private void startWaiting(int duration) {
        waitUntil = System.currentTimeMillis() + duration;
    }

    ;

    boolean waiting() {
        return System.currentTimeMillis() < waitUntil;
    }


    @Override
    public void onAdded() {
        texture = texture("pirates/006-monkey.png", 48, 48);
        texture.setTranslateX(-48 / 2);
        texture.setTranslateY(-48 / 2);
        entity.setZ(10);
        entity.getViewComponent().addChild(texture);
    }

    private Resource checkForResources(TileComponent tileComponent) {
//        tileComponent.getResourcesWithTargets();
        for (Resource resource : tileComponent.inventoryList) {
            LengthPair sourceLengthPair = tileComponent.getPathsectionTo(resource.target);
            if (resource.target != null && sourceLengthPair != null && resource.reservedByWorker == null) {
                TileComponent compareTile = pathComponent.getOtherSide(tileComponent);
                LengthPair compareLengthPair = compareTile.getPathsectionTo(resource.target);
                if (compareLengthPair != null) {
                    if (compareLengthPair.distance < sourceLengthPair.distance) {
                        return resource;
                    }
                } else if (compareTile.house == resource.target) {
                    return resource;
                }
            }
        }
        for (Resource resource : tileComponent.inventoryList) {
            if (resource.reservedByWorker == null)
                if (resource.target == null) {
                    tileComponent.signalResource(resource, 0);
                }
            if (resource.target != null) {
                LengthPair sourceLengthPair = tileComponent.getPathsectionTo(resource.target);
                if (resource.target != null && sourceLengthPair != null && resource.reservedByWorker == null) {
                    TileComponent compareTile = pathComponent.getOtherSide(tileComponent);
                    LengthPair compareLengthPair = compareTile.getPathsectionTo(resource.target);
                    if (compareLengthPair != null) {
                        if (compareLengthPair.distance < sourceLengthPair.distance) {
                            return resource;
                        }
                    } else if (compareTile.house == resource.target) {
                        return resource;
                    }
                }
            }

        }
        HouseComponent house = tileComponent.house;
        if (house != null)
            for (Resource resource : tileComponent.house.inventoryList) {
                if (resource.reservedByWorker == null)
                    if (resource.target == null) {
                        tileComponent.signalResource(resource, house.wantResource(resource));
                    }
                if (resource.target != null) {
                    LengthPair sourceLengthPair = tileComponent.getPathsectionTo(resource.target);
                    if (resource.target != null && sourceLengthPair != null && resource.reservedByWorker == null) {
                        TileComponent compareTile = pathComponent.getOtherSide(tileComponent);
                        LengthPair compareLengthPair = compareTile.getPathsectionTo(resource.target);
                        if (compareLengthPair != null) {
                            if (compareLengthPair.distance < sourceLengthPair.distance) {
                                return resource;
                            }
                        } else if (compareTile.house == resource.target) {
                            return resource;
                        }
                    }
                }

            }
        return null;
    }
    //more code

    @Override
    public void onUpdate(double tpf) {
        if (getEntity().getX() < 0 || getEntity().getY() < 0) {
            System.out.println("Monkey escaped!!!!!");
        }
        move(tpf);
        if (!waiting()) {
            switch (currentTaskType) {

                case MOVINGUPANDDOWN:
                    if (targetIndex == currentIndex) {
                        if (currentPosition2D.equals(pathPoints2D[pathPoints2D.length - 1])) {
                            targetIndex = 0;
                        } else {
                            targetIndex = pathPoints2D.length - 1;
                        }
                        currentTaskType = TaskType.MOVINGUPANDDOWN;
                    }

                    break;
                case GATHERING:

                    if (targetIndex == currentIndex) {
                        if (targetIndex == pathPoints2D.length - 1) {
                            if (bTile.pickUp(resource)) {

                                setResourceTexture(resource, true);
                                hasResource = true;
                                currentTaskType = TaskType.RETURNING;
                                targetIndex = 0;
                            } else {
                                clearTargetResource();
                                System.out.println("cleared target");
                            }
                        } else {
                            if (targetIndex == 0) {
                                if (aTile.pickUp(resource)) {
                                    setResourceTexture(resource, true);
                                    hasResource = true;
                                    currentTaskType = TaskType.RETURNING;
                                    targetIndex = pathPoints2D.length - 1;

                                } else {
                                    clearTargetResource();
                                    System.out.println("cleared target");
                                }
                            }
                        }
                    }


                    break;
                case IDLE: {


                    Resource targetResource = checkForResources(aTile);
                    if (targetResource != null) {
                        setTargetResource(targetResource, aTile);
                    } else {
                        targetResource = checkForResources(bTile);
                        if (targetResource != null)
                            setTargetResource(targetResource, bTile);
                    }
                    if (resource == null) {
                        targetIndex = pathPoints2D.length / 2;
                        startWaiting(1000);
                    }


//                currentTaskType = TaskType.MOVINGUPANDDOWN;
                    break;
                }
                case RETURNING: {

                    if (targetIndex == currentIndex) {
                        dropOffTile.addResource(resource);
                        getEntity().getViewComponent().removeChild(log);
                        getEntity().getViewComponent().removeChild(stone);
                        getEntity().getViewComponent().removeChild(plank);
                        setResourceTexture(resource, false);
//                        getEntity().getViewComponent().removeChild(resourceTexture);
                        currentTaskType = TaskType.IDLE;
                        hasResource = false;
                        resource = null;
                        dropOffTile = null;

                        TileComponent currentATile;
                        TileComponent currentBTile;
                        if (currentIndex == 0) {
                            currentATile = aTile;
                            currentBTile = bTile;
                        } else {
                            currentBTile = aTile;
                            currentATile = bTile;
                        }
                        Resource targetResource = checkForResources(currentATile);
                        if (targetResource != null) {
                            setTargetResource(targetResource, currentATile);
                        } else {
                            targetResource = checkForResources(currentBTile);
                            if (targetResource != null)
                                setTargetResource(targetResource, currentBTile);
                        }
                        if (resource == null) {
                            targetIndex = pathPoints2D.length / 2;
                        }

                    }
                    break;
                }
            }
        }
//        super.onUpdate(tpf);
    }

    public void clearTargetResource() {
        System.out.println("cleartargetresource");
        currentPosition2D = pathPoints2D[currentIndex];
        entity.getViewComponent().getChildren().remove(log);
        entity.getViewComponent().getChildren().remove(stone);
        entity.getViewComponent().getChildren().remove(plank);


        hasResource = false;
        resource = null;
        dropOffTile = null;
        targetIndex = pathPoints2D.length / 2;
        currentTaskType = TaskType.IDLE;
    }

    Point2D middle;

    public void setPathComponent(PathComponent pathComponent) {
        this.pathComponent = pathComponent;
        LinkedList<Point2D> tempLocations = new LinkedList<Point2D>();
        pathPoints2D = new Point2D[pathComponent.currentTileList.size()];
        int i = 0;
        for (TileComponent tile : pathComponent.currentTileList) {
            pathPoints2D[i] = tile.getEntity().getPosition();
            i++;
        }
        aTile = pathComponent.a;
        bTile = pathComponent.b;
        middle = pathPoints2D[pathPoints2D.length / 2];
        currentIndex = pathPoints2D.length / 2;
        currentPosition2D = middle;

    }

    boolean hasResource = false;
    Resource resource = null;
    TileComponent dropOffTile = null;

    private void setTargetResource(Resource newResource, TileComponent fromTile) {
        if (fromTile == aTile) {
            targetIndex = 0;
            resource = newResource;
            dropOffTile = bTile;
        } else {
            targetIndex = pathPoints2D.length - 1;
            resource = newResource;
            dropOffTile = aTile;
        }
        newResource.reservedByWorker = this;
        currentTaskType = TaskType.GATHERING;
    }

    public void signalResource(Resource newResource, TileComponent fromTile) {
//        if (currentTaskType==TaskType.IDLE)
//            startWaiting(0);

    }

    Texture resourceTexture;

    private void setResourceTexture(Resource resource, boolean visible) {
        getEntity().getViewComponent().removeChild(log);
        getEntity().getViewComponent().removeChild(plank);
        getEntity().getViewComponent().removeChild(stone);
        resourceTexture = null;
        switch (resource.type) {
            case LOG:
                resourceTexture = log;
                break;
            case PLANK:
                resourceTexture = plank;
                break;
            case STONE:
                resourceTexture = stone;
                break;
        }
        if (visible) {
            this.getEntity().getViewComponent().addChild(resourceTexture);
        } else {
            this.getEntity().getViewComponent().removeChild(resourceTexture);
        }
    }

    public Resource remove() {
        boolean hadResource = hasResource;
        Resource oldResource = resource;
        if (resource != null) {
            resource.setTarget(null);
        }
        getEntity().removeFromWorld();
        if (hadResource)
            return oldResource;
        else return null;
    }
}
