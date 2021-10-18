
package com.jtconnors.sudokufx2;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static com.jtconnors.sudokufx2.Globals.*;

public class HowToPlayNode extends ParentWithKeyTraversal {

    private final static String INSTRUCTIONS_STR =
        "How to Play Sudoku\n" +
        "\n" +
        "Sudoku Puzzles have 9 columns, " +
        "9 rows and 9 boxes (or regions) " +
        "each of which contain 9 spaces.  " +
        "When a Sudoku puzzle is solved, " +
        "the numbers 1 through 9 will " +
        "appear in each row, column and box " +
        "-- but only once -- and not " +
        "in any particular order.\n\n" +
        "To begin Playing, move to the 'New Puzzle' icon " +
        "using the arrow keys " +
        "and hit the enter or OK button. " +
        "Each new puzzle, based upon level of difficulty, " +
        "reveals a certain quantity of numbers " +
        "that are part of the solution.  These " +
        "numbers will appear emboldened and cannot be modified. " +
        "Your job is to use logic to fill in " +
        "the rest of the spaces.\n\n" +
        "To change the value of a modifiable space, " +
        "use the up, down, right or left buttons to move " +
        "over that space and type in a number.  To clear a space," +
        "type '0' while the space is highlighted.\n" ;

    private static final double STARTING_PADDING = 20f;
    private static final double STARTING_FONT_SIZE = 17f;
    private static final int STARTING_WRAPPING_WIDTH =
            (int)(BOARD_AREA_WIDTH_UNSCALED * 0.85f);
    private static final double STARTING_ARC_WIDTH = 25f;
    private static final double STARTING_ARC_HEIGHT = 25f;

    // Should be recalculated whenever scaleFactor changes
    private double padding;
    private double fontSize;
    private int wrappingWidth;
    private double arcHeight;
    private double arcWidth;

    private CloseButtonNode closeButtonNode;

    /*
     * Pointer to a Node which blocks mouse input to entire board
     * while this Node is visible
     */
    private Node mouseBlocker;
    
    // Pointer to IconButtonNode, whose action opened this window
    private IconButtonNode iconButtonNode = null;

    public HowToPlayNode(IconButtonNode iconButtonNode) {
        this(iconButtonNode, 1.0);
    }

    public HowToPlayNode(IconButtonNode iconButtonNode, double scaleFactor) {
        this.iconButtonNode = iconButtonNode;
        recalculateScale(scaleFactor);

        Text text = new Text();
        text.setLayoutX(padding);
        text.setLayoutY(padding);
        text.setFont(Font.font("Sans Serif", FontWeight.BOLD, fontSize));
        text.setText(INSTRUCTIONS_STR);
        text.setWrappingWidth(wrappingWidth);

        double width = (double) text.getLayoutBounds().getWidth() + (2*padding);
        closeButtonNode = new CloseButtonNode(scaleFactor);
        double height = (double) text.getLayoutBounds().getHeight() + (2*padding)
                + (double) closeButtonNode.getLayoutBounds().getHeight();
        closeButtonNode.setLayoutX(width/2 -
                closeButtonNode.getLayoutBounds().getWidth()/2);
        closeButtonNode.setLayoutY(height -
                closeButtonNode.getLayoutBounds().getHeight() - padding);

        closeButtonNode.setOnMousePressed((MouseEvent event) -> {
            setVisible(false);
            if (mouseBlocker != null) {
                mouseBlocker.setVisible(false);
            }
            lastFocused = getIconButtonNode();
            getIconButtonNode().showFocusHint();
            getIconButtonNode().getBoardNode().requestFocus();
        });
        setOnKeyPressed((KeyEvent ke) -> {
            KeyCode key = ke.getCode();
            if (key == KeyCode.ENTER) {
                setVisible(false);
                if (mouseBlocker != null) {
                    mouseBlocker.setVisible(false);
                }
                lastFocused = getIconButtonNode();
                lastFocused.showFocusHint();
//getIconButtonNode().getBoardNode().requestFocus();
            }
        });

        Rectangle rect = new Rectangle();
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.WHITE);
        rect.setArcWidth(arcWidth);
        rect.setArcHeight(arcHeight);
        rect.setOpacity(0.9f);

        getChildren().addAll(rect, text, closeButtonNode);
        setFocusTraversable(true);
    }

    private void recalculateScale(double scaleFactor) {
        padding = STARTING_PADDING * scaleFactor;
        fontSize = STARTING_FONT_SIZE * scaleFactor;
        wrappingWidth = (int)(STARTING_WRAPPING_WIDTH * scaleFactor);
        arcWidth = STARTING_ARC_WIDTH * scaleFactor;
        arcHeight = STARTING_ARC_HEIGHT * scaleFactor;
    }
    
    public IconButtonNode getIconButtonNode() {
        return iconButtonNode;
    }

    public void setMouseBlocker(Rectangle mouseBlocker) {
        this.mouseBlocker = mouseBlocker;
    }

    public void popup() {
        setVisible(true);
        requestFocus();
        closeButtonNode.showFocusHint();
        if (mouseBlocker != null) {
            mouseBlocker.setVisible(true);
        }
    }
}