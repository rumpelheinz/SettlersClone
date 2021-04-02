package Settlers.Workers;

import Settlers.*;
import Settlers.TileResources.RockComponent;
import Settlers.Types.ResourceType;
import Settlers.Types.WorkerType;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class RockCutter extends WorkerComponent {

    @Override
    String getTextureString() {
        return "pirates/029-seagull.png";
    }

    @Override
    public WorkerType getWorkerType() {
        return WorkerType.GATHERER;
    }

    @Override
    public boolean isValidTargetTile(TileComponent compareTile) {
        if (!compareTile.getEntity().hasComponent(RockComponent.class))
            return false;
        return true;
    }

    @Override
    void initResourceTexture() {
        resourceTexture = texture("rock.png", 64, 64);
    }

    @Override
    boolean attemptGatherResource() {
        if (currentTile.getEntity().hasComponent(RockComponent.class)) {
            currentTile.getEntity().getComponent(RockComponent.class).remove();

            return true;
        }
        return false;
    }

    @Override
    protected ResourceType createsResource() {
        return ResourceType.STONE;
    }
}
