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
    float speed = 1f / 60.0f;
    protected TaskType currentTaskType = TaskType.IDLE;
    private Point2D currentTile;
    private TileComponent a;
    private TileComponent b;
    LinkedList <Point2D>locations= new LinkedList<Point2D>();
    int currentinex;
    private int targetindex;

    public CarrierComponent() {

    }

    @Override
    public void onAdded() {
        Texture texture= texture("pirates/006-monkey.png",32,32);
        texture.setTranslateX(-32/2);
        texture.setTranslateY(-32/2);
        texture.setScaleX(2);
        texture.setScaleY(2);
        entity.setZ(10);
        entity.getViewComponent().addChild(texture);
    }



    void move(double tpf) {
        if (targetindex==currentinex){
            return;
        }
        if (targetindex > -1 ) {
            int curTargetIndex = targetindex>currentinex? currentinex+1: currentinex-1;
            Point2D currTargetLoc = locations.get(curTargetIndex);
       //     System.out.println("from " +currentinex+ " " +currentTile+"  -> "+ targetindex +" "+ locations.getLast()+" via  "+curTargetIndex+" " +currTargetLoc );

            Point2D aLoc = locations.get(currentinex);
            Point2D bLoc = currTargetLoc;
            Vec2 dir = new Vec2(bLoc.getX() - aLoc.getX(), bLoc.getY() - aLoc.getY());
            // System.out.println(dir.toString());
            dir = dir.mul(speed);
            if (dir.length() >= new Vec2(currentTile.subtract(bLoc)).length()) {
                currentTile=currTargetLoc;
                entity.setPosition(currTargetLoc);
                currentinex=curTargetIndex;
            } else {
                //  System.out.println(dir.toString());

                currentTile=dir.add(currentTile).toPoint2D();
            //    entity.setPosition(currentTile);

            }
        }
    }

    @Override
    public void onUpdate(double tpf) {
        //   System.out.println(" " + currentTask + "X " + entity.getX() + " Y " + entity.getY());

        switch (currentTaskType) {


            case GATHERING:
                move(tpf);
                if (targetindex==currentinex) {
                    currentTaskType = TaskType.IDLE;
                }

                break;
            case IDLE:
                if (currentTile.equals(locations.getLast())){
                    targetindex = 0;
                }
                else {
                    targetindex=locations.size()-1;
                }
            //    path = locations;
                currentTaskType=TaskType.GATHERING;


//                if (findPath(findTreeQuery)) {
//                    currentTaskType = TaskType.GATHERING;
//                }
                break;
            case RETURNING:
//                move(tpf);
//                if (currentTile == home) {
//                    hasTree = false;
//                    currentTaskType = TaskType.IDLE;
//                }
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
    Point2D middle;
    public void setPathSection(PathSection pathSection) {
        this.pathSection=pathSection;
        for (TileComponent tile:pathSection.currentTileList){
            locations.add(new Point2D(tile.getEntity().getX(),tile.getEntity().getY()));
        }
        a=pathSection.a;
        b=pathSection.b;
        middle=locations.get(locations.size() / 2);
        currentinex= locations.size()/2;
        currentTile = middle;

    }
}
