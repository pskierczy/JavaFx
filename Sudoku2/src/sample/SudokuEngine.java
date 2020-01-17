package sample;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Random;

public class SudokuEngine {
    private SudokuBoard Board;
    private SudokuGraphics Graphics;
    private boolean showPossibleNumbers;
    private boolean showInvalidFields;

    public SudokuEngine(SudokuBoard board, SudokuGraphics graphics) {
        this.Board = board;
        this.Graphics = graphics;
        this.showInvalidFields = false;
        this.showInvalidFields = false;
        ComputePossibleNumbers();

    }

    public void setShowPossibleNumbers(boolean showPossibleNumbers) {
        this.showPossibleNumbers = showPossibleNumbers;
    }

    public void setShowInvalidFields(boolean showInvalidFields) {
        this.showInvalidFields = showInvalidFields;
    }

    public SudokuBoard getBoard() {
        return Board;
    }

    public void setBoard(SudokuBoard board) {
        Board = board;
    }

    public SudokuGraphics getGraphics() {
        return Graphics;
    }

    public void setGraphics(SudokuGraphics graphics) {
        Graphics = graphics;
    }

    public void InitializeGraphics() {
        Graphics.Initialize();
    }

    public void InitializeGraphics(int blockSize) {
        Graphics.Initialize(blockSize);
    }

    public void UpdateGraphics() {
        Graphics.Update(Board, showPossibleNumbers);
    }


    public void UpdateGraphics(int row, int column, int mainNumber, boolean possibleNumbers[]) {
        Graphics.Update(row, column, mainNumber, possibleNumbers, showPossibleNumbers);
    }

    public void Update() {
        UpdateGraphics();
        ValidateFields();
    }

    public void Update(int row, int column, int mainNumber) throws Exception {
        Validate(row, column, mainNumber);
        Board.setValueAt(row, column, mainNumber);
        ComputePossibleNumbers();

        Update();
    }

    public void Update(boolean showPossibleNumbers) {
        setShowPossibleNumbers(showPossibleNumbers);
        Update();
    }

    public void ValidateFields() {
        boolean isFieldValid;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                if (Board.getItemAt(i, j).isClue())
                    this.getGraphics().setNumberColor(i, j, Color.BLUE);
                else {
                    isFieldValid = Validate(i, j);
                    if (showInvalidFields && !isFieldValid)
                        this.getGraphics().setNumberColor(i, j, Color.RED);
                    else
                        this.getGraphics().setNumberColor(i, j, Color.BLACK);
                }
            }
    }


    public void ValidateFields(boolean showInvalidFields) {
        this.showInvalidFields = showInvalidFields;
        ValidateFields();
    }


    public void ComputePossibleNumbers() {
        SudokuBoard.GridValue gridValue;

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                gridValue = Board.getItemAt(i, j);
                if (gridValue.isClue()) {
                    gridValue.setAllPossibleNumbers(false);
                } else {
                    for (int n = 1; n < 10; n++)
                        gridValue.setPossibleNumbers(n, Validate(i, j, n));
                }
            }
    }


    public boolean Validate(int row, int column, int value)
    //returns true if number is valid, false if rules violated
    {
        if (Board.isClue(row, column))
            return false;
        if (!ValidateRow(row, column, value))
            return false;
        if (!ValidateColumn(row, column, value))
            return false;
        if (!ValidateSquare(row, column, value))
            return false;

        return true;
    }

    private boolean Validate(int row, int column) {
        return Validate(row, column, getBoard().getValueAt(row, column));
    }

    private boolean ValidateRow(int row, int column, int value) {
        for (int i = 0; i < Board.getSize(); i++) {
            if (i == column)
                continue;
            if (Board.getValueAt(row, i) == value)
                return false;
        }
        return true;
    }

    private boolean ValidateColumn(int row, int column, int value) {
        for (int i = 0; i < Board.getSize(); i++) {
            if (i == row)
                continue;
            if (Board.getValueAt(i, column) == value)
                return false;
        }
        return true;
    }

    private boolean ValidateSquare(int row, int column, int value) {
        int SquareRow = row / 3;
        int SquareColumn = column / 3;
        for (int i = SquareRow * 3; i < 3; i++)
            for (int j = SquareColumn * 3; j < 3; j++) {
                if (i == row && j == column)
                    continue;
                if (Board.getValueAt(i, j) == value)
                    return false;
            }
        return true;
    }

    public void setOnMouseEventForFields(EventHandler<MouseEvent> eventForFields) {
        this.getGraphics().setOnMouseEventForFields(eventForFields);
    }

    public boolean ValidateSolution() throws Exception {
        SudokuBoard.GridValue gridValue;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                gridValue = Board.getItemAt(i, j);
                if (gridValue.getValue() == 0)
                    throw new Exception("SUDOKU NOT COMPLETED");
                if (gridValue.isClue()) {
                    continue;
                } else {
                    if (!Validate(i, j, gridValue.getValue()))
                        return false;
                }
            }
        return true;
    }

    public void Reset() {
        Board.Reset();
        ComputePossibleNumbers();
        Update();

    }

    public void Solve() {
        Solve(false);
    }

    public boolean Solve(boolean showAnimation) {
        Board.Reset();
        ComputePossibleNumbers();

        return SolveStepRecursive(0, 0, showAnimation);
    }

    public boolean SolveStepRecursive(int row, int column, boolean showAnimation) {
        int id = row * 9 + column;
        try {
            if (id > 80)
                return true;

            SudokuBoard.GridValue gridValue = getBoard().getItemAt(row, column);

            for (int i = 1; i < 10; i++) {
                if (gridValue.isClue())
                    return SolveStepRecursive((id + 1) / 9, (id + 1) % 9, showAnimation);
                if (gridValue.isPossibleNumber(i)) {
                    if (Validate(row, column, i)) {
                        getBoard().setValueAt(row, column, i);
                        ComputePossibleNumbers();
                        if (showAnimation) {
                            Update();
                            Thread.sleep(50);
                        }
                        if (SolveStepRecursive((id + 1) / 9, (id + 1) % 9, showAnimation))
                            return true;
                    }
                }
            }
            getBoard().setValueAt(row, column, 0);
            ComputePossibleNumbers();
            if (showAnimation) {
                Update();
                Thread.sleep(50);
            }
            Update();
            return false;
        } catch (Exception ex) {
            return false;
        }

    }

    //TO be implemented
    public void Generate(int difficulty, long seed)
    {
        Random random=new Random(seed);

        int newBoard[][]=new int[9][9];


        Board=new SudokuBoard(newBoard);
    }

}
