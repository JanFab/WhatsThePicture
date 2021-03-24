package sk.tuke.gamestudio.game.guessthepicture.core;

public class TileField {
    private final int rowCount;
    private final int colCount;
    private int openTiles;
    private final String pictureName;
    private final Tile[][] tiles;
    private FieldState state;

    public TileField(int rowCount, int colCount, String pictureName) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.pictureName = pictureName;
        this.tiles = new Tile[rowCount][colCount];
        this.openTiles = 0;
        this.state = FieldState.PLAYING;
        generate();
    }

    public void generate() {
        for (int rows = 0; rows < this.rowCount; rows++) {
            for (int cols = 0; cols < this.colCount; cols++) {
                this.tiles[rows][cols] = new Tile(false);
            }
        }
    }

    public void openTile(int row, int column) {
        Tile tile = tiles[row][column];
        if (!tile.isOpen()) {
            tile.setOpen(true);
            this.openTiles++;
            if (this.openTiles >= this.rowCount * this.colCount && this.state != FieldState.SOLVED || this.getScore() <= 0) {
                this.state = FieldState.FAILED;
            }
        }
    }

    public boolean guessPictureName(String name) {
        if (name.equals(this.pictureName)) {
            this.state = FieldState.SOLVED;
            return true;
        } else {
            return false;
        }
    }

    public int getScore() {
        return rowCount * colCount * 10 - openTiles * 5;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public int getOpenTiles() {
        return openTiles;
    }

    public String getPictureName() {
        return pictureName;
    }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    public FieldState getState() {
        return state;
    }
}
