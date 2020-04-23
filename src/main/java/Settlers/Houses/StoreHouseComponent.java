package Settlers.Houses;

import Settlers.Resource;
import Settlers.TileComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;
import org.w3c.dom.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class StoreHouseComponent extends HouseComponent{



    public StoreHouseComponent(TileComponent location, TileComponent flagTile) {
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

    public static Texture getNewTexture(int width, int height) {
        Texture texture = FXGL.texture("objects/oldBuilding.png",width,height);
        return texture;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public int wantResource(Resource resource) {
        switch ( resource.type){
            case LOG:   return 1;
            case PLANK: return 1;
            case STONE: return 1;
        }
        return 0;
    }

    @Override
    public void addResource(Resource resource) {
        inventoryList.add(resource);
        resource.setTarget(null);
//        flagComponent.signalResource(resource);
    }

    @Override
    public boolean pickUp(Resource resource) {
        return inventoryList.remove(resource);
    }


}
