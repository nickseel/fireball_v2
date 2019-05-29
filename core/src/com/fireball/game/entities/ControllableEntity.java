package com.fireball.game.entities;

import com.fireball.game.entities.abilities.Ability;
import com.fireball.game.entities.abilities.SortAbility;
import com.fireball.game.util.DataFile;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public abstract class ControllableEntity extends Entity {
    protected double moveX, moveY;
    protected double targetX, targetY;

    protected Ability[] abilities;
    protected boolean[] prevAbilityInputs;
    protected boolean[] abilityInputs;
    protected double[][] abilityTimers;
    protected double abilityCastTimer, abilityCastTimerMax;
    protected int maxAbilityCombo;
    protected ArrayList<Ability> abilitiesCasting;
    protected String abilityCastNameString;
    protected double abilityCost;
    protected boolean abilitiesStreaming;
    protected double abilityStreamTime, minAbilityStreamTime;
    protected double abilityStreamMovementDebuff;

    protected double maxHealth;
    protected double health;
    protected double accel;
    protected double friction;
    protected double maxSpeed;
    protected double turnAssist;

    public ControllableEntity(Team team, String name, double x, double y, Ability[] abilities, int maxAbilityCombo) {
        super(team, name, x, y);

        this.abilities = abilities;
        prevAbilityInputs = new boolean[abilities.length];
        abilityInputs = new boolean[abilities.length];
        abilityTimers = new double[abilities.length][];
        for(int i = 0; i < abilities.length; i++)
            abilityTimers[i] = new double[] {0, 0};
        this.maxAbilityCombo = maxAbilityCombo;
        abilitiesCasting = new ArrayList<Ability>();
        abilitiesStreaming = false;
        abilityStreamMovementDebuff = 1;
    }

    protected void updateAbilities(double delta) {
        for(int i = 0; i < abilities.length; i++) {
            switch(abilities[i].getCooldownType()) {
                case NORMAL:
                    if(abilityTimers[i][0] < 0) {
                        abilityTimers[i][0] = Math.min(0, abilityTimers[i][0] + delta);

                        if(abilityTimers[i][0] == 0) {
                            prevAbilityInputs[i] = false; //allows for holding down abilities
                        }
                    }
                    break;
                case RING_OBJECTS_DESTROYED:
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
            health -= abilityCost * delta;

            updateStreamingAbilities(delta);

            if(abilityStreamTime >= minAbilityStreamTime /*|| stunTimer > 0*/) {
                for(int i = 0; i < abilities.length; i++) {
                    if(abilitiesCasting.contains(abilities[i])) {
                        if(!abilityInputs[i] /*|| stunTimer > 0*/) {
                            //add to recent abilities graphic

                            for(int j = 0; j < abilities.length; j++) {
                                prevAbilityInputs[j] = false;
                                abilityInputs[j] = false;
                                if(abilitiesCasting.contains(abilities[j])) {
                                    abilityCooldown(j);
                                }
                            }

                            abilitiesStreaming = false;
                            abilitiesCasting.clear();
                        }
                    }
                }
            }
        } else if(abilityCastTimer >= 0 || abilitiesCasting.size() > 0) {
            //check if abilities are casted
            abilityCastTimer -= delta;
            if(abilityCastTimer <= 0 && abilitiesCasting.size() > 0) {
                //cast abilities

                //simplify the name if specified
                DataFile.setCurrentLocation("abilities", name, abilityCastNameString);
                String castName = abilityCastNameString;
                try {
                    castName = DataFile.getString("cast_name");
                } catch(IllegalArgumentException e) {}


                //actually create ability objects here
                System.out.println("cast " + abilityCastNameString + " as " + castName);


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
                    health -= abilityCost;

                    //add to recent abilities graphic

                    for(int i = 0; i < abilities.length; i++) {
                        if(abilitiesCasting.contains(abilities[i])) {
                            abilityCooldown(i);
                        }
                    }
                    abilitiesCasting.clear();
                }
            }
        }
    }

    private boolean shouldBeginCastingAbility(int index) {
        if(abilityInputs[index] && abilityTimers[index][0] == 0 &&
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
                            if(abilityTimers[i][0] != 0) {
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

    protected void abilityCooldown(int index) {
        DataFile.setCurrentLocation("abilities");

        double cooldown = 0;
        boolean done = false;
        try {
            cooldown = DataFile.getFloat(name, abilityCastNameString, "cooldown");
            done = true;
        } catch(IllegalArgumentException e) {}

        if(!done)
            try {
                cooldown = DataFile.getFloat(name, abilityCastNameString, "cooldown_" + abilities[index].getString());
                done = true;
            } catch(IllegalArgumentException e) {}

        if(!done)
            try {
                cooldown = DataFile.getFloat(name, abilities[index].getString(), "cooldown");
                done = true;
            } catch(IllegalArgumentException e) {}

        if(!done)
            System.out.println("COOLDOWN NOT FOUND: " + abilityCastNameString);

        cooldown = Math.max(cooldown, 0.1);

        double modifier = 1;
        try {
            modifier = DataFile.getFloat(name, abilityCastNameString, "cooldown_pct_" + abilities[index].getString());
            cooldown *= modifier;
        } catch(IllegalArgumentException e) {}

        abilityTimers[index][0] = -cooldown;
        abilityTimers[index][1] = -cooldown;
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
        //accelerate
        if(moveX != 0) {
            xVel += moveX * accel * delta;
        }
        if(moveY != 0) {
            yVel += moveY * accel * delta;
        }


        //cap max speed
        double debuffedMaxSpeed = maxSpeed * getMovementDebuff();
        double vel = Math.hypot(xVel, yVel);
        double angle = Math.atan2(yVel, xVel);
        if(vel > debuffedMaxSpeed) {
            vel = signum(vel) * min(debuffedMaxSpeed, abs(vel) - accel * delta);
        } else if(moveX == 0 && moveY == 0) {
            vel = signum(vel) * max(0, abs(vel) - accel * friction * delta);
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
        xVel = vel * cos(angle);
        yVel = vel * sin(angle);
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public void kill() {
        health = 0;
    }
}
