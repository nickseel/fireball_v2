package com.fireball.game.entities;

import com.fireball.game.entities.abilities.*;
import com.fireball.game.entities.enemies.ai.AI;
import com.fireball.game.util.DataFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public abstract class ControllableEntity extends Entity {
    protected AI ai;
    protected double moveX, moveY;
    protected double targetX, targetY;

    protected AbilityType[] abilities;
    protected boolean[] prevAbilityInputs;
    protected boolean[] abilityInputs;
    protected LinkedList<AbilityCooldown>[] abilityCooldowns;
    protected double abilityCastTimer, abilityCastTimerMax;
    protected int maxAbilityCombo;
    protected ArrayList<AbilityType> abilitiesCasting;
    protected String abilityCastNameString;
    protected double abilityCost;
    protected boolean abilitiesStreaming;
    protected double abilityStreamTime, minAbilityStreamTime;
    protected double abilityStreamMovementDebuff;

    protected double stunTimer;
    protected double stunFriction;

    protected double maxHealth;
    protected double health;
    protected double accel = 1000;
    protected double friction;
    protected double maxSpeed;
    protected double turnAssist;

    public ControllableEntity(Team team, String name, double x, double y, AbilityType[] abilities, int maxAbilityCombo, AI ai) {
        super(team, name, x, y);

        this.ai = ai;
        this.abilities = abilities;
        prevAbilityInputs = new boolean[abilities.length];
        abilityInputs = new boolean[abilities.length];
        abilityCooldowns = new LinkedList[abilities.length];
        for(int i = 0; i < abilities.length; i++)
            abilityCooldowns[i] = new LinkedList<AbilityCooldown>();
        this.maxAbilityCombo = maxAbilityCombo;
        abilitiesCasting = new ArrayList<AbilityType>();
        abilitiesStreaming = false;
        abilityStreamMovementDebuff = 1;
    }

    public void cycleAbilityInputs() {
        for(int i = 0; i < abilities.length; i++) {
            prevAbilityInputs[i] = abilityInputs[i];
        }
    }

    protected void updateAbilities(double delta) {
        for(int i = 0; i < abilities.length; i++) {
            for(int j = 0; j < abilityCooldowns[i].size(); j++) {
                if(abilityCooldowns[i].get(j).update(j == 0, delta)) {
                    abilityCooldowns[i].remove(j--).finish(true);
                }

                if(abilityCooldowns[i].size() == 0) {
                    prevAbilityInputs[i] = false; //allow for holding down ability
                }
            }

            if(shouldBeginCastingAbility(i)) {
                prevAbilityInputs[i] = true;
                abilitiesCasting.add(abilities[i]);
                Collections.sort(abilitiesCasting, new SortAbility());

                abilityCastNameString = "";
                for(int j = 0; j < abilitiesCasting.size(); j++) {
                    if(j > 0)
                        abilityCastNameString += "-";
                    abilityCastNameString += abilitiesCasting.get(j).getString();
                }

                boolean abilityExists = false;
                try {
                    DataFile.setCurrentLocation("abilities", name, abilityCastNameString);
                    abilityExists = true;
                } catch (IllegalArgumentException e) {
                    //ABILITY DOESN'T EXIST IN MAP
                    System.out.println("ABILITY " + abilityCastNameString + " DOESN'T EXIST IN MAP");

                    abilityCastTimer = 0;
                    abilityCastTimerMax = 0;
                    abilityCastNameString = "";
                    abilitiesCasting.clear();
                }

                if(abilityExists) {
                    try {
                        abilityCost = DataFile.getFloat("cost");
                    } catch (IllegalArgumentException e) {
                        abilityCost = 0;
                    }

                    double castTime = DataFile.getFloat("cast_time");
                    if(castTime > abilityCastTimerMax) {
                        abilityCastTimer = castTime - (abilityCastTimerMax - abilityCastTimer);
                        abilityCastTimerMax = castTime;
                    }
                }
            }
        }


        if(abilitiesStreaming) {
            //update streaming abilities and check if should stop streaming
            abilityStreamTime += delta;
            health = Math.max(0.01, health - abilityCost * delta);

            updateStreamingAbilities(delta);

            if(abilityStreamTime >= minAbilityStreamTime /*|| stunTimer > 0*/) {
                for(int i = 0; i < abilities.length; i++) {
                    if(abilitiesCasting.contains(abilities[i])) {
                        if(!abilityInputs[i] /*|| stunTimer > 0*/) {
                            //add to recent abilities graphic

                            for(int j = 0; j < abilities.length; j++) {
                                prevAbilityInputs[j] = false;
                                abilityInputs[j] = false;
                            }

                            abilitiesStreaming = false;
                            abilitiesCasting.clear();
                            abilityCooldown(null);
                            break;
                        }
                    }
                }
            }
        } else if(abilityCastTimer >= 0 || abilitiesCasting.size() > 0) {
            //check if abilities are casted
            abilityCastTimer -= delta;
            if(abilityCastTimer <= 0 && abilitiesCasting.size() > 0) {
                abilityCastTimer = 0;
                abilityCastTimerMax = 0;
                //cast abilities

                DataFile.setCurrentLocation("abilities", name, abilityCastNameString);
                ArrayList<CastArgumentOverride> overrides = new ArrayList<CastArgumentOverride>();


                abilitiesStreaming = false;
                try {
                    DataFile.getInt("stream");
                    abilitiesStreaming = true;
                } catch(IllegalArgumentException e) {}
                if(abilitiesStreaming) {
                    abilityStreamTime = 0;
                    minAbilityStreamTime = DataFile.getFloat("min_stream_time");
                    abilityStreamMovementDebuff = DataFile.getFloat("movement_debuff");
                } else {
                    health = Math.max(0.01, health - abilityCost);

                    //add to recent abilities graphic

                    abilityCooldown(overrides);
                    abilitiesCasting.clear();
                }


                CastArgumentOverride[] overridesA = new CastArgumentOverride[overrides.size()];
                for(int i = 0; i < overrides.size(); i++)
                    overridesA[i] = overrides.remove(0);

                //actually create ability objects here
                Ability.castAbility(this, this, abilityCastNameString, overridesA);
            }
        }
    }

    private boolean shouldBeginCastingAbility(int index) {
        if(abilityInputs[index] && abilityCooldowns[index].size() == 0 &&
                abilitiesCasting.size() < maxAbilityCombo &&
                !abilitiesStreaming /*&& stunTimer <= 0 && not ability locked*/) {

            boolean alreadyCastingSameAbility = abilitiesCasting.contains(abilities[index]);
            if(!alreadyCastingSameAbility || !prevAbilityInputs[index]) {
                //if holding down two abilities to be cast together, wait for their cooldowns together
                if(abilitiesCasting.size() == 0) {
                    int numHeldOffCooldown = 1;
                    int numHeldOnCooldown = 0;
                    for(int i = 0; i < abilities.length; i++) {
                        if(i != index && abilityInputs[i]) {
                            if(abilityCooldowns[i].size() != 0) {
                                numHeldOnCooldown += 1;
                            } else {
                                numHeldOffCooldown += 1;
                            }
                        }
                    }
                    //Print(num_held_on_cooldown, num_held_off_cooldown);
                    if(numHeldOnCooldown > 0 && numHeldOffCooldown + numHeldOnCooldown <= maxAbilityCombo) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    protected void abilityCooldown(ArrayList<CastArgumentOverride> overrides) {
        DataFile.setCurrentLocation("abilities");

        boolean ringObjectCooldown = false;
        try {
            ringObjectCooldown = DataFile.getString(name, abilityCastNameString, "cooldown_type").equals("ring_objects_destroyed");
        } catch(IllegalArgumentException e) {
        }
        if(ringObjectCooldown) {
            int num = 0;
            try {
                num = DataFile.getInt(name, abilityCastNameString, "num");
            } catch(IllegalArgumentException e) {
            }
            AbilityCooldown ro = new AbilityCooldown(CooldownType.RING_OBJECTS_DESTROYED, num);
            AbilityCooldown rfs = new AbilityCooldown(CooldownType.RING_FAILSAFE, CooldownType.RING_FAILSAFE_COOLDOWN);
            ro.setLinked(rfs);
            rfs.setLinked(ro);
            if(overrides != null)
                overrides.add(new CastArgumentOverride(CastArgumentOverride.ARGUMENT_OTHER).setOther(new Object[] {ro}));

            for(int i = 0; i < abilities.length; i++) {
                if(abilitiesCasting.contains(abilities[i])) {
                    abilityCooldowns[i].add(ro);
                    abilityCooldowns[i].add(rfs);
                }
            }
        }



        double[] cooldowns = new double[abilities.length];
        boolean done = false;
        try {
            for(int i = 0; i < abilities.length; i++)
                cooldowns[i] = DataFile.getFloat(name, abilityCastNameString, "cooldown");
            done = true;
        } catch(IllegalArgumentException e) {
        }

        if(!done)
            try {
                for(int i = 0; i < abilities.length; i++)
                    cooldowns[i] = DataFile.getFloat(name, abilityCastNameString, "cooldown_" + abilities[i].getString());
                done = true;
            } catch(IllegalArgumentException e) {
            }

        if(!done)
            try {
                for(int i = 0; i < abilities.length; i++)
                    cooldowns[i] = DataFile.getFloat(name, abilities[i].getString(), "cooldown");
                done = true;
            } catch(IllegalArgumentException e) {
            }

        if(done) {
            for(int i = 0; i < abilities.length; i++)
                cooldowns[i] = Math.max(cooldowns[i], 0.1);

            double modifier = 1;
            try {
                for(int i = 0; i < abilities.length; i++) {
                    modifier = DataFile.getFloat(name, abilityCastNameString, "cooldown_pct_" + abilities[i].getString());
                    cooldowns[i] *= modifier;
                }
            } catch(IllegalArgumentException e) {
            }

            for(int i = 0; i < abilities.length; i++)
                if(abilitiesCasting.contains(abilities[i]))
                    abilityCooldowns[i].add(new AbilityCooldown(CooldownType.NORMAL, cooldowns[i]));
        }
    }

    protected void updateStreamingAbilities(double delta) {

    }

    protected double getMovementDebuff() {
        double m = 1;
        if(abilitiesStreaming)
            m *= abilityStreamMovementDebuff;
        return m;
    }

    protected void move(double delta) {
        double currentFriction = friction;
        if(stunTimer > 0) {
            stunTimer = Math.max(0, stunTimer - delta);
            currentFriction = stunFriction;
        }
        if(stunTimer > 0.1) {
            moveX = 0;
            moveY = 0;
        }

        //accelerate
        if(moveX != 0) {
            xVel += moveX * accel * delta;
        }
        if(moveY != 0) {
            yVel += moveY * accel * delta;
        }


        double debuffedMaxSpeed = maxSpeed * getMovementDebuff();
        double vel = Math.hypot(xVel, yVel);
        double angle = Math.atan2(yVel, xVel);

        //cap max speed
        if(vel > debuffedMaxSpeed && stunTimer <= 0) {
            vel = Math.signum(vel) * Math.min(debuffedMaxSpeed, Math.abs(vel) - accel * delta);
        } else if(moveX == 0 && moveY == 0) {
            vel = Math.signum(vel) * Math.max(0, Math.abs(vel) - accel * currentFriction * delta);
        }


        //assist in turning
        if(moveX != 0 || moveY != 0) {
            double targetAngle = Math.atan2(moveY, moveX);
            double turnAssistAmount = Math.cos(targetAngle) * Math.cos(angle) + Math.sin(targetAngle) * Math.sin(angle);
            //System.out.println(turnAssistAmount);

            double angleDiff = targetAngle - angle;
            if(angleDiff > Math.PI)
                angleDiff -= 2*Math.PI;
            if(angleDiff < -Math.PI)
                angleDiff += 2*Math.PI;

            angle += angleDiff * Math.min(1, turnAssist * turnAssistAmount * delta);
        }
        xVel = vel * Math.cos(angle);
        yVel = vel * Math.sin(angle);
    }

    public void setMove(double moveX, double moveY) {
        this.moveX = moveX;
        this.moveY = moveY;
    }

    public void setTarget(double targetX, double targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setAbilityInput(int index, boolean input) {
        if(index < abilityInputs.length)
            abilityInputs[index] = input;
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public void kill() {
        health = 0;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public boolean abilitiesStreaming() {
        return abilitiesStreaming;
    }
}
