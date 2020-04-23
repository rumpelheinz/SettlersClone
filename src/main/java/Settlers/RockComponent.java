package Settlers;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class RockComponent extends Component {
    public int size;
    Group group;

    public RockComponent(int size) {
        this.size = size;
        group = new Group();
    }

    @Override
    public void onAdded() {
        reDraw();
        entity.getViewComponent().addChild(group);
    }

    public void remove() {
        size = size - 1;
        if (size == 0) {
            entity.getViewComponent().removeChild(group);
            entity.removeComponent(RockComponent.class);
        } else
            reDraw();
    }

    public void reDraw() {
        group.getChildren().clear();
        if (size > 0) {
            Texture texture1 = null;
            Texture texture2 = null;
            Texture texture3 = null;

            if (size == 1)
                texture1 = texture("objects/rockGrey_small7.png", 48, 48);
            if (size == 2)
                texture1 = texture("objects/rockGrey_medium1.png", 48, 48);
            if (size > 2)
                texture1 = texture("objects/rockGrey_large.png", 48, 48);

            texture1.setTranslateX(-32);
            texture1.setTranslateY(-10);


            if (size > 3) {

                if (size == 4)
                    texture2 = texture("objects/rockGrey_small6.png", 64, 64);
                if (size == 5)
                    texture2 = texture("objects/rockGrey_medium2.png", 64, 64);
                if (size > 5)
                    texture2 = texture("objects/rockGrey_large.png", 64, 64);

                texture2.setTranslateX(-64/2);
                texture2.setTranslateY(-64);

            }
            if (size > 6) {

                if (size == 7)
                    texture3 = texture("objects/rockGrey_small4.png", 48, 48);
                if (size == 8)
                    texture3 = texture("objects/rockGrey_medium3.png", 48, 48);
                if (size > 8)
                    texture3 = texture("objects/rockGrey_large.png", 48, 48);

                texture3.setTranslateX(0);
                texture3.setTranslateY(-32);

            }
            if (texture2 != null) {
                group.getChildren().add(texture2);
            }
            if (texture3 != null) {
                group.getChildren().add(texture3);
            }
            if (texture1 != null) {
                group.getChildren().add(texture1);
            }
        }

    }
}
