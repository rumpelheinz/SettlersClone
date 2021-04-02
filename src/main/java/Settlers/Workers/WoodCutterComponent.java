package Settlers.Workers;

import Settlers.*;
import Settlers.TileResources.TreeComponent;
import Settlers.Types.ResourceType;
import Settlers.Types.WorkerType;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class WoodCutterComponent extends WorkerComponent {


    public WoodCutterComponent() {

    }

    @Override
    String getTextureString() {
        return "pirates/024-crab.png";
    }

    @Override
    public WorkerType getWorkerType() {
        return WorkerType.GATHERER;
    }

    @Override
    public boolean isValidTargetTile(TileComponent compareTile) {
        if (!compareTile.getEntity().hasComponent(TreeComponent.class))
            return false;
        return compareTile.getEntity().getComponent(TreeComponent.class).ripe;
    }

    @Override
    void initResourceTexture() {
        resourceTexture = texture("log.png", 64, 64);
    }

    @Override
    boolean attemptGatherResource() {
        TreeComponent tree = null;
        if (currentTile.getEntity().hasComponent(TreeComponent.class))
            tree = currentTile.getEntity().getComponent(TreeComponent.class);
        if (tree != null && !hasGatheredResource && tree.ripe) {
            tree.remove();
            return true;
        }
        return false;
    }

    @Override
    protected ResourceType createsResource() {
        return ResourceType.LOG;
    }


}
