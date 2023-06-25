package net.orange.game.combat;

public enum AttributeType {
    attack(0, 0.0, null),
    attack_speed(100, 0.0, null),
    max_health(1, 0.0, null),
    defense(0, 0.0, null),
    resistance(0, 0.0, 100.0),
    move_speed(0, 0.0, null),
    attack_range(1, 0.0, null),
    attack_interval(1, 0.0, null),
    attack_count(1, 0.0, null),
    block_count(1, 0.0, null),
    priority(0, null, null),
    sp_recover_ratio(1, 0.0, null),
    cost(0, 0.0, null);
    public final double default_value;
    public final Double minimum;
    public final Double maximum;
    AttributeType(double defaultValue, Double minimum, Double maximum) {
        default_value = defaultValue;
        this.minimum = minimum;
        this.maximum = maximum;
    }
}
