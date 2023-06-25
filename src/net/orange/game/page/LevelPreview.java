package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.character.Plate;
import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObject;
import net.orange.game.display.BlockPos;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.game.GameLayout;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LevelPreview extends Page{
    private final GameLayout layout;
    private final TheButton backbtn;

    public LevelPreview(@NotNull JsonObject data) {
        JsonObject base = data.getObject("base");
        int width = base.getInt("width");
        int height = base.getInt("height");
        layout = new GameLayout(null, width, height);
        JsonArray map = base.getArray("map");
        Pos delta = layout.initPlates(map);
        JsonArray reddoors = base.getArray("red_doors");
        for(JsonArray o : reddoors.arrays()){
            layout.getPlate(BlockPos.from_json(o)).addFlag(Plate.Flag.red);
        }
        JsonArray bluedoors = base.getArray("blue_doors");
        for(JsonArray o : bluedoors.arrays()){
            layout.getPlate(BlockPos.from_json(o)).addFlag(Plate.Flag.blue);
        }
        backbtn = new TheButton(delta.sub(30,30), Picture.fromText("×", Main.textFont(50,Font.BOLD),Color.red));
        backbtn.connect(this::back);
        addPart(backbtn);
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        for (Plate plate : layout) plate.paint(g);
        backbtn.paint(g);
    }
}
