package sample;

public class SudokuBoard {
    //Field - class for single field on the board. Allows to keep track if value is clue (read only) or not.
    class GridValue {
        private int value;
        private boolean isClue;
        private boolean[] possibleNumbers;
        private int possibleNumbersCount;

        public GridValue(int value, boolean isClue) {
            this.value = value;
            this.isClue = isClue;
            possibleNumbers = new boolean[9];
            setAllPossibleNumbers(true);
        }

        public GridValue(int value) {
            this(value, false);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) throws Exception {
            if (this.isClue)
                throw new Exception("Field is a clue and cannot be modified");
            else
                this.value = value;
        }

        public void setPossibleNumbers(int number, boolean isPossible)
        //Number from 1 to 9!!!!
        {
            this.possibleNumbers[number - 1] = isPossible;
            recountPossibleNumbers();
        }

        public boolean[] getPossibleNumbers() {
            return possibleNumbers;
        }

        public void setAllPossibleNumbers(boolean value) {
            for (int i = 0; i < this.possibleNumbers.length; i++)
                this.possibleNumbers[i] = value;
            recountPossibleNumbers();
        }

        public boolean isPossibleNumber(int number)
        //Number from 1 to 9!!!!
        {
            return this.possibleNumbers[number - 1];
        }

        public boolean isClue() {
            return isClue;
        }

        public int getPossibleNumbersCount() {
            return possibleNumbersCount;
        }

        public void recountPossibleNumbers() {
            possibleNumbersCount = 0;
            for (int i = 1; i < this.possibleNumbers.length; i++)
                possibleNumbersCount += this.possibleNumbers[i] ? 1 : 0;
        }
    }//END OF Field CLASS


    private GridValue[][] board;
    private int size = 9;


    private SudokuBoard(int size, GridValue[][] board) {
        this.size = size;
        setBoard(board);
    }

    public SudokuBoard(GridValue[][] board) {
        this(9, board);
    }

    public SudokuBoard(int[][] board) {
        int value;

        this.size = 9;
        this.board = new GridValue[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                value = board[i][j];
                this.board[i][j] = new GridValue(value, value > 0);
            }
    }

    static public SudokuBoard TestCase() {
        final int[][] testCase = {
                {0, 1, 0, 2, 0, 0, 0, 9, 4},
                {0, 5, 2, 0, 1, 4, 0, 7, 0},
                {7, 3, 0, 6, 0, 5, 0, 0, 1},
                {2, 0, 3, 4, 6, 0, 0, 0, 8},
                {0, 8, 1, 7, 3, 0, 0, 6, 2},
                {4, 0, 0, 0, 2, 8, 1, 0, 0},
                {0, 4, 8, 0, 5, 0, 3, 2, 0},
                {3, 2, 5, 1, 0, 7, 0, 0, 6},
                {6, 0, 0, 3, 4, 2, 8, 1, 5}

        };
        return new SudokuBoard(testCase);
    }

    public int getSize() {
        return size;
    }

    public GridValue[][] getBoard() {
        return board;
    }

    private void setBoard(GridValue[][] board) {
        this.board = board;
    }


    public GridValue getItemAt(int row, int column) {
        return board[row][column];
    }

    public int getValueAt(int row, int column) {
        return board[row][column].getValue();
    }

    public boolean isClue(int row, int column) {
        return this.getItemAt(row, column).isClue;
    }

    public void setValueAt(int row, int column, int value)
            throws Exception {
        board[row][column].setValue(value);
    }

    public void Reset() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (!board[i][j].isClue())
                    board[i][j].value = 0;
    }
}
