package Settlers.Types;

public enum GameSpeed {
    NORMAL(1),
    FAST(3),
    VERY_FAST(5);

    public final int val;

    GameSpeed(int i) {
        this.val=i;
    }
}
