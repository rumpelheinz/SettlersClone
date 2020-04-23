package Settlers.Houses;

import Settlers.*;
import Settlers.Workers.ForresterComponent;
import Settlers.Workers.WoodCutterComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class ForresterHouseComponent extends HouseComponent {
    public ForresterHouseComponent(TileComponent location, TileComponent flagTile) {
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
        ForresterComponent worker = spawn("forrester", location.getEntity().getX(), location.getEntity().getY()) .getComponent(ForresterComponent.class);
        worker.setCurrentTile(location);
        worker.homeTile = location;
        worker.house = this;
        super.onAdded();
    }
    public static Texture getNewTexture(int width, int height) {
        Texture texture = FXGL.texture("objects/windmill_base.png",width,height);
        return texture;
    }


    @Override
    public int wantResource(Resource resource) {
        return 0;
    }


    @Override
    public void addResource(Resource resource)  {
        throw new Error();
    }

    @Override
    public boolean pickUp(Resource resource) {
        return inventoryList.remove(resource);
    }

}
