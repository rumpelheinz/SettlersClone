package Settlers.Houses;

import Settlers.*;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class WoodcutterHouseComponent extends HouseComponent {

    @Override
    public void onAdded() {
        //     entity.setScaleX(-1);
        texture = FXGL.texture("objects/tavern.png", 64, 64);
        texture.setTranslateX(-40);
        texture.setTranslateY(-80);

        WoodCutterComponent worker2 = spawn("worker", entity.getX(), entity.getY()).getComponent(WoodCutterComponent.class);
        worker2.setCurrentTile(entity.getComponent(TileComponent.class));
        worker2.homeTile = (entity.getComponent(TileComponent.class));
        worker2.house = this;
        flagComponent.setHouse(this);
        //   entity.getViewComponent().clearChildren();
         entity.getViewComponent().addChild(texture);
        findStoreHouseQuery = PathSection.findPathQuerry(compareTile -> {
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

    PathSection storeHousePath;
    boolean hasSearchedPath = false;

    @Override
    public void onUpdate(double tpf) {

        if (storeHousePath == null && !hasSearchedPath) {
            buildStoreHousePath();
            hasSearchedPath = true;
        }
    }

    private SearchQuery findStoreHouseQuery;

    private void buildStoreHousePath() {

        SearchResult result = Map.findPath(findStoreHouseQuery, entity.getComponent(TileComponent.class),true);
        if (result.success) {
            result.path.addFirst(entity.getComponent(TileComponent.class));
            storeHousePath = new PathSection(result.path);
        }
        flagComponent.reCalculatePath();

    }


    @Override
    public int wantResource(Resource resource) {
        return 0;
    }

    @Override
    public void addResource(Resource resource) {
        inventoryList.add(resource);
        flagComponent.signalResource(resource, 0);
    }

    @Override
    public boolean pickUp(Resource resource) {
        return inventoryList.remove(resource);
    }
}
