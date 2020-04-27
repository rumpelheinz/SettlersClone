package Settlers.Houses;

import Settlers.Resource;
import Settlers.TileComponent;
import Settlers.Types.HouseSize;
import Settlers.Types.ResourceType;
import Settlers.Workers.WorkerComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;
import org.w3c.dom.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class StoreHouseComponent extends HouseComponent {


    public StoreHouseComponent(TileComponent location, TileComponent flagTile, boolean instantbuild) {
        init(location, flagTile, instantbuild);
        if (instantbuild) {
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.PLANK));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
            addResource(new Resource(ResourceType.STONE));
        }
    }

    public static Texture getNewTexture(int width, int height) {
        Texture texture = FXGL.texture("objects/oldBuilding.png", width, height);
        return texture;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public HouseSize getSize() {
        return HouseSize.House;
    }

    @Override
    String getHouseTypeName() {
        return "Storehouse";
    }

    @Override
    public int wantResourceSub(Resource resource) {
        switch (resource.type) {
            case LOG:
                return 1;
            case PLANK:
                return 1;
            case STONE:
                return 1;
        }
        return 0;
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }

    @Override
    public void addResourceSub(Resource resource) {
        inventoryList.add(resource);
        resource.setTarget(null);
//        flagComponent.signalResource(resource);
    }

    @Override
    public WorkerComponent spawnWorker() {
        return null;
    }

    @Override
    boolean usesWorker() {
        return false;
    }

    @Override
    public Texture getTexture(int size, int height) {
        return getNewTexture(size, height);
    }

    @Override
    public boolean pickUpSub(Resource resource) {
        return inventoryList.remove(resource);
    }


}
