package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObj;
import net.orange.game.data.json.JsonObject;
import net.orange.game.data.object.Reward;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.game.GameResult;
import net.orange.game.tools.DrawTool;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public class SettlePage extends Page {
    private final String level;
    private final String name;
    private final GameResult result;
    private final ArrayList<Reward> rewards;

    public SettlePage(String level, GameResult result) {
        this.level = level;
        this.result = result;
        JsonObject object = Main.read("levels/"+level);
        JsonObject base = object.getObject("base");
        name = base.getString("name");
        this.rewards = new ArrayList<>();
        boolean completed = Main.userData.completed(level);
        boolean full_completed = Main.userData.full_completed(level);
        TheButton btn = new TheButton(new Pos(1400,700),
                Picture.fromText("NEXT",Main.textFont(200, Font.BOLD),Color.GREEN));
        btn.connect(this::next);
        addPart(btn);
        JsonObject rewards = new JsonObject();
        if (object.has("rewards")){
            rewards = object.getObject("rewards");
        }
        if (result == GameResult.success || result == GameResult.fullsuccess){
            if (!completed){
                addReward(rewards,"first");
            }
            if (!full_completed&&result == GameResult.fullsuccess){
                addReward(rewards,"first_full");
            }
            if (result == GameResult.fullsuccess){
                addReward(rewards,"full");
            }else {
                addReward(rewards,"partial");
            }
            for(Reward reward : this.rewards){
                reward.apply();
            }
            if (!completed) {
                Main.userData.complete(level);
            }
            if (result == GameResult.fullsuccess && !full_completed) {
                Main.userData.full_complete(level);
            }
        }
    }
    private void addReward(@NotNull JsonObject rewords, String key){
        if (rewords.has(key)){
            JsonArray array = rewords.getArray(key);
            for(JsonObj o : array){
                if (o instanceof JsonObject obj){
                    this.rewards.add(new Reward(obj));
                }
            }
        }
    }
    public void next(){
        Main.mainWindow.changePage(new LevelPage());
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        DrawTool.drawString(g,new Pos(300,300),level,Color.black,Main.textFont(100));
        DrawTool.drawString(g,new Pos(300,400),name,Color.gray,Main.textFont(80));
        String text = result.text;
        DrawTool.drawString(g,new Pos(300,550),text,Color.black,Main.textFont(150));
        if (result != GameResult.faild) {
            DrawTool.drawString(g, new Pos(100, 750), "獎勵:", Color.black, Main.textFont(80));
        }
        Pos pos = new Pos(300,750);
        for(Reward reward : rewards){
            if (reward.isDisplay()) {
                Picture icon = reward.getIcon();
                icon.paint(pos, g);
                pos = pos.add(icon.getSize().x(), 0);
            }
        }
    }
}
