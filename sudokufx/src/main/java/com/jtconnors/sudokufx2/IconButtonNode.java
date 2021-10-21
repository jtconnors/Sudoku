
package com.jtconnors.sudokufx2;

import javafx.event.EventHandler;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import javafx.util.Duration;
import static com.jtconnors.sudokufx2.Globals.*;


public class IconButtonNode extends ParentWithKeyTraversal {
    public static final double SCALE_DURATION = 300;  // In milliseconds
    public double scaleFactor = 1.0f;
    public double iconWidth;
    protected double iconWidthScaled;
    public double iconHeight;
    protected double iconHeightScaled;
    public String iconURL;
    public String iconString;
    public Color iconTextFill = Color.BLACK;
    public boolean iconStringOnAtInit = false;
    private ImageView iv;
    private Text text;
    private ScaleTransition scaleOut;
    private ScaleTransition scaleIn;
    private FadeTransition fadeOut;
    private FadeTransition fadeIn;
    
    // Pointer back to BoardNode
    private BoardNode boardNode;

    public IconButtonNode(BoardNode boardNode,
            double width, double height, String URL, String text) {
        this(boardNode, width, height, URL, text, false);
    }

    public IconButtonNode(BoardNode boardNode,
            double width, double height, String URL, String text,
            boolean iconStringOnAtInit) {
        this.boardNode = boardNode;
        iconWidth = width;
        iconWidthScaled = iconWidth * scaleFactor;
        iconHeight = height;
        iconHeightScaled = iconHeight * scaleFactor;
        iconURL = URL;
        iconString = text;
        this.iconStringOnAtInit = iconStringOnAtInit;
        init();
    }

    private void init() {
        getChildren().add(createIconButtonGroup());
    }

    private Group createIconButtonGroup() {
        Image image = new Image(iconURL, iconWidthScaled, iconHeightScaled,
                false, false);
        iv = new ImageView();
        iv.setEffect(new Glow());
        iv.setScaleX(1.0f);
        iv.setScaleY(1.0f);
        iv.setImage(image);  
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.4f);
        iv.setEffect(reflection);
        final Glow glowAndReflection = new Glow();
        glowAndReflection.setLevel(0.3f);
        glowAndReflection.setInput(reflection);

        text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextOrigin(VPos.TOP);
        text.setFont(Font.font("Sans serif", FontWeight.NORMAL,
                (iconWidth*.5f) * scaleFactor));
        text.setText(iconString);
        text.setLayoutX((image.getWidth() / 2) -
                        (text.getLayoutBounds().getWidth()) / 2);
        text.setLayoutY(image.getHeight());
        text.setFill(iconTextFill);
        if (iconStringOnAtInit) {
            text.setOpacity(1f);
        } else {
            text.setOpacity(0f);
        }

        final Group group = new Group();
        group.getChildren().addAll(iv, text);

        iv.setOnMousePressed((MouseEvent event) -> {
            iv.setEffect(glowAndReflection);
        });
        iv.setOnMouseReleased((MouseEvent event) -> {
            iv.setEffect(reflection);
        });
        iv.setOnMouseClicked((MouseEvent event) -> {
            if (action != null) {
                action.invoke();
            }
        });

        scaleOut = new ScaleTransition(Duration.millis(SCALE_DURATION), this);
        scaleOut.setFromX(1.0f);
        scaleOut.setFromY(1.0f);
        scaleOut.setToX(1.25f);
        scaleOut.setToY(1.25f);
        
        fadeIn = new FadeTransition(Duration.millis(SCALE_DURATION), text);
        fadeIn.setFromValue(0f);
        fadeIn.setToValue(1f);

        iv.setOnMouseEntered((MouseEvent event) -> {
            if (lastFocused != null) {
                lastFocused.unShowFocusHint();
            }
            showFocusHint();
        });

        scaleIn = new ScaleTransition(Duration.millis(SCALE_DURATION), this);
        scaleIn.setFromX(1.25f);
        scaleIn.setFromY(1.25f);
        scaleIn.setToX(1.0f);
        scaleIn.setToY(1.0f);

        fadeOut = new FadeTransition(Duration.millis(SCALE_DURATION), text);
        fadeOut.setFromValue(1f);
        fadeOut.setToValue(0f);

        iv.setOnMouseExited((MouseEvent event) -> {
            unShowFocusHint();
        });
        return group;
    }
    
    public void setIconTextFill(Color color) {
        iconTextFill = color;
        text.setFill(iconTextFill);
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    public BoardNode getBoardNode() {
        return boardNode;
    }

    /*
     * The following methods, defined in ParentWithKeyTraversal,
     * are overriden and must call the superclass method first.
     */
    @Override
    public void showFocusHint() {
        super.showFocusHint();
        scaleOut.stop();
        scaleOut.setFromX(getScaleX());
        scaleOut.setFromY(getScaleY());
        fadeIn.stop();
        fadeIn.setFromValue(text.getOpacity());
        scaleOut.playFromStart();
        fadeIn.playFromStart();
        lastFocused = this;
    }

    @Override
    public void unShowFocusHint() {
        super.unShowFocusHint();
        scaleIn.stop();
        scaleIn.setFromX(getScaleX());
        scaleIn.setFromY(getScaleY());
        fadeOut.stop();
        fadeOut.setFromValue(text.getOpacity());
        scaleIn.playFromStart();
        fadeOut.playFromStart();
    }
}