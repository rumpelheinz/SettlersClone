package Settlers.Houses;

import Settlers.*;
import Settlers.Types.HouseSize;
import Settlers.Workers.WoodCutterComponent;
import Settlers.Workers.WorkerComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class WoodcutterHouseComponent extends HouseComponent {
    public WoodcutterHouseComponent(TileComponent location, TileComponent flagTile, boolean instantbuild) {
        init(location, flagTile, instantbuild);
    }


    @Override
    public void onAdded() {

        findStoreHouseQuery = PathComponent.findPathQuerry(compareTile -> {
            if ((compareTile.house instanceof StoreHouseComponent)) {
                return true;
            }
            LinkedList<LengthPair> connectionList = compareTile.getEntity().getComponent(TileComponent.class).connections;
            if (connectionList != null) {
                for (LengthPair connection : connectionList) {
                    if (connection.component instanceof StoreHouseComponent) {
                        return true;
                    }
                }
            }
            return false;
        });
        super.onAdded();
    }

    public static Texture getNewTexture(int width, int height) {
        Texture texture = FXGL.texture("objects/housing.png", width, height);
        return texture;
    }

    PathComponent storeHousePath;
    boolean hasSearchedPath = false;

    @Override
    public void onUpdate(double tpf) {

        if (storeHousePath == null && !hasSearchedPath) {
//            buildStoreHousePath();
            hasSearchedPath = true;
        }
        super.onUpdate(tpf);
    }

    private SearchQuery findStoreHouseQuery;

    private void buildStoreHousePath() {

        SearchResult result = Map.findPath(findStoreHouseQuery, flagTile, true);
        if (result.success) {
            result.path.addFirst(flagTile);
            storeHousePath = new PathComponent(result.path);
        }
        flagTile.reCalculatePath();

    }


    @Override
    public HouseSize getSize() {
        return HouseSize.HUT;
    }

    @Override
    String getHouseTypeName() {
        return "Woodcutter";
    }

    @Override
    public int wantResourceSub(Resource resource) {
        return 0;
    }


    @Override
    public void addResourceSub(Resource resource) {
        inventoryList.add(resource);
        flagTile.signalResource(resource, 0);
    }

    @Override
    public WorkerComponent spawnWorker() {
        WoodCutterComponent worker2 = spawn("worker", location.getEntity().getX(), location.getEntity().getY()).getComponent(WoodCutterComponent.class);
        return worker2;
    }

    @Override
    boolean usesWorker() {
        return true;
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
