package com.fireball.game.entities.projectiles;

import com.fireball.game.entities.Entity;
import com.fireball.game.entities.Team;

public abstract class Projectile extends Entity {
    //protected WeaponController source;
    //protected Weapon weaponSource;

    //inherited: protected Team team;
    //inherited: protected double x, y;
    //inherited: protected double xVel, yVel;
    //inherited: protected double nextX, nextY;
    //inherited: protected BodyHitbox[] bodyHitboxes = new BodyHitbox[0];
    //inherited: protected DamagerHitbox[] damagerHitboxes = new DamagerHitbox[0];
    //inherited: protected double terrainCollisionRadius = -1;

    public Projectile(Team team, String name, double x, double y) {
        super(team, name, x, y);
    }

    /*public Projectile(WeaponController source, Weapon weaponSource) {
        super(source.getTeam());
        this.source = source;
        this.weaponSource = weaponSource;
    }*/

    //public abstract void
}
