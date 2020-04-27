package Settlers.Workers;

import Settlers.TileComponent;
import Settlers.Types.ResourceType;
import Settlers.Types.WorkerType;

public class HouseBuilderComponent extends WorkerComponent {
    @Override
    String getTextureString() {
        return "pirates/013-parrot.png";

    }

    @Override
    public WorkerType getWorkerType() {
        return WorkerType.BUILDER;
    }

    @Override
    public boolean isValidTargetTile(TileComponent compareTile) {
        return false;
    }

    @Override
    void initResourceTexture() {
    }

    @Override
    boolean attemptGatherResource() {
        return false;
    }

    @Override
    protected ResourceType createsResource() {
        return null;
    }
}
