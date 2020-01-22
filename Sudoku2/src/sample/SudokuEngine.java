package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.beans.ExceptionListener;
import java.io.Console;
import java.util.*;

public class SudokuEngine {
    private SudokuBoard Board;
    private SudokuGraphics Graphics;
    private boolean showPossibleNumbers;
    private boolean showInvalidFields;

    private boolean isDebug = false;

    public SudokuEngine(SudokuBoard board, SudokuGraphics graphics) {
        this.Board = board;
        this.Graphics = graphics;
        this.showInvalidFields = false;
        this.showInvalidFields = false;
        ComputePossibleNumbers();

    }

    private void setDebug(boolean debug) {
        isDebug = debug;
    }

    public void setShowPossibleNumbers(boolean showPossibleNumbers) {
        this.showPossibleNumbers = showPossibleNumbers;
    }

    public void setShowInvalidFields(boolean showInvalidFields) {
        this.showInvalidFields = showInvalidFields;
        setDebug(showInvalidFields);
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
        try {
            ComputePossibleNumbers();
            UpdateGraphics();
            ValidateFields();
        } catch (Exception ex) {
            Exception e = new Exception();
            e.addSuppressed(ex);
            e.printStackTrace();
        }
    }

    public void Update(int row, int column, int mainNumber) throws Exception {
        //Validate(row, column, mainNumber);
        Board.setValueAt(row, column, mainNumber);

        Update();
    }

    public void Update(boolean showPossibleNumbers)
            throws Exception {
        try {


            setShowPossibleNumbers(showPossibleNumbers);
            Update();
        } catch (Exception ex) {
            Exception e = new Exception();
            e.addSuppressed(ex);
            throw e;
        }
    }

    public void ValidateFields() {
        System.out.println("VALIDATE FIELDS START");
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
        System.out.println("VALIDATE FIELDS END");
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

    //TODO:CHECK CORRECTNESS
    private boolean ValidateSquare(int row, int column, int value) {
        int SquareRow = row / 3;
        int SquareColumn = column / 3;
        //System.out.print("SQUARE (" + SquareRow + "," + SquareColumn + ")");
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                if (row == (i + SquareRow * 3) && column == (j + SquareColumn * 3))
                    continue;
                if (Board.getValueAt(SquareRow * 3 + i, SquareColumn * 3 + j) == value)
                    return false;
            }
        return true;
    }

    public void setOnMouseEventForFields(EventHandler<MouseEvent> eventForFields) {
        this.getGraphics().setOnMouseEventForFields(eventForFields);
    }

    public boolean ValidateSolution() throws Exception {
        try {
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
        } catch (Exception ex) {
            throw new Exception("Exception in function ValidateSolution()."
                    + ex.getMessage());
        }
    }

    public void Reset() {
        //throws Exception{
        Board.Reset();
        ComputePossibleNumbers();
        Update();
    }

    public void Solve()
            throws Exception {
        Solve(false);
    }

    public boolean Solve(boolean showAnimation)
            throws Exception {
        Board.Reset();
        ComputePossibleNumbers();

        return SolveStepRecursive(0, 0, showAnimation);
    }

    public boolean SolveStepRecursive(int row, int column, boolean showAnimation)
            throws Exception {
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
                            Platform.runLater(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Update();
                                                //Thread.sleep(5);
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                            Thread.sleep(50);
                        }
                        if (SolveStepRecursive((id + 1) / 9, (id + 1) % 9, showAnimation))
                            return true;
                    }
                }
            }
            getBoard().setValueAt(row, column, 0);
            ComputePossibleNumbers();
            return false;
        } catch (Exception ex) {
            Exception e = new Exception();
            e.addSuppressed(ex);
            throw e;
            //return false;
        }

    }

    public void Generate(int difficulty, long seed) {
        Random random = new Random(seed);
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        List<Integer> cluesList = new ArrayList<>();
        int row, column, id;

        try {
            for (int i = 0; i < 81; i++)
                cluesList.add(i);
            Collections.shuffle(cluesList, random);


            //Board = SudokuBoard.InitWithZero();

            int newBoard[][] = new int[9][9];
            for (int k = 0; k < 9; k = k + 3) {
                Collections.shuffle(numbers, random);
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        newBoard[k + i][k + j] = numbers.get(i + j * 3);
            }
            Board = new SudokuBoard(newBoard);

            this.Solve();


            newBoard = new int[9][9];
            for (int i = 0; i < difficulty; i++) {
                id = cluesList.get(i);
                row = id / 9;
                column = id % 9;
                newBoard[row][column] = this.getBoard().getValueAt(row, column);
            }

            Board = new SudokuBoard(newBoard);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
