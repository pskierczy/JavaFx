package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;

public class SudokuGraphics_bak
        extends Group {

    class BoardField extends Group {
        private RectangleWithText mainNumber;
        private RectangleWithText[] possibleNumbers;

        BoardField(int size, int number) {
            this.setLayoutX(0);
            this.setLayoutY(0);
            //this.getChildren().add(new Rectangle(size,size));

            this.possibleNumbers = new RectangleWithText[9];
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    possibleNumbers[i * 3 + j] = new RectangleWithText(size / 3.0, String.valueOf(i * 3 + j + 1));
                    possibleNumbers[i * 3 + j].getRectangle().setStrokeWidth(0);
                    possibleNumbers[i * 3 + j].setLayoutX(i * size / 3.0);
                    possibleNumbers[i * 3 + j].setLayoutY(j * size / 3.0);
                }

            this.getChildren().addAll(possibleNumbers);

            mainNumber = new RectangleWithText(size, number == 0 ? "" : String.valueOf(number));
            mainNumber.setFontWeight(FontWeight.BOLD);
            mainNumber.getText().setFill(Color.BLACK);
            mainNumber.getRectangle().setFill(Color.TRANSPARENT);
            this.getChildren().add(mainNumber);

        }

//        BoardField(int size, SudokuBoard.ValueField valueField) {
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

        BoardField(int size) {
            this(size, 0);
        }

        BoardField() {
            this(10, 0);
        }

        public void setVisibility(int index, boolean visible)
        //0-8 possible numbers, 9 - main number,
        {
            if (index < 9)
                this.possibleNumbers[index].setVisible(visible);
            else
                this.mainNumber.setVisible(visible);
        }


//        @Override
//        public void handle(MouseEvent event) {
//            if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
//                if (this.borders.getFill() == Color.WHITE)
//                    this.borders.setFill(Color.YELLOW);
//                else
//                    this.borders.setFill(Color.WHITE);
//            }
//        }
    }

    int size = 9;
    private SudokuBoard mainBoard;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public SudokuBoard getMainBoard() {
        return mainBoard;
    }

    public void setMainBoard(SudokuBoard mainBoard) {
        this.mainBoard = mainBoard;
    }

//    public void Draw() {
//        int blockSize = 60;
//        Rectangle groupRectangle;
//        BoardField bf;
//
//        //Draw main grid
//        for (int i = 0; i < size; i++)
//            for (int j = 0; j < size; j++) {
//                bf = new BoardField(blockSize, mainBoard.getItemAt(i, j));
//                bf.setLayoutX(blockSize * j);
//                bf.setLayoutY(blockSize * i);
//                this.getChildren().add(bf);
//            }
//
//        //Draw group rectangles
//        for (int i = 0; i < 3; i++)
//            for (int j = 0; j < 3; j++) {
//                groupRectangle = new Rectangle(i * blockSize * 3, j * blockSize * 3, blockSize * 3, blockSize * 3);
//                groupRectangle.setStrokeWidth(1);
//                groupRectangle.setStroke(Color.BLACK);
//                groupRectangle.setFill(Color.TRANSPARENT);
//                this.getChildren().add(groupRectangle);
//            }
//
//        //Test purpose
//        //DrawAxes();
//    }

//    public void Redraw() {
//        BoardField bf;
//        SudokuBoard.ValueField valueField;
//        for (int i = 0; i < size; i++)
//            for (int j = 0; j < size; j++) {
//                bf = (BoardField) (this.getChildren().get(i + j * 3));
//                valueField = mainBoard.getItemAt(i, j);
//                if (valueField.getValue() == 0)
//                    bf.setVisibility(9, false);
//                else {
//                    bf.setVisibility(9, false);
////                    for (int n = 0; n < 9; n++)
////                        bf.setVisibility(n, valueField.isPossibleNumber(n + 1));
//                }
//            }
//    }

}
