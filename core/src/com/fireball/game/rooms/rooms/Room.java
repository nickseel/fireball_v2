package com.fireball.game.rooms.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.fireball.game.rooms.collision.CellSlotter;
import com.fireball.game.rooms.rooms.room_objects.SpawnPoint;
import com.fireball.game.rooms.tiles.TileMap;
import com.fireball.game.rooms.collision.DestructibleWall;
import com.fireball.game.rooms.collision.Wall;
import com.fireball.game.textures.TextureData;
import com.fireball.game.textures.TextureManager;
import com.fireball.game.views.GameView;

import java.util.ArrayList;

public class Room {
    public static final double CELL_SIZE = 100.0;

    protected GameView parentView;

    protected CellSlotter<Wall> slottedStaticWalls;
    protected CellSlotter<DestructibleWall> slottedDynamicWalls;

    protected Wall[] staticWalls;
    protected ArrayList<DestructibleWall> dynamicWalls;
    protected TileMap wallTiles, groundTiles;
    protected RoomJsonEntityData[] initialEntities;

    private SpawnPoint spawnPoint;
    private Sprite wallSprite;

    private FrameBuffer buffer;
    private SpriteBatch bufferBatch;

    public Room(GameView parentVew, TileMap wallTiles, TileMap groundTiles, Wall[] staticWalls, RoomJsonEntityData[] initialEntities) {
        this.parentView = parentVew;
        this.wallTiles = wallTiles;
        this.groundTiles = groundTiles;
        this.staticWalls = staticWalls;
        this.initialEntities = initialEntities;

        dynamicWalls = new ArrayList<DestructibleWall>();

        slottedStaticWalls = new CellSlotter<Wall>();
        slottedDynamicWalls = new CellSlotter<DestructibleWall>();

        wallSprite = new Sprite(TextureManager.getTexture(TextureData.TEST_IMAGE));

        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, wallTiles.getWidth(), wallTiles.getHeight(), false);
        buffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        bufferBatch = new SpriteBatch();

        for(RoomJsonEntityData entity: initialEntities) {
            if(entity.getName().equals("spawn point")) {
                spawnPoint = new SpawnPoint(entity.getCenterX(), entity.getCenterY());
            }
        }
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
        batch.draw(buffer.getColorBufferTexture(), 0, buffer.getHeight(), buffer.getWidth(), -buffer.getHeight());

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

        buffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        bufferBatch.begin();
        wallTiles.draw(bufferBatch);
        bufferBatch.end();
        FrameBuffer.unbind();
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

    public SpawnPoint getSpawnPoint() {
        return spawnPoint;
    }


    public static Room fromFile(GameView parentView, RoomData room) {
        FileHandle file = Gdx.files.internal("rooms/final/" + room.getName() + ".txt");
        String text = file.readString();
        String[] lines = text.split("\n");

        int lineIndex = 0;

        while(!lines[lineIndex++].contains("width")) {}
        int width = Integer.parseInt(lines[lineIndex]);

        while(!lines[lineIndex++].contains("height")) {}
        int height = Integer.parseInt(lines[lineIndex]);

        while(!lines[lineIndex++].contains("walls")) {}
        int[][] wallTiles = new int[height][width];
        for(int r = 0; r < height; r++) {
            String[] lineTiles = lines[lineIndex].split(",");
            for(int c = 0; c < width; c++) {
                wallTiles[r][c] = Integer.parseInt(lineTiles[c]);
            }
            lineIndex++;
        }

        while(!lines[lineIndex++].contains("ground")) {}
        int[][] groundTiles = new int[height][width];
        for(int r = 0; r < height; r++) {
            String[] lineTiles = lines[lineIndex].split(",");
            for(int c = 0; c < width; c++) {
                groundTiles[r][c] = Integer.parseInt(lineTiles[c]);
            }
            lineIndex++;
        }

        while(!lines[lineIndex++].contains("entities")) {}
        int numEntities = Integer.parseInt(lines[lineIndex-1].substring(10, lines[lineIndex-1].length()-2));
        RoomJsonEntityData[] initialEntities = new RoomJsonEntityData[numEntities];
        for(int i = 0; i < numEntities; i++) {
            initialEntities[i] = new RoomJsonEntityData(
                    lines[lineIndex++].substring(8),
                    lines[lineIndex++].substring(8),
                    Integer.parseInt(lines[lineIndex++].substring(8)),
                    Integer.parseInt(lines[lineIndex++].substring(8)),
                    Integer.parseInt(lines[lineIndex++].substring(8)),
                    Integer.parseInt(lines[lineIndex++].substring(8))
            );
        }

        return new Room(parentView,
                new TileMap(wallTiles, TextureData.WALLS),
                new TileMap(groundTiles, TextureData.GROUND_BIG),
                new Wall[0], initialEntities);
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
        outputFile.writeString("width:\n" + width + "\n", false);
        outputFile.writeString("\nheight:\n" + height + "\n", true);
        outputFile.writeString("\nwalls:\n", true);
        outputFile.writeString(wallTileStringFinal, true);
        outputFile.writeString("\nground:\n", true);
        outputFile.writeString(groundTileStringFinal, true);
        outputFile.writeString("\nentities (" + entityData.size() + "):\n", true);
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
