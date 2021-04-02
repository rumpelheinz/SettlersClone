package Settlers.Workers;

import Settlers.TileComponent;
import Settlers.Types.ResourceType;
import Settlers.Types.WorkerType;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class SawmillWorker extends WorkerComponent {

    @Override
    String getTextureString() {
        return "pirates/032-macaw.png";
    }

    @Override
    public WorkerType getWorkerType() {
        return WorkerType.MANUFACTURER;
    }

    @Override
    public boolean isValidTargetTile(TileComponent compareTile) {
        return true;
    }

    @Override
    void initResourceTexture() {
//        return null;
    }

    @Override
    boolean attemptGatherResource() {
        return false;
    }


    @Override
    protected ResourceType createsResource() {
        return ResourceType.PLANK;
    }
}
