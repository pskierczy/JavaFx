package sample;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;

public class SudokuGraphics
        extends Group {

    class GridField extends Group {
        private RectangleWithText mainNumber;
        private RectangleWithText[] possibleNumbers;
        private Rectangle border;
        /*
        Children:
        0-main number
        1-9 possible numbers
        10 - border
        */

        public GridField() {
            this(10, 0);
        }

        public GridField(int size, int number) {
            this.setLayoutX(0);
            this.setLayoutY(0);

            mainNumber = new RectangleWithText(size, String.valueOf(number));
            mainNumber.setFontWeight(FontWeight.BOLD);
            mainNumber.getText().setFill(Color.BLACK);
            mainNumber.getRectangle().setFill(Color.TRANSPARENT);
            this.getChildren().add(mainNumber);

            this.possibleNumbers = new RectangleWithText[9];
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    possibleNumbers[i * 3 + j] = new RectangleWithText(size / 3.0, String.valueOf(i * 3 + j + 1));
                    possibleNumbers[i * 3 + j].getRectangle().setStrokeWidth(0);
                    possibleNumbers[i * 3 + j].setLayoutX(i * size / 3.0);
                    possibleNumbers[i * 3 + j].setLayoutY(j * size / 3.0);
                }
            this.getChildren().addAll(possibleNumbers);

            border = new Rectangle(0, 0, size, size);
            border.setStrokeWidth(0.5);
            border.setStroke(Color.GREY);
            border.setFill(Color.TRANSPARENT);
            this.getChildren().add(border);
        }

//        public GridField(int size, SudokuBoard.ValueField valueField) {
//            this.setLayoutX(0);
//            this.setLayoutY(0);
//            //this.getChildren().add(new Rectangle(size,size));
//
//            //if (valueField.getValue() == 0) {
//            this.possibleNumbers = new RectangleWithText[9];
//            for (int i = 0; i < 3; i++)
//                for (int j = 0; j < 3; j++) {
//                    possibleNumbers[i * 3 + j] = new RectangleWithText(size / 3.0, String.valueOf(i * 3 + j + 1));
//                    possibleNumbers[i * 3 + j].getRectangle().setStrokeWidth(0);
//                    possibleNumbers[i * 3 + j].setLayoutX(i * size / 3.0);
//                    possibleNumbers[i * 3 + j].setLayoutY(j * size / 3.0);
//                }
//            this.getChildren().addAll(possibleNumbers);
//            //}
//
//            mainNumber = new RectangleWithText(size, String.valueOf(valueField.getValue()));
//            mainNumber.setFontWeight(FontWeight.BOLD);
//            mainNumber.getText().setFill(Color.BLACK);
//            mainNumber.getRectangle().setFill(Color.TRANSPARENT);
//            this.getChildren().add(mainNumber);
//
//        }

        GridField(int size) {
            this(size, 0);
        }

        public void setVisibility(int index, boolean visible) {
            this.getChildren().get(index).setVisible(visible);
        }

        private void setMainNumber(int number) {
            mainNumber.getText().setText(String.valueOf(number));
        }

        public void setMainNumberColor(Paint paint) {
            mainNumber.getText().setFill(paint);
        }

        public void Update(int mainNumber, boolean possibleNumbers[], boolean showPossibleNumbers) {
            if (mainNumber > -1) {
                setMainNumber(mainNumber);
                setVisibility(0, mainNumber > 0);
                setMainNumberColor(Color.BLACK);
            }

            for (int i = 1; i < 10; i++) {
                if (showPossibleNumbers)
                    setVisibility(i, mainNumber == 0 && possibleNumbers[i - 1]);
                else
                    setVisibility(i, false);
            }
        }

        public void Update(int mainNumber, boolean possibleNumbers[]) {
            Update(mainNumber, possibleNumbers, false);
        }

        public void Update(boolean showPossibleNumbers) {
            for (int i = 1; i < 10; i++)
                setVisibility(i, false);
        }

    }
    //END OF GridField class

    private int blockSize;

    public int getBlockSize() {
        return blockSize;
    }

    public void Initialize() {
        Initialize(30);
    }


    public void Initialize(int blockSize) {
        this.getChildren().clear();
        this.blockSize = blockSize;

        GridField gridField;
        Rectangle groupRectangle;

        //Draw main grid field with numbers
        for (int i = 0; i < 81; i++) {
            gridField = new GridField(blockSize);
            gridField.setLayoutX(blockSize * (i % 9));
            gridField.setLayoutY(blockSize * (i / 9));
            gridField.setId(String.valueOf(i));
            this.getChildren().add(gridField);
        }


        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                groupRectangle = new Rectangle(i * blockSize * 3, j * blockSize * 3, blockSize * 3, blockSize * 3);
                groupRectangle.setStrokeWidth(1);
                groupRectangle.setStroke(Color.BLACK);
                groupRectangle.setFill(Color.TRANSPARENT);
                groupRectangle.setMouseTransparent(true);
                this.getChildren().add(groupRectangle);
            }
    }

    public void setOnMouseClickEventForFields(EventHandler<MouseEvent> eventForFields) {
        for (int i = 0; i < 81; i++) {
            this.getChildren().get(i).setOnMouseClicked(eventForFields);
        }
    }

    public void setOnMouseEventForFields(EventHandler<MouseEvent> eventForFields) {
        for (int i = 0; i < 81; i++) {
            this.getChildren().get(i).setOnMouseClicked(eventForFields);
            this.getChildren().get(i).setOnMouseMoved(eventForFields);
            // this.getChildren().get(i).seton(eventForFields);

        }
    }

    public void Update(int row, int column, int mainValue, boolean possibleNumbers[], boolean showPossibleNumbers) {
        ((GridField) this.getChildren().get(row * 9 + column)).Update(mainValue, possibleNumbers, showPossibleNumbers);
    }

    public void Update(SudokuBoard mainBoard, boolean showPossibleNumbers) {
        SudokuBoard.GridValue gridValue;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                gridValue = mainBoard.getItemAt(i, j);
                Update(i, j, gridValue.getValue(), gridValue.getPossibleNumbers(), showPossibleNumbers);
            }
    }

    public void Update(SudokuBoard mainBoard) {
        Update(mainBoard, false);
    }

    public void Update(boolean showPossibleNumbers) {
        //SudokuBoard.GridValue gridValue;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                Update(showPossibleNumbers);
            }
    }

    public void setNumberColor(int row, int column, Paint color) {
        ((GridField) this.getChildren().get(row * 9 + column)).setMainNumberColor(color);
    }

}
