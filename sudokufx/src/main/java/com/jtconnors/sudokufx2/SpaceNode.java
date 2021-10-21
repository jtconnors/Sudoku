
package com.jtconnors.sudokufx2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import static com.jtconnors.sudokufx2.Globals.IMAGE_FILES_PREFIX;
import static com.jtconnors.sudokufx2.Globals.lastFocused;

public class SpaceNode extends ParentWithKeyTraversal {

    private static final double SCALE_DURATION = 300;      // In milliseconds
    private static final double INCREASE_SCALE = 1.25;

    private static final String[] imageFiles = {
    // 0-9 represents editable spaces, 0 being a blank space
    IMAGE_FILES_PREFIX + "blank.png",
    IMAGE_FILES_PREFIX + "1-white.png",
    IMAGE_FILES_PREFIX + "2-white.png",
    IMAGE_FILES_PREFIX + "3-white.png",
    IMAGE_FILES_PREFIX + "4-white.png",
    IMAGE_FILES_PREFIX + "5-white.png",
    IMAGE_FILES_PREFIX + "6-white.png",
    IMAGE_FILES_PREFIX + "7-white.png",
    IMAGE_FILES_PREFIX + "8-white.png",
    IMAGE_FILES_PREFIX + "9-white.png",
    // 10: Should never get here
    IMAGE_FILES_PREFIX + "blank.png",
    // 11-19 represents non-editable (bold) spaces
    IMAGE_FILES_PREFIX + "1-bold.png",
    IMAGE_FILES_PREFIX + "2-bold.png",
    IMAGE_FILES_PREFIX + "3-bold.png",
    IMAGE_FILES_PREFIX + "4-bold.png",
    IMAGE_FILES_PREFIX + "5-bold.png",
    IMAGE_FILES_PREFIX + "6-bold.png",
    IMAGE_FILES_PREFIX + "7-bold.png",
    IMAGE_FILES_PREFIX + "8-bold.png",
    IMAGE_FILES_PREFIX + "9-bold.png",
    // 20: Should never get here
    IMAGE_FILES_PREFIX + "blank.png",
    // 21-29 represents editable spaces in conflict
    IMAGE_FILES_PREFIX + "1-red.png",
    IMAGE_FILES_PREFIX + "2-red.png",
    IMAGE_FILES_PREFIX + "3-red.png",
    IMAGE_FILES_PREFIX + "4-red.png",
    IMAGE_FILES_PREFIX + "5-red.png",
    IMAGE_FILES_PREFIX + "6-red.png",
    IMAGE_FILES_PREFIX + "7-red.png",
    IMAGE_FILES_PREFIX + "8-red.png",
    IMAGE_FILES_PREFIX + "9-red.png",
    // 30: Should never get here
    IMAGE_FILES_PREFIX + "blank.png",
    // 31-39 represents non-editable spaces in conflict
    IMAGE_FILES_PREFIX + "1-red-bold.png",
    IMAGE_FILES_PREFIX + "2-red-bold.png",
    IMAGE_FILES_PREFIX + "3-red-bold.png",
    IMAGE_FILES_PREFIX + "4-red-bold.png",
    IMAGE_FILES_PREFIX + "5-red-bold.png",
    IMAGE_FILES_PREFIX + "6-red-bold.png",
    IMAGE_FILES_PREFIX + "7-red-bold.png",
    IMAGE_FILES_PREFIX + "8-red-bold.png",
    IMAGE_FILES_PREFIX + "9-red-bold.png"
    };

    // Cached copy of all number images
    private static Image[] images = new Image[imageFiles.length];
    static  {
        int index = 0;
        for (String url : imageFiles) {
            images[index++] = new Image(url);
        }
    }

    /*
     * All SpaceNodes point back to one BoardNode reference.  Set in
     * BoardNode.java
     */
    private static BoardNode boardNode;
    public static BoardNode getBoardNode() { return boardNode ; }
    public static void setBoardNode(BoardNode bn) { boardNode = bn; }

    private double imageSize;
    private Space space;
    private ImageView imageView;
    private ScaleTransition scaleOut;
    private ScaleTransition scaleIn;
    private FadeTransition fadeOut;
    private FadeTransition fadeIn;

    public SpaceNode(Space space, double imageSize) {
        this(space, imageSize, 1.0f);
    }

    public SpaceNode(Space space, double imageSize, double scaleFactor) {
        /*
         * Each SpaceNode has a Space instance.  "Bind" to the Space's number
         * variable such that when it is modified, the display for the
         * SpaceNode will be updated to match the new number value.
         */
        space.numberProperty().addListener((Observable ov) -> {
            updateSpaceNodeDisplay();
        });
        this.space = space;

        this.imageSize = imageSize;
        recalculateScale(scaleFactor);
        imageView = new ImageView();
        imageView.setFitHeight(this.imageSize);
        imageView.setFitWidth(this.imageSize);
        imageView.setImage(images[0]);  // Blank

        scaleOut = new ScaleTransition(
                Duration.millis(SCALE_DURATION), imageView);
        scaleOut.setFromX(1.0f);
        scaleOut.setFromY(1.0f);
        scaleOut.setToX(INCREASE_SCALE);
        scaleOut.setToY(INCREASE_SCALE);

        fadeIn = new FadeTransition(
                Duration.millis(SCALE_DURATION), imageView);
        fadeIn.setFromValue(0f);
        fadeIn.setToValue(1f);

        imageView.setOnMouseEntered((MouseEvent event) -> {
            if (getSpace().isEditable()) {
                if (lastFocused != null) {
                    lastFocused.unShowFocusHint();
                }
                showFocusHint();
            }
        });

        scaleIn = new ScaleTransition(
                Duration.millis(SCALE_DURATION), imageView);
        scaleIn.setFromX(INCREASE_SCALE);
        scaleIn.setFromY(INCREASE_SCALE);
        scaleIn.setToX(1.0f);
        scaleIn.setToY(1.0f);

        fadeOut = new FadeTransition(
                Duration.millis(SCALE_DURATION), imageView);
        fadeOut.setFromValue(1f);
        fadeOut.setToValue(0f);

        imageView.setOnMouseExited((MouseEvent event) -> {
            if (getSpace().isEditable()) {
                unShowFocusHint();
            }
        });
        imageView.setOnMousePressed((MouseEvent event) -> {
            if (getSpace().isEditable()) {
                if (action != null) {
                    action.invoke();
                }
            }
        });

        updateSpaceNodeDisplay();
        getChildren().add(imageView);
    }

    private void updateSpaceNodeDisplay() {
        imageView.setImage(images[getSpace().getNumberRaw()]);
        if (getSpace().isEmpty()) {
            imageView.setOpacity(0f);
        } else {
            imageView.setOpacity(1f);
        }
    }

    private void recalculateScale(double scaleFactor) {
        imageSize = imageSize * scaleFactor;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    /*
     * The following methods, declared in
     * ParentWithKeyTraversal, are overriden below.
     */
    @Override
    public void showFocusHint() {
        super.showFocusHint();
        scaleOut.stop();
        scaleOut.setFromX(imageView.getScaleX());
        scaleOut.setFromY(imageView.getScaleY());
        if (getSpace().isEmpty()) {
            fadeIn.stop();
            fadeIn.setFromValue(imageView.getOpacity());
        }
        scaleOut.playFromStart();
        if (getSpace().isEmpty()) {
            fadeIn.playFromStart();
        }
        lastFocused = this;
    }

    @Override
    public void unShowFocusHint() {
        super.unShowFocusHint();
        scaleIn.stop();
        scaleIn.setFromX(imageView.getScaleX());
        scaleIn.setFromY(imageView.getScaleY());
        if (getSpace().isEmpty()) {
            fadeOut.stop();
            fadeOut.setFromValue(imageView.getOpacity());
        }
        scaleIn.playFromStart();
        if (getSpace().isEmpty()) {
            fadeOut.playFromStart();
        }
    }
}