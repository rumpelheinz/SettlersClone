package Settlers;

import Settlers.Types.TaskType;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class CarrierComponent extends Component {

    public PathSection pathSection;
    float speed = 1f / 60.0f;  //Move 1 Tile per second
    protected TaskType currentTaskType = TaskType.IDLE;
    private Point2D currentPosition2D;
    private TileComponent aTile;
    private TileComponent bTile;
    Point2D[] pathPoints2D;
    int currentIndex;
    private int targetIndex;

    void move(double tpf) {  //called once per onUpdate
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
            if (dir.length() >= new Vec2(currentPosition2D.subtract(bLoc)).length()) {   // update next location when the next point is reached
                currentPosition2D = currTargetLoc;
                entity.setPosition(currTargetLoc);
//                texture.setTranslateX(currentPosition2D.getX());
//                texture.setTranslateY(currentPosition2D.getY());
                currentIndex = curTargetIndex;
            } else {
                currentPosition2D = dir.add(currentPosition2D).toPoint2D();
//                texture.setTranslateX(currentPosition2D.getX());
//                texture.setTranslateY(currentPosition2D.getY());
                entity.setPosition(currentPosition2D); //Uncommenting this line increases CPUusage significantly
            }
        }
    }


    Texture texture;

    @Override
    public void onAdded() {
        texture = texture("pirates/006-monkey.png", 32, 32);
        texture.setTranslateX(-32 / 2);
        texture.setTranslateY(-32 / 2);
        texture.setScaleX(2);
        texture.setScaleY(2);
        entity.setZ(10);
        entity.getViewComponent().addChild(texture);
    }

    //more code


    @Override
    public void onUpdate(double tpf) {
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
                move(tpf);


            case GATHERING:
                move(tpf);
                if (targetIndex == currentIndex) {
                    currentTaskType = TaskType.IDLE;
                }

                break;
            case IDLE:

                currentTaskType = TaskType.MOVINGUPANDDOWN;
                break;
            case RETURNING:
                move(tpf);
                if (targetIndex == currentIndex) {
                    currentTaskType = TaskType.IDLE;
                }
                break;

        }
//        super.onUpdate(tpf);
    }

    Point2D middle;

    public void setPathSection(PathSection pathSection) {
        this.pathSection = pathSection;
        LinkedList<Point2D> tempLocations = new LinkedList<Point2D>();
        pathPoints2D = new Point2D[pathSection.currentTileList.size()];
        int i = 0;
        for (TileComponent tile : pathSection.currentTileList) {
            pathPoints2D[i] = tile.getEntity().getPosition();
            i++;
        }
        aTile = pathSection.a;
        bTile = pathSection.b;
        middle = pathPoints2D[pathPoints2D.length / 2];
        currentIndex = pathPoints2D.length / 2;
        currentPosition2D = middle;

    }

    //TODO: Implement resource gathering functionality
    boolean hasResource = false;
    Resource resource = null;
    TileComponent dropOffTile = null;

    private void setTargetResource(Resource newResource, TileComponent fromTile) {
        if (fromTile == aTile) {
            targetIndex = 0;
            resource = newResource;
            dropOffTile = aTile;
        } else {
            targetIndex = pathPoints2D.length - 1;
            resource = newResource;
            dropOffTile = bTile;
        }
    }

    public boolean signalResource(Resource newResource, TileComponent fromTile) {
        if (resource == null) {
            setTargetResource(newResource, fromTile);
            return true;
        }
        return false;
    }
}
