package com.fireball.game.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.rooms.collision.CellSlotter;
import com.fireball.game.rooms.tiles.TileMap;
import com.fireball.game.rooms.walls.DestructibleWall;
import com.fireball.game.rooms.walls.Wall;
import com.fireball.game.textures.TextureData;
import com.fireball.game.textures.TextureManager;
import com.fireball.game.views.GameView;

import java.util.ArrayList;

public abstract class Room {
    public static final double CELL_SIZE = 100.0;

    protected GameView parentView;

    protected CellSlotter<Wall> slottedStaticWalls;
    protected CellSlotter<DestructibleWall> slottedDynamicWalls;

    protected TileMap wallTiles, groundTiles;

    protected Wall[] staticWalls;
    protected ArrayList<DestructibleWall> dynamicWalls;
    private Sprite wallSprite;
    /*protected FloatBuffer vertexBuffer;
    protected IntBuffer indexBuffer;
    protected int vertexVbo, vao;*/

    public Room(GameView parentVew) {
        this.parentView = parentVew;

        dynamicWalls = new ArrayList<DestructibleWall>();

        slottedStaticWalls = new CellSlotter<Wall>();
        slottedDynamicWalls = new CellSlotter<DestructibleWall>();

        wallSprite = new Sprite(TextureManager.getTexture(TextureData.TEST_IMAGE));
    }

    protected void updateWallSlotPositions() {
        for(Wall wall: staticWalls) {
            wall.updateSlotPositions(Room.CELL_SIZE);
        }
        for(Wall wall: dynamicWalls) {
            wall.updateSlotPositions(Room.CELL_SIZE);
        }
    }

    protected void slotWalls() {
        slottedStaticWalls.clear();
        slottedStaticWalls.addAll(staticWalls);
        slottedDynamicWalls.clear();
        slottedDynamicWalls.addAll(dynamicWalls);
    }

    public void draw(SpriteBatch batch) {
        for(Wall w: staticWalls) {
            drawWall(w, batch);
        }
        for(Wall w: dynamicWalls) {
            drawWall(w, batch);
        }
    }

    private void drawWall(Wall w, SpriteBatch batch) {
        wallSprite.setOrigin((float)w.getLength()/2, 5);
        wallSprite.setSize((float)w.getLength(), 10);
        wallSprite.setRotation((float)Math.toDegrees(w.getAngle()));
        wallSprite.setCenter((float)w.getCenterX(), (float)w.getCenterY());
        wallSprite.draw(batch);
    }

    public void update(double delta) {
        for(int i = 0; i < dynamicWalls.size(); i++) {
            if(dynamicWalls.get(i).isDestroyed())
                dynamicWalls.remove(i--);
        }
    }

    public Wall[] getStaticWalls() {
        return staticWalls;
    }

    public ArrayList<DestructibleWall> getDynamicWalls() {
        return dynamicWalls;
    }

    public CellSlotter<Wall> getSlottedStaticWalls() {
        return slottedStaticWalls;
    }

    public CellSlotter<DestructibleWall> getSlottedDynamicWalls() {
        return slottedDynamicWalls;
    }


    public static void processRoomJson(RoomData room) {
        FileHandle file = Gdx.files.internal("rooms/raw_json/" + room.getName() + ".json");
        String text = file.readString();
        String[] lines = text.split("\n");

        int width = findInt(lines, "\"width\"", 0, lines.length);
        int height = findInt(lines, "\"height\"", 0, lines.length);


        int groundTilesetLine = findLine(lines, "ground_tileset", 0, lines.length);
        int groundLine = findLine(lines, "\"Ground\"", 0, lines.length);

        int groundTileStart = findInt(lines, "\"firstgid\"", groundTilesetLine-2, groundTilesetLine);
        String[] groundTileString = findString(lines, "\"data\"", groundLine - 4, groundLine).split(" ");
        String groundTileStringFinal = "";
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                groundTileStringFinal += (Integer.parseInt(groundTileString[r*width + c]) - groundTileStart) + ",";
            }
            groundTileStringFinal += "\n";
        }


        int wallTilesetLine = findLine(lines, "wall_tileset", 0, lines.length);
        int wallLine = findLine(lines, "\"Walls\"", 0, lines.length);

        int wallTileStart = findInt(lines, "\"firstgid\"", wallTilesetLine-2, wallTilesetLine);
        String[] wallTileString = findString(lines, "\"data\"", wallLine - 4, wallLine).split(" ");
        String wallTileStringFinal = "";
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                wallTileStringFinal += (Integer.parseInt(wallTileString[r*width + c]) - wallTileStart) + ",";
            }
            wallTileStringFinal += "\n";
        }


        int objectStartLine = findLine(lines, "\"objects\"", 0, lines.length);
        int objectEndLine = findLine(lines, "\"nextobjectid\"", objectStartLine, lines.length);
        int nextObjectLine = objectStartLine;
        ArrayList<RoomJsonEntityData> entityData = new ArrayList<RoomJsonEntityData>();
        while(true) {
            nextObjectLine = findLine(lines, "{", nextObjectLine, objectEndLine);
            if(nextObjectLine == -1)
                break;

            entityData.add(new RoomJsonEntityData(
                    findString(lines, "\"name\"", nextObjectLine, objectEndLine).replaceAll("\"", ""),
                    findString(lines, "\"type\"", nextObjectLine, objectEndLine).replaceAll("\"", ""),
                    findInt(lines, "\"x\"", nextObjectLine, objectEndLine),
                    findInt(lines, "\"y\"", nextObjectLine, objectEndLine),
                    findInt(lines, "\"width\"", nextObjectLine, objectEndLine),
                    findInt(lines, "\"height\"", nextObjectLine, objectEndLine)));
            nextObjectLine++;
        }


        FileHandle outputFile = Gdx.files.local("rooms/final/" + room.getName() + ".txt");
        outputFile.writeString("walls:\n", false);
        outputFile.writeString(wallTileStringFinal, true);
        outputFile.writeString("\nground:\n", true);
        outputFile.writeString(groundTileStringFinal, true);
        outputFile.writeString("\nentities:\n", true);
        for(RoomJsonEntityData entity: entityData) {
            outputFile.writeString(entity.toString(), true);
        }
    }

    private static int findLine(String[] lines, String tag, int start, int end) {
        for(int i = start; i < end; i++) {
            if(lines[i].contains(tag))
                return i;
        }
        return -1;
    }

    private static String getJsonValue(String line, String tag) {
        String s = line;//.replaceAll("\"", "");
        s = s.substring(s.indexOf(tag) + tag.length() + 1).replaceAll(",", "")
                .replaceAll("\\[", "").replaceAll("]", "");
        s = s.substring(0, s.length()-1);
        return s;
    }

    private static int findInt(String[] lines, String tag, int start, int end) {
        return Integer.parseInt(getJsonValue(lines[findLine(lines, tag, start, end)], tag));
    }

    private static String findString(String[] lines, String tag, int start, int end) {
        return getJsonValue(lines[findLine(lines, tag, start, end)], tag);
    }
}
