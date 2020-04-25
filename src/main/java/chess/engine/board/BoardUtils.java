package chess.engine.board;

public class BoardUtils {
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW = initRow(1);
    public static final boolean[] THIRD_ROW = initRow(2);
    public static final boolean[] FOURTH_ROW = initRow(3);
    public static final boolean[] FIFTH_ROW = initRow(4);
    public static final boolean[] SIXTH_ROW = initRow(5);
    public static final boolean[] SEVENTH_ROW = initRow(6);
    public static final boolean[] EIGHTH_ROW = initRow(7);

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private BoardUtils() {
        throw new RuntimeException("You can not instantiate me!");
    }

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUM_TILES];
        while(columnNumber < NUM_TILES) {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        }
        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES];
        for (int i = 0; i < NUM_TILES_PER_ROW; i++) {
            row[rowNumber * NUM_TILES_PER_ROW + i] = true;
        }
        return row;
    }

    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }
}
