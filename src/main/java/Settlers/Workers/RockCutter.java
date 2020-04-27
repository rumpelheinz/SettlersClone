package Settlers.Workers;

import Settlers.Houses.RockCutterHouseComponent;
import Settlers.Houses.WoodcutterHouseComponent;
import Settlers.*;
import Settlers.Types.ResourceType;
import Settlers.Types.TaskType;
import Settlers.Types.TileType;
import Settlers.Types.WorkerType;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.LinkedList;

import static Settlers.BasicGameApp.stepsBetweenRedraw;
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
