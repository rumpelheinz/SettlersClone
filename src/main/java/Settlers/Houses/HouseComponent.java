package Settlers.Houses;


import Settlers.InventoryComponent;
import Settlers.LengthPair;
import Settlers.Resource;
import Settlers.TileComponent;
import Settlers.Types.Direction;
import Settlers.Types.HouseSize;
import Settlers.Types.ResourceType;
import Settlers.UI.UIManager;
import Settlers.Workers.HouseBuilderComponent;
import Settlers.Workers.WoodCutterComponent;
import Settlers.Workers.WorkerComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Settlers.TileComponent.TILEHEIGHT;
import static Settlers.TileComponent.TILEWIDTH;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public abstract class HouseComponent extends InventoryComponent {

    public boolean destroyed = false;
    public TileComponent flagTile;
    TileComponent location;
    Texture texture;
    WorkerComponent worker;
    HouseBuilderComponent builder;
    public String name;

    public abstract HouseSize getSize();

    static int houseId;

    abstract String getHouseTypeName();

    ProgressBar progressBar;

    public void addResource(Resource resource) {

        if (!finished) {
//            resource.setTarget(this);
            inventoryList.add(resource);
            reservedList.remove(resource);
            List<ResourceType> required = new ArrayList<>(Arrays.asList(requiredResource()));
            int count = required.size();
            for (Resource tmpResource : inventoryList) {
                required.remove(tmpResource.type);
            }
            System.out.println(100 - (required.size() / (1.0 * count) * 100));
            if (required.size() == 0)
                progressBar.setFill(Color.GREEN);
        } else addResourceSub(resource);

        if (UIManager.selectedHouse == this) {
            UIManager.repaintResourcePane();
        }
        System.out.println(pad(name, 10) + ": Logs " + getResourcesFromInventory(ResourceType.LOG).size() + " in stock, " + getResourcesFromReserve(ResourceType.LOG).size() + " arriving\n" +

                pad(" ", 12) + "Stones " + getResourcesFromInventory(ResourceType.STONE).size() + " in stock, " + getResourcesFromReserve(ResourceType.STONE).size() + " arriving\n" +
                pad(" ", 12) + "Planks " + getResourcesFromInventory(ResourceType.PLANK).size() + " in stock, " + getResourcesFromReserve(ResourceType.PLANK).size() + " arriving\n"
        );
    }

    abstract public WorkerComponent spawnWorker();

    abstract boolean usesWorker();

    public boolean finished;

    void init(TileComponent location, TileComponent flagTile, boolean instantBuild) {
        SpawnData data = new SpawnData(location.getEntity().getX(), location.getEntity().getY());
        finished = instantBuild;
        if (finished) {
            if (getSize() == HouseSize.HUT) {
                texture = getTexture(64, 64);
                texture.setTranslateX(-64 / 2);
                texture.setTranslateY(-64 / 2);
            } else if (getSize() == HouseSize.House) {
                texture = getTexture(100, 100);
                texture.setTranslateX(-100);
                texture.setTranslateY(-100);
            }
        } else {
            texture = texture("objects/beigeBuilding.png", 64, 64);
            texture.setTranslateX(-64 / 2);
            texture.setTranslateY(-64 / 2);
        }
        Line line = ((Line) new Line(0, 0, flagTile.getEntity().getX() - location.getEntity().getX(), flagTile.getEntity().getY() - location.getEntity().getY()));
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(10);
        data.put("path", line);
        data.put("house", this);
        data.put("view", texture);
        spawn("house", data);
        this.location = location;
        location.occupied = true;
        this.flagTile = flagTile;
        flagTile.setHouse(this);
        if (getSize() == HouseSize.House || getSize() == HouseSize.CASTLE) {
            for (Direction dir : new Direction[]{Direction.W, Direction.NW, Direction.NE}) {
                TileComponent tile = location.getNeighbour(dir);
                tile.occupied = true;
            }
        }
        if (!finished) {
            progressBar = new ProgressBar();
            progressBar.setWidth(64);
            progressBar.setFill(Color.RED);
            progressBar.setTranslateX(-64 / 2);
            progressBar.setTranslateY(-64);
            entity.getViewComponent().addChild(progressBar);
        }
        name = getHouseTypeName() + houseId;
        houseId++;
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            UIManager.selectedHouse = this;
            UIManager.repaintResourcePane();
        });
        flagTile.reCalculatePath();
    }

    abstract public Texture getTexture(int size, int height);

    public void createWorkerAtWarehouse() {
        if (!waiting())
            if (worker == null && usesWorker() && finished) {
                for (LengthPair lengthPair : flagTile.connections) {
                    if (lengthPair.component instanceof StoreHouseComponent) {
                        StoreHouseComponent storeHouse = (StoreHouseComponent) lengthPair.component;
                        WorkerComponent worker2 = spawnWorker();
                        worker2.setCurrentTile(location);
                        worker2.homeTile = location;
                        worker2.house = this;
                        worker2.spawnAtWareHouse(storeHouse);
                        return;
                    }
                }
                startWaiting(5000);
            }
    }

    public void createBuilderAtWarehouse() {
        if (!waiting())
            if (builder == null && !finished) {
                for (LengthPair lengthPair : flagTile.connections) {
                    if (lengthPair.component instanceof StoreHouseComponent) {
                        StoreHouseComponent storeHouse = (StoreHouseComponent) lengthPair.component;
                        HouseBuilderComponent worker2 = spawn("builder", location.getEntity().getX(), location.getEntity().getY()).getComponent(HouseBuilderComponent.class);
                        worker2.setCurrentTile(location);
                        worker2.homeTile = location;
                        worker2.house = this;
                        worker2.spawnAtWareHouse(storeHouse);
                        this.builder = worker2;
                        return;
                    }
                }
                startWaiting(5000);
            }
    }


    @Override
    public void onUpdate(double tpf) {
        createWorkerAtWarehouse();
        createBuilderAtWarehouse();
    }

    public void reCalculateStock() {
        for (Resource resource : inventoryList) {
            if (resource.target != null) {
                if (flagTile.getPathsectionTo(resource.target) == null && resource.target != this) {
                    resource.setTarget(null);
                }
            }
        }
    }

    void finish() {
        finished = true;
        entity.getViewComponent().removeChild(texture);

        if (getSize() == HouseSize.HUT) {
            texture = getTexture(64, 64);
            texture.setTranslateX(-64 / 2);
            texture.setTranslateY(-64 / 2);
        } else if (getSize() == HouseSize.House) {
            texture = getTexture(100, 100);
            texture.setTranslateX(-100);
            texture.setTranslateY(-100);
        }
        while (!reservedList.isEmpty()) {
            Resource a = reservedList.remove();
            a.setTarget(null);
        }
        while (!inventoryList.isEmpty()) {
            Resource a = inventoryList.remove();
            a.setTarget(null);
        }
        entity.getViewComponent().removeChild(progressBar);
        entity.getViewComponent().addChild(texture);
    }


    ResourceType[] requiredResource() {
        switch (getSize()) {

            case HUT:
                return new ResourceType[]{ResourceType.PLANK, ResourceType.PLANK, ResourceType.STONE};
            case House:
                return new ResourceType[]{ResourceType.PLANK, ResourceType.PLANK, ResourceType.PLANK, ResourceType.PLANK, ResourceType.STONE, ResourceType.STONE, ResourceType.STONE, ResourceType.STONE};
            case CASTLE:
                break;
            case FLAG:
            case NONE:
                break;
        }
        return null;
    }


    boolean wantResourceUnfinished(Resource resource) {
        if (reservedList.contains(resource) || inventoryList.contains(resource))
            return true;
        List<ResourceType> required = new ArrayList<ResourceType>(Arrays.asList(requiredResource()));
        for (Resource tmpResource : reservedList) {
            required.remove(tmpResource.type);
        }
        for (Resource tmpResource : inventoryList) {
            required.remove(tmpResource.type);
        }
        return (required.contains(resource.type));
    }

    abstract public int wantResourceSub(Resource resource);


    public int wantResource(Resource resource) {
        if (!finished) {
            if (wantResourceUnfinished(resource)) {
                return 3;
            } else return 0;
        }

        return wantResourceSub(resource);

    }

    public void destroy() {
        this.flagTile.house = null;
        this.location.occupied = false;
        if (getSize() == HouseSize.House || getSize() == HouseSize.CASTLE) {
            for (Direction dir : new Direction[]{Direction.W, Direction.NW, Direction.NE}) {
                TileComponent tile = location.getNeighbour(dir);
                tile.occupied = false;
            }
        }
        for (Resource resource : (List<Resource>) reservedList.clone()) {
            resource.setTarget(null);
        }
        for (Resource resource : inventoryList) {
            resource.setTarget(null);
        }
        destroyed = true;
        entity.removeFromWorld();
    }

    public void setWorker(WorkerComponent workerComponent) {
        this.worker = workerComponent;
    }

    long waitUntil = System.currentTimeMillis();

    boolean waiting() {
        return System.currentTimeMillis() < waitUntil;
    }

    String pad(String in, int length) {
        String ret = "";
        for (int i = 0; i < length - in.length(); i++) {
            ret += " ";
        }
        return ret + in;
    }

    public final boolean pickUp(Resource resource) {
        System.out.println(pad(name, 10) + ": Logs " + getResourcesFromInventory(ResourceType.LOG).size() + " in stock, " + getResourcesFromReserve(ResourceType.LOG).size() + " arriving\n" +

                pad(" ", 12) + "Stones " + getResourcesFromInventory(ResourceType.STONE).size() + " in stock, " + getResourcesFromReserve(ResourceType.STONE).size() + " arriving\n" +
                pad(" ", 12) + "Planks " + getResourcesFromInventory(ResourceType.PLANK).size() + " in stock, " + getResourcesFromReserve(ResourceType.PLANK).size() + " arriving\n"
        );
        boolean ret = pickUpSub(resource);
        if (UIManager.selectedHouse == this) {
            UIManager.repaintResourcePane();
        }
        return ret;
    }

    protected abstract boolean pickUpSub(Resource resource);


    public abstract void addResourceSub(Resource resource);

    void startWaiting(int duration) {
        waitUntil = System.currentTimeMillis() + duration;
    }

    private int buildProgress;

    public boolean build() {
        List<ResourceType> required = new ArrayList<>(Arrays.asList(requiredResource()));
        int count = required.size();
        for (Resource tmpResource : inventoryList) {
            required.remove(tmpResource.type);
        }
        int maxPercent = (int) Math.round(100 - (required.size() / (1.0 * count) * 100));
        if (buildProgress < maxPercent) {
            if (getSize() == HouseSize.HUT)
                buildProgress += 5;
            else buildProgress += 3;
            progressBar.currentValueProperty().set(buildProgress);
            if (buildProgress >= 100) {
                finish();
            }
            return true;
        }
        return false;
    }
}
