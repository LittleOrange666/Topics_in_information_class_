package net.orange.game.display;

import net.orange.game.Main;
import net.orange.game.character.AllyCharacter;
import net.orange.game.combat.Skill;
import net.orange.game.tools.DrawTool;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CharacterBlock extends TheButton{
    public AllyCharacter getCharacter() {
        return character;
    }

    private final AllyCharacter character;

    public CharacterBlock(@NotNull AllyCharacter character, Pos pos) {
        super(pos,new Picture(character.labelpath));
        this.character = character;
    }
    public Part getActivePart(){
        return new ActivePart(character,getPos().add(90,10));
    }
    public Part getRemovePart(){
        return new RemovePart(character,getPos().add(90,40));
    }
    private static class ActivePart extends TheButton{

        private final AllyCharacter character;
        private static final Picture picture = Picture.fromText("⚡",Main.emojiFont(30,Font.BOLD),Color.GREEN);

        public ActivePart(@NotNull AllyCharacter character, Pos pos) {
            super(pos,picture);
            this.character = character;
            connect(()->character.getMainSkill().activate());
        }

        @Override
        public boolean isDisplayed() {
            return character.isAlive() && character.isActivated() && character.hasMainSkill() && character.getMainSkill().canActivate();
        }
    }
    private static class RemovePart extends TheButton{

        private final AllyCharacter character;
        private static final Picture picture = Picture.fromText("X",Main.numberFont(30,Font.BOLD),Color.RED);

        public RemovePart(@NotNull AllyCharacter character, Pos pos) {
            super(pos,picture);
            this.character = character;
            connect(character::retreat);
        }

        @Override
        public boolean isDisplayed() {
            return character.isAlive() && character.isActivated();
        }
    }
}
