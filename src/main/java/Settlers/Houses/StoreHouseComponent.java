package Settlers.Houses;

import Settlers.Resource;
import Settlers.TileComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class StoreHouseComponent extends HouseComponent {
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
            case LOG:   return 1;
            case PLANK: return 1;
            case STONE: return 1;
        }
        return 0;
    };
}
