
package com.jtconnors.sudokufx2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;

public class SliderNode extends Parent {
    private double scaleFactor = 1f;

    class SliderThumb extends ParentWithKeyTraversal {
        private double thumbWidth;
        private double thumbHeight;
        private double scaleFactor;
        private Rectangle rect;

        public SliderThumb() {
            this(1.0f);
        }
        public SliderThumb(double scaleFactor) {
            this.scaleFactor = scaleFactor;
            thumbWidth = 20f * scaleFactor;
            thumbHeight = 12f * scaleFactor;
            rect = new Rectangle();
            rect.setWidth(thumbWidth);
            rect.setHeight(thumbHeight);
            rect.setArcWidth(7 * this.scaleFactor);
            rect.setArcHeight(7 * this.scaleFactor);
            Stop[] fillStops = new Stop[] {
                new Stop(0f, Color.rgb(107, 107, 107)),
                new Stop(0.55f, Color.BLACK),
                new Stop(0.7f, Color.rgb(75, 75, 75)),
                new Stop(1.0f, Color.rgb(23, 23, 23)),
            };
            LinearGradient fillLinearGradient = new LinearGradient(0f, 0f, 0f, 1f,
                    true, CycleMethod.NO_CYCLE, fillStops);
            rect.setFill(fillLinearGradient);
            rect.setStroke(Color.BLUE);

            Polygon leftTriangle = new Polygon();
            leftTriangle.setLayoutX((thumbWidth/2f-3.8f));
            leftTriangle.setLayoutY((thumbHeight/2f));
            leftTriangle.getPoints().addAll(new Double[] {
                0d, 0d,
                3d * scaleFactor, -3d * scaleFactor,
                3d * scaleFactor, 3d * scaleFactor });
            leftTriangle.setFill(Color.WHITE);

            Polygon rightTriangle = new Polygon();
            rightTriangle.setLayoutX((thumbWidth/2f+1.8f));
            rightTriangle.setLayoutY((thumbHeight/2f));
            rightTriangle.getPoints().addAll(new Double[] {
                3d * scaleFactor, 0d,
                0d, -3d * scaleFactor,
                0d, 3d * scaleFactor });
            rightTriangle.setFill(Color.WHITE);

            Group group = new Group();
            group.setCursor(Cursor.HAND);
            group.getChildren().addAll(rect, leftTriangle, rightTriangle);
            getChildren().add(group);
        }

        @Override
        public void showFocusHint() {
            super.showFocusHint();
            rect.setStroke(Color.BLUE);
            requestFocus();
        }

        @Override
        public void unShowFocusHint() {
            super.unShowFocusHint();
            rect.setStroke(Color.TRANSPARENT);
        }
    }

    private double value = 0.0f;
    private double minimum = 0.0f;
    private double maximum = 1.0f;
    private int numTicks;
    private String leftLabel;
    private String rightLabel;

    private final double thumbWidth = 20f;
    private final double thumbHeight = 12f;
    private final double startingWidth = 150f;
    private final double startingHeight = 170f;

    // Should be recalculated whenever scaleFactor changes
    private double scaledWidth;
    private double scaledHeight;
    private double padding;
    private double width;
    private double height;
    private double tickWidth;

    private CloseButtonNode closeButtonNode;
    private SliderThumb sliderThumb;
    /*
     * Pointer to a Node which blocks mouse input to entire board
     * while this Node is visible
     */
    public Node mouseBlocker;

    private double origValue = 0f;

    /*
     * The value of the SliderNode object adjusted to the range [0,1]
     * Create a Beans DoubleProperty so that changes to this variable
     * can be monitored.
     */
    private DoubleProperty adjValue;

    public final void setAdjValue(double adjValue) {
        adjValueProperty().setValue(adjValue);
    }

    public final double getAdjValue() {
        return adjValue == null ? 0.0 : adjValue.getValue();
    }

     public DoubleProperty adjValueProperty() {
        if (adjValue == null) {
            adjValue = new DoublePropertyBase() {
                
                @Override
                public Object getBean() {
                    return SliderNode.this;
                }
                
                @Override
                public String getName() {
                    return "adjValue";
                }    
            };
        }
        return adjValue;
    }


    public SliderNode(int numTicks,
            String leftLabel, String rightLabel) {
        this(numTicks, leftLabel, rightLabel, 1.0f);
    }
        
    public SliderNode(int numTicks,
            String leftLabel, String rightLabel, double scaleFactor) {
        this.numTicks = numTicks;
        this.leftLabel = leftLabel;
        this.rightLabel = rightLabel;
        this.scaleFactor = scaleFactor;
        recalculateScale();
        Group group = new Group();
        group.getChildren().add(createCloseButtonGroup());
        group.getChildren().add(createSliderBarGroup());
        getChildren().add(group); 
    }

    private void recalculateScale() {
        scaledWidth = startingWidth * scaleFactor;
        scaledHeight = startingHeight * scaleFactor;
        padding = 20f * scaleFactor;
        width = scaledWidth + (2f * padding);
        height = scaledHeight;
        tickWidth = (numTicks <= 0f) ? 0f : scaledWidth / (numTicks-1f);
        setAdjValue((value - minimum) / (maximum - minimum));
    }

    private Node createCloseButtonGroup() {
        Rectangle rect = new Rectangle();
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setArcWidth(10f * scaleFactor);
        rect.setArcHeight(10f * scaleFactor);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.WHITE);
        rect.setOpacity(0.8f);

        closeButtonNode = new CloseButtonNode(scaleFactor);
        closeButtonNode.setLayoutX(width/2f -
                closeButtonNode.getLayoutBounds().getWidth()/2f);  // bind?
        closeButtonNode.setLayoutY(height -
                closeButtonNode.getLayoutBounds().getHeight() - padding);    // bind?
        closeButtonNode.setOnMousePressed((MouseEvent event) -> {
            setVisible(false);
            if (mouseBlocker != null) {
                mouseBlocker.setVisible(false);
            }
        });
        closeButtonNode.setOnKeyPressed((KeyEvent ke) -> {
            KeyCode key = ke.getCode();
            if (key == KeyCode.TAB ||
                    key == KeyCode.UP || key == KeyCode.KP_UP ||
                    key == KeyCode.DOWN || key == KeyCode.KP_DOWN) {
                sliderThumb.showFocusHint();
                closeButtonNode.unShowFocusHint();
            }
            if (key == KeyCode.ENTER) {
                setVisible(false);
                if (mouseBlocker != null) {
                    mouseBlocker.setVisible(false);
                }
            }
        });
        closeButtonNode.setFocusTraversable(true);
        Group group = new Group();
        group.getChildren().addAll(rect, closeButtonNode);
        return group;
    }

    private Node createSliderBarGroup() {
        Group group = new Group();
        group.setLayoutX(padding);
        group.setLayoutY(height / 3f);

        // Slider Bar
        Rectangle rect = new Rectangle();
        rect.setWidth(scaledWidth);             // bind?
        rect.setHeight(8f * scaleFactor);
        rect.setArcWidth(10 * scaleFactor);
        rect.setArcHeight(10 * scaleFactor);
        Stop[] fillStops = new Stop[] {
            new Stop(0.0f, Color.rgb(172, 172, 172)),
            new Stop(0.6f, Color.rgb(115, 115, 115)),
            new Stop(1.0f, Color.rgb(124, 124, 124)),
        };
        LinearGradient fillLinearGradient = new LinearGradient(0f, 0f, 0f, 1f,
                true, CycleMethod.NO_CYCLE, fillStops);
        rect.setFill(fillLinearGradient);
        Stop[] strokeStops = new Stop[] {
            new Stop(0f, Color.rgb(15, 15, 15)),
            new Stop(1.0f, Color.rgb(224, 224, 224)),
        };
        LinearGradient strokeLinearGradient = new LinearGradient(0f, 0f,
                0f, 1f, true, CycleMethod.NO_CYCLE, strokeStops);
        rect.setStroke(strokeLinearGradient);
        group.getChildren().add(rect);

        // Tick marks below slider bar
        for (int i=0; i<numTicks; i++) {
            Line line = new Line();
            line.setLayoutY(12f * scaleFactor);
            line.setStartX(i * tickWidth - 1f);
            line.setStartY(0f);
            line.setEndX(i * tickWidth - 1f);
            line.setEndY(6f * scaleFactor);
            line.setStrokeWidth(2f * scaleFactor);
            group.getChildren().add(line);
        }

        // Left label
        Text leftText = new Text();
        leftText.setLayoutX(- (thumbWidth / 2f) * scaleFactor);
        leftText.setLayoutY(padding);
        leftText.setTextAlignment(TextAlignment.LEFT);
        leftText.setTextOrigin(VPos.TOP);
        leftText.setText(leftLabel);
        leftText.setFill(Color.BLACK);
        leftText.setFont(Font.font("Sans serif", 15f * scaleFactor));
        group.getChildren().add(leftText);
        // Right label
        Text rightText = new Text();
        rightText.setTextAlignment(TextAlignment.RIGHT);
        rightText.setTextOrigin(VPos.TOP);
        rightText.setText(rightLabel);
        rightText.setFill(Color.BLACK);
        rightText.setFont(Font.font("Sans serif", 15f * scaleFactor));
        rightText.setLayoutX(scaledWidth -
                rightText.getLayoutBounds().getWidth() + (thumbWidth / 2));
        rightText.setLayoutY(padding);
        group.getChildren().add(rightText);

        sliderThumb = new SliderThumb(scaleFactor);
        sliderThumb.setLayoutX(0f);
        sliderThumb.setTranslateX(- (thumbWidth/2) * scaleFactor);
        sliderThumb.setLayoutY(-2f * scaleFactor);
        sliderThumb.setOnMousePressed((MouseEvent event) -> {
            origValue = getAdjValue();
        });
        sliderThumb.setOnMouseDragged((MouseEvent event) -> {
            double v = origValue + ((double)event.getX() / scaledWidth);
            if (v < 0) {
                v = 0;
            } else if (v > 1) {
                v = 1;
            }
            value = minimum + (v * (maximum - minimum));
            setAdjValue((value - minimum) / (maximum - minimum));
            origValue=getAdjValue();
            sliderThumb.setTranslateX(((getAdjValue() * startingWidth) -
                    (thumbWidth / 2)) * scaleFactor);
        });
        group.getChildren().add(sliderThumb);
        sliderThumb.setOnKeyPressed((KeyEvent ke) -> {
            KeyCode key = ke.getCode();
            if (null != key) switch (key) {
                case TAB:
                case UP:
                case KP_UP:
                case DOWN:
                case KP_DOWN:
                    if (sliderThumb.hasFocusHint()) {
                        closeButtonNode.showFocusHint();
                        sliderThumb.unShowFocusHint();
                    } else {
                        sliderThumb.showFocusHint();
                        closeButtonNode.unShowFocusHint();
                    }   break;
                case RIGHT:
                case KP_RIGHT:
                case LEFT:
                case KP_LEFT:
                    if (key == KeyCode.RIGHT || key == KeyCode.KP_RIGHT) {
                        if (value < maximum)
                            value = Math.min(maximum, value + 0.1f);
                    }
                    else {
                        if(value > 0)
                            value = Math.max(minimum, value - 0.1f);
                    }   setAdjValue((value - minimum) / (maximum - minimum));
                    sliderThumb.setTranslateX(((getAdjValue() * startingWidth) -
                            (thumbWidth / 2)) * scaleFactor);
                    break;
                case ENTER:
                    if (closeButtonNode.hasFocusHint()) {
                        setVisible(false);
                        if (mouseBlocker != null) {
                            mouseBlocker.setVisible(false);
                        }
                    }   break;
                default:
                    break;
            }
        });
        sliderThumb.setFocusTraversable(true);
        sliderThumb.requestFocus();

        return group;
    }

    public void setMouseBlocker(Node mouseBlocker) {
        this.mouseBlocker = mouseBlocker;
    }

    public void popup() {
        setVisible(true);
        requestFocus();
        if (mouseBlocker != null) {
            mouseBlocker.setVisible(true);
        }
        sliderThumb.showFocusHint();
        closeButtonNode.unShowFocusHint();
    }
}