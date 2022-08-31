import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

class Connect4Frame extends JFrame implements ActionListener {

    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    private Button randomButton, lowIQButton, miniMaxButton, alfaBetaButton,evaluateButton;
    private Label           lblSpacer;
    MenuItem                newMI, exitMI, redMI, yellowMI, pvp, pvr, rvr, pvpMinimax, playerVSminiMax, miniMaxVSminiMax;
    private int[][] board;
    private boolean                 end=false;
    private boolean                 gameStart;
    private int                     numberOfTurnsPlayer1Played = 1;
    private int                     numberOfTurnsPlayer2Played = 1;
    private int                     totalNumberOfTurnesPlayed = 1;
    private Panel           turnsPanel;
    private Label           player1TurnsPlayed;
    private Label           player2TurnsPlayed;
    private Label           playersTotalTurnsPlayed;
    private Label           numberOfTurns;

    private int[][]         scoreAtHorisontalFromLeft = new int[MAXROW][MAXCOL];
    private int[][]         scoreAtHorisontalFromRight = new int [MAXROW][MAXCOL];
    private int[][]         scoreAtVertical = new int[MAXROW][MAXCOL];
    private int[][]         scoreAtPositiveDiagonal = new int[MAXROW][MAXCOL];
    private int[][]         scoreAtNegativeDiagonal = new int[MAXROW][MAXCOL];
    private int[][]         scoreAtBadMoveMatrix = new int[MAXROW][MAXCOL];
    private int[][]         scoreAtMatrix = new int[MAXROW][MAXCOL];
    private int fourInARow = 900, threeInARow = 50, twoInARow = 5, oponentThreeInARow = -500, middleColumn = 2, siblingsColums = 1;
    private int negativeStaringValue = -999999999;


    // version 2 init
    private int[]     validLocations = new int [MAXCOL];
    private int[]     score = new int [MAXCOL];

    // for delite later, because it is kind a duplicate
    private boolean[][]     avalableSpotsMatrix = new boolean [MAXROW][MAXCOL];

    public static final int EMPTY = 0;
    public static final int RED = 1;
    public static final int YELLOW = 2;

    public static final int MAXROW = 6;     // 6 rows
    public static final int MAXCOL = 7;     // 7 columns

    public static final String SPACE = "                  "; // 18 spaces

    int globalActiveColour = RED;
    int globalOppnentColor = YELLOW;

    int lastMoveSimulationRow;
    int lastMoveSimulationColumn;

    public Connect4Frame() {
        setTitle("Connect4 game modified by Boris Turudija");
        MenuBar mbar = new MenuBar();
        Menu fileMenu = new Menu("File");
        newMI = new MenuItem("New");
        newMI.addActionListener(this);
        fileMenu.add(newMI);
        exitMI = new MenuItem("Exit");
        exitMI.addActionListener(this);
        fileMenu.add(exitMI);
        mbar.add(fileMenu);
        Menu optMenu = new Menu("Options");
        redMI = new MenuItem("Red starts");
        redMI.addActionListener(this);
        optMenu.add(redMI);
        yellowMI = new MenuItem("Yellow starts");
        yellowMI.addActionListener(this);
        optMenu.add(yellowMI);
        mbar.add(optMenu);
//Random Menu
        Menu randomMenu = new Menu ("Random");
        pvp = new MenuItem("Player vs. Player");
        pvp.addActionListener(this);
        randomMenu.add(pvp);
        pvr = new MenuItem("Player vs. Random");
        pvr.addActionListener(this);
        randomMenu.add(pvr);
        rvr = new MenuItem("Random vs. Random");
        rvr.addActionListener(this);
        randomMenu.add(rvr);
        mbar.add(randomMenu);
//MiniMax Menu
        Menu miniMaxMenu = new Menu ("Mini-Max");
        pvpMinimax = new MenuItem("Player vs. Player");
        pvpMinimax.addActionListener(this);
        miniMaxMenu.add(pvpMinimax);
        playerVSminiMax = new MenuItem("Player vs. Mini-Max");
        playerVSminiMax.addActionListener(this);
        miniMaxMenu.add(playerVSminiMax);
        miniMaxVSminiMax = new MenuItem("Mini-Max vs. Mini-Max");
        miniMaxVSminiMax.addActionListener(this);
        miniMaxMenu.add(miniMaxVSminiMax);
        mbar.add(miniMaxMenu);
        setMenuBar(mbar);



        // Build control panel.
        Panel panel = new Panel();

        btn1 = new Button("1");
        btn1.addActionListener(this);
        panel.add(btn1);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn2 = new Button("2");
        btn2.addActionListener(this);
        panel.add(btn2);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn3 = new Button("3");
        btn3.addActionListener(this);
        panel.add(btn3);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn4 = new Button("4");
        btn4.addActionListener(this);
        panel.add(btn4);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn5 = new Button("5");
        btn5.addActionListener(this);
        panel.add(btn5);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn6 = new Button("6");
        btn6.addActionListener(this);
        panel.add(btn6);
        lblSpacer = new Label(SPACE);
        panel.add(lblSpacer);

        btn7 = new Button("7");
        btn7.addActionListener(this);
        panel.add(btn7);
        add(panel, BorderLayout.NORTH);

// New panel for faster playing, single move. Contain All tree types of moves.
        Panel rightPanel = new Panel();
        rightPanel.setLayout(new GridLayout(5,0));
        randomButton = new Button("Random");
        randomButton.addActionListener(this);
        rightPanel.add(randomButton);
        lowIQButton = new Button("Low IQ");
        lowIQButton.addActionListener(this);
        rightPanel.add(lowIQButton);
        miniMaxButton = new Button("Mini-Max");
        miniMaxButton.addActionListener(this);
        rightPanel.add(miniMaxButton);
        alfaBetaButton = new Button("Alfa-Beta");
        alfaBetaButton.addActionListener(this);
        rightPanel.add(alfaBetaButton);
        evaluateButton = new Button ("Evaluate Move");
        evaluateButton.addActionListener(this);
        rightPanel.add(evaluateButton);

        add(rightPanel, BorderLayout.EAST);

// New panel for counting the turns
        turnsPanel = new Panel();
        turnsPanel.setLayout(new GridLayout(4,0));
        numberOfTurns = new Label("Number of turns played: ");
        player1TurnsPlayed = new Label("Player I: "+ 0);
        player2TurnsPlayed = new Label("Player II: "+ 0);
        playersTotalTurnsPlayed = new Label("Total turns played: " + 0);
        turnsPanel.add(numberOfTurns);
        turnsPanel.add(player1TurnsPlayed);
        turnsPanel.add(player2TurnsPlayed);
        turnsPanel.add(playersTotalTurnsPlayed);
        add(turnsPanel,BorderLayout.WEST);
        turnsPanel.setVisible(true);

        initialize();
        // Set to a reasonable size.
        setSize(1024, 768);

        // Build control panel
    } // Connect4

    public void initialize() {
        board =new int[MAXROW][MAXCOL];
        for (int row=0; row<MAXROW; row++)
            for (int col=0; col<MAXCOL; col++)
                board[row][col]= EMPTY;
        gameStart=false;
//        toDoAfterEveryMove();
    } // initialize

    public void paint(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(110, 50, 100+100*MAXCOL, 100+100*MAXROW);
        for (int row=0; row<MAXROW; row++)
            for (int col=0; col<MAXCOL; col++) {
                if (board[row][col]== EMPTY) g.setColor(Color.WHITE);
                if (board[row][col]==RED) g.setColor(Color.RED);
                if (board[row][col]==YELLOW) g.setColor(Color.YELLOW);
                g.fillOval(160+100*col, 100+100*row, 100, 100);
            }
        check4(g);
    } // paint



    public void putDisk(int n,int[][] currentBoard) {
        System.out.println("put disn column " + n);
        // put a disk on top of column n
        // if game is over do nothing.
        if (end) return;
        gameStart=true;
        int row;
        n--;
        for (row=0; row<MAXROW; row++) {
            if (currentBoard[row][n] > 0) break;
        }
        if (row>0) {
//            System.out.println("cudni row je  " + row);
//            System.out.println("pozicija je " + row + " " + n);
            incrementation();
            currentBoard[--row][n] = globalActiveColour;
            if (globalActiveColour == RED){
                globalActiveColour = YELLOW;
                globalOppnentColor = RED;

            }
            else{
                globalActiveColour = RED;
                globalOppnentColor = YELLOW;
            }
            repaint();
        }

        toDoAfterEveryMove();
    }

    public void displayWinner(Graphics g, int n) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", Font.BOLD, 100));
        if (n==RED)
            g.drawString("Red wins!", 100, 400);
        else
            g.drawString("Yellow wins!", 100, 400);
        end=true;
    }

    public void check4(Graphics g) {
        // see if there are 4 disks in a row: horizontal, vertical or diagonal
        // horizontal rows
        for (int row=0; row<MAXROW; row++) {
            for (int col=0; col<MAXCOL-3; col++) {
                int curr = board[row][col];
                if (curr>0
                        && curr == board[row][col+1]
                        && curr == board[row][col+2]
                        && curr == board[row][col+3]) {
                    displayWinner(g, board[row][col]);
                }
            }
        }
        // vertical columns
        for (int col=0; col<MAXCOL; col++) {
            for (int row=0; row<MAXROW-3; row++) {
                int curr = board[row][col];
                if (curr>0
                        && curr == board[row+1][col]
                        && curr == board[row+2][col]
                        && curr == board[row+3][col])
                    displayWinner(g, board[row][col]);
            }
        }
        // diagonal lower left to upper right
        for (int row=0; row<MAXROW-3; row++) {
            for (int col=0; col<MAXCOL-3; col++) {
                int curr = board[row][col];
                if (curr>0
                        && curr == board[row+1][col+1]
                        && curr == board[row+2][col+2]
                        && curr == board[row+3][col+3])
                    displayWinner(g, board[row][col]);
            }
        }
        // diagonal upper left to lower right
        for (int row=MAXROW-1; row>=3; row--) {
            for (int col=0; col<MAXCOL-3; col++) {
                int curr = board[row][col];
                if (curr>0
                        && curr == board[row-1][col+1]
                        && curr == board[row-2][col+2]
                        && curr == board[row-3][col+3])
                    displayWinner(g, board[row][col]);
            }
        }
    } // end check4




    public void restartMatrix(int[][] matrix){
        for (int i = 0; i < MAXROW; i++) {
            for (int j = 0; j < MAXCOL; j++) {
                matrix[i][j] = 0;
            }
        }
    }





    //Method for counting of moves for both players
    public void incrementation(){
        if (totalNumberOfTurnesPlayed
                % 2 == 1 ){
            playersTotalTurnsPlayed.setText("Total : " + totalNumberOfTurnesPlayed
                    ++);
            player1TurnsPlayed.setText("Player  I : " + numberOfTurnsPlayer1Played++);
        }
        else{
            playersTotalTurnsPlayed.setText("Total : " +totalNumberOfTurnesPlayed
                    ++);
            player2TurnsPlayed.setText("Player  II : " +numberOfTurnsPlayer1Played++);
        }
    }

    //Method for restarting of the counter
    public void restartCountTurns(){
        numberOfTurnsPlayer1Played = 1;
        numberOfTurnsPlayer1Played = 1;
        totalNumberOfTurnesPlayed
                = 1;
        player1TurnsPlayed.setText("Player I: "+ 0);
        player2TurnsPlayed.setText("Player II: "+ 0);
        playersTotalTurnsPlayed.setText("Total turns played: " + 0);
    }




    public int lowestRow (boolean [][] matrix, int n){
        int lowestRow = 0;
        for (int i = MAXROW-1; i > 0 ; i--) {
            if(matrix[i][n] == true )
                lowestRow = i;
        }
        return lowestRow;
    }


    //////////////////////////RANDOM
//Method witch gives back a random number, needed later for random play.
    public int randomGenerator(){
        Random rand = new Random();
        int randomNo;
        randomNo = (rand.nextInt(7)+1);
        System.out.println(randomNo);
        return randomNo;
    }

    // Method allows player to play vs computer who plays random moves
// Method crate new thread to repaint ranodom moves in live time
    public void playerVsRandom() {
        Thread rvrThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(globalActiveColour == YELLOW){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (end == false) {
                    if (globalActiveColour == RED) {
                        putDisk(randomGenerator(),board);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        rvrThread.start();
    }

    // Method play random moves until someone wins or until player has no any move to play
// Method crate new thread to repaint ranodom moves in live time
    public void randomVsRandom() {
        Thread pvrThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (end == false) {

                    try {
                        putDisk(randomGenerator(),board);
                        Thread.sleep(300);
                        repaint();

                    } catch (InterruptedException e) {
                    }

                }
            }
        });
        pvrThread.start();
    }

    public void newGame() {
        end=false;
        initialize();
        repaint();
        restartCountTurns();

    }
//////////////////////////Evaluation

    public void validLocations(int[][] Array){
        int[] emptySpots = new int[MAXCOL];
        int minRow = 0;
        for (int i = 0; i < MAXROW; i++) {
            for (int j = 0; j < MAXCOL; j++) {

            }
        }
    }









//////////////////////////MINI-MAX

    public void playerVSminiMax() {
//        Thread rvrThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(globalActiveColour == YELLOW){
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                while (end == false) {
//                    evaluateMove();
//                    if (globalActiveColour == RED) {
//                        putDisk(findBestMove(scoreAtMatrix,avalableSpotsMatrix) + 1,board);                // + 1 beacuse of difference in put disk
//                    } else {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            }
//        });
//        rvrThread.start();
    }


    public void miniMaxVSminiMax() {
//        Thread miniMaxVSminiMaxThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
////                while (end == false) {
////                    try {
////                        putDisk(findBestMove(scoreAtMatrix,avalableSpotsMatrix) + 1,board);                    // + 1 beacuse of differnece in put disk
////                        Thread.sleep(300);
////                        repaint();
////
////                    } catch (InterruptedException e) {
////                    }
////
////                }
////            }
////        });
//        miniMaxVSminiMaxThread.start();
    }


    // UI ACTIONS
    // UI ACTIONS
    // UI ACTIONS
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn1)
            putDisk(1,board);
        else if (e.getSource() == btn2)
            putDisk(2,board);
        else if (e.getSource() == btn3)
            putDisk(3,board);
        else if (e.getSource() == btn4)
            putDisk(4,board);
        else if (e.getSource() == btn5)
            putDisk(5,board);
        else if (e.getSource() == btn6)
            putDisk(6,board);
        else if (e.getSource() == btn7)
            putDisk(7,board);
        else if (e.getSource() == randomButton)
            putDisk(randomGenerator(),board);

        else if (e.getSource() == lowIQButton) {
            int bestLowIQMove = findBestMove(board) + 1; // plus 1 is because buttons numbers starting from 0
            putDisk(bestLowIQMove,board);

        }

        else if (e.getSource() == miniMaxButton){

        }
        else if (e.getSource() == evaluateButton){
            findBestMove(board);
        }

        //For now this is juts testing button for some function, to check behaviour
        else if (e.getSource() == alfaBetaButton){

        }

        else if (e.getSource() == newMI) {
            newGame();
        } else if (e.getSource() == exitMI) {
            System.exit(0);
        } else if (e.getSource() == redMI) {
            // don't change colour to play in middle of game
            if (!gameStart) {
                globalActiveColour =RED;
                globalOppnentColor = YELLOW;
            }
        } else if (e.getSource() == yellowMI) {
            if (!gameStart) {
                globalActiveColour =YELLOW;
                globalOppnentColor = RED;
            }
        } else if (e.getSource() == pvp){
            newGame();
        } else if (e.getSource() == pvr){
            if(globalActiveColour == RED) {
                playerVsRandom();
            }
        } else if (e.getSource() == rvr){
            randomVsRandom();
        } else if (e.getSource() == pvpMinimax){
            newGame();
        } else if (e.getSource() == playerVSminiMax){
            playerVSminiMax();
        } else if (e.getSource() == miniMaxVSminiMax){
            miniMaxVSminiMax();
        }

    }

    // version2 helper function
    // version2 helper function
    // version2 helper function



    /**
     * Funtion prints matrix vales
     * @param mat
     */
    public void print2D(int mat[][])
    {
        System.out.println("");

        // Loop through all rows
        for (int i = 0; i < mat.length; i++){
            System.out.println(" ");

            // Loop through all elements of current row
            for (int j = 0; j < mat[i].length; j++)
                System.out.print(mat[i][j] + " ");
        }}

    /**
     * Function chacks for all valid moves/ empty spaces on board;
     *
     * @param mat -> board usualy
     * @return
     */
    public boolean[] checkForValidLocation(int[][] mat){
        boolean[] validLocations = new boolean[MAXCOL];
        for (int i = 0; i < MAXCOL; i++) {
            for (int j = 0; j <  MAXROW; j++) {
                if (mat[j][i] == EMPTY){
                    validLocations[i] = true;
                }
            }
        }
        System.out.println(Arrays.toString(validLocations));

        return validLocations;
    }

    /**
     * All function which we repeat after every move
     */
    public void  toDoAfterEveryMove(){
//       print2D(board);
        checkForValidLocation(board);
    }


    /**
     * Function copy integer matrix.
     * @param original -> matrix you want to copy
     * @return -> newMatrix
     */
    public int[][] copyMatrix(int[][] original){
        int[][] newMatrix = new int[MAXROW][MAXCOL];
        for (int i = 0; i < MAXROW; i++) {
            for (int j = 0; j < MAXCOL; j++) {
                newMatrix[i][j] = original[i][j];
            }
        }
        return newMatrix;
    }


    /**
     * Function make a virtual moves then get score from evaluateBoard function for every move and
     * finds the best move for a current situation on the board.
     * @param boardAttribute
     * @return -> best move to make
     */
    public int findBestMove(int[][] boardAttribute){
        int bestColumn = 0;
        int bestScore =  negativeStaringValue;
        int evaluatedScoreForCurrentMove = negativeStaringValue;
        int[] scoresForMove = new int[MAXCOL];
        int[][] tempBoard = copyMatrix(boardAttribute);
        boolean[] currentValidLocation = checkForValidLocation(tempBoard);

        System.out.println(" ");
        System.out.println("Score at themoment " + Arrays.toString(scoresForMove));
        System.out.println("**********************************");
        System.out.println("BEST MOVE IS:");
        for (int i = 0; i < currentValidLocation.length; i++) {
//            scoresForMove[i] = 0;
//            System.out.println("******** TEST FOR LOCATION " + i + " **************");
            if(currentValidLocation[i] == true){
                simulatePutDisk(i+1, tempBoard,globalActiveColour); // simulate move we can evaluae it score                print2D(tempBoard);
//                print2D(tempBoard);
                evaluatedScoreForCurrentMove = evaluateBoard(tempBoard, globalActiveColour);
                if(Math.abs(evaluatedScoreForCurrentMove) > scoresForMove[i] && evaluatedScoreForCurrentMove != negativeStaringValue){
                    scoresForMove[i] += evaluatedScoreForCurrentMove;
                }
//                System.out.println("Score at the moment  "  + Arrays.toString(scoresForMove));

                tempBoard = copyMatrix(boardAttribute);
            }
        }



        for (int i = 0; i < scoresForMove.length; i++) {
            if(totalNumberOfTurnesPlayed < 10 && i == 3){
                scoresForMove[i] += middleColumn;
            }
            if(totalNumberOfTurnesPlayed < 10 && (i == 2 || i == 4)){
                scoresForMove[i] += siblingsColums;
            }
            if (scoresForMove[i]>bestScore){
                bestScore = scoresForMove[i];
                bestColumn = i;
            }

//            scoresForMove[i] = 0;
        }

        System.out.println("Score at themoment " + Arrays.toString(scoresForMove));
        System.out.println("bestScore " + bestScore);
        System.out.println("bestMove " + bestColumn);


        return  bestColumn;
    }






    /**
     * Fucntion evalaute board for a current sition, gives back highest score
     * @param boardAttribute
     * @return
     */
    public int evaluateBoard(int [][] boardAttribute, int activeColor) {
        int bestScore = negativeStaringValue;

        // HORIZONTAL CHECK
        // HORIZONTAL CHECK
        // check for 2 in a row
        for (int row = 0; row < MAXROW; row++) {
            for (int col = 0; col < MAXCOL - 1; col++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row][col + 1]
                        && (board[row][col] == EMPTY
                        ||  board[row][col+1] == EMPTY )
                ) {
                    bestScore = passScoreValue(twoInARow, bestScore);
//                    scoresForMove = passScoreValue(twoInARow,scoresForMove,col);
//

                }
            }
        }

        // check for 3 in a row
        for (int row = 0; row < MAXROW; row++) {
            for (int col = 0; col < MAXCOL - 2; col++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row][col + 1]
                        && activeColor == boardAttribute[row][col + 2]
                        && (board[row][col] == EMPTY
                        ||  board[row][col+1] == EMPTY
                        || board[row][col+2] == EMPTY)
                ){
                    bestScore = passScoreValue(threeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);
//
                }
            }
        }
        // check for 4 in a row
        for (int row = 0; row < MAXROW; row++) {
            for (int col = 0; col < MAXCOL - 3; col++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row][col + 1]
                        && activeColor == boardAttribute[row][col + 2]
                        && activeColor == boardAttribute[row][col + 3]
                        && (board[row][col] == EMPTY
                        || board[row][col+1] == EMPTY
                        || board[row][col+2] == EMPTY
                        || board[row][col+3] == EMPTY)
                ){
                    bestScore = passScoreValue(fourInARow, bestScore);
                    System.out.println(fourInARow);
//                        scoresForMove = passScoreValue(fourInARow,scoresForMove,col);

//
                }
            }
        }

        // VERTICAL CHECK
        // VERTICAL CHECK
        // check for 2 in a row
        for (int col=0; col<MAXCOL; col++) {
            for (int row=0; row<MAXROW-1; row++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col]
                        && (board[row][col] == EMPTY
                        ||  board[row+1][col] == EMPTY )
                ) {
                    bestScore = passScoreValue(twoInARow, bestScore);
//                    scoresForMove = passScoreValue(twoInARow,scoresForMove,col);

//

                }
            }
        }

        // check for 3 in a row
        for (int col=0; col<MAXCOL; col++) {
            for (int row=0; row<MAXROW-2; row++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col]
                        && activeColor == boardAttribute[row + 2][col]
                        && (board[row][col] == EMPTY
                        ||  board[row+1][col] == EMPTY
                        ||  board[row+2][col] == EMPTY )
                ) {
                    bestScore = passScoreValue(threeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);

                }
            }
        }
        // check for 4 in a row
        for (int col=0; col<MAXCOL; col++) {
            for (int row=0; row<MAXROW-3; row++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col]
                        && activeColor == boardAttribute[row + 2][col]
                        && activeColor == boardAttribute[row + 3][col]
                        && (board[row][col] == EMPTY
                        ||  board[row+1][col] == EMPTY
                        ||  board[row+2][col] == EMPTY
                        || board[row+3][col] == EMPTY)
                ) {

                    bestScore = passScoreValue(fourInARow, bestScore);
//                    scoresForMove = passScoreValue(fourInARow,scoresForMove,col);


                }
            }
        }

        // LOWER DIAGONAL CHECK
        // LOWER DIAGONAL CHECK

        for (int row=0; row<MAXROW-1; row++) {
            for (int col=0; col<MAXCOL-1; col++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col + 1]
                        && (board[row][col] == EMPTY
                        || board[row+1][col+1] == EMPTY)
                ) {
                    bestScore = passScoreValue(twoInARow, bestScore);
//                    scoresForMove = passScoreValue(twoInARow,scoresForMove,col);

                }
            }
        }

        // check for 3 in a row
        for (int row=0; row<MAXROW-2; row++) {
            for (int col=0; col<MAXCOL-2; col++) {

                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col + 1]
                        && activeColor == boardAttribute[row + 2][col + 2]
                        && (board[row][col] == EMPTY
                        || board[row+1][col+1] == EMPTY
                        || board[row+2][col+2] == EMPTY)
                ) {
                    bestScore = passScoreValue(threeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);

                }
            }
        }
        // check for 4 in a row
        for (int row=0; row<MAXROW-3; row++) {
            for (int col=0; col<MAXCOL-3; col++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 3][col + 1]
                        && activeColor == boardAttribute[row + 3][col + 2]
                        && activeColor == boardAttribute[row + 3][col + 3]
                        && (board[row][col] == EMPTY
                        || board[row+1][col+1] == EMPTY
                        || board[row+1][col+2] == EMPTY
                        || board[row+1][col+3] == EMPTY)
                ) {

                    bestScore = passScoreValue(fourInARow, bestScore);
//                    scoresForMove = passScoreValue(fourInARow,scoresForMove,col);


                }
            }
        }


        // UPPER DIAGONAL
        // UPPER DIAGONAL
        // check for 2 in a row

        for (int col = MAXCOL-6; col < MAXCOL; col++) {
            for (int row = 0; row < MAXROW - 1; row++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col - 1]
                        && (board[row][col] == EMPTY
                        || board[row+1][col-1] == EMPTY)
                ) {
                    bestScore = passScoreValue(twoInARow, bestScore);
//                    scoresForMove = passScoreValue(twoInARow,scoresForMove,col);


                }
            }
        }

        // check for 3 in a row
        for (int col = MAXCOL-5; col < MAXCOL; col++) {
            for (int row = 0; row < MAXROW - 2; row++) {

                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col - 1]
                        && activeColor == boardAttribute[row + 2][col - 2]
                        && (board[row][col] == EMPTY
                        || board[row+1][col-1] == EMPTY
                        || board[row+2][col-2] == EMPTY)
                ) {
                    bestScore = passScoreValue(threeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);


                }
            }
        }
        // check for 4 in a row
        for (int col = MAXCOL-4; col < MAXCOL; col++) {
            for (int row = 0; row < MAXROW - 3; row++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 3][col - 1]
                        && activeColor == boardAttribute[row + 3][col - 2]
                        && activeColor == boardAttribute[row + 3][col - 3]
                        && (board[row][col] == EMPTY
                        || board[row+3][col-1] == EMPTY
                        || board[row+3][col-2] == EMPTY
                        || board[row+3][col-3] == EMPTY)
                ) {
                    bestScore = passScoreValue(fourInARow, bestScore);
//                    scoresForMove = passScoreValue(fourInARow,scoresForMove,col);


                }
            }
        }

        if(bestScore > 600){
            return bestScore;
        }


        //check for opponet 4 in a row
        if (globalActiveColour == activeColor){
            activeColor = globalOppnentColor;
        }
        boardAttribute[lastMoveSimulationRow][lastMoveSimulationColumn] = activeColor;
        // horisontal
        for (int row = 0; row < MAXROW; row++) {
            for (int col = 0; col < MAXCOL - 3; col++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row][col + 1]
                        && activeColor == boardAttribute[row][col + 2]
                        && activeColor == boardAttribute[row][col + 3]
                        && (board[row][col] == EMPTY
                        ||  board[row][col+1] == EMPTY
                        || board[row][col+2] == EMPTY
                        || board[row][col+3] == EMPTY
                )
                ){
                    bestScore = passScoreValue(oponentThreeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);
//
                }
            }
        }
        // vertical
        for (int col=0; col<MAXCOL; col++) {
            for (int row=0; row<MAXROW-3; row++) {
                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col]
                        && activeColor == boardAttribute[row + 2][col]
                        && activeColor == boardAttribute[row + 3][col]
                        && (board[row][col] == EMPTY
                        ||  board[row+1][col] == EMPTY
                        ||  board[row+2][col] == EMPTY
                        ||  board[row+3][col] == EMPTY
                )
                ) {
                    bestScore = passScoreValue(oponentThreeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);

                }
            }
        }
        // check for 3 in a row
        for (int row=0; row<MAXROW-3; row++) {
            for (int col=0; col<MAXCOL-3; col++) {

                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col + 1]
                        && activeColor == boardAttribute[row + 2][col + 2]
                        && activeColor == boardAttribute[row + 3][col + 3]
                        && (board[row][col] == EMPTY
                        || board[row+1][col+1] == EMPTY
                        || board[row+2][col+2] == EMPTY
                        || board[row+3][col+3] == EMPTY

                )
                ) {
                    bestScore = passScoreValue(oponentThreeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);

                }
            }
        }
        // check for 3 in a row
        for (int col = MAXCOL-4; col < MAXCOL; col++) {
            for (int row = 0; row < MAXROW - 3; row++) {

                if (boardAttribute[row][col] == activeColor
                        && activeColor == boardAttribute[row + 1][col - 1]
                        && activeColor == boardAttribute[row + 2][col - 2]
                        && activeColor == boardAttribute[row + 3][col - 3]
                        && (board[row][col] == EMPTY
                        || board[row+1][col-1] == EMPTY
                        || board[row+2][col-2] == EMPTY
                        || board[row+3][col-3] == EMPTY

                )
                ) {
                    bestScore = passScoreValue(oponentThreeInARow, bestScore);
//                    scoresForMove = passScoreValue(threeInARow,scoresForMove,col);


                }
            }
        }

        // it is enouph to pass hightest score for the board at the moment. Because it will be filterd later...
        return bestScore;
    }

    /**
     * Funtion just pass temp value, i put in the function because of code repetition
     * @param scoreValue
     * @param bestScore
     * @return
     */
    public int passScoreValue(int scoreValue, int  bestScore){

        if(scoreValue < 0)
            System.out.println();
        if(Math.abs(scoreValue) > bestScore){
            bestScore = scoreValue;
        }
        return bestScore;
    }


    /**
     * Fucntin simulate putting disk into one specific column. It is used to evaluate next move.
     * @param n
     * @param currentBoard
     */
    public void simulatePutDisk(int n, int[][] currentBoard, int color){
        if (end) return;
        gameStart=true;
        int row;
        n--;
        for (row=0; row<MAXROW; row++) {
            if (currentBoard[row][n] > 0) break;
        }
        if (row>0){
            currentBoard[--row][n] = color;

        }
        // it also remember where did function put last simulated move
        lastMoveSimulationColumn = n;
        lastMoveSimulationRow = row;
    }


    public boolean isTerminalNode(int[][] boardAttribute,int activeColor){
        int score = evaluateBoard(boardAttribute, activeColor);
        if (score > 600){
            return true;
        }
        return false;
    }


    public void miniMax(int[][] boardAttribute, int dept, int player){
        int playedTurnes = totalNumberOfTurnesPlayed;
        boolean terminalNode = isTerminalNode(boardAttribute,globalActiveColour);


    }
}




// end ActionPerformed


// classimport java.awt.*;
