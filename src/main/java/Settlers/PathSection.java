package Settlers;

import Settlers.Types.TileType;
import javafx.scene.paint.Color;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class PathSection {
    TileComponent a;
    TileComponent b;
    private LinkedList<PathComponent> pathComponentList;
    LinkedList<TileComponent> currentTileList;
    CarrierComponent carrier;

    public PathSection(LinkedList<TileComponent> tileList) {
        currentTileList = new LinkedList<TileComponent>();
        pathComponentList = new LinkedList<PathComponent>();
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
                PathComponent pathComponent = new PathComponent(mCurrentTile, mNextTile, color, this);
                pathComponentList.addLast(pathComponent);
                if (mNextTile != b) {
                    mNextTile.pathPassingThrough = this;
                }
            } else {
                PathSection oldpassingthrough = mNextTile.pathPassingThrough;
                mNextTile.pathPassingThrough = null;
                oldpassingthrough.split(mNextTile);
                PathComponent pathComponent = new PathComponent(mCurrentTile, mNextTile, color, this);
                pathComponentList.addLast(pathComponent);
                b = mNextTile;
                tileList.clear();
            }
            mCurrentTile = mNextTile;
        }

        TileComponent flaga;
        TileComponent flagb;
        flaga = a.getEntity().getComponent(TileComponent.class);
        flagb = b.getEntity().getComponent(TileComponent.class);
        flaga.addPath(this);
        flagb.addPath(this);
        TileComponent middle = currentTileList.get(currentTileList.size() / 2);
        carrier = spawn("carrier", middle.getEntity().getX(), middle.getEntity().getY()).getComponent(CarrierComponent.class);
        carrier.setPathSection(this);

    }

    public void split(TileComponent middleTile) {
//        return;
        a.pathPassingThrough = null;
        b.pathPassingThrough = null;
        System.out.println("Splitting " + a.x + " " + a.y + " <---->" + b.x + " " + b.y);
        LinkedList<TileComponent> listA = new LinkedList<TileComponent>();
        LinkedList<TileComponent> listB = new LinkedList<TileComponent>();
        boolean foundTile = false;
        System.out.println("0");
        LinkedList<TileComponent> oldList = (LinkedList<TileComponent>) currentTileList.clone();
        for (TileComponent tile : oldList) {
            tile.pathPassingThrough = null;
        }
        TileComponent currentTileComponent = oldList.removeFirst();
        listA.add(currentTileComponent);
        System.out.println("a");
        while (!foundTile) {
            currentTileComponent = oldList.removeFirst();
            listA.add(currentTileComponent);
            if (currentTileComponent == middleTile) {
                foundTile = true;
                listB.add(currentTileComponent);
            }
        }

        System.out.println("b");
        while (oldList.size() > 0) {
            currentTileComponent = oldList.removeFirst();
            listB.add(currentTileComponent);
        }

        System.out.println("c");
        a.getEntity().getComponent(TileComponent.class).removePath(this);
        b.getEntity().getComponent(TileComponent.class).removePath(this);
        for (PathComponent pathComponent : pathComponentList) {
            pathComponent.delete();
        }
        carrier.getEntity().removeFromWorld();
        carrier=null;
        System.out.println("d");
        new PathSection(listA);
        new PathSection(listB);
        System.out.println("Finished splitting" + a.x + " " + a.y + " <---->" + b.x + " " + b.y);

    }

    static PathSection buildPath(TileComponent from, TileComponent to) {
        if (from == to) {
            from.setFlag(true);
            return null;
        } else {
            SearchResult result = Map.findPath(new SearchQuery() {
                @Override
                public boolean isValidTarget(TileComponent compareTile) {
                    return compareTile == to && compareTile.type!= TileType.WATER;
                }

                @Override
                public boolean canGoThrough(TileComponent compareTile) {
                    return ((compareTile.pathPassingThrough == null) && !compareTile.flag && compareTile.type!=TileType.WATER);
                }

                @Override
                public boolean canStartAt(TileComponent startTile) {
                    return startTile.type!=TileType.WATER;
                }
            }, from);
            if (result.success) {
                result.path.addFirst(from);
                return (new PathSection(result.path));
            }
        }
        return null;
    }


    public void signalResource(Resource resource,TileComponent fromTile){
        carrier.signalResource(resource,fromTile);
    }
}
