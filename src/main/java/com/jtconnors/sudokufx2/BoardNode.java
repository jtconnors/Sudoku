
package com.jtconnors.sudokufx2;

import java.util.ArrayList;
import javafx.beans.Observable;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.VPos;
import static com.jtconnors.sudokufx2.Globals.sudokuStage;
import static com.jtconnors.sudokufx2.Globals.lastFocused;
import static com.jtconnors.sudokufx2.Globals.NROWS;
import static com.jtconnors.sudokufx2.Globals.NCOLUMNS;
import static com.jtconnors.sudokufx2.Globals.NUM_HINTS_EASY;
import static com.jtconnors.sudokufx2.Globals.NUM_HINTS_HARD;
import static com.jtconnors.sudokufx2.Globals.BOARD_AREA_WIDTH_UNSCALED;
import static com.jtconnors.sudokufx2.Globals.IMAGE_FILES_PREFIX;
import static com.jtconnors.sudokufx2.Globals.ICON_SIZE;

public class BoardNode extends Parent {

    class MinimizeNode extends ParentWithKeyTraversal {

        private final Rectangle enclosingRect;

        public MinimizeNode() {
            double strokeWidth = 1.5f * gap;
            enclosingRect = new Rectangle();
            enclosingRect.setHeight(dragAreaHeight - strokeWidth);
            enclosingRect.setWidth(dragAreaHeight - strokeWidth);
            enclosingRect.setStroke(Color.WHITE);
            enclosingRect.setStrokeWidth(strokeWidth);

            Line line = new Line();
            line.setStartX(strokeWidth * 2);
            line.setStartY(dragAreaHeight - (strokeWidth * 3));
            line.setEndX(dragAreaHeight - (strokeWidth * 3));
            line.setEndY(dragAreaHeight - (strokeWidth * 3));
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(strokeWidth);

            final Group group = new Group();
            group.getChildren().addAll(enclosingRect, line);
            setLayoutX(boardAreaWidth - borderWidth -
                (2 * enclosingRect.getLayoutBounds().getWidth()));
            setLayoutY(borderWidth);
            setOpacity(0.85f);

            setOnMouseEntered((MouseEvent event) -> {
                lastFocused.unShowFocusHint();
                showFocusHint();
            });
            setOnMouseExited((MouseEvent event) -> {
                unShowFocusHint();
            });
            setOnMouseClicked((MouseEvent event) -> {
                sudokuStage.setIconified(true);
            });

            getChildren().add(group);
        }

        @Override
        public void showFocusHint() {
            super.showFocusHint();
            enclosingRect.setFill(Color.RED);
            lastFocused = this;
        }

        @Override
        public void unShowFocusHint() {
            super.unShowFocusHint();
            enclosingRect.setFill(Color.TRANSPARENT);
        }
    }

    class ExitNode extends ParentWithKeyTraversal {

        private final Rectangle enclosingRect;

        public ExitNode() {
            double strokeWidth = 1.5f * gap;
            enclosingRect = new Rectangle();
            enclosingRect.setHeight(dragAreaHeight - strokeWidth);
            enclosingRect.setWidth(dragAreaHeight - strokeWidth);
            enclosingRect.setStroke(Color.WHITE);
            enclosingRect.setStrokeWidth(strokeWidth);

            Line line1 = new Line();
            line1.setStartX(strokeWidth * 2);
            line1.setStartY(strokeWidth * 2);
            line1.setEndX(dragAreaHeight - (strokeWidth * 3));
            line1.setEndY(dragAreaHeight - (strokeWidth * 3));
            line1.setStroke(Color.WHITE);
            line1.setStrokeWidth(strokeWidth);

            Line line2 = new Line();
            line2.setStartX(dragAreaHeight - (strokeWidth * 3));
            line2.setStartY(strokeWidth * 2);
            line2.setEndX(strokeWidth * 2);
            line2.setEndY(dragAreaHeight - (strokeWidth * 3));
            line2.setStroke(Color.WHITE);
            line2.setStrokeWidth(strokeWidth);

            final Group group = new Group();
            group.getChildren().addAll(enclosingRect, line1, line2);
            group.setLayoutX(boardAreaWidth - borderWidth -
                enclosingRect.getLayoutBounds().getWidth());
            group.setLayoutY(borderWidth);
            group.setOpacity(0.85f);

            group.setOnMouseEntered((MouseEvent event) -> {
                lastFocused.unShowFocusHint();
                showFocusHint();
            });
            group.setOnMouseExited((MouseEvent event) -> {
                unShowFocusHint();
            });
            group.setOnMouseClicked((MouseEvent event) -> {
                Platform.exit();
            });

            getChildren().add(group);
        }

        @Override
        public void showFocusHint() {
            super.showFocusHint();
            enclosingRect.setFill(Color.RED);
            lastFocused = this;
        }

        @Override
        public void unShowFocusHint() {
            enclosingRect.setFill(Color.TRANSPARENT);
        }
    }

    private double scaleFactor;
    public ImageView javafxLogoRef;
    public Text sudokuTextRef;

    /*
     * These values are calculated in CalculateDimensions(), and should be
     * recomputed whenever the value of scaleFactor changes.
     */
    private double boardAreaWidth;
    private double borderWidth;
    private double dragAreaHeight;
    private double boardAreaHeight;
    private double menuAreaHeight;
    public double height;
    public double width;
    private double dragAreaRectangleHeight;
    public double getDragAreaRectangleHeight() {return dragAreaRectangleHeight;}
    //
    // A total of 81 (9x9) spaces will be displayed.  The gaps represent the
    // number of pixels that separate spaces in the x and y direction
    // on the board, where every third space has a larger (3x) gap.  These
    // larger gaps represent Sudoku board region separators.
    //
    private double gap;
    private double[] gapOffset;
    // For best results these should be evenly divisible by 9
    private double sudokuWidth;
    // For best results these should be even numbers
    private double spaceSize;
    private double imageSize;
    // Fine tuning to center tile in each space.
    private double centerAdjust;
    /*
     * End recalculation section
     */

    private Board board = new Board();
    public Board getBoard() { return board ; }
    private final SpaceNode[] spaceNodes = new SpaceNode[NROWS * NCOLUMNS];

    /*
     * This variable gets recalculated via a PropertyChangeListener
     * whenever the skill level is changed via the slider UI.
     */
    private int difficulty = NUM_HINTS_EASY;

    /*
     * Pointer to a Node which blocks mouse input to entire board
     * while popup nodes (e.g. howToPlayNode) are visible
     */
    private Rectangle mouseBlocker;

    private MinimizeNode minimizeNode;
    public Parent getMinimizeNode() { return minimizeNode; }
    private ExitNode exitNode;
    private IconButtonNode[] ibNodeArr;
    private HowToPlayNode howToPlayNode;
    private SliderNode difficultyNode;
    private ChooseNumberNode chooseNumberNode;
    private BoardNode boardNode = this;

    public BoardNode() {
        this(1.0f);
    }

    public BoardNode(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        calculateDimensions();
        init();
    }

    /*
     * This should be called whenever scaleFactor is modified
     */
    private void calculateDimensions() {
        boardAreaWidth = BOARD_AREA_WIDTH_UNSCALED * scaleFactor;  //488 also?
        borderWidth = 10f * scaleFactor;
        dragAreaHeight = 30f * scaleFactor ;
        boardAreaHeight = boardAreaWidth - (2f * borderWidth);
        menuAreaHeight = 110f * scaleFactor;
        height = dragAreaHeight + boardAreaHeight + menuAreaHeight +
            (2 * borderWidth);
        width = boardAreaWidth;
        dragAreaRectangleHeight = dragAreaHeight + borderWidth;
        gap = 2f * scaleFactor;
        gapOffset = new double[] {
           gap*3f, gap*4f, gap*5f, gap*8f, gap*9f, gap*10f, gap*13f, gap*14f,
           gap*15f, gap*18f
        };
        sudokuWidth = width - (2f * borderWidth) - gapOffset[NROWS];
        spaceSize = sudokuWidth / NROWS;
        imageSize = spaceSize - (6 * scaleFactor);
        centerAdjust = (spaceSize - imageSize) / 2;

    }

    private void init() {
        getChildren().add(createBoardImage());
        getChildren().add(createUpperLeftLogo());
        getChildren().add(createMinimizeNode());
        getChildren().add(createExitNode());
        getChildren().add(createPuzzleGroup());
        getChildren().add(createBottomIconGroup());
        getChildren().add(createSpaceNodesGroup());
        getChildren().add(createPopupGroup());

        setOnKeyPressed((KeyEvent ke) -> {
            processKeyEvent(ke.getCode());
        });

        setupKeyTraversals();
        setFocusTraversable(true);
        lastFocused = ibNodeArr[0];
        requestFocus();
        ibNodeArr[0].showFocusHint();
    }

    private Group createBoardImage() {
        Image image = new Image(IMAGE_FILES_PREFIX +
                "boardImage-black-560x690.png", width, height, false, false);
        ImageView iv = new ImageView();
        iv.setImage(image);
        Group group = new Group();
        group.getChildren().add(iv);
        return group;
    }

    private Group createUpperLeftLogo() {
        Group group = new Group();

        // JavaFX Logo -- upper left
        Image image = new Image(IMAGE_FILES_PREFIX + "javafxlogo98x40.png",
                74 * scaleFactor, dragAreaHeight, false, false);
        ImageView iv = new ImageView();
        iv.setLayoutX(borderWidth);
        iv.setLayoutY(borderWidth);
        iv.setImage(image);

        // Sudoku Text -- immediately to the right of the JavaFX logo
        Text t = new Text();
        t.setLayoutX(borderWidth + iv.getLayoutBounds().getWidth() - 4);
        t.setLayoutY(borderWidth + dragAreaHeight - 1);
        t.setTextOrigin(VPos.BOTTOM);
        t.setText("Sudoku");
        t.setFill(Color.WHITE);
        t.setFont(Font.font("Arial", FontWeight.BOLD,
                15 * scaleFactor));
        t.setOpacity(0.85f);

        group.getChildren().addAll(iv, t);
        return group;
    }

    private Parent createMinimizeNode() {
        minimizeNode = new MinimizeNode();
        minimizeNode.setAction(() -> {
            sudokuStage.setIconified(true);
        });
        return minimizeNode;
    }

    private Parent createExitNode() {
        exitNode = new ExitNode();
        exitNode.setAction(() -> {
            Platform.exit();
        });
        return exitNode;
    }

    private Group createPuzzleGroup() {
        final Group group = new Group();
        group.setLayoutX(borderWidth);
        group.setLayoutY(borderWidth + dragAreaHeight);

        Rectangle rect = new Rectangle();
        rect.setWidth(width - (2 * borderWidth));
        rect.setHeight(boardAreaHeight);
        rect.setArcHeight(10.0f);
        rect.setArcWidth(10.0f);
        rect.setFill(Color.WHITE);
        group.getChildren().addAll(rect);

        for (int r=0; r<NROWS; r++) {
            for (int c=0; c<NCOLUMNS; c++) {
                Image image = new Image(IMAGE_FILES_PREFIX +
                    "black-indent.png", spaceSize, spaceSize, false, false);
                ImageView iv = new ImageView();
                iv.setX((c * spaceSize) + gapOffset[c]);
                iv.setY(r * spaceSize + gapOffset[r]);
                iv.setImage(image);
                group.getChildren().addAll(iv);
            }
        }
        return group;
    }

    /*
     * As IconButtons are added or removed, these arrays must be updated
     * accordingly.
     */
    private static final int ICON_STRING_IX = 0;
    private static final int ICON_URL_IX = 1;
    private static final String[][] iconInfo = {
        { "New", IMAGE_FILES_PREFIX + "new-game.png" },
        { "How", IMAGE_FILES_PREFIX + "how-to-play.png" },
        { "Skill", IMAGE_FILES_PREFIX + "skill-level.png" },
        { "Reset", IMAGE_FILES_PREFIX + "reset.png" },
        { "Solve", IMAGE_FILES_PREFIX + "solve.png" },
        { "Quit", IMAGE_FILES_PREFIX + "on-off.png"}
    };
    private static final FunctionPtr[] iconAction =
            new FunctionPtr[iconInfo.length];
    {
        iconAction[0] = new FunctionPtr () {
            @Override
            public void invoke() {
                try {
                     board.newPuzzle(difficulty);
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.exit();
                }
            }
        };
        iconAction[1] = new FunctionPtr () {
            @Override
            public void invoke() {
                boardNode.setFocused(false);
                howToPlayNode.popup();
            }
        };
        iconAction[2] = new FunctionPtr () {
            @Override
            public void invoke() {
                boardNode.setFocused(false);
                difficultyNode.popup();
            }
        };
        iconAction[3] = new FunctionPtr () {
            @Override
            public void invoke() {
                board.resetPuzzle();
            }
        };
        iconAction[4] = new FunctionPtr () {
            @Override
            public void invoke() {
                board.solvePuzzle();
            }
        };
        iconAction[5] = () -> {
            Platform.exit();
        };
    }
    /*
     * End IconButtonNodes initializer section
     */

    private Group createBottomIconGroup() {
        Group group = new Group();
        IconButtonNode ib = null;
        ibNodeArr = new IconButtonNode[iconInfo.length];
        double iconSpacing = (width - (2 * borderWidth) -
            (iconInfo.length * ICON_SIZE * scaleFactor)) / iconInfo.length;
        group.setLayoutX(borderWidth + (iconSpacing / 2));
        for (int i=0; i<iconInfo.length; i++) {
           ib = new IconButtonNode(this,
                   ICON_SIZE*scaleFactor, ICON_SIZE*scaleFactor,
                   iconInfo[i][ICON_URL_IX], iconInfo[i][ICON_STRING_IX]);
           ib.setLayoutX(i * (ICON_SIZE * scaleFactor + iconSpacing));
           ib.setIconTextFill(Color.WHITE);
           ib.setScaleFactor(1.0f);
           ib.setAction(iconAction[i]);
           ibNodeArr[i] = ib;
           group.getChildren().addAll(ib);
        }
        group.setLayoutY(borderWidth + dragAreaHeight + boardAreaHeight +
            (menuAreaHeight -
            ib.getLayoutBounds().getHeight() * scaleFactor) / 2);
        return group;
    }

    private Group createSpaceNodesGroup() {
        final Group group = new Group();
        group.setLayoutX(borderWidth);
        group.setLayoutY(borderWidth + dragAreaHeight);

        for (int r=0; r<NROWS; r++) {
            for (int c=0; c<NCOLUMNS; c++) {
                final SpaceNode spaceNode = new
                    SpaceNode(board.getSpace(r, c), imageSize);
                spaceNode.setLayoutX(c * spaceSize + gapOffset[c] +
                   centerAdjust);
                spaceNode.setLayoutY(r * spaceSize + gapOffset[r] +
                   centerAdjust);
                spaceNode.setAction(() -> {
                    boardNode.setFocused(false);
                    chooseNumberNode.setLayoutX(centerAdjust +
                            spaceNode.getLayoutX() +
                            ((chooseNumberNode.getLayoutBounds().getWidth() -
                                    spaceNode.getLayoutBounds().getWidth()) / 2));
                    chooseNumberNode.setLayoutY(centerAdjust +
                            spaceNode.getLayoutY() +
                            ((chooseNumberNode.getLayoutBounds().getHeight() -
                                    spaceNode.getLayoutBounds().getHeight()) / 2));
                    chooseNumberNode.popup(spaceNode);
                });
                spaceNodes[r * NROWS + c] = spaceNode;
            }
        }
        SpaceNode.setBoardNode(this);
        
        group.getChildren().addAll(spaceNodes);
        return group;
    }

    private Group createPopupGroup() {
        mouseBlocker = new Rectangle();
        mouseBlocker.setLayoutY(dragAreaHeight + borderWidth);
        mouseBlocker.setWidth(width);
        mouseBlocker.setHeight(height - dragAreaHeight - borderWidth);
        mouseBlocker.setFill(Color.WHITE);
        mouseBlocker.setOpacity(0.2f);
        mouseBlocker.setVisible(false);
//        mouseBlocker.setBlocksMouse(true);

        difficultyNode = new SliderNode(8, "Easy", "Hard", scaleFactor);
        difficultyNode.setLayoutX(width * .25f);
        difficultyNode.setLayoutY(borderWidth + dragAreaHeight +
            boardAreaHeight -
            difficultyNode.getLayoutBounds().getHeight() - (25*scaleFactor));
        difficultyNode.setVisible(false);
//        difficultyNode.setBlocksMouse(true);
        difficultyNode.setMouseBlocker(mouseBlocker);

        /*
         * "Bind" to the difficultyNode object's adjValue variable
         * such that when it is modified, the difficulty variable
         * will be recalculated based on the new value of adjValue.
         */
        difficultyNode.adjValueProperty().addListener((Observable ov) -> {
            difficulty = (int) (NUM_HINTS_EASY -
                    (difficultyNode.getAdjValue() *
                    (NUM_HINTS_EASY - NUM_HINTS_HARD)));
        });
     
        howToPlayNode = new HowToPlayNode(ibNodeArr[1], scaleFactor);
        howToPlayNode.setLayoutX((width - 
            howToPlayNode.getLayoutBounds().getWidth()) / 2);
        howToPlayNode.setLayoutY(((boardAreaHeight -
            howToPlayNode.getLayoutBounds().getHeight()) / 2) +
            dragAreaHeight + borderWidth);
        howToPlayNode.setVisible(false);
//        howToPlayNode.setBlocksMouse(true);
        howToPlayNode.setMouseBlocker(mouseBlocker);

        chooseNumberNode = new ChooseNumberNode();
        chooseNumberNode.setVisible(false);
//        chooseNumberNode.setBlocksMouse(true);

        Group group = new Group();
        group.getChildren().addAll(mouseBlocker, difficultyNode,
                howToPlayNode, chooseNumberNode);
        return group;
    }

    /*
     * In order to handle keyboard as well as mouse input, set up mechanism
     * to enable users to use the arrow and tab keys to select a particular
     * node for input focus.  Note: this method must be called after all nodes
     * have been initialized.
     */
    private void setupKeyTraversals() {
        /*
         * For traversing the Sudoku board spaces, this table determines
         * which node to bring focus to next, based on the current SpaceNode
         * instance in focus and the provided key input:
         * { ArrowKey.LEFT, ArrowKey.RIGHT, ArrowKey.UP, ArrowKey.DOWN }
         */
        Parent SpaceTraversalArr[][] = {
            // Row 0: SpaceNodes[0..8]
            { exitNode,       spaceNodes[1],  ibNodeArr[5], spaceNodes[9] },
            { spaceNodes[0],  spaceNodes[2],  ibNodeArr[0],   spaceNodes[10] },
            { spaceNodes[1],  spaceNodes[3],  spaceNodes[73], spaceNodes[11] },
            { spaceNodes[2],  spaceNodes[4],  ibNodeArr[1],   spaceNodes[12] },
            { spaceNodes[3],  spaceNodes[5],  ibNodeArr[2],   spaceNodes[13] },
            { spaceNodes[4],  spaceNodes[6],  spaceNodes[76], spaceNodes[14] },
            { spaceNodes[5],  spaceNodes[7],  ibNodeArr[3],   spaceNodes[15] },
            { spaceNodes[6],  spaceNodes[8],  minimizeNode,   spaceNodes[16] },
            { spaceNodes[7],  spaceNodes[9],  exitNode,       spaceNodes[17] },
            // Row 1: SpaceNodes[9..17]
            { spaceNodes[8],  spaceNodes[10], spaceNodes[0],  spaceNodes[18] },
            { spaceNodes[9],  spaceNodes[11], spaceNodes[1],  spaceNodes[19] },
            { spaceNodes[10], spaceNodes[12], spaceNodes[2],  spaceNodes[20] },
            { spaceNodes[11], spaceNodes[13], spaceNodes[3],  spaceNodes[21] },
            { spaceNodes[12], spaceNodes[14], spaceNodes[4],  spaceNodes[22] },
            { spaceNodes[13], spaceNodes[15], spaceNodes[5],  spaceNodes[23] },
            { spaceNodes[14], spaceNodes[16], spaceNodes[6],  spaceNodes[24] },
            { spaceNodes[15], spaceNodes[17], spaceNodes[7],  spaceNodes[25] },
            { spaceNodes[16], spaceNodes[18], spaceNodes[8],  spaceNodes[26] },
            // Row 2: SpaceNodes[18..26]
            { spaceNodes[17], spaceNodes[19], spaceNodes[9],  spaceNodes[27] },
            { spaceNodes[18], spaceNodes[20], spaceNodes[10], spaceNodes[28] },
            { spaceNodes[19], spaceNodes[21], spaceNodes[11], spaceNodes[29] },
            { spaceNodes[20], spaceNodes[22], spaceNodes[12], spaceNodes[30] },
            { spaceNodes[21], spaceNodes[23], spaceNodes[13], spaceNodes[31] },
            { spaceNodes[22], spaceNodes[24], spaceNodes[14], spaceNodes[32] },
            { spaceNodes[23], spaceNodes[25], spaceNodes[15], spaceNodes[33] },
            { spaceNodes[24], spaceNodes[26], spaceNodes[16], spaceNodes[34] },
            { spaceNodes[25], spaceNodes[27], spaceNodes[17], spaceNodes[35] },
            // Row 3: SpaceNodes[27..35]
            { spaceNodes[26], spaceNodes[28], spaceNodes[18], spaceNodes[36] },
            { spaceNodes[27], spaceNodes[29], spaceNodes[19], spaceNodes[37] },
            { spaceNodes[28], spaceNodes[30], spaceNodes[20], spaceNodes[38] },
            { spaceNodes[29], spaceNodes[31], spaceNodes[21], spaceNodes[39] },
            { spaceNodes[30], spaceNodes[32], spaceNodes[22], spaceNodes[40] },
            { spaceNodes[31], spaceNodes[33], spaceNodes[23], spaceNodes[41] },
            { spaceNodes[32], spaceNodes[34], spaceNodes[24], spaceNodes[42] },
            { spaceNodes[33], spaceNodes[35], spaceNodes[25], spaceNodes[43] },
            { spaceNodes[34], spaceNodes[36], spaceNodes[26], spaceNodes[44] },
            // Row 4: SpaceNodes[36..44]
            { spaceNodes[35], spaceNodes[37], spaceNodes[27], spaceNodes[45] },
            { spaceNodes[36], spaceNodes[38], spaceNodes[28], spaceNodes[46] },
            { spaceNodes[37], spaceNodes[39], spaceNodes[29], spaceNodes[47] },
            { spaceNodes[38], spaceNodes[40], spaceNodes[30], spaceNodes[48] },
            { spaceNodes[39], spaceNodes[41], spaceNodes[31], spaceNodes[49] },
            { spaceNodes[40], spaceNodes[42], spaceNodes[32], spaceNodes[50] },
            { spaceNodes[41], spaceNodes[43], spaceNodes[33], spaceNodes[51] },
            { spaceNodes[42], spaceNodes[44], spaceNodes[34], spaceNodes[52] },
            { spaceNodes[43], spaceNodes[45], spaceNodes[35], spaceNodes[53] },
            // Row 5: SpaceNodes[45..53]
            { spaceNodes[44], spaceNodes[46], spaceNodes[36], spaceNodes[54] },
            { spaceNodes[45], spaceNodes[47], spaceNodes[37], spaceNodes[55] },
            { spaceNodes[46], spaceNodes[48], spaceNodes[38], spaceNodes[56] },
            { spaceNodes[47], spaceNodes[49], spaceNodes[39], spaceNodes[57] },
            { spaceNodes[48], spaceNodes[50], spaceNodes[40], spaceNodes[58] },
            { spaceNodes[49], spaceNodes[51], spaceNodes[41], spaceNodes[59] },
            { spaceNodes[50], spaceNodes[52], spaceNodes[42], spaceNodes[60] },
            { spaceNodes[51], spaceNodes[53], spaceNodes[43], spaceNodes[61] },
            { spaceNodes[52], spaceNodes[54], spaceNodes[44], spaceNodes[62] },
            // Row 6: SpaceNodes[54..62]
            { spaceNodes[53], spaceNodes[55], spaceNodes[45], spaceNodes[63] },
            { spaceNodes[54], spaceNodes[56], spaceNodes[46], spaceNodes[64] },
            { spaceNodes[55], spaceNodes[57], spaceNodes[47], spaceNodes[65] },
            { spaceNodes[56], spaceNodes[58], spaceNodes[48], spaceNodes[66] },
            { spaceNodes[57], spaceNodes[59], spaceNodes[49], spaceNodes[67] },
            { spaceNodes[58], spaceNodes[60], spaceNodes[50], spaceNodes[68] },
            { spaceNodes[59], spaceNodes[61], spaceNodes[51], spaceNodes[69] },
            { spaceNodes[60], spaceNodes[62], spaceNodes[52], spaceNodes[70] },
            { spaceNodes[61], spaceNodes[63], spaceNodes[53], spaceNodes[71] },
            // Row 7: SpaceNodes[63..71]
            { spaceNodes[62], spaceNodes[64], spaceNodes[54], spaceNodes[72] },
            { spaceNodes[63], spaceNodes[65], spaceNodes[55], spaceNodes[73] },
            { spaceNodes[64], spaceNodes[66], spaceNodes[56], spaceNodes[74] },
            { spaceNodes[65], spaceNodes[67], spaceNodes[57], spaceNodes[75] },
            { spaceNodes[66], spaceNodes[68], spaceNodes[58], spaceNodes[76] },
            { spaceNodes[67], spaceNodes[69], spaceNodes[59], spaceNodes[77] },
            { spaceNodes[68], spaceNodes[70], spaceNodes[60], spaceNodes[78] },
            { spaceNodes[69], spaceNodes[71], spaceNodes[61], spaceNodes[79] },
            { spaceNodes[70], spaceNodes[72], spaceNodes[62], spaceNodes[80] },
            // Row 8: SpaceNodes[72..80]
            { spaceNodes[71], spaceNodes[73], spaceNodes[63], ibNodeArr[0] },
            { spaceNodes[72], spaceNodes[74], spaceNodes[64], spaceNodes[2] },
            { spaceNodes[73], spaceNodes[75], spaceNodes[65], ibNodeArr[1] },
            { spaceNodes[74], spaceNodes[76], spaceNodes[66], ibNodeArr[2] },
            { spaceNodes[75], spaceNodes[77], spaceNodes[67], spaceNodes[5] },
            { spaceNodes[76], spaceNodes[78], spaceNodes[68], ibNodeArr[3] },
            { spaceNodes[77], spaceNodes[79], spaceNodes[69], ibNodeArr[4], },
            { spaceNodes[78], spaceNodes[80], spaceNodes[70], exitNode },
            { spaceNodes[79], ibNodeArr[0],   spaceNodes[71], ibNodeArr[5] },
        };
        /*
         * Based on the initializer above, set up key traversal for each
         * SpaceNode instance on the Sudoku board.
         */
        for (SpaceNode sn : spaceNodes) {
            int i = sn.getSpace().getRow() * NROWS + sn.getSpace().getColumn();
            sn.setKeyLeftNode((ParentWithKeyTraversal)
                    SpaceTraversalArr[i][ArrowKey.LEFT.ordinal()]);
            sn.setKeyRightNode((ParentWithKeyTraversal)
                    SpaceTraversalArr[i][ArrowKey.RIGHT.ordinal()]);
            sn.setKeyUpNode((ParentWithKeyTraversal)
                    SpaceTraversalArr[i][ArrowKey.UP.ordinal()]);
            sn.setKeyDownNode((ParentWithKeyTraversal)
                    SpaceTraversalArr[i][ArrowKey.DOWN.ordinal()]);
        }
        /*
         * For traversing the IconButtons, this table determines
         * which node to bring focus to next, based on the current IconButton
         * instance in focus and the provided key input:
         * { ArrowKey.LEFT, ArrowKey.RIGHT, ArrowKey.UP, ArrowKey.DOWN }
         */
       Parent ibTraversalArr[][] = {
            { spaceNodes[80], ibNodeArr[1], spaceNodes[72], spaceNodes[1] },
            { ibNodeArr[0],   ibNodeArr[2], spaceNodes[74], spaceNodes[3] },
            { ibNodeArr[1],   ibNodeArr[3], spaceNodes[75], spaceNodes[4] },
            { ibNodeArr[2],   ibNodeArr[4], spaceNodes[77], spaceNodes[6] },
            { ibNodeArr[3],   ibNodeArr[5], spaceNodes[78], minimizeNode },
            { ibNodeArr[4],   minimizeNode, spaceNodes[80], spaceNodes[0] },
        };
        /*
         * Based on the initializer above, set up key traversal for each
         * IconButton instance appearing on the Sudoku user interface.
         */
        for (int i=0; i<ibNodeArr.length; i++ ) {
            ibNodeArr[i].setKeyLeftNode((ParentWithKeyTraversal)
                ibTraversalArr[i][ArrowKey.LEFT.ordinal()]);
            ibNodeArr[i].setKeyRightNode((ParentWithKeyTraversal)
                ibTraversalArr[i][ArrowKey.RIGHT.ordinal()]);
            ibNodeArr[i].setKeyUpNode((ParentWithKeyTraversal)
                ibTraversalArr[i][ArrowKey.UP.ordinal()]);
            ibNodeArr[i].setKeyDownNode((ParentWithKeyTraversal)
                ibTraversalArr[i][ArrowKey.DOWN.ordinal()]);
        }
        /*
         * Connect up the minimizeNode
         */
        minimizeNode.setKeyLeftNode(ibNodeArr[5]);
        minimizeNode.setKeyRightNode(exitNode);
        minimizeNode.setKeyUpNode(ibNodeArr[4]);
        minimizeNode.setKeyDownNode(spaceNodes[7]);
        /*
         * Connect up the exitNode
         */
        exitNode.setKeyLeftNode(minimizeNode);
        exitNode.setKeyRightNode(spaceNodes[0]);
        exitNode.setKeyUpNode(spaceNodes[79]);
        exitNode.setKeyDownNode(spaceNodes[8]);
    }

    private void processKeyEvent(KeyCode key) {
        /*
         * Ignore keyboard input in this method if it doesn't have focus.  Not
         * sure why this method is called if it doesn't have keyboard focus.
         * Bug?
         */
        if (!isFocused()) {
            return;
        }
        ParentWithKeyTraversal highlight = null;
        boolean validKey = false;
        boolean selected = false;
        switch(key) {
            case LEFT:
            case KP_LEFT:
                highlight = lastFocused.getKeyLeftNode();
                validKey = true;
                break;
            case RIGHT:
            case KP_RIGHT:
            case TAB:
                highlight = lastFocused.getKeyRightNode();
                validKey = true;
                break;
            case UP:
            case KP_UP:
                highlight = lastFocused.getKeyUpNode();
                validKey = true;
                break;
            case DOWN:
            case KP_DOWN:
                highlight = lastFocused.getKeyDownNode();
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
                if (lastFocused instanceof SpaceNode) {
                    int numberKey = key.ordinal() - KeyCode.DIGIT0.ordinal();
                    Space space = ((SpaceNode)lastFocused).getSpace();
                    space.setNumberEditable(numberKey);
                    board.clearConflicts();
                    ArrayList<Space> conflicts = board.getConflictsOptimized();
                    for (Space conflict : conflicts) {
                        conflict.setConflict();
                    }
                }
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
                if (lastFocused instanceof SpaceNode) {
                    int numberKey = key.ordinal() - KeyCode.NUMPAD0.ordinal();
                    Space space = ((SpaceNode)lastFocused).getSpace();
                    space.setNumberEditable(numberKey);
                    board.clearConflicts();
                    ArrayList<Space> conflicts = board.getConflictsOptimized();
                    for (Space conflict : conflicts) {
                        conflict.setConflict();
                    }
                }
                break;
            case ENTER:
                highlight = lastFocused;
                validKey = true;
                selected = true;
                break;
        }
        if (validKey) {
            lastFocused.unShowFocusHint();
            highlight.showFocusHint();
        }
        if (selected) {
            highlight.invokeAction();
        }
    }
}