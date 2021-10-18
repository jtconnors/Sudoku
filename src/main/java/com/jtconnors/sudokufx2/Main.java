
package com.jtconnors.sudokufx2;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import static com.jtconnors.sudokufx2.Globals.sudokuStage;

public class Main extends Application {

    private double initX;
    private double initY;

    @Override
    public void start(Stage stage) {
        sudokuStage = stage;
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("SudokuFX2");
        stage.show();

        BoardNode boardNode = new BoardNode(1.0f);

        final Rectangle dragArea = new Rectangle();
        dragArea.setWidth(boardNode.getMinimizeNode().getLayoutX() - 2);
        dragArea.setHeight(boardNode.getDragAreaRectangleHeight());
        dragArea.setFill(Color.WHITE);
        dragArea.setOpacity(0f);
//        dragArea.setBlocksMouse(true);
        dragArea.setOnMousePressed((MouseEvent me) -> {
            dragArea.setOpacity(0.1f);
            initX = me.getScreenX() - sudokuStage.getX();
            initY = me.getScreenY() - sudokuStage.getY();
        });
        dragArea.setOnMouseReleased((MouseEvent me) -> {
            dragArea.setOpacity(0f);
        });
        dragArea.setOnMouseDragged((MouseEvent me) -> {
            sudokuStage.setX(me.getScreenX() - initX);
            sudokuStage.setY(me.getScreenY() - initY);
        });
        Group group = new Group();
        group.getChildren().addAll(boardNode, dragArea);
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setHeight(boardNode.height);
        stage.setWidth(boardNode.width);
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
