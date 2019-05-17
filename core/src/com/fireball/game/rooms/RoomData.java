package com.fireball.game.rooms;

public enum RoomData {
    DEBUG ("debug");

    private String name;
    RoomData(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public static void processAllRooms() {
        for(RoomData room: values())
            Room.processRoomJson(room);
    }
}
