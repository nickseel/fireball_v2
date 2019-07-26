package com.fireball.game.entities.enemies.ai;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.EntityManager;
import com.fireball.game.util.Util;

public class WalkerAI extends AI {
    private final double minStrafeDistance;
    private final double maxStrafeDistance;
    private final int[] directionsChooseList;
    private final double changeDirectionTimerMin;
    private final double changeDirectionTimerMax;
    private final double changeDirectionTimerWeight;
    private final double minTowardsPlayerWeight;

    private double directionChangeTimer = 0;
    private double directionChangeTimerMax = 0;
    private int currentDirection = 0;

    public WalkerAI(double minStrafeDistance, double maxStrafeDistance, int[] directionsChooseList,
                    double changeDirectionTimerMin, double changeDirectionTimerMax,
                    double changeDirectionTimerWeight, double minTowardsPlayerWeight) {
        this.minStrafeDistance = minStrafeDistance;
        this.maxStrafeDistance = maxStrafeDistance;
        this.directionsChooseList = directionsChooseList;
        this.changeDirectionTimerMin = changeDirectionTimerMin;
        this.changeDirectionTimerMax = changeDirectionTimerMax;
        this.changeDirectionTimerWeight = changeDirectionTimerWeight;
        this.minTowardsPlayerWeight = minTowardsPlayerWeight;
    }

    @Override
    public void run(ControllableEntity entity, double delta) {
        double moveX = 0, moveY = 0;
        Entity player = EntityManager.current.nearestEntity(entity.getX(), entity.getY(), null, "player");

        System.out.println(player);
        if(player != null) {
            double distanceToPlayer = Math.hypot(entity.getX() - player.getX(), entity.getY() - player.getY());
            double angleToPlayer = Math.atan2(entity.getY() - player.getY(), entity.getX() - player.getX());
            System.out.println(angleToPlayer);

            directionChangeTimer += delta;
            if(directionChangeTimer >= directionChangeTimerMax) {
                directionChangeTimer = 0;
                directionChangeTimerMax = Util.mix(changeDirectionTimerMin, changeDirectionTimerMax,
                        Util.weightedRandom(changeDirectionTimerWeight));
                currentDirection = Util.choose(directionsChooseList);
            }

            double strafeWeight = Math.min(Math.max((distanceToPlayer - minStrafeDistance) / (maxStrafeDistance - minStrafeDistance), 0), 1);

            moveX = strafeWeight * Math.abs(currentDirection) * Math.cos(angleToPlayer + ((Math.PI/2) * currentDirection));
            moveY = strafeWeight * Math.abs(currentDirection) * Math.sin(angleToPlayer + ((Math.PI/2) * currentDirection));
            System.out.println("1 " + moveX + " " + moveY);

            moveX += ((1 + minTowardsPlayerWeight) - strafeWeight) * -Math.cos(angleToPlayer);
            moveY += ((1 + minTowardsPlayerWeight) - strafeWeight) * -Math.sin(angleToPlayer);
            System.out.println("2" + moveX + " " + moveY);
        }

        entity.setMove(moveX, moveY);
    }
}
