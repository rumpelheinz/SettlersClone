package Settlers;

import java.io.*;

import Settlers.Houses.SawmillHouseComponent;
import Settlers.Houses.StoreHouseComponent;
import Settlers.Houses.WoodcutterHouseComponent;
import Settlers.Types.TileType;
import com.almasb.fxgl.entity.SpawnData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class MyLevelLoader {
    public Map load(File file) {
        InputStream is = null;
        String in = "";
        try {
            is = new FileInputStream(file);

            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = null;
            line = buf.readLine();

            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            String fileAsString = sb.toString();
            in = fileAsString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Contents : " + in);
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(in);
            JSONArray layers = (JSONArray) obj.get("layers");
            JSONObject tilelayer = (JSONObject) layers.get(0);
            Long heightl = (Long) tilelayer.get("height");
            int height = Math.toIntExact(heightl);
            Long widthl = (Long) tilelayer.get("width");
            int width = Math.toIntExact(widthl);
            System.out.println("Width: " + width + " height: " + height);
            JSONArray data = (JSONArray) tilelayer.get("data");
            int x = 0;
            int y = 0;

            int storeLocationX=0;
            int storeLocationY=0;
            Map map = new Map(width, height);
            while (data.size() > 0) {
                Long typeInt = (Long) data.get(0);
                data.remove(0);

                TileType tileType;
                if (typeInt == 2) {
                    tileType = TileType.WATER;
                    map.addTile(x, y, tileType);
                } else if (typeInt == 34) {
                    tileType = TileType.GRASS;
                    map.addTile(x, y, tileType);
                } else if (typeInt == 18) {//Forrest
                    tileType = TileType.GRASS;
                    TileComponent tile = map.addTile(x, y, tileType);
                    tile.getEntity().addComponent(new TreeComponent(1));
                } else if (typeInt == 4) {//Hut
                    tileType = TileType.GRASS;
                    TileComponent tile = map.addTile(x, y, tileType);
                } else if (typeInt == 8) {//StoreHouse
                    storeLocationX=x;
                    storeLocationY=y;
                    tileType = TileType.GRASS;
                    TileComponent tile = map.addTile(x, y, tileType);
                } else if (typeInt == 50) {//StoreHouse
                    tileType = TileType.GRASS;
                    TileComponent tile = map.addTile(x, y, tileType);
                    tile.getEntity().addComponent(new RockComponent(9));
                } else if (typeInt == 9) {//StoreHouse
                    tileType = TileType.GRASS;
                    TileComponent tile = map.addTile(x, y, tileType);
                } else {
                    tileType = TileType.WATER;
                    map.addTile(x, y, tileType);
                    System.out.println("Unexpected value: " + typeInt);
                }
                x++;
                if (x == width) {
                    x = 0;
                    y++;
                }
            }
//            SpawnData spawnData=new SpawnData(0,0);
//            spawn("tileContainer",spawnData);
            new StoreHouseComponent(map.get(storeLocationX,storeLocationY),map.get(storeLocationX+1,storeLocationY+1),true);
            return map;
//            obj.layers[0].data


        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;
    }

}
