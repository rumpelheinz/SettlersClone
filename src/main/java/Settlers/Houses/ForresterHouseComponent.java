package Settlers.Houses;

import Settlers.*;
import Settlers.Types.HouseSize;
import Settlers.Workers.ForresterComponent;
import Settlers.Workers.WoodCutterComponent;
import Settlers.Workers.WorkerComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class ForresterHouseComponent extends HouseComponent {
    public ForresterHouseComponent(TileComponent location, TileComponent flagTile, boolean instantbuild) {
        init(location, flagTile, instantbuild);
    }


    @Override
    public void onAdded() {
        super.onAdded();
    }

    public static Texture getNewTexture(int width, int height) {
        Texture texture = FXGL.texture("objects/windmill_base.png", width, height);
        return texture;
    }


    @Override
    public HouseSize getSize() {
        return HouseSize.HUT;
    }

    @Override
    String getHouseTypeName() {
        return "Forrester";
    }

    @Override
    public int wantResourceSub(Resource resource) {
        return 0;
    }


    @Override
    public void addResourceSub(Resource resource) {
        throw new Error();
    }

    @Override
    public WorkerComponent spawnWorker() {
        ForresterComponent worker = spawn("forrester", location.getEntity().getX(), location.getEntity().getY()).getComponent(ForresterComponent.class);
        return worker;
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
