package Settlers;

import com.almasb.fxgl.entity.Entity;

public interface SearchQuery {
    boolean isValidTarget(TileComponent compareTile);
    boolean canGoThrough(TileComponent compareTile);
    boolean canStartAt(TileComponent startTile);
  //  boolean isValidTarget(TileComponent compareTile);
}