package Settlers;

import com.almasb.fxgl.entity.component.Component;

public class LengthPair {
    int distance;
    public Component component;
    public PathComponent pathComponent;
    public LengthPairType type;
    protected enum LengthPairType {
        BUILDING, FLAG
    }

    LengthPair(int distance, Component component, PathComponent pathComponent, LengthPairType type) {
        this.distance = distance;
        this.component = component;
        this.pathComponent = pathComponent;
        this.type = type;
    }
}
