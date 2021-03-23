package sk.tuke.gamestudio.game.guessthepicture.core;

public class Tile {
    private boolean open;

    public Tile(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
