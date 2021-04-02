package Settlers.TileResources;

import Settlers.BasicGameApp;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

import static Settlers.TileComponent.TILEHEIGHT;
import static Settlers.TileComponent.TILEWIDTH;
import static com.almasb.fxgl.dsl.FXGLForKtKt.random;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class TreeComponent extends Component {
    Texture texture;
    public boolean ripe;
    long start;
    static String[] treeStrings=new String[]{"objects/treePine_large.png","objects/treePine_small.png","objects/treeRound_large.png","objects/treeRound_large.png"};
    public TreeComponent(int trees) {

        int rand= (int) (Math.random()*4);
        texture = texture(treeStrings[rand],TILEWIDTH,TILEHEIGHT);
        if (trees==1){
            ripe=true;
            start=0;
        }
        else {
            start=System.currentTimeMillis();
            texture.setScaleX(0);
            texture.setScaleY(0);
        }
    }

    @Override
    public void onAdded() {
            texture.setTranslateY(-TILEHEIGHT*0.7);
            texture.setTranslateX(-TILEWIDTH/2);
            entity.getViewComponent().addChild(texture);

    }

    @Override
    public void onUpdate(double tpf) {
        if (!ripe){
            double scale=interpolate(start,start+(60*1000/ BasicGameApp.gameSpeed.val),System.currentTimeMillis());
            texture.setScaleX(scale);
            texture.setScaleY(scale);
            entity.getViewComponent().onUpdate(tpf);
            if (scale==1)
                ripe=true;
        }

    }
    double interpolate(long minin, long maxin , long value ){
        if (value<minin)
            return 0;
        if (value>maxin)
            return 1;
        return  (1.0) * (value - minin) / (maxin - minin);
//        else return (minin+value*(maxin-minin));
    }


    public void remove() {
        this.getEntity().getViewComponent().removeChild(texture);
        this.getEntity().removeComponent(TreeComponent.class);
    }
}
