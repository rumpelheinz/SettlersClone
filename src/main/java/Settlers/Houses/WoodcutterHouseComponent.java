package Settlers.Houses;

import Settlers.*;
import Settlers.Types.TileType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class WoodcutterHouseComponent extends HouseComponent {
    private Texture texture;


    @Override
    public void onAdded() {
        //     entity.setScaleX(-1);
        texture = FXGL.texture("house.png");
        texture.setTranslateX(-40);
        texture.setTranslateY(-40);

        entity.getViewComponent().addChild(texture);
        WorkerComponent worker2 = spawn("worker", entity.getX(), entity.getY()).getComponent(WorkerComponent.class);
        worker2.setCurrentTile(entity.getComponent(TileComponent.class));
        worker2.home = (entity.getComponent(TileComponent.class));
        flagComponent = entity.getComponent(TileComponent.class);
        flagComponent.setHouse(this);
        //   entity.getViewComponent().clearChildren();
        // entity.getViewComponent().addChild(texture);
        findStoreHouseQuery= new SearchQuery() {
            @Override
            public boolean isValidTarget(TileComponent compareTile) {
           //     return compareTile == flagComponent && compareTile.type!=TileType.WATER;
                if (compareTile.getEntity().hasComponent(StoreHouseComponent.class)) {
                    return true;
                }
                TileComponent.clearAllSearches();
                compareTile.calculatePath();
                LinkedList<TileComponent.LengthPair> connectionList = compareTile.getEntity().getComponent(TileComponent.class).connections;
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
                return ((compareTile.pathPassingThrough == null) && !compareTile.flag && compareTile.type!= TileType.WATER);
            }

            @Override
            public boolean canStartAt(TileComponent startTile) {
                return startTile.type!=TileType.WATER;
            }
        };


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

        SearchResult result = Map.findPath(findStoreHouseQuery, entity.getComponent(TileComponent.class));
        if (result.success) {
            result.path.addFirst(entity.getComponent(TileComponent.class));
            storeHousePath = new PathSection(result.path);
        }
    }


    @Override
    public int wantResource(Resource resource) {
        return 0;
    }
}
