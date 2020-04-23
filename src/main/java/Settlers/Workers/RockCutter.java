package Settlers.Workers;

import Settlers.Houses.RockCutterHouseComponent;
import Settlers.Houses.WoodcutterHouseComponent;
import Settlers.*;
import Settlers.Types.ResourceType;
import Settlers.Types.TaskType;
import Settlers.Types.TileType;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.LinkedList;

import static Settlers.BasicGameApp.stepsBetweenRedraw;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class RockCutter extends Component {

    private TileComponent target;
    private TileComponent currentTile;
    private LinkedList<TileComponent> path;
    float speed = 1f / 60.0f;
    private boolean hasTree = false;
    protected TaskType currentTaskType = TaskType.IDLE;
    public TileComponent homeTile;
    public RockCutterHouseComponent house;
    private int timesincelastredraw = 0;
    private boolean inside;

    public RockCutter() {

    }

    Texture texture;

    @Override
    public void onAdded() {
        texture = texture("pirates/029-seagull.png", 32, 32);
//        Circle body = new Circle(10);
//        body.setStroke(Color.RED);
//        body.setStrokeWidth(5);
        texture.setTranslateX(-32 / 2);
        texture.setTranslateY(-32 / 2);
        texture.setScaleX(2);
        texture.setScaleY(2);
        entity.setZ(10);
        entity.getViewComponent().addChild(texture);
        currentPosition = entity.getPosition();
        inside = true;
        texture.setVisible(false);
    }


//    public void setTarget(TileComponent target) {
////     //   this.target = target;
////        currentTask=Task.GATHERING;
//
//    }

    public void setCurrentTile(TileComponent currentTile) {
        this.currentTile = currentTile;
        entity.setX(currentTile.getEntity().getX());
        entity.setY(currentTile.getEntity().getY());
        currentPosition = entity.getPosition();
    }

    //    private void pickUp() {
//        TreeComponent tree = currentTile.getEntity().getComponent(TreeComponent.class);
//        if (tree != null && tree.trees > 0) {
//            tree.setTrees(tree.trees - 1);
//            hasTree = true;
//            currentTask = Task.RETURNING;
//        }
//    }
    Point2D currentPosition;

    void move(double tpf) {
        if (path != null && path.size() > 0) {
            TileComponent curtarget = path.getFirst();
            if (curtarget == currentTile) return;
            Point2D a = currentTile.getEntity().getPosition();
            Point2D b = curtarget.getEntity().getPosition();
            Vec2 dir = new Vec2(b.getX() - a.getX(), b.getY() - a.getY());
            // System.out.println(dir.toString());
            dir = dir.mul(speed);
            if (dir.length() >= new Vec2(currentPosition.subtract(b)).length()) {
                currentPosition = curtarget.getEntity().getPosition();
                entity.setPosition(currentPosition);
                currentTile = curtarget;
                path.remove(0);
            } else {
                //  System.out.println(dir.toString());
                currentPosition = dir.add(currentPosition).toPoint2D();
                timesincelastredraw++;
                if (timesincelastredraw > stepsBetweenRedraw) {
                    timesincelastredraw = 0;
                    entity.setPosition(currentPosition); //Uncommenting this line increases CPUusage significantly
                }
            }
        }
    }

    private SearchQuery findHomeQuery = new SearchQuery() {
        @Override
        public boolean isValidTarget(TileComponent compareTile) {
            return compareTile.equals(homeTile);
        }

        @Override
        public boolean canGoThrough(TileComponent compareTile) {
            return (compareTile.type != TileType.WATER)&& !compareTile.occupied;
        }

        @Override
        public boolean canStartAt(TileComponent startTile) {
            return true;
        }


    };

    private SearchQuery findTreeQuery = new SearchQuery() {
        @Override
        public boolean isValidTarget(TileComponent compareTile) {
            return compareTile.getEntity().hasComponent(RockComponent.class);
        }

        @Override
        public boolean canGoThrough(TileComponent compareTile) {
            return (compareTile.type != TileType.WATER && compareTile.pathHere.size() < 10);

        }

        @Override
        public boolean canStartAt(TileComponent startTile) {
            return true;
        }
    };
    Texture log = texture("rock.png", 64, 64);

    @Override
    public void onUpdate(double tpf) {
        //   System.out.println(" " + currentTask + "X " + entity.getX() + " Y " + entity.getY());
        if (!waiting()) {
            switch (currentTaskType) {
                case GATHERING:
                    move(tpf);
                    if (target == currentTile) {
                        RockComponent tree=null;
                        if (currentTile.getEntity().hasComponent(RockComponent.class))
                            tree = currentTile.getEntity().getComponent(RockComponent.class);
                        if (tree != null && !hasTree) {
                            hasTree = true;
                            entity.getViewComponent().addChild(log);
                            tree.remove();
                            startWaiting(2000);
                        } else {
                            if (findPath(findTreeQuery)) {
                                currentTaskType = TaskType.GATHERING;
                            } else {
                                if (findPath(findHomeQuery)) {
                                    currentTaskType = TaskType.RETURNING;
                                }
                            }
                        }
                    }
                    if (hasTree) {
                        if (findPath(findHomeQuery)) {
                            currentTaskType = TaskType.RETURNING;
                        }
                    }
                    break;
                case IDLE:
                    if (findPath(findTreeQuery)) {
                        if (inside) {
                            texture.setVisible(true);
                        }
                        inside = false;
                        currentTaskType = TaskType.GATHERING;
                    } else {
                        startWaiting(5000);
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
                        if (hasTree) {
                            house.addResource(new Resource(ResourceType.STONE));
                            entity.getViewComponent().removeChild(log);
                        }
                        hasTree = false;
                        texture.setVisible(false);
                        currentTaskType = TaskType.IDLE;
                        inside = true;
                        texture.setVisible(false);
                        startWaiting(5000);
                    }

                    break;
            }
        }
    }

    long waitUntil = System.currentTimeMillis();

    private void startWaiting(int duration) {
        waitUntil = System.currentTimeMillis() + duration;
    }

    ;

    boolean waiting() {
        return System.currentTimeMillis() < waitUntil;
    }

    private boolean findPath(SearchQuery searchQuery) {
        SearchResult result = Map.findPath(searchQuery, currentTile, false);
        if (result.success) {
            path = result.path;
            target = result.target;
            return true;
        } else {
            return false;
        }
//return false;
    }

}
