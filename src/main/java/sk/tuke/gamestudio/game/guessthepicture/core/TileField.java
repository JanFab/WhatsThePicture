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
    }

    public void openTile(int row, int column){
        Tile tile = tiles[row][column];
        if(!tile.isOpen()){
            tile.setOpen(true);
            this.openTiles++;
            if(this.openTiles == row*column){
                this.state = FieldState.FAILED;
            }
        }
    }

    public void guessPictureName(String name){
        if(name.equals(pictureName)){
            this.state = FieldState.SOLVED;
        }
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

    public Tile[][] getTiles() {
        return tiles;
    }

    public FieldState getState() {
        return state;
    }
}
