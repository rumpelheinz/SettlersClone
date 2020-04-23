package Settlers.Houses;

import Settlers.*;
import Settlers.Types.ResourceType;
import Settlers.Types.TileType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class SawmillHouseComponent extends HouseComponent {
    private Texture texture;
    private SearchQuery findStoreHouseQuery;
    private long startedWorking;

    public SawmillHouseComponent(TileComponent location, TileComponent flagTile) {
        this.location=location;
        location.occupied=true;
        this.flagTile=flagTile;
        flagTile.setHouse(this);
        SpawnData data = new SpawnData(location.getEntity().getX(), location.getEntity().getY());
        texture = getNewTexture(64,64);
        texture.setTranslateX(-64/2);
        texture.setTranslateY(-64/2);
        data.put("house",this);
        data.put("view",texture);
        spawn("house",data);
    }


    public static Texture getNewTexture(int width,int height) {
        Texture texture = FXGL.texture("objects/medieval_doorway.png",width,height);
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
                LinkedList<TileComponent.LengthPair> connectionList = compareTile.connections;
                if (connectionList == null) {
                } else {
                    for (TileComponent.LengthPair connection : connectionList) {
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
            if (System.currentTimeMillis() - startedWorking > 5000) {
                addResource(new Resource(ResourceType.PLANK));
                working = false;
            }
        } else {
            List<Resource>logs=getResourcesFromInventory(ResourceType.LOG);
            if (logs.size()>0) {
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
    public int wantResource(Resource resource) {
        switch (resource.type) {
            case LOG:
                if (reservedList.contains(resource) && inventoryList.contains(resource)) {
                    return 2;
                }
                int inventory = getResourcesFromInventory(ResourceType.LOG).size();
                int reserved = getResourcesFromReserve(ResourceType.LOG).size();
                int sum = inventory + reserved;
                return (sum < 6 ? 2 : 0);
            case PLANK:
                return 0;
            case STONE:
                return 0;
        }
        return 0;
    }


    @Override
    public void addResource(Resource resource) {
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
    public boolean pickUp(Resource resource) {

        return inventoryList.remove(resource);
    }
}
