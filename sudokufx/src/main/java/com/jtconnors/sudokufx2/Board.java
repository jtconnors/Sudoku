
package com.jtconnors.sudokufx2;

import java.util.ArrayList;
import net.sourceforge.playsudoku.*;
import static com.jtconnors.sudokufx2.Globals.NROWS;
import static com.jtconnors.sudokufx2.Globals.NCOLUMNS;

public class Board {

    /*
     * Internal representation of each space on the Sudoku Board.  The
     * structure draws its roots from the original JavaFX Script version,
     * which does not support multi-dimensional arrays.  For the Java version
     * this could be updated.
     */
    private final Space[] spaces;
    private final Space[][] rowGrouping = new Space[NROWS][NCOLUMNS];
    private final Space[][] columnGrouping = new Space[NROWS][NCOLUMNS];
    private final Space[][] regionGrouping = new Space[NROWS][NCOLUMNS];

    /*
     * Constructor initializes each Space object on the Board
     */
    public Board() {
        spaces = new Space[NROWS * NCOLUMNS];
        for (int r=0; r<NROWS; r++) {
            for (int c=0; c<NCOLUMNS; c++) {
                Space space = new Space(r, c, getRegion(r, c));
                spaces[r * NCOLUMNS + c] = space;
                rowGrouping[r][c] = space;
                columnGrouping[c][r] = space;
                regionGrouping[space.getRegion()][getRegionIndex(r, c)] = space;
            }
        }
    }

    public static int getIndex(int row, int column) {
        return (row * NCOLUMNS) + column;
    }

    public Space getSpace(int row, int column) {
        return spaces[getIndex(row, column)];
    }

    public int getNumberRaw(int row, int column) {
        Space space = getSpace(row, column);
        return space.getNumberRaw();
    }

    public int getNumberExternal(int row, int column) {
        Space space = getSpace(row, column);
        return space.getNumberExternal();
    }

    public void setNumberEditable(int row, int column, int number) {
        Space space = getSpace(row, column);
        space.setNumberEditable(number);
    }

    public void setNumberUnEditable(int row, int column, int number) {
        Space space = getSpace(row, column);
        space.setNumberUnEditable(number);
    }

    /*
     * Given a (row, col) coordinate, calculate which region this space
     * belongs to on the Sudoku Board. There are 9 reigons on the board,
     * numbered from 0..8 starting from the upper left, working left to right
     * then top to bottom.
     */
    public static int getRegion(int row, int column) {
        if (row <= 2 ) {
            if (column <= 2) { return 0; }
            else if (column <= 5) { return 1; }
            else { return 2; }
        }
        else if (row <= 5) {
            if (column <= 2) { return 3; }
            else if (column <= 5) { return 4; }
            else { return 5; }
        }
        else {
            if (column <= 2) { return 6; }
            else if (column <= 5) { return 7; }
            else { return 8; }
        }
    }

    /*
     * Given a (row, col) coordinate, calculate within the region what index
     * into the regionGrouping array this space belongs.  Moving from left to
     * right then top to bottom, the upper left space would be index 0,
     * while the bottom right space would be 8.
     */
    private static int getRegionIndex(int row, int column) {
       return ((row % 3) *3) + (column % 3);
    }

    /*
     * Return the sequences of Spaces that make up a row of the
     * Sudoku Board.
     */
    public Space[] getRowGrouping(Space space) {
        return(rowGrouping[space.getRow()]);
    }

    /*
     * Return the sequences of Spaces that make up a column of the
     * Sudoku Board.
     */
    public Space[] getColumnGrouping(Space space) {
        return(columnGrouping[space.getColumn()]);
    }

    /*
     * Return the sequences of Spaces that make up a region of the
     * Sudoku Board.
     */
    public Space[] getRegionGrouping(Space space) {
        return(regionGrouping[space.getRegion()]);
    }

    /*
     * Check to see if spaces on the board have duplicate values in their
     * respective row, colummn and region.
     *
     * Arguments:
     *     completeCheck (Boolean) - Flag to check all spaces, typically needed
     *         only at board initialization.  During game, completeCheck
     *         should be set to false, to avoid checking unnecessary
     *         (i.e. unEditable) spaces.
     *
     * Return value:
     *     A sequence containing the spaces that are in conflict.  It's OK
     *     if the same space shows up twice in this sequence.
     *     If there are no conflicts return null.
     */
    public ArrayList<Space> getConflicts(boolean completeCheck) {
        ArrayList<Space> conflicts = new ArrayList<>();
        for (Space space : spaces) {
            boolean spaceConflict = false;
            int number = space.getNumberExternal();
            /*
             *  Skip the check for the current space if any of these
             *  conditions are met
             */
            if (number == 0) { continue; }         // blank space
            if (space.isConflict()) { continue; }  // already a conflict
            if ((!completeCheck) && (!space.isEditable())) {
                continue;                          // non-editable space
            }
            // Check against Row
            for (Space rowSpace : getRowGrouping(space)) {
                if (rowSpace.getColumn() == space.getColumn()) {
                    continue;  // skip - comparing against itself
                }
                if (rowSpace.getNumberExternal() == number) {
                    conflicts.add(getSpace(rowSpace.getRow(),
                            rowSpace.getColumn()));
                    spaceConflict = true;
                }
            }
            // Check against Column
            for (Space columnSpace : getColumnGrouping(space)) {
                if (columnSpace.getRow() == space.getRow()) {
                    continue;  // skip - comparing against itself
                }
                if (columnSpace.getNumberExternal() == number) {
                    conflicts.add(getSpace(columnSpace.getRow(),
                            columnSpace.getColumn()));
                    spaceConflict = true;
                }
            }
            // Check against Region
            for (Space regionSpace : getRegionGrouping(space)) {
                if ((regionSpace.getRow() == space.getRow()) &&
                    (regionSpace.getColumn() == space.getColumn())) {
                    continue;  // skip - comparing against itself
                }
                if (regionSpace.getNumberExternal() == number) {
                    conflicts.add(getSpace(regionSpace.getRow(),
                            regionSpace.getColumn()));
                    spaceConflict = true;
                }
            }
            if (spaceConflict) {
                conflicts.add(space);
            }
        }
        return conflicts;
    }

    public ArrayList<Space> getConflictsOptimized() {
        return getConflicts(false);
    }

    public void clearConflicts() {
        for (Space space : spaces) {
            space.clearConflict();
        }
    }

    /*
     * Use the SudokuGenerator code found at sourceforge.net to create a
     * new Puzzle.  Written in Java, the SudokuGenerator requires a small
     * amount of translation code to move the data structure over to a suitable
     * JavaFX form.  The sudokuGrid.returnGridSequence() method performs
     * that task.
     *
     * Arguments:
     *    numHints:  Determines the Number of initial spaces (hints)
     *               that will be displayed at startup.  The larger
     *               this number is, the easier the puzzle is to solve.
     */
    public void newPuzzle(int numHints) throws Exception {
        /*
         * Call SudokuGenerator Java code to generate new puzzle.
         */
        SudokuGenerator sudokuGenerator = new SudokuGenerator();
        sudokuGenerator.generatePuzzle(numHints,
                GV.NumDistributuon.evenlyFilled3x3Square3 );
        SudokuGrid sudokuGrid = sudokuGenerator.getGrid();
        /*
         * convert results to a format suitable for JavaFX
         */
        int[] puzzle = sudokuGrid.returnGridSequence();

        if (puzzle.length != spaces.length) {
            throw new Exception("SudokuGenerator puzzle incompatible");
        }
        /*
         * Populate our spaces with the Integer sequence returned by
         * the sudokuGrid.returnGridSequence() glue.
         */
        clearPuzzle();
        for (int i=0; i<puzzle.length; i++) {
            if ((spaces[i].getRow() != SudokuGrid.getX(puzzle[i])) ||
                (spaces[i].getColumn() != SudokuGrid.getY(puzzle[i]))) {
                throw new Exception(
                    "Bad data returned by SudokuGenerator, index={i}");
            }
            spaces[i].setSolvedNumber(SudokuGrid.getGridVal(puzzle[i]));
            if (SudokuGrid.isDefault(puzzle[i])) {
                spaces[i].setNumberUnEditable(SudokuGrid.getGridVal(puzzle[i]));
            }
            else {
                spaces[i].setNumberEditable(0);
            }
        }
    }

    public void solvePuzzle() {
        clearConflicts();
        for (Space space : spaces) {
            if (space.isEditable()) {
                space.setNumberEditable(space.getSolvedNumber());
            }
        }
    }

    /*
     * Return the puzzle back to it's original state at generation time (clear
     * all editable spaces, leave uneditable spaces intact)
     */
    public void resetPuzzle() {
        clearConflicts();
        for (Space space : spaces) {
            if (space.isEditable()) {
                space.setEmpty();
            }
        }
    }

    /*
     * Clear all spaces in the puzzle
     */
    public void clearPuzzle() {
        for (Space space : spaces) {
            space.clear();
        }
    }
}