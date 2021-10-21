/* SerGrid created on 12.07.2006 */
package net.sourceforge.playsudoku.io;

//import java.io.Serializable;

public class SerGrid /*implements Serializable*/ {
    
    public int[][] grid;
    public int difficulty;
    
    public SerGrid() {
        grid = new int[9][9];
    }

}
