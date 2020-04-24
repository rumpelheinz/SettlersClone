package Settlers;

import Settlers.Types.Direction;
import Settlers.Types.TileType;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class PathComponent extends Component {
    public TileComponent a;
    TileComponent b;
    public List<TileComponent> currentTileList;
    CarrierComponent carrier;

    public PathComponent(LinkedList<TileComponent> tileList) {
        currentTileList = new ArrayList<>();
        Color color = Color.rgb(BasicGameApp.random.nextInt(256), BasicGameApp.random.nextInt(256), BasicGameApp.random.nextInt(256));
        a = tileList.getFirst();
        b = tileList.getLast();
        if (a.pathPassingThrough != null) {
            a.pathPassingThrough.split(a);
        }
        TileComponent mCurrentTile = tileList.removeFirst();
        currentTileList.add(mCurrentTile);
        while (tileList.size() > 0) {
            TileComponent mNextTile = tileList.removeFirst();
            currentTileList.add(mNextTile);
            if (mNextTile.pathPassingThrough == null) {
                if (mNextTile != b) {
                    mNextTile.pathPassingThrough = this;
                }
            } else {
                PathComponent oldpassingthrough = mNextTile.pathPassingThrough;
                mNextTile.pathPassingThrough = null;
                oldpassingthrough.split(mNextTile);
//                PathComponent pathComponent = new PathComponent(mCurrentTile, mNextTile, color, this);
//                pathComponentList.addLast(pathComponent);
                b = mNextTile;
                tileList.clear();
            }
            mCurrentTile = mNextTile;
        }

        TileComponent flaga;
        TileComponent flagb;
        flaga = a;
        flagb = b;
        flaga.addPath(this);
        flagb.addPath(this);
        TileComponent middle = currentTileList.get(currentTileList.size() / 2);
        carrier = spawn("carrier", middle.getEntity().getX(), middle.getEntity().getY()).getComponent(CarrierComponent.class);
        carrier.setPathComponent(this);
        TileComponent.clearAllSearches();
        Group group = new Group();
        SpawnData data = new SpawnData(a.getEntity().getX(), a.getEntity().getY());
        Point2D lastPos = a.getEntity().getPosition();
        Point2D currentPos;
        int i = 0;
        Polyline line = new Polyline();
        for (TileComponent tile : currentTileList) {
            currentPos = new Point2D(tile.getEntity().getX() - a.getEntity().getX(), tile.getEntity().getY() - a.getEntity().getY());
//            if (i != 0) {
            line.getPoints().add(currentPos.getX());
            line.getPoints().add(currentPos.getY());
//                Line line = new Line(lastPos.getX(), lastPos.getY(), currentPos.getX(), currentPos.getY());
//                group.getChildren().add(line);
//            }
            currentPos = lastPos;
            i++;
        }
        data.put("component", this);
        line.setStroke(color);
        line.setStrokeWidth(5);
        data.put("view", line);
        spawn("pathSection", data);
        System.out.println("Built path from " + a.getEntity().getPosition() + " to " + b.getEntity().getPosition());


    }


    public TileComponent getOtherSide(TileComponent from) {
        if (from == a) {
            return b;
        } else if (from == b)
            return a;
        else return null;
    }

    public static PathComponent buildPath(TileComponent from, TileComponent to) {
        if (from == to) {
            if (from.canBuildFlag()) {
                from.setFlag(true);
                from.reCalculatePath();
            }
            return null;

        } else {
            SearchResult result = Map.findPath(new SearchQuery() {
                private TileComponent startTile;

                @Override
                public boolean isValidTarget(TileComponent compareTile) {
                    if (compareTile == to && compareTile.type != TileType.WATER) {
                        for (Direction dir : Direction.values()) {
                            TileComponent comp = compareTile.getNeighbour(dir);
                            if (comp != null && ((comp.flag) || comp == startTile)) {
                                return false;
                            }
                        }
                        return true;
                    } else return false;
                }

                @Override
                public boolean canGoThrough(TileComponent compareTile) {
                    return ((compareTile.pathPassingThrough == null) && !compareTile.flag && compareTile.type != TileType.WATER &&!compareTile.occupied&&!compareTile.hasResourceComponent());
                }

                @Override
                public boolean canStartAt(TileComponent startTile) {
                    this.startTile = startTile;
                    if (startTile.type != TileType.WATER) {
                        for (Direction dir : Direction.values()) {
                            TileComponent comp = startTile.getNeighbour(dir);
                            if (comp != null && (comp.flag)) {
                                return false;
                            }

                        }
                        return true;

                    } else {
                        return false;
                    }
                }
            }, from, true);
            if (result.success) {
                result.path.addFirst(from);
                PathComponent path = new PathComponent(result.path);
                from.reCalculatePath();

                return (path);
            }
        }
        return null;
    }


    public void signalResource(Resource resource, TileComponent fromTile) {
        carrier.signalResource(resource, fromTile);
    }

    public boolean identicalPath(LinkedList<TileComponent> comparePath) {
        return comparePath.containsAll(currentTileList) && comparePath.size() == currentTileList.size();
    }

    public interface ValidTargetQuery {
        public boolean isValidTarget(TileComponent compareTile);
    }

    static public SearchQuery findPathQuerry(ValidTargetQuery validTargetQuery) {
        return new SearchQuery() {
            private TileComponent startTile;

            @Override
            public boolean isValidTarget(TileComponent compareTile) {
                if (validTargetQuery.isValidTarget(compareTile)) {
//                    compareTile.pathHere.add(compareTile);
//                    LinkedList<TileComponent.LengthPair> allPathSectionsTo = startTile.getAllPathsectionsTo(compareTile);
//                    for (TileComponent.LengthPair pair:allPathSectionsTo){
//                        if (pair.pathSection!=null){
//                            if (pair.pathSection.identicalPath(compareTile.pathHere)){
//                                compareTile.pathHere.remove(compareTile);
//                                return false;
//                            }
//                        }
//                    }
//
//                    compareTile.pathHere.remove(compareTile);
                    return true;
                }
                return false;
            }

            @Override
            public boolean canGoThrough(TileComponent compareTile) {
                return ((compareTile.pathPassingThrough == null) && !compareTile.flag && compareTile.type != TileType.WATER);
            }

            @Override
            public boolean canStartAt(TileComponent startTile) {
                this.startTile = startTile;
                return startTile.type != TileType.WATER;
            }
        };
    }


    public void split(TileComponent middleTile) {
//        return;

        System.out.println("Splitting " + a.x + " " + a.y + " <---->" + b.x + " " + b.y);
        LinkedList<TileComponent> listA = new LinkedList<TileComponent>();
        LinkedList<TileComponent> listB = new LinkedList<TileComponent>();
        boolean foundTile = false;

        List<TileComponent> oldList = new ArrayList<>(currentTileList);

        TileComponent currentTileComponent = oldList.remove(0);
        listA.add(currentTileComponent);

        while (!foundTile) {
            currentTileComponent = oldList.remove(0);
            listA.add(currentTileComponent);
            if (currentTileComponent == middleTile) {
                foundTile = true;
                listB.add(currentTileComponent);
            }
        }


        while (oldList.size() > 0) {
            currentTileComponent = oldList.remove(0);
            listB.add(currentTileComponent);
        }
//        middleTile.setFlag(true);
        Resource returnedResource = destroyPath(false, null);
        new PathComponent(listA);
        new PathComponent(listB);
        if (returnedResource != null) {
            middleTile.addResource(returnedResource);
        }
        System.out.println("Finished Splitting " + a.x + " " + a.y + " <---->" + b.x + " " + b.y + " " + returnedResource);

    }


    public Resource destroyPath(boolean roadDestroyed, TileComponent destroyedFlag) {
        TileComponent tileBeingCarriedTo = carrier.dropOffTile;
        Resource ret = carrier.remove();
        if (roadDestroyed) {// Only a road destruction, ie no split
            if (ret != null) {
                if (tileBeingCarriedTo == a) {
                    b.addResource(ret);
                } else b.addResource(ret);
            }


        }

        if (destroyedFlag != null && ret != null) {//The flag was destroyed, so the resource is put to the other side
            if (destroyedFlag == a) {
                b.addResource(ret);
            } else a.addResource(ret);
        }

        for (TileComponent tile : currentTileList) {
            if ((tile == a && destroyedFlag == b) || (tile == b && destroyedFlag == a)) {

            } else {
                tile.pathPassingThrough = null;
            }
        }
        if (destroyedFlag == null) {
            a.removePath(this);
            b.removePath(this);
        } else {
            getOtherSide(destroyedFlag).removePath(this);
        }
        if (roadDestroyed) {
            a.reCalculatePath();
//            b.reCalculatePath();
            if (a.getAllPathsectionsTo(b).size() == 0) {
                b.reCalculatePath();
            }
        }
        entity.removeFromWorld();
        //If we did a split, we will place the resource at the center flag
        return ret;
    }
}
