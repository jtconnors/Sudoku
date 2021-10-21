
package com.jtconnors.sudokufx2;

import javafx.stage.Stage;

public class Globals {
    public static final int NROWS = 9;
    public static final int NCOLUMNS = 9;
    public static final double BOARD_AREA_WIDTH_UNSCALED = 560f;
    public static final double ICON_SIZE = 50f;
    public static final int NUM_HINTS_EASY = 34;
    public static final int NUM_HINTS_HARD = 26;

    public static final String IMAGE_FILES_PREFIX =
            //"file:src/main/java/sudokufx2/images/";
            "com/jtconnors/sudokufx2/images/";
    public static ParentWithKeyTraversal lastFocused;

    public static Stage sudokuStage;
}
