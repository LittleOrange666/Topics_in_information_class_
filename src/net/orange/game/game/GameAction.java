package net.orange.game.game;

import net.orange.game.Main;
import net.orange.game.character.EnemyCharacter;
import net.orange.game.character.EnemyMessage;
import net.orange.game.combat.EnemyAction;
import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObject;
import net.orange.game.display.BlockPos;
import net.orange.game.display.Picture;
import net.orange.game.page.GamePage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class GameAction {
    public abstract void apply(GamePage page);
    public static final GameAction noaction = new GameAction() {
        @Override
        public void apply(GamePage page) {

        }
    };
    public static class Spawn extends GameAction{
        private final EnemyMessage message;
        private final BlockPos pos;
        private final ArrayList<EnemyAction> commands;
        public Spawn(@NotNull JsonObject data, @NotNull GamePage page){
            message = page.messages.get(data.getString("name"));
            pos = BlockPos.from_json(data.getArray("pos"));
            commands = page.traces.get(data.getInt("trace"));
        }

        @Override
        public void apply(GamePage page) {
            EnemyCharacter character = new EnemyCharacter(page,page.getBlockCenter(pos),message);
            character.setDisplayed(true);
            character.activate();
            for(EnemyAction action : commands){
                character.addAction(action);
            }
            page.characters.add(character);
        }
    }
    public static class Tutorial extends GameAction {
        private final String text;
        public Tutorial(@NotNull JsonObject data){
            text = data.getString("text");
        }

        @Override
        public void apply(@NotNull GamePage page) {
            page.setTutorialtext(Picture.fromText(text, Main.textFont(50)));
        }
    }
    public static GameAction analyse(@NotNull JsonObject data,GamePage page) {
        switch(data.getString("action")){
            case "spawn" -> {
                return new Spawn(data,page);
            }
            case "tutorial" -> {
                return new Tutorial(data);
            }
            default -> {
                return noaction;
            }
        }
    }
}
