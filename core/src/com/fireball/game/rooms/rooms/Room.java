package com.fireball.game.rooms.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.fireball.game.rooms.collision.CellSlotter;
import com.fireball.game.rooms.rooms.room_objects.SpawnPoint;
import com.fireball.game.rooms.tiles.TileMap;
import com.fireball.game.rooms.collision.DestructibleWall;
import com.fireball.game.rooms.collision.Wall;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.util.Util;
import com.fireball.game.views.GameView;

import java.util.ArrayList;
import java.util.Random;

public class Room {
    public static final double CELL_SIZE = 100.0;
    private static final double WALL_INSET_TOP = 0.5;
    private static final double WALL_INSET_LEFT = 0.25;
    private static final double WALL_INSET_RIGHT = 0.25;
    private static final double WALL_INSET_BOT = 0.125;
    private static final int GROUND_CRACKS_NUM_SPRITES = 6;
    private static final double GROUND_CRACK_RATIO = 1;
    private static final double GROUND_CRACK_FREQUENCY_X = 0.055;
    private static final double GROUND_CRACK_FREQUENCY_Y = GROUND_CRACK_FREQUENCY_X * GROUND_CRACK_RATIO;
    private static final double GROUND_CRACK_POSITION_VARIATION = 0.35;
    private static final double GROUND_CRACK_SCALE_MIN = 0.6;
    private static final double GROUND_CRACK_SCALE_MAX = 0.85;

    protected Random groundGenRandom;

    protected GameView parentView;

    protected CellSlotter<Wall> slottedStaticWalls;
    protected CellSlotter<DestructibleWall> slottedDynamicWalls;

    protected Wall[] staticWalls;
    protected ArrayList<DestructibleWall> dynamicWalls;
    protected TileMap wallTiles, groundTiles;
    protected RoomEntityData[] initialEntities;

    private SpawnPoint spawnPoint;

    private Sprite wallSprite;
    private FrameBuffer wallBuffer, groundBuffer;
    private SpriteBatch bufferBatch;

    public Room(GameView parentVew, TileMap wallTiles, TileMap groundTiles, Wall[] staticWalls, RoomEntityData[] initialEntities) {
        this.parentView = parentVew;
        this.wallTiles = wallTiles;
        this.groundTiles = groundTiles;
        this.staticWalls = staticWalls;
        this.initialEntities = initialEntities;

        dynamicWalls = new ArrayList<DestructibleWall>();

        slottedStaticWalls = new CellSlotter<Wall>();
        slottedDynamicWalls = new CellSlotter<DestructibleWall>();

        wallSprite = new Sprite(TextureManager.getColorTexture(Color.BLACK));

        wallBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, wallTiles.getWidth(), wallTiles.getHeight(), false);
        wallBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        groundBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, wallTiles.getWidth(), wallTiles.getHeight(), false);
        groundBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        bufferBatch = new SpriteBatch();

        for(RoomEntityData entity: initialEntities) {
            if(entity.getName().equals("spawn point")) {
                spawnPoint = new SpawnPoint(entity.getCenterX(), entity.getCenterY());
            }
        }

        groundGenRandom = new Random();

        updateWallSlotPositions();
        slotWalls();
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

    public void drawWalls(SpriteBatch batch) {
        batch.draw(wallBuffer.getColorBufferTexture(), 0, wallBuffer.getHeight(), wallBuffer.getWidth(), -wallBuffer.getHeight());

        for(Wall w: staticWalls) {
            drawWall(w, batch);
        }
        for(Wall w: dynamicWalls) {
            drawWall(w, batch);
        }
    }

    public void drawGround(SpriteBatch batch) {
        batch.draw(groundBuffer.getColorBufferTexture(), 0, groundBuffer.getHeight(), groundBuffer.getWidth(), -groundBuffer.getHeight());
    }

    private void drawWall(Wall w, SpriteBatch batch) {
        wallSprite.setOrigin((float)w.getLength()/2, 1);
        wallSprite.setSize((float)w.getLength(), 2);
        wallSprite.setRotation((float)Math.toDegrees(w.getAngle()));
        wallSprite.setCenter((float)w.getCenterX(), (float)w.getCenterY());
        wallSprite.draw(batch);
    }

    public void update(double delta) {
        for(int i = 0; i < dynamicWalls.size(); i++) {
            if(dynamicWalls.get(i).isDestroyed())
                dynamicWalls.remove(i--);
        }

        wallBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        bufferBatch.begin();
        wallTiles.draw(bufferBatch);
        bufferBatch.end();
        FrameBuffer.unbind();


        groundBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        bufferBatch.begin();
        bufferBatch.setBlendFunctionSeparate(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA, Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        groundTiles.draw(bufferBatch);
        int numCracksX = (int)(GROUND_CRACK_FREQUENCY_X * groundTiles.getWidth());
        int numCracksY = (int)(GROUND_CRACK_FREQUENCY_Y * groundTiles.getHeight());
        groundGenRandom.setSeed(this.hashCode());
        for(int x = 0; x < numCracksX; x++) {
            for(int y = 0; y < numCracksY; y++) {
                TextureRegion textureRegion = TextureManager.getTextureRegion(TextureData.CRACK, (int)(groundGenRandom.nextDouble() * GROUND_CRACKS_NUM_SPRITES));
                float xpos = (float)((groundGenRandom.nextDouble()-0.5) * GROUND_CRACK_POSITION_VARIATION + ((x * groundTiles.getWidth()) / numCracksX));
                float ypos = (float)((groundGenRandom.nextDouble()-0.5) * GROUND_CRACK_POSITION_VARIATION + ((y * groundTiles.getHeight()) / numCracksY));
                float width = textureRegion.getRegionWidth() * (float)(Util.mix(GROUND_CRACK_SCALE_MIN, GROUND_CRACK_SCALE_MAX, groundGenRandom.nextDouble()));
                float height = textureRegion.getRegionHeight() * (float)(Util.mix(GROUND_CRACK_SCALE_MIN, GROUND_CRACK_SCALE_MAX, groundGenRandom.nextDouble()) / GROUND_CRACK_RATIO);
                float rotation = groundGenRandom.nextFloat() * (float)(Math.PI * 2);
                float alpha = (float)(Math.pow(groundGenRandom.nextDouble(), 3) * 0.30 + 0.3);
                bufferBatch.setColor(1, 1, 1, alpha);
                bufferBatch.draw(textureRegion, xpos-width/2, ypos-height/2, width/2, height/2, width, height, 1, 1, rotation);
            }
        }
        bufferBatch.setColor(Color.WHITE);
        bufferBatch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
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

    public TileMap getWallTiles() {
        return wallTiles;
    }

    public TileMap getGroundTiles() {
        return groundTiles;
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


        while(!lines[lineIndex++].contains("wall tiles")) {}
        int[][] wallTiles = new int[height][width];
        for(int r = 0; r < height; r++) {
            String[] lineTiles = lines[lineIndex].split(",");
            for(int c = 0; c < width; c++) {
                wallTiles[r][c] = Integer.parseInt(lineTiles[c]);
            }
            lineIndex++;
        }
        TileMap wallTileMap = new TileMap(wallTiles, TextureData.WALLS_GRAY);


        while(!lines[lineIndex++].contains("ground tiles")) {}
        int[][] groundTiles = new int[height][width];
        for(int r = 0; r < height; r++) {
            String[] lineTiles = lines[lineIndex].split(",");
            for(int c = 0; c < width; c++) {
                groundTiles[r][c] = Integer.parseInt(lineTiles[c]);
            }
            lineIndex++;
        }
        TileMap groundTileMap = new TileMap(groundTiles, TextureData.GROUND_BIG);


        while(!lines[lineIndex++].contains("static walls")) {}
        int numWalls = Integer.parseInt(lines[lineIndex-1].substring(14, lines[lineIndex-1].length()-2));
        Wall[] walls = new Wall[numWalls];
        for(int i = 0; i < numWalls; i++) {
            String[] wallData = lines[lineIndex].split(" ");
            double x1 = Double.parseDouble(wallData[0]) * wallTileMap.getTileWidth();
            double y1 = Double.parseDouble(wallData[1]) * wallTileMap.getTileHeight();
            double x2 = Double.parseDouble(wallData[2]) * wallTileMap.getTileWidth();
            double y2 = Double.parseDouble(wallData[3]) * wallTileMap.getTileHeight();
            double dir = Double.parseDouble(wallData[4]);
            if(dir == 1) {
                walls[i] = new Wall(
                        Math.min(x1, x2),
                        Math.min(y1, y2),
                        Math.max(x1, x2),
                        Math.max(y1, y2)
                );
            } else if(dir == -1) {
                walls[i] = new Wall(
                        Math.max(x1, x2),
                        Math.max(y1, y2),
                        Math.min(x1, x2),
                        Math.min(y1, y2)
                );
            }
            lineIndex++;
        }


        while(!lines[lineIndex++].contains("entities")) {}
        int numEntities = Integer.parseInt(lines[lineIndex-1].substring(10, lines[lineIndex-1].length()-2));
        RoomEntityData[] initialEntities = new RoomEntityData[numEntities];
        for(int i = 0; i < numEntities; i++) {
            initialEntities[i] = new RoomEntityData(
                    lines[lineIndex++].substring(8),
                    lines[lineIndex++].substring(8),
                    Integer.parseInt(lines[lineIndex++].substring(8)),
                    Integer.parseInt(lines[lineIndex++].substring(8)),
                    Integer.parseInt(lines[lineIndex++].substring(8)),
                    Integer.parseInt(lines[lineIndex++].substring(8))
            );
        }

        return new Room(parentView,
                wallTileMap,
                groundTileMap,
                walls,
                initialEntities);
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
        boolean[][] wallTiles = new boolean[height][width];
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                wallTileStringFinal += (Integer.parseInt(wallTileString[r*width + c]) - wallTileStart) + ",";
                wallTiles[r][c] = (Integer.parseInt(wallTileString[r*width + c]) - wallTileStart) > 0;
            }
            wallTileStringFinal += "\n";
        }


        ArrayList<double[]> walls = new ArrayList<double[]>();
        for(int r = 1; r < height-1; r++) {
            for(int c = 1; c < width-1; c++) {
                if(wallTiles[r][c]) {
                    if(!wallTiles[r][c + 1])
                        walls.add(new double[] {c+1, r, c+1, r+1, 1, 0, 0, 0, 0, 0, 0});
                    if(!wallTiles[r][c - 1])
                        walls.add(new double[] {c, r, c, r+1, -1, 0, 0, 0, 0, 0, 0});
                    if(!wallTiles[r + 1][c])
                        walls.add(new double[] {c, r+1, c+1, r+1, -1, 0, 0, 0, 0, 0, 0});
                    if(!wallTiles[r - 1][c])
                        walls.add(new double[] {c, r, c+1, r, 1, 0, 0, 0, 0, 0, 0});
                }
            }
        }
        while(true) {
            boolean combined = false;
            for(int i = 0; i < walls.size(); i++) {
                for(int j = i+1; j < walls.size(); j++) {
                    double[] wall1 = walls.get(i);
                    double[] wall2 = walls.get(j);
                    if((wall1[0] == wall2[0] && wall1[1] == wall2[1]) ||
                            (wall1[2] == wall2[0] && wall1[3] == wall2[1]) ||
                            (wall1[0] == wall2[2] && wall1[1] == wall2[3]) ||
                            (wall1[2] == wall2[2] && wall1[3] == wall2[3])) {
                        if(((wall1[0] == wall1[2] && wall2[0] == wall2[2]) ||
                                (wall1[1] == wall1[3] && wall2[1] == wall2[3]))
                                && wall1[4] == wall2[4]) {
                            double[] combinedWall = new double[] {
                                    Math.min(wall1[0], Math.min(wall1[2], Math.min(wall2[0], wall2[2]))),
                                    Math.min(wall1[1], Math.min(wall1[3], Math.min(wall2[1], wall2[3]))),
                                    Math.max(wall1[0], Math.max(wall1[2], Math.max(wall2[0], wall2[2]))),
                                    Math.max(wall1[1], Math.max(wall1[3], Math.max(wall2[1], wall2[3]))),
                                    wall1[4], 0, 0, 0, 0, 0, 0
                            };
                            walls.remove(wall1);
                            walls.remove(wall2);
                            walls.add(combinedWall);
                            combined = true;
                            break;
                        }
                    }
                }
                if(combined)
                    break;
            }

            if(!combined)
                break;
        }
        while(true) {
            boolean modified = false;
            for(int i = 0; i < walls.size(); i++) {
                double[] wall1 = walls.get(i);
                if(wall1[5] == 0 || wall1[6] == 0) {
                    modified = true;
                    int connectPoint1;
                    if(wall1[5] == 0) {
                        connectPoint1 = 0;
                        wall1[5] = 1;
                    } else {
                        connectPoint1 = 1;
                        wall1[6] = 1;
                    }
                    double modifyX = wall1[connectPoint1*2];
                    double modifyY = wall1[connectPoint1*2+1];

                    for(int j = 0; j < walls.size(); j++) {
                        if(i != j) {
                            double[] wall2 = walls.get(j);
                            int connectPoint2 = -1;
                            if(wall2[0] == modifyX && wall2[1] == modifyY && wall2[5] == 0) {
                                connectPoint2 = 0;
                                wall2[5] = 1;
                            } else if(wall2[2] == modifyX && wall2[3] == modifyY && wall2[6] == 0) {
                                connectPoint2 = 1;
                                wall2[6] = 1;
                            }
                            if(connectPoint2 != -1) {
                                boolean up, left;
                                if(wall1[2] == wall1[0]) {
                                    //wall1 horizontal, wall2 vertical
                                    left = (wall1[4] == 1);
                                    up = (wall2[4] == -1);
                                } else {
                                    //wall2 vertical, wall1 horizontal
                                    left = (wall2[4] == 1);
                                    up = (wall1[4] == -1);
                                }
                                if(left) {
                                    //left
                                    wall1[connectPoint1*2+7] = wall1[connectPoint1*2] - WALL_INSET_LEFT;
                                    wall2[connectPoint2*2+7] = wall2[connectPoint2*2] - WALL_INSET_LEFT;
                                } else {
                                    //right
                                    wall1[connectPoint1*2+7] = wall1[connectPoint1*2] + WALL_INSET_RIGHT;
                                    wall2[connectPoint2*2+7] = wall2[connectPoint2*2] + WALL_INSET_RIGHT;
                                }
                                if(up) {
                                    //up
                                    wall1[connectPoint1*2+8] = wall1[connectPoint1*2+1] - WALL_INSET_TOP;
                                    wall2[connectPoint2*2+8] = wall2[connectPoint2*2+1] - WALL_INSET_TOP;
                                } else {
                                    //down
                                    wall1[connectPoint1*2+8] = wall1[connectPoint1*2+1] + WALL_INSET_BOT;
                                    wall2[connectPoint2*2+8] = wall2[connectPoint2*2+1] + WALL_INSET_BOT;
                                }
                                break;
                            }
                        }
                    }
                }
            }

            if(!modified)
                break;
        }


        int objectStartLine = findLine(lines, "\"objects\"", 0, lines.length);
        int objectEndLine = findLine(lines, "\"nextobjectid\"", objectStartLine, lines.length);
        int nextObjectLine = objectStartLine;
        ArrayList<RoomEntityData> entityData = new ArrayList<RoomEntityData>();
        while(true) {
            nextObjectLine = findLine(lines, "{", nextObjectLine, objectEndLine);
            if(nextObjectLine == -1)
                break;

            entityData.add(new RoomEntityData(
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
        outputFile.writeString("\nwall tiles:\n", true);
        outputFile.writeString(wallTileStringFinal, true);
        outputFile.writeString("\nground tiles:\n", true);
        outputFile.writeString(groundTileStringFinal, true);
        outputFile.writeString("\nstatic walls (" + walls.size() + "):\n", true);
        for(double[] wall: walls)
            outputFile.writeString(wall[7] + " " + wall[8] + " " + wall[9] + " " + wall[10] + " " + wall[4] + "\n", true);
        outputFile.writeString("\nentities (" + entityData.size() + "):\n", true);
        for(RoomEntityData entity: entityData)
            outputFile.writeString(entity.toString(), true);
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
