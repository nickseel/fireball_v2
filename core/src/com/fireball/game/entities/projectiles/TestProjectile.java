package com.fireball.game.entities.projectiles;

public class TestProjectile {//} extends Projectile {
    /*private TextureRegion textureRegion;
    private float animationTimer = 0;

    private DamagerHitbox hitbox;

    private boolean alive = true;

    private final double damage;
    private final double speed;
    private final double angle;
    private final double radius;
    private final double knockback;

    //inherited: protected Entity source;
    //inherited: protected Weapon weaponSource;
    //inherited: protected Team team;
    //inherited: protected double x, y;
    //inherited: protected double xVel, yVel;
    //inherited: protected double nextX, nextY;
    //inherited: protected BodyHitbox[] bodyHitboxes = new BodyHitbox[0];
    //inherited: protected DamagerHitbox[] damagerHitboxes = new DamagerHitbox[0];
    //inherited: protected double terrainCollisionRadius = -1;

    public TestProjectile(WeaponController source, Weapon weaponSource, double x, double y, double dmg, double spd, double ang, double rad, double kbck) {
        super(source, weaponSource);
        this.x = x;
        this.y = y;
        this.damage = dmg;
        this.speed = spd;
        this.angle = ang;
        this.radius = rad;
        this.knockback = kbck;
        this.terrainCollisionRadius = radius;

        textureRegion = TextureManager.getTextureRegion(TextureData.SHAPES, 8);

        hitbox = new DamagerHitbox(this, team, radius) {
            @Override
            public void damage(BodyHitbox other) {
                alive = false;

                double collisionAngle = Math.atan2(other.getY() - y, other.getX() - x);
                double knockbackAngle = (angle + collisionAngle) / 2;
                other.takeDamage(damage, knockback, knockbackAngle);
            }
        };
        damagerHitboxes = new DamagerHitbox[] {hitbox};
        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        xVel = speed * Math.cos(angle);
        yVel = speed * Math.sin(angle);

        nextX = x + xVel * delta;
        nextY = y + yVel * delta;
    }

    @Override
    public void updatePost(double delta) {
        x = nextX;
        y = nextY;

        animationTimer += delta;

        hitbox.setPosition(x, y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        double textureRadius = radius * 0.8;
        batch.setColor(Color.BLACK);
        batch.draw(textureRegion,
                (float)(x-textureRadius),
                (float)(y-textureRadius),
                (float)textureRadius,
                (float)textureRadius,
                (float)textureRadius*2,
                (float)textureRadius*2,
                1f,
                1f,
                0);
    }

    @Override
    public void eventTerrainCollision(double angle) {
        alive = false;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void kill() {
        alive = false;
    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - radius) / slotSize);
        slotMaxX = (int)floor((x + radius) / slotSize);
        slotMinY = (int)floor((y - radius) / slotSize);
        slotMaxY = (int)floor((y + radius) / slotSize);
    }*/
}
