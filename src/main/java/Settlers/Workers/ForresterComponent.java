package Settlers.Workers;

import Settlers.*;
import Settlers.Houses.ForresterHouseComponent;

import Settlers.Types.ResourceType;
import Settlers.Types.TaskType;
import Settlers.Types.TileType;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.LinkedList;
import java.util.Optional;

import static Settlers.BasicGameApp.stepsBetweenRedraw;
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
