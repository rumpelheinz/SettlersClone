package Settlers;

import Settlers.Types.TaskType;
import Settlers.Types.TileType;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class WorkerComponent extends Component {

    private TileComponent target;
    private TileComponent currentTile;
    private LinkedList<TileComponent> path;
    float speed = 1f / 60.0f;
    private boolean hasTree;
    protected TaskType currentTaskType = TaskType.IDLE;
    public TileComponent home;

    public WorkerComponent() {

    }

    @Override
    public void onAdded() {
        Texture texture = texture("pirates/024-crab.png");
//        Circle body = new Circle(10);
//        body.setStroke(Color.RED);
//        body.setStrokeWidth(5);
        texture.setTranslateX(-32 / 2);
        texture.setTranslateY(-32 / 2);
        texture.setScaleX(2);
        texture.setScaleY(2);
        entity.setZ(10);
        entity.getViewComponent().addChild(texture);
        currentPosition=entity.getPosition();
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
        currentPosition=entity.getPosition();
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
            if (dir.length() >= new Vec2(entity.getPosition().subtract(b)).length()) {
                currentPosition = curtarget.getEntity().getPosition();
                entity.setPosition(currentPosition);
                currentTile = curtarget;
                path.remove(0);
            } else {
                //  System.out.println(dir.toString());
                currentPosition=dir.add(currentPosition).toPoint2D();
//                entity.setPosition(dir.add(entity.getPosition()));
            }
        }
    }


    private SearchQuery findHomeQuery = new SearchQuery() {
        @Override
        public boolean isValidTarget(TileComponent compareTile) {
            return compareTile.equals(home);
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
    private SearchQuery findTreeQuery = new SearchQuery() {
        @Override
        public boolean isValidTarget(TileComponent compareTile) {
            TreeComponent treeComponent = compareTile.getEntity().hasComponent(TreeComponent.class) ? compareTile.getEntity().getComponent(TreeComponent.class) : null;
            return treeComponent != null && treeComponent.trees > 0;
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


    @Override
    public void onUpdate(double tpf) {
        //   System.out.println(" " + currentTask + "X " + entity.getX() + " Y " + entity.getY());

        switch (currentTaskType) {


            case GATHERING:
                move(tpf);
                if (target == currentTile) {
                    currentTile.getEntity().getComponent(TreeComponent.class);
                    TreeComponent tree = currentTile.getEntity().getComponent(TreeComponent.class);
                    if (tree != null && tree.trees > 0 && !hasTree) {
                        tree.setTrees(tree.trees - 1);
                        hasTree = true;
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
                    currentTaskType = TaskType.GATHERING;
                }
                break;
            case RETURNING:
                move(tpf);
                if (currentTile == home) {
                    hasTree = false;
                    currentTaskType = TaskType.IDLE;
                }
                break;
        }
//        if (path != null && path.size() > 0) {
//            TileComponent curtarget = path.getFirst();
//            Point2D a = currentTile.getEntity().getPosition();
//            Point2D b = curtarget.getEntity().getPosition();
//            Vec2 dir = new Vec2(b.getX() - a.getX(), b.getY() - a.getY());
//            System.out.println(dir.toString());
//            dir = dir.mul(speed);
//            if (dir.length() > new Vec2(entity.getPosition().subtract(b)).length()) {
//                entity.setPosition(curtarget.getEntity().getPosition());
//                currentTile = curtarget;
//                path.remove(0);
//            } else {
//                System.out.println(dir.toString());
//                entity.setPosition(dir.add(entity.getPosition()));
//            }
//
//        }

        //    System.out.println(tpf);
        super.onUpdate(tpf);
    }

    private boolean findPath(SearchQuery searchQuery) {
        SearchResult result = Map.findPath(searchQuery, currentTile);
        if (result.success) {
            path = result.path;
            target = result.target;
            return true;
        } else {
            return false;
        }
    }

}
