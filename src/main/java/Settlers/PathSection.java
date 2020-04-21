package Settlers;

import Settlers.Houses.StoreHouseComponent;
import Settlers.Types.Direction;
import Settlers.Types.TileType;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class PathSection extends Component {
    TileComponent a;
    TileComponent b;
    LinkedList<TileComponent> currentTileList;
    CarrierComponent carrier;

    public PathSection(LinkedList<TileComponent> tileList) {
        currentTileList = new LinkedList<TileComponent>();
//        pathComponentList = new LinkedList<PathComponent>();
        Color color = Color.rgb(BasicGameApp.random.nextInt(256), BasicGameApp.random.nextInt(256), BasicGameApp.random.nextInt(256));
        a = tileList.getFirst();
        b = tileList.getLast();
        if (a.pathPassingThrough != null) {
            a.pathPassingThrough.split(a);
        }
        //       a.pathPassingThrough=null;
//        b.pathPassingThrough=null;
        TileComponent mCurrentTile = tileList.removeFirst();
        currentTileList.add(mCurrentTile);
        while (tileList.size() > 0) {
            TileComponent mNextTile = tileList.removeFirst();
            currentTileList.add(mNextTile);
            if (mNextTile.pathPassingThrough == null) {
//                PathComponent pathComponent = new PathComponent(mCurrentTile, mNextTile, color, this);
//                pathComponentList.addLast(pathComponent);
                if (mNextTile != b) {
                    mNextTile.pathPassingThrough = this;
                }
            } else {
                PathSection oldpassingthrough = mNextTile.pathPassingThrough;
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
        carrier.setPathSection(this);
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

    static PathSection buildPath(TileComponent from, TileComponent to) {
        if (from == to) {
            for (Direction dir : Direction.values()) {
                TileComponent comp = from.getNeighbour(dir);
                if (comp != null && (comp.flag)) {
                    return null;
                }

            }
            from.setFlag(true);
            from.reCalculatePath();
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
                    return ((compareTile.pathPassingThrough == null) && !compareTile.flag && compareTile.type != TileType.WATER);
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
                PathSection path = new PathSection(result.path);
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

        LinkedList<TileComponent> oldList = (LinkedList<TileComponent>) currentTileList.clone();

        TileComponent currentTileComponent = oldList.removeFirst();
        listA.add(currentTileComponent);

        while (!foundTile) {
            currentTileComponent = oldList.removeFirst();
            listA.add(currentTileComponent);
            if (currentTileComponent == middleTile) {
                foundTile = true;
                listB.add(currentTileComponent);
            }
        }


        while (oldList.size() > 0) {
            currentTileComponent = oldList.removeFirst();
            listB.add(currentTileComponent);
        }
//        middleTile.setFlag(true);
        Resource returnedResource = destroyPath(false, null);
        new PathSection(listA);
        new PathSection(listB);
        if (returnedResource != null) {
            middleTile.addResource(returnedResource);
        }
        System.out.println("Finished Splitting " + a.x + " " + a.y + " <---->" + b.x + " " + b.y+" "+ returnedResource);

    }


    Resource destroyPath(boolean roadDestroyed, TileComponent destroyedFlag) {
        TileComponent tileBeingCarriedTo = carrier.dropOffTile;
        Resource ret = carrier.remove();
        if (roadDestroyed) {// Only a road destruction, ie no split
            if (ret != null) {
                if (tileBeingCarriedTo == a) {
                    b.addResource(ret);
                } else b.addResource(ret);
            }
            a.reCalculatePath();
            if (a.getAllPathsectionsTo(b).size() == 0) {
                b.reCalculatePath();
            }

        }

        if (destroyedFlag != null) {//The flag was destroyed, so the resource is put to the other side
            if (destroyedFlag == a) {
                b.addResource(ret);
            } else a.addResource(ret);
        }

        for (TileComponent tile : currentTileList) {
            tile.pathPassingThrough = null;
        }
        a.removePath(this);
        b.removePath(this);
        entity.removeFromWorld();
        //If we did a split, we will place the resource at the center flag
        return ret;
    }
}
