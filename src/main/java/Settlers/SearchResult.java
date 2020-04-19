package Settlers;

import java.util.LinkedList;

public class SearchResult {
    public boolean success;
    public TileComponent target;
    public TileComponent currentTile;
    public LinkedList<TileComponent> path;
    public SearchResult(boolean success, TileComponent currentTile, TileComponent target, LinkedList<TileComponent> path){
        this.success=success;
        this.currentTile=currentTile;
        this.target=target;
        this.path=path;
    }
}
