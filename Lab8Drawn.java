import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Lab8Drawn{

   static Scanner CONSOLE = new Scanner(System.in); 
   static String MOON_PATH = "moon.jpg";
   static String SUN_PATH = "sun.png";
   static Random RND = new Random();     
   static char HUMAN_PLAYER = 'X', COMPUTER_PLAYER = 'O';
    
   public static void main(String[] args) { 
      DrawingPanel panel = new DrawingPanel(602, 670);    
      Graphics g = panel.getGraphics(); 
      panel.setBackground(Color.WHITE);
      Graphics graphicsSurface = panel.getGraphics();
      Image moon = panel.loadImage(MOON_PATH);
      Image sun = panel.loadImage(SUN_PATH);
   
      int play = 0;
      String winner = "";
      while (play == 0){
         //displays blank board   
         displayInstructions(g);
         displayBoard(g);
         
         // initial board.
         char[][] board
            = {{'1', '2', '3'},
               {'4', '5', '6'},
               {'7', '8', '9'}};
               
         while (!haveWinner(board) && !isBoardFull(board)) {             
               // Handle the human's choice/move
            humanMove(panel, g, board, sun);
               
               //check if human won
            if (haveWinner(board) || isBoardFull(board)){
               break;
            }
               
               // handle the computers choice/move
            computerMove(panel, board, moon);
         }
           // Display game results.
         displayResults(board); // TODO: more parameters may be necessary.
         play = JOptionPane.showConfirmDialog(null,"Would you like to play again", "Select an Option", JOptionPane.YES_NO_OPTION);
         panel.clear();
      }
   }
   
   // returns true of the board is full, false if not.
   static boolean isBoardFull(char[][] board) {
      int filledSpaces = 0;
         
      for (char[] row : board) {
         for (char element : row) {
            char status = element;
            if (status == HUMAN_PLAYER || status == COMPUTER_PLAYER){
               filledSpaces++;                
            }
         }
      }
         
      return filledSpaces == 9;
   }

   static char whoWon(char[][] board){
      char element = ' ';
      char winner = ' ';
      
      for (int length = 0; length < 3; length++){
         element = board[0][length];
         //horizontal win 
         if(element == board[1][length] && element == board[2][length]){
            winner = element;
         } 
         
         element = board[length][0];
         //vertical win 
         if(element == board[length][1] && element == board[2][length]){
            winner = element;
         }   
      }
         
      if(winner==' '){
         element = board[0][0];
         //L-R diagonal win 
         if(element == board[1][1] && element == board[2][2]){
            winner = element;
         } else {   
            element = board[2][0];
            //R-L diagonal win 
            if(element == board[1][1] && element == board[0][2]){
               winner = element;
            } 
         }
      }                              
      return winner;
   }
    
   //returns true if either player won, false if no winner yet.
   static boolean haveWinner(char[][] board) {
      boolean winner = false;
      char element = ' ';
         
      for (int length = 0; length < 3; length++){       
         element = board[length][0];
         //horizontal win 
         if(element == board[length][1] && element == board[length][2]){
            winner=true;
         } 
            
         element = board[0][length];
         //horizontal win 
         if(element == board[1][length] && element == board[2][length]){
            winner=true;
         }   
      }
         
      element = board[0][0];
      //starting from top L-R diagonal win 
      if(element == board[1][1] && element == board[2][2]){
         winner=true;
      }   
      
      element = board[2][0];
      //starting from top R-L diagonal win 
      if(element == board[1][1] && element == board[0][2]){
         winner=true;
      } 
      return winner;
   }
   
   //updates board array with vaild move by the players and returns cell number
   static void humanMove(DrawingPanel panel, Graphics g, char[][] board, Image sun) {
      int cell = getHumanChoice(board, g);
      boolean isUpdateValid = updateArray(board, cell, HUMAN_PLAYER);
      displayPieces(panel, sun, cell);
   }
   
   static int getHumanChoice(char[][] board, Graphics g) throws NullPointerException{
      int size = 0;
      int index = 0;
      int cell = -1;
      for(char[] row : board){
         for(char c : row){
            if( c != HUMAN_PLAYER && c != COMPUTER_PLAYER){
               size++;
            }
         }
      }
      Integer[] options = new Integer[size];
      for(int a = 0; a < 3; a++){
         for(int b = 0; b < 3; b++){
            if(board[a][b] != HUMAN_PLAYER && board[a][b] != COMPUTER_PLAYER){
               options[index] = (a*3)+(b+1);
               index++;
            }
         }
      } 
      cell = (Integer)JOptionPane.showInputDialog(null, "Choose a Cell: ", "Your Move",JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
      return cell; 
   }
   
   //Computer makes a winning blocking or random move and returns the cell number
   static void computerMove(DrawingPanel panel, char[][] board, Image moon) {
      //Chooses blocking or winning or random move if the first 2 action do not exist
      int compuMove = blockOrWin(board);
      if(compuMove == -1){
         compuMove = RND.nextInt(9)+1;
      }
        
      boolean isUpdateValid = updateBoard(board, compuMove, COMPUTER_PLAYER);
        
      while (!isUpdateValid){
         compuMove = RND.nextInt(9)+1;
         isUpdateValid = updateBoard(board, compuMove, COMPUTER_PLAYER);
      }
      displayPieces(panel, moon, compuMove);   
   }

   //Determines which cell can block the human player or win for the computer 
   static int blockOrWin(char[][] board){
      int open = 0;
      int human = 0;
      int comp = 0;
      boolean win = false;
      int cell = -1;
      int openPos = 0;
   
      //check for horizontal block/win
      for (int row = 0; row < 3; row++){
         for (int col = 0; col < 3; col++) {
            if(board[row][col] == HUMAN_PLAYER){
               human++;
            } else if(board[row][col] == COMPUTER_PLAYER){
               comp++;
            } else {
               open++;
               openPos = col;
            }
            
            //prioritize win over block
            if(open == 1 && comp == 2){
               cell = (row*3)+openPos+1;
               win = true;
            } else if(open == 1 && human == 2 && !win){
               cell = (row*3)+openPos+1;
            }
         }
      
         human=0;
         comp=0;
         open=0;
         openPos=0;
      }
      
      //check for vertical block/win
      for (int col = 0; col < 3; col++){
         for (int row = 0; row < 3; row++) {
            if(board[row][col] == HUMAN_PLAYER){
               human++;
            } else if(board[row][col] == COMPUTER_PLAYER){
               comp++;
            } else {
               open++;
               openPos = row;
            }
             
            if(open == 1 && comp == 2){
               cell = (openPos*3)+col+1;
               win = true;
            } else if(open == 1 && human == 2 && !win){
               cell = (openPos*3)+col+1;
            }
         }
         
         human=0;
         comp=0;
         open=0;
         openPos=0;
      }
      
      //check for top L-r diagonal win/block
      for (int rocol = 0; rocol < 3; rocol++){
         if(board[rocol][rocol] == HUMAN_PLAYER){
            human++; 
         } else if(board[rocol][rocol] == COMPUTER_PLAYER){
            comp++; 
         } else {
            open++;
            openPos = rocol;
         }
         if(open == 1 && comp == 2){
            cell = (openPos*3+openPos+1);
            win = true;
         } else if(open == 1 && human == 2 && !win){
            cell = (openPos*3+openPos+1);
         }
         
      }
      human=0;
      comp=0;
      open=0;
      openPos=0;
      
      //check for top r-L diagonal win/block
      if(board[1][1] == HUMAN_PLAYER){
         human++; 
      } else if(board[1][1] == COMPUTER_PLAYER){
         comp++; 
      } else {
         open++;
         openPos = 1;
      }
      
      if(board[0][2] == HUMAN_PLAYER){
         human++; 
      } else if(board[0][2] == COMPUTER_PLAYER){
         comp++; 
      } else {
         open++;
         openPos = 0;
      }
      
      if(board[2][0] == HUMAN_PLAYER){
         human++; 
      } else if(board[2][0] == COMPUTER_PLAYER){
         comp++; 
      } else {
         open++;
         openPos = 2;
      }
      
      if(open == 1 && comp == 2){
         cell = (openPos*3)+(3-openPos);
         win = true;
      } else if(open == 1 && human == 2 && !win){
         cell = (openPos*3)+(3-openPos);
      }     
      human=0;
      comp=0;
      open=0;
      openPos=0;
      
      return cell;
   } 

   //Update the board with either players move.
   static boolean updateBoard(char[][] board, int cellNumber, char player) {
      boolean available = false;
      if(!(cellNumber < 1 && cellNumber > 9)){   
         int row = (cellNumber - 1) / board.length;
         int col = (cellNumber - 1) % board.length;
         char status = board[row][col];
      
         if (status != HUMAN_PLAYER && status != COMPUTER_PLAYER){
            board[row][col] = player;
            available = true;
         }
      }
      return available;
   }
   
   //Update board with human's and computer's moves
   static void displayPieces(DrawingPanel panel, Image image, int cell){ 
      Graphics graphicsSurface = panel.getGraphics();
      graphicsSurface.drawImage(image,200*((cell-1)%3)+(cell-1)%3, 200*((cell-1)/3)+67+(cell-1)/3, panel);
   }
   
   // Instructions
   static void displayInstructions(Graphics g) {
      g.setFont(new Font("Arial", Font.PLAIN, 20));
      g.drawString("Instrcutions: ", 15,20);
      g.drawString("Play tic-tac-toe agianst the computer", 130,20);
      g.drawString("get 3 suns in a row to win", 130,50);
   }  
   
   //Update the board with either players move.
   static boolean updateArray(char[][] board, int cellNumber, char player) {
      boolean available = false;
      if(!(cellNumber < 1 && cellNumber > 9)){   
         int row = (cellNumber - 1) / board.length;
         int col = (cellNumber - 1) % board.length;
         char status = board[row][col];
      
         if (status != HUMAN_PLAYER && status != COMPUTER_PLAYER){
            board[row][col] = player;
            available = true;
         }
      }
      return available;
   }
   
   // Draw board lines
   static void displayBoard(Graphics g){
      for(int i = 1; i < 3; i++){   
         g.drawLine(i*200+(i-1),67,i*200+(i-1),670);
      } 
      for(int i = 1; i < 3; i++){   
         g.drawLine(0,67+(i-1)+(i*200),601,67+(i-1)+(i*200));
      }  
   }
   
   // Game Over (Results)
   static void displayResults(char[][] board) {
      System.out.println();
      if(isBoardFull(board) && !haveWinner(board)){
         JOptionPane.showMessageDialog(null, "So close! It was a tie.","Game Over", JOptionPane.INFORMATION_MESSAGE);
      } else {
         if(whoWon(board) == HUMAN_PLAYER){
            JOptionPane.showMessageDialog(null, "You won. Congrtulations!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
         } else {
            JOptionPane.showMessageDialog(null, "The computer won. Better luck next time", "Game Over", JOptionPane.INFORMATION_MESSAGE);
         }
      }      
   }
}