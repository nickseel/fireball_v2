package com.fireball.game.entities.abilities;

public class AbilityCooldown {
    private CooldownType type;
    private double value;
    private AbilityCooldown[] linked;

    public AbilityCooldown(CooldownType type, double value, AbilityCooldown... linked) {
        this.type = type;
        this.value = value;
        this.linked = linked;
    }

    public void setLinked(AbilityCooldown... linked) {
        this.linked = linked;
    }

    public boolean update(boolean first, double delta) {
        switch(type) {
            case NORMAL:
                if(!first)
                    return false;
            case RING_FAILSAFE:
                value -= delta;
                return value <= 0;
            case RING_OBJECTS_DESTROYED:
                return value == 0;
        }
        return false;
    }

    public void modifyValue(double m) {
        value += m;
    }

    public void finish(boolean initial) {
        value = 0;

        if(initial) {
            for(AbilityCooldown ac: linked) {
                ac.finish(false);
            }
        }
    }

    public String toString() {
        return type + " " + value;
    }
}
