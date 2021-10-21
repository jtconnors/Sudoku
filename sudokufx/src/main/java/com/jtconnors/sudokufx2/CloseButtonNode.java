
package com.jtconnors.sudokufx2;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.VPos;

public class CloseButtonNode extends ParentWithKeyTraversal {

    public double scaleFactor;

    private double fontSize;
    // pointer to the Rectangle Node to highlight when in focus
    private Rectangle rect;
    
    public CloseButtonNode() {
        this(1.0f);
    }

    public CloseButtonNode(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        fontSize = 18.0f * scaleFactor ;
        init();
    }

    private void init() {
        getChildren().add(createCloseButtonGroup());
    }

    private Group createCloseButtonGroup() {
        final Group group = new Group();
        double padding = 5 * scaleFactor;

        final Text text = new Text();
        text.setLayoutX(padding);
        text.setLayoutY(padding);
        text.setTextOrigin(VPos.TOP);
        text.setText("Close");
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Sans Serif", FontWeight.BOLD, fontSize));

        rect = new Rectangle();
        rect.setWidth(text.getLayoutBounds().getWidth() + (2*padding));
        rect.setHeight(text.getLayoutBounds().getHeight() + (2*padding));
        rect.setArcWidth(8 * scaleFactor);
        rect.setArcHeight(8 * scaleFactor);
        Stop[] stops = new Stop[] {
            new Stop(0f, Color.rgb(172, 172, 172)),
            new Stop(0.6f, Color.rgb(115, 115, 115)),
            new Stop(1.0f, Color.rgb(124, 124, 124))
        };
        LinearGradient linearGradient = new LinearGradient(0f, 0f, 0f, 1f,
                true, CycleMethod.NO_CYCLE, stops);
        rect.setFill(linearGradient);
        
        group.getChildren().addAll(rect, text);
        final Glow glow = new Glow();
        glow.setLevel(0.3f);

        group.setOnMouseEntered((MouseEvent event) -> {
            rect.setEffect(glow);
        });
        group.setOnMouseExited((MouseEvent event) -> {
            rect.setEffect(null);
        });

        group.setFocusTraversable(true);
        return group;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public void showFocusHint() {
        super.showFocusHint();
        rect.setStroke(Color.BLUE);
    }

    @Override
    public void unShowFocusHint() {
        super.unShowFocusHint();
        rect.setStroke(Color.TRANSPARENT);
    }
}