
package com.jtconnors.sudokufx2;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;

public class Space {
    /* A real Sudoku board space can either be blank or have a value between
     * [1..9].  Internally, each space is assigned a number value with a
     * larger range:  Here's how those values are defined:
     *
     * 0:     Blank
     * 1-9:   The actual numeric values from 1..9
     *        A number from 1..9 signifies that this space can be modified
     * 10:    Undefined.  A space should never be assigned this number
     * 11-19: Signifies that this space CANNOT be modified.  To get
     *        the real numeric value perform a 'number mod 10'
     * 20:    Undefined.  A space should never be assigned this number
     * 21-29: Signifies that this space can be modified, but conflicts with
     *        another space with the same value in it's row/column/region.
     * 30:    Undefined.  A space should never be assigned this number
     * 31-39: Signifies that this space CANNOT be modified, and conflicts with
     *        another space with the same value in it's row/column/region
     */

    /*
     * Create a Beans IntegerProperty for the number variable so that
     * listeners can be appraised of any updates.
     */
    private IntegerProperty number;

    public final void setNumber(int value) {
        numberProperty().setValue(value);
    }

    public final int getNumber() {
        return number == null ? 0 : number.getValue();
    }

    public IntegerProperty numberProperty() {
        if (number == null) {
            number = new IntegerPropertyBase() {
                
                @Override
                public Object getBean() {
                    return Space.this;
                }
                
                @Override
                public String getName() {
                    return "number";
                }    
            };
        }
        return number;
    }

    /*
     * As part of the provided SudokuGenerator code, a newly generated puzzle
     * contains not only a new puzzle but the solution too.  Keep the solved
     * number for this space in the 'solvedNumber' instance variable.
     */
    private int solvedNumber;

    /*
     * Each space on the board has a (row, column) coordinate and uniquely
     * belongs to 1 of 9 vertical lines, 1 of 9 horizontal lines,
     * and 1 of 9 reigons.  The vertical and horizontal
     * lines can be determined by looking at the assigned row and column
     * attributes.  The reigon number for each space will be calclauted and
     * stored in the reigon attribute.
     */
    private final int row;
    private final int column;
    private final int region;

    public Space (int row, int column, int region) {
        this.row = row;
        this.column = column;
        this.region = region;
        setNumber(0);
    }

    /*
     * Get the raw value of the 'number' instance variable, unmodified
     */
    public int getNumberRaw() {
        return getNumber();
    }

    /*
     * Get the external value of the number variable, achieved by
     * performing a 'mod 10' on 'number'.
     */
    public int getNumberExternal() {
        return getNumber() % 10;
    }
    
    public void setNumberEditable(int number) {
        setNumber(number);
    }

    /*
     * Add 10 to 'number' in order to make it uneditable.
     */
    public void setNumberUnEditable(int number) {
        setNumber((number % 10) + 10);
    }

    /*
     * Set the number to a state indicating that it is in conflict (i.e.
     * has a duplicate value) with another space in its row/column/region
     */
    public void setConflict() {
        if (! isConflict()) {
            setNumber(getNumber() + 20);
        }
    }

    public void clearConflict() {
        setNumber(getNumber() % 20);
    }

    public void setEmpty() {
        setNumber(0);
    }

    public boolean isEmpty() {
        return getNumber() == 0;
    }

    public boolean isEditable() {
        int num = getNumber();
        return (num < 10) || (num >= 21 && num <= 29);
    }

    public boolean isConflict() {
        int num = getNumber();
        return (num >= 21) && (num <= 39);
    }

    public int getSolvedNumber() {
        return this.solvedNumber;
    }

    public void setSolvedNumber(int solvedNumber) {
        this.solvedNumber = solvedNumber;
    }

    public void clear() {
        setNumber(0);
        this.solvedNumber = 0;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRegion() {
        return this.region;
    }
}
