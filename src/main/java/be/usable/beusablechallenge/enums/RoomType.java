package be.usable.beusablechallenge.enums;


public enum RoomType {
    PREMIUM("Premium"),
    ECONOMY("Economy");

    private final String name;

    RoomType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
