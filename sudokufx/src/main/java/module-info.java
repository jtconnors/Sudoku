module sudokufx {
    requires javafx.base;
    requires javafx.graphics;
    requires playsudoku;

    exports com.jtconnors.sudokufx2;

    opens com.jtconnors.sudokufx2;
    opens com.jtconnors.sudokufx2.images;
}
