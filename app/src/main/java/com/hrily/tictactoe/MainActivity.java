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
    int[] score_cell;
    // Whether the game in in progress or not
    boolean isRunning=false;

    // Constants
    int X = 1;
    int O = 0;
    int UNMARKED = -1;
    int N_CELLS = 9;
    int N_TRIADS = 8;

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

        // Calculate score of each triad
        scoreTriad();
        // Using scores of triads
        // Calculate score of each cell
        scoreCell();
        // Find cell with max score
        int maxi=0;
        for(int i=0;i<N_CELLS;i++){
            if(score_cell[maxi]<score_cell[i])
                maxi  = i;
        }
        // Most favourable cell is the cell with max score
        return maxi;
    }

    public void scoreTriad(){
        // Function to calculate score of each triad
        // CPU plays O
        // Scoring is done based on number of Xs and Os

        // For each triad
        for(int i=0;i<N_TRIADS;i++){
            // Initialize score to 0
            int score=0;
            // Get number of Xs in the triad
            int nX = getNX(i);
            // Get number of Os in the triad
            int nO = getNO(i);

            // If Os are 2 and Xs are 0
            // This is the winning triad, score it with highest value
            if(nO==2 && nX==0) score=6;

            // If Xs are 2 and Os are
            // This might be losing triad, better mark this
            if(nO==0 && nX==2) score=5;

            // Score other triads arbitrarily
            if(nO==1 && nX==0) score=2;
            if(nO==0 && nX==1) score=2;
            if(nO==0 && nX==0) score=1;

            // Finally store the score in array
            score_triad[i]=score;
        }
    }

    public void scoreCell(){
        // Function to calculate score of each cell using scores of triads

        // For each cell
        for(int i=0;i<N_CELLS;i++){
            // Initialize a default negative score
            int score = -1;
            // If the cell is mark able
            if(matrix[i] == UNMARKED){
                // Initialize score to zero
                score = 0;
                // Add score of each triad belonging to this cell
                for(int j:triads_of_cell[i]){
                    score += score_triad[j];
                }
            }
            // Finally store the calculated score
            score_cell[i] = score;
        }
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
        score_cell=new int[N_CELLS];
        // Start new game
        isRunning=true;
    }

}
