package net.orange.game.tools;

import net.orange.game.Main;
import net.orange.game.character.GameCharacter;
import net.orange.game.combat.CharacterState;
import net.orange.game.combat.Skill;
import net.orange.game.combat.effects.HealAttack;
import net.orange.game.combat.effects.MagicAttack;
import net.orange.game.combat.skills.*;
import net.orange.game.data.exception.DataException;
import net.orange.game.data.exception.DataVerifyException;
import net.orange.game.data.exception.JsonTypeException;
import net.orange.game.data.json.JsonObject;
import net.orange.game.display.Pos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SkillFactory {
    @Contract("_, _ -> new")
    public static @NotNull Skill create(GameCharacter character, @NotNull JsonObject data){
        String name = data.getString("name");
        switch (name) {
            case "strongattack" -> {
                int sp = data.getInt("sp");
                double mul = data.getInt("mul");
                return new StrongAttack(character, sp, mul);
            }
            case "smallpush" -> {
                int sp = data.getInt("sp");
                double power = data.getDouble("power");
                return new SmallPush(character, sp, power);
            }
            case "instantstrong" -> {
                int duration = data.getInt("duration");
                double mul0 = data.getInt("mul");
                return new InstantStrong(character, duration, mul0);
            }
            case "attackincrease" -> {
                int sp = data.getInt("sp");
                int duration = data.getInt("duration");
                double mul1 = data.getInt("mul");
                return new AttackIncrease(character, sp, duration, mul1);
            }
            case "fastattack" -> {
                int sp = data.getInt("sp");
                int duration = data.getInt("duration");
                double speed0 = data.getDouble("speed");
                double attack0 = data.getDouble("attack");
                return new FastAttack(character, sp, duration, speed0, attack0);
            }
            case "nothing" -> {
                return Skill.doNothing(character);
            }
            case "magicattacker" -> {
                return MagicAttack.object.toSkill(character);
            }
            case "healer" -> {
                return HealAttack.object.toSkill(character);
            }
            case "attackhigh" -> {
                return CharacterState.can_attack_high.toEffect().toSkill(character);
            }
            default -> throw new DataException("Unknown skill name: \"" + name + "\"");
        }
    }
    private static final GameCharacter instance = new GameCharacter(null, Pos.zero);
    public static void verify(JsonObject data) throws DataVerifyException{
        try {
            create(instance, data);
        }catch(DataException e){
            throw new DataVerifyException("failed verify skill data",e);
        }
    }
}
