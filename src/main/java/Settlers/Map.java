package Settlers;

import Settlers.Types.Direction;
import Settlers.Types.TileType;
import com.almasb.fxgl.core.math.Vec2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Map {
    int width;
    int height;
    Random random = new Random();
    static TileContainerComponent tileContainer;

    HashMap<Vec2, TileComponent> tiles;

    public Map(int width, int height) {
        tiles = new HashMap<>();
        this.width = width;
        this.height = height;
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                   TileType type=TileType.values()[random.nextInt(TileType.values().length)];
////                TileType type = TileType.GRASS;
//                if ((x == 2 && y == 2) || (x == 3 && y == 2) || (x == 3 && y == 3) || (x == 3 && y == 4) || (x == 4 && y == 4) || (x == 5 && y == 4)) {
//                    type = TileType.GRASS;
//                }
//                TileComponent newTileComponent = new TileComponent(x, y, type);
//                tiles.put(new Vec2(x, y), newTileComponent);
//                TileComponent neighbourW;
//                TileComponent neighbourNE;
//                TileComponent neighbourNW;
//                neighbourW = tiles.get(new Vec2(x - 1, y));
//                if (y % 2 == 0) {
//                    neighbourNE = tiles.get(new Vec2(x, y - 1));
//                    neighbourNW = tiles.get(new Vec2(x - 1, y - 1));
//                } else {
//                    neighbourNE = tiles.get(new Vec2(x + 1, y - 1));
//                    neighbourNW = tiles.get(new Vec2(x, y - 1));
//                }
//                if (neighbourW != null) neighbourW.E = newTileComponent;
//                newTileComponent.W = neighbourW;
//                if (neighbourNW != null) neighbourNW.SE = newTileComponent;
//                newTileComponent.NW = neighbourNW;
//                if (neighbourNE != null) neighbourNE.SW = newTileComponent;
//                newTileComponent.NE = neighbourNE;
//            }
//        }
    }

    public TileComponent addTile(int x, int y, TileType type) {
        TileComponent newTileComponent = new TileComponent(x, y, type);
        tiles.put(new Vec2(x, y), newTileComponent);
        TileComponent neighbourW;
        TileComponent neighbourNE;
        TileComponent neighbourNW;
        neighbourW = tiles.get(new Vec2(x - 1, y));
        if (y % 2 == 0) {
            neighbourNE = tiles.get(new Vec2(x, y - 1));
            neighbourNW = tiles.get(new Vec2(x - 1, y - 1));
        } else {
            neighbourNE = tiles.get(new Vec2(x + 1, y - 1));
            neighbourNW = tiles.get(new Vec2(x, y - 1));
        }
        if (neighbourW != null) neighbourW.E = newTileComponent;
        newTileComponent.W = neighbourW;
        if (neighbourNW != null) neighbourNW.SE = newTileComponent;
        newTileComponent.NW = neighbourNW;
        if (neighbourNE != null) neighbourNE.SW = newTileComponent;
        newTileComponent.NE = neighbourNE;
        return newTileComponent;
    }

    public void clearSearch() {
        for (TileComponent a : tiles.values()) {
            a.clearAreaSearch();
        }
    }

    public TileComponent get(int x, int y) {
        return tiles.get(new Vec2(x, y));
    }


    public static SearchResult findPath(SearchQuery parameter, TileComponent currentTile, boolean avoidExistingPaths) {
        if (!parameter.canStartAt(currentTile)) {
            return new SearchResult(false, currentTile, null, null);
        }
        BasicGameApp.map.clearSearch();
        LinkedList<TileComponent> path = new LinkedList<TileComponent>();
        LinkedList<TileComponent> toLookat = new LinkedList<TileComponent>();
        toLookat.add(currentTile);
        currentTile.pathHere = new LinkedList<TileComponent>();
        currentTile.lookedAt = true;
        TileComponent target;
        if (parameter.isValidTarget(currentTile)) {
            path = currentTile.pathHere;
            path.addLast(currentTile);
            target = currentTile;
            return new SearchResult(true, currentTile, target, path);
        }
        while (toLookat.size() > 0) {
            TileComponent cTile = toLookat.getFirst();
            toLookat.remove(0);
            for (Direction dir : Direction.values()) {
                if (cTile.hasPath(dir)) {
                    TileComponent nextTile = cTile.getNeighbour(dir);
                    LinkedList<TileComponent> newList = (LinkedList<TileComponent>) cTile.pathHere.clone();
                    boolean alreadypresent = newList.contains(nextTile);
                    newList.addLast(nextTile);
                    boolean validtarget = parameter.isValidTarget(nextTile) && currentTile != nextTile && (parameter.canGoThrough(cTile) || cTile == currentTile);
                    if (validtarget) {
                        if (avoidExistingPaths) {
                            newList.addFirst(currentTile);
                            LinkedList<TileComponent.LengthPair> allPathSectionsTo = currentTile.getAllPathsectionsTo(nextTile);
                            for (TileComponent.LengthPair pair : allPathSectionsTo) {
                                if (pair.pathSection != null) {
                                    if (pair.pathSection.identicalPath(newList)) {
                                        validtarget = false;
                                    }
                                }
                            }
                            newList.remove(currentTile);
                        }
//
                        if (validtarget) {
                            nextTile.pathHere = newList;
                            path = nextTile.pathHere;
                            target = nextTile;
                            return new SearchResult(true, currentTile, target, path);
                        }

                    }
                    if (!nextTile.lookedAt && (parameter.canGoThrough(cTile) || cTile == currentTile)) {
                        nextTile.pathHere = newList;
                        nextTile.lookedAt = true;
                        toLookat.addLast(nextTile);


                    }
                }

            }
        }
        return new SearchResult(false, currentTile, null, null);
    }

}
