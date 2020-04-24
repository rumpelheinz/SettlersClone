package Settlers.Houses;

import Settlers.*;
import Settlers.Types.HouseSize;
import Settlers.Types.ResourceType;
import Settlers.Types.TileType;
import Settlers.Workers.WorkerComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class SawmillHouseComponent extends HouseComponent {
    private SearchQuery findStoreHouseQuery;
    private long startedWorking;

    public SawmillHouseComponent(TileComponent location, TileComponent flagTile, boolean instantbuild) {
        init(location, flagTile, instantbuild);
    }


    public static Texture getNewTexture(int width, int height) {
        Texture texture = FXGL.texture("objects/medieval_doorway.png", width, height);
        return texture;
    }

    @Override
    public void onAdded() {
        findStoreHouseQuery = new SearchQuery() {
            @Override
            public boolean isValidTarget(TileComponent compareTile) {
                //     return compareTile == flagComponent && compareTile.type!=TileType.WATER;
                if (compareTile.getEntity().hasComponent(StoreHouseComponent.class)) {
                    return true;
                }
//                TileComponent.clearAllSearches();
//                compareTile.calculatePath();
                LinkedList<LengthPair> connectionList = compareTile.connections;
                if (connectionList == null) {
                } else {
                    for (LengthPair connection : connectionList) {
                        if (connection.component instanceof StoreHouseComponent) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean canGoThrough(TileComponent compareTile) {
                return ((compareTile.pathPassingThrough == null) && !compareTile.flag && compareTile.type != TileType.WATER);
            }

            @Override
            public boolean canStartAt(TileComponent startTile) {
                return startTile.type != TileType.WATER;
            }
        };
    }

    PathComponent storeHousePath;
    boolean hasSearchedPath = false;

    @Override
    public void onUpdate(double tpf) {

        if (storeHousePath == null && !hasSearchedPath) {
            buildStoreHousePath();
            hasSearchedPath = true;
        }

        if (working) {
            if (System.currentTimeMillis() - startedWorking > 10000) {
                addResource(new Resource(ResourceType.PLANK));
                working = false;
            }
        } else {
            List<Resource> logs = getResourcesFromInventory(ResourceType.LOG);
            if (logs.size() > 0 && finished) {
                inventoryList.remove(logs.get(0));
                startedWorking = System.currentTimeMillis();
                working = true;
            }
        }
    }

    boolean working;


    private void buildStoreHousePath() {

//        SearchResult result = Map.findPath(findStoreHouseQuery, entity.getComponent(TileComponent.class), true);
//        if (result.success) {
//            result.path.addFirst(entity.getComponent(TileComponent.class));
//            storeHousePath = new PathSection(result.path);
//        }
//        flagComponent.reCalculatePath();
    }

    @Override
    public HouseSize getSize() {
        return HouseSize.House;
    }

    @Override
    String getHouseTypeName() {
        return "Sawmill";
    }

    @Override
    public int wantResourceSub(Resource resource) {
        switch (resource.type) {
            case LOG:
                if (reservedList.contains(resource) || inventoryList.contains(resource)) {
                    return 2;
                }
                int inventory = getResourceCountFromInventory(ResourceType.LOG);
                int reserved = getResourceCountFromReserve(ResourceType.LOG);
                int sum = inventory + reserved;
                if (sum < 4) return 2;
                if (sum < 6)
                    return 1;
                return 0;
//                return (sum < 6 ? 2 : 0);
            case PLANK:
                return 0;
            case STONE:
                return 0;
        }
        return 0;
    }


    @Override
    public void addResourceSub(Resource resource) {
        switch (resource.type) {
            case LOG:
                inventoryList.add(resource);
                reservedList.remove(resource);
                break;
            case PLANK:
                inventoryList.add(resource);
                flagTile.signalResource(resource, 0);
                break;
            case STONE:
                break;
        }
    }

    @Override
    public WorkerComponent spawnWorker() {
        return null;
    }

    @Override
    boolean usesWorker() {
        return false;
    }

    @Override
    public Texture getTexture(int size, int height) {
        return getNewTexture(size, height);
    }


    @Override
    protected boolean pickUpSub(Resource resource) {
        return inventoryList.remove(resource);
    }
}
