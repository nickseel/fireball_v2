package com.fireball.game.entities.abilities;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.player.Fireball;
import com.fireball.game.util.DataFile;

public abstract class Ability extends Entity {
    protected ControllableEntity owner;
    protected Entity castOwner;
    protected String subAbilityName;

    public Ability(String name, ControllableEntity owner, Entity castOwner, String subAbilityName, double x, double y) {
        super(owner.getTeam(), name, x, y);
        this.owner = owner;
        this.castOwner = castOwner;
        this.subAbilityName = subAbilityName;
    }

    public static void castAbility(ControllableEntity owner, Entity castOwner, String abilityCastNameString, String castName, CastArgumentOverride... argumentOverrides) {
        System.out.println(owner.getName() + " cast " + abilityCastNameString + " as " + castName);
        Entity[] createdObjects;

        double createX = owner.getX();
        double createY = owner.getY();
        double targetX = owner.getTargetX();
        double targetY = owner.getTargetY();
        double angle = Math.atan2(targetY - createY, targetX - createX);
        System.out.println((targetX - createX) + " " + (targetY - createY) + " " + angle);
        double[] extraArgs = new double[0];
        for(CastArgumentOverride override: argumentOverrides) {
            switch(override.getType()) {
                case CastArgumentOverride.ARGUMENT_OWNER:
                    castOwner = override.getOwner(); break;
                case CastArgumentOverride.ARGUMENT_CREATE_POSITION:
                    createX = override.getCreateX();
                    createY = override.getCreateY(); break;
                case CastArgumentOverride.ARGUMENT_TARGET_POSITION:
                    targetX = override.getTargetX();
                    targetY = override.getTargetY(); break;
                case CastArgumentOverride.ARGUMENT_ANGLE:
                    angle = override.getAngle(); break;
                case CastArgumentOverride.ARGUMENT_OTHER:
                    extraArgs = override.getOther(); break;
            }
        }
        String subAbilityName = "";
        try {
            subAbilityName = DataFile.getString("sub_ability_name");
        } catch(IllegalArgumentException e) {}


        DataFile.setCurrentLocation("abilities", owner.getName(), abilityCastNameString);
        if(castName.equals("fireball")) {
            Entity e = new Fireball(owner,
                    castOwner,
                    subAbilityName,
                    createX,
                    createY,
                    DataFile.getFloat("radius"),
                    angle,
                    DataFile.getFloat("velocity"));
            createdObjects = new Entity[] {e};
        }
    }
}
