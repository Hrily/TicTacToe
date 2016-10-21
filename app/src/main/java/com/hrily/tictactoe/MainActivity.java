package com.hrily.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Cells are numbered as follows :
    // 0 1 2
    // 3 4 5
    // 6 7 8
    // User plays X and CPU plays O

    // Array of Buttons denoting cells
    Button cells[];
    // TextView to show status of game
    TextView status;
    // Matrix to store current state of game
    // Each cell of matrix takes one of the following values:
    // -1 unmarked
    //  0 marked O
    //  1 marked X
    int[] matrix;
    // Define triads of cells which on same marks lead to win
    // For eg: first row forms a triad
    //         so cells 0, 1, 2 form a triad
    private int[][] triads = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    // Define which triad contributes to i'th cell
    private int[][] triads_of_cell = {{0,3,6},{0,4},{0,5,7},{1,3},{1,4,6,7},{1,5},{2,3,7},{2,4},{2,5,6}};
    // Array to calculate score of each triad
    int[] score_triad;
    // Array to calculate score of each cell
    //int[] score_cell;
    float[] score_cell;
    // Whether the game in in progress or not
    boolean isRunning=false;

    // Constants
    final int X = 1;
    final int O = 0;
    final int UNMARKED = -1;
    final int N_CELLS = 9;
    final int N_TRIADS = 8;

    int playerLastMovement = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.status);
        // Bind each button on Activity to button array defined
        cells = new Button[N_CELLS];
        cells[0] = (Button) findViewById(R.id.b0);
        cells[1] = (Button) findViewById(R.id.b1);
        cells[2] = (Button) findViewById(R.id.b2);
        cells[3] = (Button) findViewById(R.id.b3);
        cells[4] = (Button) findViewById(R.id.b4);
        cells[5] = (Button) findViewById(R.id.b5);
        cells[6] = (Button) findViewById(R.id.b6);
        cells[7] = (Button) findViewById(R.id.b7);
        cells[8] = (Button) findViewById(R.id.b8);
        // Bind OnClick function to Reset button
        findViewById(R.id.reset_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        // Reset the game
        reset();
    }


    public void hit(View v){
        // Function to handle users hit on cell button

        // Get index of the clicked cell
        int index = Integer.parseInt(v.getTag().toString());
        // If the game is running and the cell is unmarked
        if(isRunning && matrix[index]==UNMARKED){
            // Mark the cell
            cells[index].setText("X");
            matrix[index] = X;
            // Check status of the game
            checkStatus();
            // If the game is still running
            if(isRunning){
                // Make the CPU's move
                cpuPlay();
                // Check status of game again
                checkStatus();
            }
        }
        playerLastMovement = index;
    }

    public void cpuPlay(){
        // Function to make CPU's move

        // Get index of most favorable cell to mark
        int index=getFavourableCell();
        // Mark the cell
        cells[index].setText("O");
        matrix[index] = O;
    }

    public int getFavourableCell(){
        // Function to get the most favorable cell to make CPU move


        int activation = 0;
        //Rule 1: if there is any triad which is a Winning movement do it.
        activation = checkForWinningMovement();
        if(activation != -1) return  activation;

        //Rule 2: if there is any triad which is a enemy Winning movement block it.
        activation = checkForBlockWinningMovement();
        if(activation != -1) return  activation;

        //Rule 3: if center is free use it!
        activation = checkForCenter();
        if (activation != -1) return activation;


        //Rule 4: play a corner
        activation = checkForCorners();
        if (activation != -1) return activation;


        //Rule 5: play a middle place
        activation = checkForMiddles();
        if (activation != -1) return activation;

        return 0; // not reacheable...
    }

    private int  checkForMiddles(){

        int[] middles = {1, 5, 7, 3};
        int[] score =  new int[4];

        for (int i = 0; i < middles.length; i++){
            if( matrix[middles[i]] != UNMARKED) continue; // score for this position is already 0
            else{
                //TODO add a more intelligent behaviour
                score[i] +=1; // If it's not busy it's a good point
            }
        }

        int maxValue = -1, maxIndex = 0;
        for (int i = 0; i < middles.length; i++){
            if(score[i] > maxValue){
                maxValue = score[i];
                maxIndex = i;
            }
        }

        if (maxValue == -1)
            return -1;
        else
            return maxIndex;
    }

    private int  checkForCorners(){

        int[] corners = {0, 2, 6, 8};
        int[] score =  new int[4];

        for (int i = 0; i < corners.length; i++){
            if( matrix[corners[i]] != UNMARKED) continue; // score for this position is already 0
            else{
                //Add score if neighbour cells are free or are AI's
                for(int j: triads_of_cell[corners[i]]){
                    if(matrix[triads[j][1]] == O) //If neighbours are AI is good for us.
                        score[i] +=3;
                    else if (matrix[triads[j][1]] == UNMARKED)
                        score[i] +=1;
                }
            }
        }

        //search for max score corner
        int maxValue = -1, maxIndex = 0;
        for (int i = 0; i < corners.length; i++){
            if(score[i] > maxValue){
                maxValue = score[i];
                maxIndex = i;
            }
        }

        if (maxValue == -1)
            return -1;
        else
            return corners[maxIndex];

    }


    private int checkForCenter(){
        if(matrix[4] != UNMARKED) return -1;
        return 4;
    }

    private int checkForWinningMovement(){

        //Check winning movement in the first column
        if (matrix[0] == UNMARKED && matrix[1] == O && matrix[2] == O) return 0;
        if (matrix[3] == UNMARKED && matrix[4] == O && matrix[5] == O) return 3;
        if (matrix[6] == UNMARKED && matrix[7] == O && matrix[8] == O) return 6;

        //Check winning movement in the second column
        if (matrix[0] == O && matrix[1] == UNMARKED && matrix[2] == O) return 1;
        if (matrix[3] == O && matrix[4] == UNMARKED && matrix[5] == O) return 4;
        if (matrix[6] == O && matrix[7] == UNMARKED && matrix[8] == O) return 7;

        //Chech  winning movement in the third column
        if (matrix[0] == O && matrix[1] == O && matrix[2] == UNMARKED) return 2;
        if (matrix[3] == O && matrix[4] == O && matrix[5] == UNMARKED) return 5;
        if (matrix[6] == O && matrix[7] == O && matrix[8] == UNMARKED) return 8;

        //Check winning movement in the first row
        if (matrix[0] == UNMARKED && matrix[3] == O && matrix[6] == O) return 0;
        if (matrix[1] == UNMARKED && matrix[4] == O && matrix[7] == O) return 1;
        if (matrix[2] == UNMARKED && matrix[5] == O && matrix[8] == O) return 2;

        //Check winning movement in the second row
        if (matrix[0] == O && matrix[3] == UNMARKED && matrix[6] == O) return 3;
        if (matrix[1] == O && matrix[4] == UNMARKED && matrix[7] == O) return 4;
        if (matrix[2] == O && matrix[5] == UNMARKED && matrix[8] == O) return 5;

        //Chech  winning movement in the third row
        if (matrix[0] == O && matrix[3] == O && matrix[6] == UNMARKED) return 6;
        if (matrix[1] == O && matrix[4] == O && matrix[7] == UNMARKED) return 7;
        if (matrix[2] == O && matrix[5] == O && matrix[8] == UNMARKED) return 8;

        //Check winning movement in the main diagonal
        if (matrix[0] == UNMARKED && matrix[4] == O && matrix[8] == O) return 0;
        if (matrix[0] == O && matrix[4] == UNMARKED && matrix[8] == O) return 4;
        if (matrix[0] == O && matrix[4] == O && matrix[8] == UNMARKED) return 8;

        //Chech  winning movement in the second diagonal
        if (matrix[2] == UNMARKED && matrix[4] == O && matrix[6] == O) return 2;
        if (matrix[2] == O && matrix[4] == UNMARKED && matrix[6] == O) return 4;
        if (matrix[2] == O && matrix[4] == O && matrix[6] == UNMARKED) return 6;

        return  -1; // this rule is not aplicable
    }

    private int checkForBlockWinningMovement(){
        //Check winning movement in the first column
        if (matrix[0] == UNMARKED && matrix[1] == X && matrix[2] == X) return 0;
        if (matrix[3] == UNMARKED && matrix[4] == X && matrix[5] == X) return 3;
        if (matrix[6] == UNMARKED && matrix[7] == X && matrix[8] == X) return 6;

        //Check winning movement in the second column
        if (matrix[0] == X && matrix[1] == UNMARKED && matrix[2] == X) return 1;
        if (matrix[3] == X && matrix[4] == UNMARKED && matrix[5] == X) return 4;
        if (matrix[6] == X && matrix[7] == UNMARKED && matrix[8] == X) return 7;

        //Chech  winning movement in the third column
        if (matrix[0] == X && matrix[1] == X && matrix[2] == UNMARKED) return 2;
        if (matrix[3] == X && matrix[4] == X && matrix[5] == UNMARKED) return 5;
        if (matrix[6] == X && matrix[7] == X && matrix[8] == UNMARKED) return 8;

        //Check winning movement in the first row
        if (matrix[0] == UNMARKED && matrix[3] == X && matrix[6] == X) return 0;
        if (matrix[1] == UNMARKED && matrix[4] == X && matrix[7] == X) return 1;
        if (matrix[2] == UNMARKED && matrix[5] == X && matrix[8] == X) return 2;

        //Check winning movement in the second row
        if (matrix[0] == X && matrix[3] == UNMARKED && matrix[6] == X) return 3;
        if (matrix[1] == X && matrix[4] == UNMARKED && matrix[7] == X) return 4;
        if (matrix[2] == X && matrix[5] == UNMARKED && matrix[8] == X) return 5;

        //Chech  winning movement in the third row
        if (matrix[0] == X && matrix[3] == X && matrix[6] == UNMARKED) return 6;
        if (matrix[1] == X && matrix[4] == X && matrix[7] == UNMARKED) return 7;
        if (matrix[2] == X && matrix[5] == X && matrix[8] == UNMARKED) return 8;

        //Check winning movement in the main diagonal
        if (matrix[0] == UNMARKED && matrix[4] == X && matrix[8] == X) return 0;
        if (matrix[0] == X && matrix[4] == UNMARKED && matrix[8] == X) return 4;
        if (matrix[0] == X && matrix[4] == X && matrix[8] == UNMARKED) return 8;

        //Chech  winning movement in the second diagonal
        if (matrix[2] == UNMARKED && matrix[4] == X && matrix[6] == X) return 2;
        if (matrix[2] == X && matrix[4] == UNMARKED && matrix[6] == X) return 4;
        if (matrix[2] == X && matrix[4] == X && matrix[6] == UNMARKED) return 6;

        return  -1; // this rule is not aplicable
    }


   

    public int getNX(int i){
        // Function to get number of Xs in i'th triad

        // Initialize number of Xs to 0
        int nX = 0;
        // For each cell in triad
        for(int j:triads[i]){
            // If cell is marked X, increment nX
            if(matrix[j]==X) nX++;
        }
        return nX;
    }

    public int getNO(int i){
        // Function to get number of Os in i'th triad

        // Initialize number of Os to 0
        int nO = 0;
        // For each cell in triad
        for(int j:triads[i]){
            // If cell is marked O, increment nO
            if(matrix[j]==O) nO++;
        }
        return nO;
    }

    public void checkStatus(){
        // Function to check current status of game

        // For each triad
        for(int i=0;i<N_TRIADS;i++){
            // Get number of Xs and Os in the triad
            int nX=getNX(i);
            int nO=getNO(i);
            // If number of Xs is 3, player wins
            if(nX==3){
                status.setText("You Win!!!");
                // End the game
                isRunning=false;
                return;
            }
            // If number of Os is 3, computer wins
            else if(nO==3){
                status.setText("You Lose!!!");
                // End the game
                isRunning=false;
                return;
            }
        }
        // If there is no result above
        // Check if the game is draw
        for(int i=0;i<N_CELLS;i++){
            // If there is any unmarked cell, game can't be a draw
            if(matrix[i]==UNMARKED){
                break;
            }
            // If all cells are marked, game is draw
            if(i==8){
                status.setText("Draw!!!");
                // End the game
                isRunning=false;
            }
        }
    }

    public void reset(){
        // Function to reset the game

        // Reset matrix
        matrix=new int[N_CELLS];
        Arrays.fill(matrix, UNMARKED);
        // Reset all cell buttons
        for(Button b:cells){
            b.setText(" ");
        }
        // Reset status text
        status.setText("");
        // Reset scores
        score_triad=new int[N_TRIADS];
        score_cell = new float[N_CELLS];
        //score_cell=new int[N_CELLS];
        // Start new game
        isRunning=true;
    }

}
