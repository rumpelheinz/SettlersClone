package Settlers.Houses;

import Settlers.Resource;
import Settlers.TileComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

public class SawmillHouseComponent extends HouseComponent {
    private Texture texture;

    @Override
    public void onAdded() {
        //     entity.setScaleX(-1);
        texture = FXGL.texture("objects/oldBuilding.png");
        texture.setTranslateX(-40);
        texture.setTranslateY(-40);
        TileComponent flag= getEntity().getComponent(TileComponent.class);
        flag.setHouse(this);
        entity.getViewComponent().addChild(texture);

//        WorkerComponent worker2=spawn("worker",entity.getX(),entity.getY()).getComponent(WorkerComponent.class);
//        worker2.setCurrentTile(entity.getComponent(TileComponent.class));
//        worker2.home=(entity.getComponent(TileComponent.class));
        //   entity.getViewComponent().clearChildren();
        // entity.getViewComponent().addChild(texture);

    }

    @Override
    public int wantResource(Resource resource) {
        switch ( resource.type){
            case LOG:   return 2;
            case PLANK: return 0;
            case STONE: return 0;
        }
        return 0;
    };
}
