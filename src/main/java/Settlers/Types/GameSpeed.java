package Settlers.Types;

public class GameSpeed {
    static GameSpeed NORMAL=new GameSpeed(1);
    public static GameSpeed FAST=new GameSpeed(3);
    static GameSpeed VERYFAST=new GameSpeed(5);
    public final int val;

    public GameSpeed(int i) {
        this.val=i;
    }
}
