package Settlers.Workers;

import Settlers.*;

import Settlers.TileResources.TreeComponent;
import Settlers.Types.ResourceType;
import Settlers.Types.TileType;
import Settlers.Types.WorkerType;

import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ForresterComponent extends WorkerComponent {

    public ForresterComponent() {
    }


    @Override
    boolean attemptGatherResource() {
        Optional<TreeComponent> tree = currentTile.getEntity().getComponentOptional(TreeComponent.class);
        if (!tree.isPresent()) {
            currentTile.getEntity().addComponent(new TreeComponent(0));
            startWaiting(2000);
            return true;
        }
        return false;
    }

    @Override
    String getTextureString() {
        return "pirates/020-kraken.png";
    }

    @Override
    public WorkerType getWorkerType() {
        return WorkerType.GATHERER;
    }


    @Override
    public boolean isValidTargetTile(TileComponent compareTile) {
        return !compareTile.occupied && compareTile.pathPassingThrough == null && compareTile.type != TileType.WATER && !compareTile.hasResourceComponent() && !compareTile.flag;
    }

    @Override
    void initResourceTexture() {
    }

    @Override
    protected ResourceType createsResource() {
        return null;
    }


}
