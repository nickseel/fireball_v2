package com.fireball.game.rooms.collision;

import com.fireball.game.rooms.collision.Slottable;
import com.fireball.game.entities.Entity;

import static java.lang.Math.*;

public class Wall extends Slottable {
    private double x1, y1, x2, y2;
    private double length;
    private double normalX, normalY, inLineX, inLineY, determinant;
    private double invMat11, invMat21, invMat12, invMat22;

    public Wall(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        calculateMatrices();
    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor(min(x1, x2) / slotSize);
        slotMaxX = (int)floor(max(x1, x2) / slotSize);
        slotMinY = (int)floor(min(y1, y2) / slotSize);
        slotMaxY = (int)floor(max(y1, y2) / slotSize);
    }

    private void calculateMatrices() {
        length = sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));

        inLineX = (x2-x1)/-length;
        inLineY = (y2-y1)/-length;
        normalX = inLineY;
        normalY = -inLineX;

        determinant = inLineX*normalY - normalX*inLineY;

        invMat11 = normalY / determinant;
        invMat21 = -normalX / determinant;
        invMat12 = -inLineY / determinant;
        invMat22 = inLineX / determinant;
    }

    public boolean collide(Entity entity) {
        double x = entity.getX();
        double y = entity.getY();
        double xVel = entity.getXVel();
        double yVel = entity.getYVel();
        double nextX = entity.getNextX();
        double nextY = entity.getNextY();
        double radius = entity.getTerrainCollisionRadius();

        double offX = x1 - x;
        double offY = y1 - y;
        double offNextX = x1 - nextX;
        double offNextY = y1 - nextY;

        double linePosition = invMat11 * offX + invMat12 * offY;
        double distanceToLine = invMat21 * offX + invMat22 * offY;

        double nextLinePosition = invMat11 * offNextX + invMat12 * offNextY;
        double nextDistanceToLine = invMat21 * offNextX + invMat22 * offNextY;

        if(linePosition > 0 && linePosition < length &&
                distanceToLine >= 0 && nextDistanceToLine < radius) {
            //wall collision
            double newDistanceToLine = radius + 0.01;
            double newNextX = x1 - (newDistanceToLine * normalX + nextLinePosition * inLineX);
            double newNextY = y1 - (newDistanceToLine * normalY + nextLinePosition * inLineY);
            entity.setNextPosition(newNextX, newNextY);

            double inLineVel = xVel * invMat11 + yVel * invMat21;
            double newXVel = inLineVel * inLineX;
            double newYVel = inLineVel * inLineY;
            entity.setVelocity(newXVel, newYVel);

            return true;
        } else {
            //no wall collision, check for endpoint collision

            double distanceToPoint = sqrt(offX * offX + offY * offY);
            double nextDistanceToPoint = sqrt(offNextX * offNextX + offNextY * offNextY);

            if (distanceToPoint >= 0 && nextDistanceToPoint < radius) {
                //endpoint collision
                double pointNormalX = offNextX / nextDistanceToPoint;
                double pointNormalY = offNextY / nextDistanceToPoint;
                //double pointInLineX = pointNormalX;
                //double pointInLineY = -pointNormalY;

                double newDistanceToPoint = radius + 0.01;
                double newNextX = x1 - newDistanceToPoint * pointNormalX;
                double newNextY = y1 - newDistanceToPoint * pointNormalY;
                entity.setNextPosition(newNextX, newNextY);

                return true;
            }
        }

        return false;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getCenterX() {
        return (x1 + x2) / 2;
    }

    public double getCenterY() {
        return (y1 + y2) / 2;
    }

    public double getLength() {
        return length;
    }

    public double getAngle() {
        return Math.atan2(y2 - y1, x2 - x1);
    }
}
