package Settlers;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Group;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class TreeComponent extends Component {
    public int trees;
    Texture texture;
    Group group;

    public TreeComponent(int trees) {
        this.trees = trees;
        texture = texture("tree.png");
        group = new Group();


    }

    @Override
    public void onAdded() {
        if (trees > 0) {
            texture.setTranslateY(-20);
            group.getChildren().add(texture);
            if (trees > 1) {
                {
                    Texture tree1 = texture("tree.png");
                    tree1.setTranslateX(-60);

                    tree1.setTranslateY(-20);
                    group.getChildren().add(tree1);
                }
            }
            if (trees > 2) {
                {
                    Texture tree2 = texture("tree.png");
                    tree2.setTranslateX(-30);
                    tree2.setTranslateY(-80);
                    group.getChildren().add(tree2);
                }
            }
            entity.getViewComponent().addChild(group);


        }
    }

    public void setTrees(int trees) {
        this.trees = trees;
        group.getChildren().clear();
        if (trees > 0) {
            texture.setTranslateY(-20);
            group.getChildren().add(texture);
            if (trees > 1) {
                {
                    Texture tree1 = texture("tree.png");
                    tree1.setTranslateX(-60);

                    tree1.setTranslateY(-20);
                    group.getChildren().add(tree1);

                }
            }
            if (trees > 2) {
                {
                    Texture tree2 = texture("tree.png");
                    tree2.setTranslateX(-30);
                    tree2.setTranslateY(-80);
                    group.getChildren().add(tree2);
                }
            }
         //   entity.getViewComponent().addChild(group);

        }
    }
}
