
package com.jtconnors.sudokufx2;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.VPos;


public class ChooseNumberNode extends Parent {

    class NumberNode extends ParentWithKeyTraversal {

        private static final double DEFAULT_STARTING_WIDTH = 20f;
        private double startingWidth = DEFAULT_STARTING_WIDTH;
        private final double startingHeight = 20f;
        private final double startingFontSize = 18f;
        private final double startingArcWidth = 5f;
        private final double startingArcHeight = 5f;

        private double scaleFactor = 1f;
        private int value ;
        private Rectangle rect;

        // Should be recalculated whenever scaleFactor changes
        private double width;
        private double height;
        private double fontSize;
        private double arcWidth;
        private double arcHeight;

        public NumberNode(int value) {
            this(value, 1.0f, DEFAULT_STARTING_WIDTH);
        }

        public NumberNode(int value, double scaleFactor) {
            this(value, scaleFactor, DEFAULT_STARTING_WIDTH);
        }

        public NumberNode(int value, double scaleFactor, double startingWidth) {
            this.value = (value < 0 || value > 9) ? 0 : value;
            this.scaleFactor = scaleFactor;
            this.startingWidth = startingWidth;
            recalculateScale();

            rect = new Rectangle();
            rect.setWidth(width);
            rect.setHeight(height);
            rect.setArcWidth(arcWidth);
            rect.setArcHeight(arcHeight);
            rect.setFill(Color.WHITE);
            rect.setStroke(Color.BLACK);

            Text text = new Text();
            text.setFont(Font.font("Arial", fontSize));
            text.setText(String.valueOf(value));
            text.setX((width - text.getLayoutBounds().getWidth()) / 2);
            text.setY((height - text.getLayoutBounds().getHeight()) / 2);
            text.setTextOrigin(VPos.TOP);
            text.setFill(Color.BLACK);

            getChildren().addAll(rect, text);
        }

        private void recalculateScale() {
            width = startingWidth * scaleFactor;
            height = startingHeight * scaleFactor;
            fontSize = startingFontSize * scaleFactor;
            arcWidth = startingArcWidth * scaleFactor;
            arcHeight = startingArcHeight * scaleFactor;
        }

        public int getValue() {
            return value;
        }

        @Override
        public void showFocusHint() {
            super.showFocusHint();
            setEffect(innerShadow);
            setOpacity(1.0f);
            lastHighlighted = this;
        }

        @Override
        public void unShowFocusHint() {
            super.showFocusHint();
            setEffect(null);
            setOpacity(defaultOpacity);
        }
    }

    private final double defaultOpacity = 0.5f;
    private NumberNode lastHighlighted;
    private static final InnerShadow innerShadow = new InnerShadow();
    private NumberNode[] nodeArr = new NumberNode[10];
    private NumberNode zeroNumberNode;
    private SpaceNode spaceNode;

    /*
     * For key traversal, this table determines which node to
     * highlight next.  It is based on the current highlighted numberNode
     * and the provided key input:
     * { ArrowKey.LEFT, ArrowKey.RIGHT, ArrowKey.UP, ArrowKey.DOWN }
     */
    static final int traversalArr[][] = {
        { 9, 1, 8, 0 },  // 0
        { 0, 2, 1, 4 },  // 1
        { 1, 3, 2, 5 },  // 2
        { 2, 4, 3, 6 },  // 3
        { 3, 5, 1, 7 },  // 4
        { 4, 6, 2, 8 },  // 5
        { 5, 7, 3, 9 },  // 6
        { 6, 8, 4, 0 },  // 7
        { 7, 9, 5, 0 },  // 8
        { 8, 0, 6, 0 }   // 9
    };

    public ChooseNumberNode() {
        this(1.0f);
    }

    public ChooseNumberNode(double scaleFactor) {
        /* 
         * Number Nodes 1..9, three per row
         */
        for (int row=0; row<=2; row++) {
            for (int col=0; col<=2; col++) {
                int index = col * 3 + row + 1;
                NumberNode numberNode = new NumberNode(index, scaleFactor);
                numberNode.setLayoutX(row *
                        numberNode.getLayoutBounds().getWidth());
                numberNode.setLayoutY(col *
                        numberNode.getLayoutBounds().getHeight());
                numberNode.setOpacity(defaultOpacity);
                numberNode.setOnMouseEntered((MouseEvent me) -> {
                    Node node = (Node)me.getSource();
                    if (node != lastHighlighted) {
                        lastHighlighted.unShowFocusHint();
                        ((NumberNode)node).showFocusHint();
                    }
                });
                numberNode.setOnMousePressed((MouseEvent me) -> {
                    doSelected((NumberNode)me.getSource());
                });
                nodeArr[index] = numberNode;
            }
        }
        /*
         * NumberNode with '0' at bottom, encompassing entire width
         */
        zeroNumberNode = new NumberNode(0, scaleFactor,
            3f * (double)nodeArr[1].getLayoutBounds().getWidth() / scaleFactor);
        zeroNumberNode.setLayoutX(0f);
        zeroNumberNode.setLayoutY(3 *
            nodeArr[1].getLayoutBounds().getHeight());
        zeroNumberNode.setOpacity(defaultOpacity);
        zeroNumberNode.setOnMouseEntered((MouseEvent me) -> {
            Node node = (Node)me.getSource();
            if (node != lastHighlighted) {
                lastHighlighted.unShowFocusHint();
                ((NumberNode)node).showFocusHint();
            }
        });
        zeroNumberNode.setOnMousePressed((MouseEvent me) -> {
            doSelected((NumberNode)me.getSource());
        });
        nodeArr[0] = zeroNumberNode;
        /*
         * Set up Key Traversal
         */
        for (int i=0; i<nodeArr.length; i++) {
            NumberNode node = nodeArr[i];
            int[] traverseIx = traversalArr[i];
            node.setKeyLeftNode(nodeArr[traverseIx[ArrowKey.LEFT.ordinal()]]);
            node.setKeyRightNode(nodeArr[traverseIx[ArrowKey.RIGHT.ordinal()]]);
            node.setKeyUpNode(nodeArr[traverseIx[ArrowKey.UP.ordinal()]]);
            node.setKeyDownNode(nodeArr[traverseIx[ArrowKey.DOWN.ordinal()]]);
        }
        /*
         * Enclosing Rectangle.  Set opacity to .01 so that this can
         * block mouse events, yet isn't visible.
         */
        Rectangle rect = new Rectangle();
        rect.setWidth(3 * nodeArr[1].getLayoutBounds().getWidth());
        rect.setHeight(4 * nodeArr[1].getLayoutBounds().getHeight());
        rect.setStroke(Color.WHITE);
        rect.setFill(Color.WHITE);
        rect.setOpacity(0.01f);
        rect.setVisible(true);
//        rect.setBlocksMouse(true);

        setOnKeyPressed((KeyEvent ke) -> {
            processKeyEvent(ke.getCode());
        });

        rect.setOnMouseExited((MouseEvent me) -> {
            setVisible(false);
        });

        setFocusTraversable(true);
        requestFocus();
        lastHighlighted = nodeArr[0];
        nodeArr[0].showFocusHint();
        getChildren().add(rect);
        getChildren().addAll(Arrays.asList(nodeArr));
    }

    private void processKeyEvent(KeyCode key) {
        ParentWithKeyTraversal highlight = null;
        boolean validKey = false;
        boolean selected = false;
        switch(key) {
            case LEFT:
            case KP_LEFT:
                highlight = lastHighlighted.getKeyLeftNode();
                validKey = true;
                break;
            case RIGHT:
            case KP_RIGHT:
                highlight = lastHighlighted.getKeyRightNode();
                validKey = true;
                break;
            case UP:
            case KP_UP:
                highlight = lastHighlighted.getKeyUpNode();
                validKey = true;
                break;
            case DOWN:
            case KP_DOWN:
                highlight = lastHighlighted.getKeyDownNode();
                validKey = true;
                break;
            case DIGIT0:
            case DIGIT1:
            case DIGIT2:
            case DIGIT3:
            case DIGIT4:
            case DIGIT5:
            case DIGIT6:
            case DIGIT7:
            case DIGIT8:
            case DIGIT9:
                int numberKey = key.ordinal() - KeyCode.DIGIT0.ordinal();
                highlight = nodeArr[numberKey];
                validKey = true;
                selected = true;
                break;
            case NUMPAD0:
            case NUMPAD1:
            case NUMPAD2:
            case NUMPAD3:
            case NUMPAD4:
            case NUMPAD5:
            case NUMPAD6:
            case NUMPAD7:
            case NUMPAD8:
            case NUMPAD9:
                numberKey = key.ordinal() - KeyCode.NUMPAD0.ordinal();
                highlight = nodeArr[numberKey];
                validKey = true;
                selected = true;
                break;
            case ENTER:
                highlight = (NumberNode) lastHighlighted;
                validKey = true;
                selected = true;
                break;
        }
        if (validKey) {
            lastHighlighted.unShowFocusHint();
            ((NumberNode)highlight).showFocusHint();
        }
        if (selected) {
            doSelected(highlight);
        }
    }

    private void doSelected(ParentWithKeyTraversal numberNode) {
        if (spaceNode != null) {
            spaceNode.getSpace().setNumberEditable(
                ((NumberNode) numberNode).getValue());
            Board board = SpaceNode.getBoardNode().getBoard();
            board.clearConflicts();
            ArrayList<Space> conflicts = board.getConflictsOptimized();
            for (Space conflict : conflicts) {
                conflict.setConflict();
            }
        }
        setVisible(false);
    }

    public void popup(SpaceNode spaceNode) {
        this.spaceNode = spaceNode;
        setVisible(true);
        requestFocus();
    }
}