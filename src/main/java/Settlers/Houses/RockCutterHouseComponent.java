package Settlers.Houses;

import Settlers.*;
import Settlers.Workers.RockCutter;
import Settlers.Workers.WoodCutterComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class RockCutterHouseComponent extends HouseComponent {
    public RockCutterHouseComponent(TileComponent location, TileComponent flagTile) {
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



    @Override
    public void onAdded() {
        RockCutter worker2 = spawn("rockcutter", location.getEntity().getX(), location.getEntity().getY()) .getComponent(RockCutter.class);
        worker2.setCurrentTile(location);
        worker2.homeTile = location;
        worker2.house = this;
        findStoreHouseQuery = PathComponent.findPathQuerry(compareTile -> {
            if ((compareTile.house instanceof StoreHouseComponent)) {
                return true;
            }
            LinkedList<TileComponent.LengthPair> connectionList = compareTile.getEntity().getComponent(TileComponent.class).connections;
            if (connectionList != null) {
                for (TileComponent.LengthPair connection : connectionList) {
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
        Texture texture = FXGL.texture("objects/outhouse.png",width,height);
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
    public int wantResource(Resource resource) {
        return 0;
    }


    @Override
    public void addResource(Resource resource) {
        inventoryList.add(resource);
        flagTile.signalResource(resource, 0);
    }

    @Override
    public boolean pickUp(Resource resource) {
        return inventoryList.remove(resource);
    }


}
