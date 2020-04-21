package Settlers.Houses;

import Settlers.*;
import Settlers.Types.ResourceType;
import Settlers.Types.TileType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class SawmillHouseComponent extends HouseComponent {
    private Texture texture;
    private int logs;
    private SearchQuery findStoreHouseQuery;
    private long startedWorking;

    @Override
    public void onAdded() {
        //     entity.setScaleX(-1);
        texture = FXGL.texture("objects/medieval_doorway.png",80,80);
        texture.setTranslateX(-40);
        texture.setTranslateY(-80);
        TileComponent flag= getEntity().getComponent(TileComponent.class);
        flag.setHouse(this);
        entity.getViewComponent().addChild(texture);


        flagComponent = entity.getComponent(TileComponent.class);
        flagComponent.setHouse(this);
        findStoreHouseQuery= new SearchQuery() {
            @Override
            public boolean isValidTarget(TileComponent compareTile) {
                //     return compareTile == flagComponent && compareTile.type!=TileType.WATER;
                if (compareTile.getEntity().hasComponent(StoreHouseComponent.class)) {
                    return true;
                }
//                TileComponent.clearAllSearches();
//                compareTile.calculatePath();
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



//        WorkerComponent worker2=spawn("worker",entity.getX(),entity.getY()).getComponent(WorkerComponent.class);
//        worker2.setCurrentTile(entity.getComponent(TileComponent.class));
//        worker2.home=(entity.getComponent(TileComponent.class));
        //   entity.getViewComponent().clearChildren();
        // entity.getViewComponent().addChild(texture);

    }
    PathSection storeHousePath;
    boolean hasSearchedPath = false;

    @Override
    public void onUpdate(double tpf) {

        if (storeHousePath == null && !hasSearchedPath) {
            buildStoreHousePath();
            hasSearchedPath = true;
        }

        if (working){
            if (System.currentTimeMillis()-startedWorking>5000){
                addResource(new Resource(ResourceType.PLANK));
                working=false;
            }
        }
        else {
            if (logs>0){
                logs--;
                startedWorking=System.currentTimeMillis();
                working=true;
            }
        }
    }
    boolean working;




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
        switch ( resource.type){
            case LOG:   return 2;
            case PLANK: return 0;
            case STONE: return 0;
        }
        return 0;
    }

    @Override
    public void addResource(Resource resource) {
        switch (resource.type){
            case LOG:
                logs++;
                break;
            case PLANK:
                inventoryList.add(resource);
                flagComponent.signalResource(resource,0);
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
