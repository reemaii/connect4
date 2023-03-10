
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Point;
import java.util.*;

public class connect4AI {

	static int depth = 8; 

	//Create Agents and State
	static minimaxAgent miniMax = new minimaxAgent(depth);
	static alphabetaAgent alphaBeta = new alphabetaAgent(depth); 
	static State s = new State(6,7);
	static Random ran = new Random();
	
	public static void main(String[] args) throws CloneNotSupportedException{

	Scanner in = new Scanner(System.in);
    long start,end,elapsedTime;
	int count = 0;
	
	
	int alphaBeta_move,miniMax_move,random_move,enemy_move;
	

	//Calculate time in millisecond
	start = System.currentTimeMillis();
	while(true){

		// main player is either mini-max Agent or alphaBeta agent
		
		/*if (count != 0) {
		alphaBeta_move = alphaBeta.getAction(s);
		s = s.generateSuccessor('O', alphaBeta_move);
		System.out.println("\nAgent moved to Column " + alphaBeta_move); }
		else {
			s = s.generateSuccessor('O', 3);	// to get better movements
		    count++; 
		    System.out.println("\nAgent moved to Column 3"); }*/
		
		
		if (count != 0) {
			miniMax_move = miniMax.getAction(s);
			s = s.generateSuccessor('O', miniMax_move);
			System.out.println("\nAgent moved to Column " + miniMax_move); }
			else {
				s = s.generateSuccessor('O', 3);	// to get better movements
			    count++; 
			    System.out.println("\nAgent moved to Column 3"); }
		
		

		
		s.printBoard();
		
		//check if O won?
		if(s.isGoal('O')) {
			System.out.println("GAME OVER! \nAgent wins! ");
		    break; }
		
		
		// enemy is either user or random agent
		
		/*random_move = ran.nextInt(7);//0..6
		s = s.generateSuccessor('X', random_move);
		System.out.println("\nRandom moved to Column " + random_move);*/
		
		enemy_move = in.nextInt();
        while(enemy_move<0 || enemy_move > 6  || !s.colAvailable(enemy_move)){
            System.out.println("Invalid move.\n\nYour move should be (0-6): "); 
            enemy_move = in.nextInt();
        } 
		s = s.generateSuccessor('X', enemy_move); 
		System.out.println("User moved to Column " + enemy_move);
		
		s.printBoard();
		
		//check if X won? break
		if(s.isGoal('X')) {
			System.out.println("WOW! \nYou wins! ");
		break; }
		//pause
		
		//check if it is a draw? break
		if (s.gameResult() == 'D') {
			System.out.println("Its a draw! try again ;) !");
		break; }
		
	}
	
	end = System.currentTimeMillis();
	elapsedTime = (((end - start)/1000)%60); // convert to seconds
	System.out.println("Time elapsed is: "+ elapsedTime +"seconds");	
		

	}// end main

}// end connect4AI

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class State implements Cloneable {

	//this class represent the state of the game
	
	int rows, cols;
	char[][] board;

	/* basic methods for constructing and proper hashing of State objects */
	public State(int n_rows, int n_cols){
		this.rows=n_rows;
		this.cols=n_cols;
		this.board=new char[n_rows][n_cols];
		
		//fill the board up with blanks
		for(int i=0; i<n_rows; i++)
			for(int j=0; j<n_cols; j++)
				this.board[i][j]='.';
	}
	
    public boolean colAvailable(int col){
        for (int i= 0; i< board.length-1 ; i++){
            if (board[i][col]=='.') {
                return true;
            }
        }
        return false;
    }
	
	public boolean equals(Object obj){
		//have faith and cast
		State other=(State)obj;
		return Arrays.deepEquals(this.board, other.board);
	}
	
	public int hashCode(){
		String b="";
		for(int i=0; i<board.length; i++)
			b+=String.valueOf(board[0]);
		return b.hashCode();
	}

	public Object clone() throws CloneNotSupportedException {
        State new_state=new State(this.rows, this.cols);
		for (int i=0; i<this.rows; i++)
			new_state.board[i] = (char[]) this.board[i].clone();
		return new_state;
	}
	
	/* returns a list of actions that can be taken from the current state
	actions are integers representing the column where a coin can be dropped */
	public ArrayList<Integer> getLegalActions(){
		ArrayList<Integer> actions=new ArrayList<Integer>();
		for(int j=0; j<this.cols; j++)
			if(this.board[0][j]=='.')
				actions.add(j);
		return actions;
	}
	
	/* returns a State object that is obtained by the agent (parameter)
	performing an action (parameter) on the current state */
	public State generateSuccessor(char agent, int action) throws CloneNotSupportedException{
		
		int row;
		for(row=0; row<this.rows && this.board[row][action]!='X' && this.board[row][action]!='O'; row++);
		State new_state=(State)this.clone();
		new_state.board[row-1][action]=agent;
		
		return new_state;
	}

	/* Print's the current state's board in a nice pretty way */
	public void printBoard(){
		System.out.println(new String(new char[this.cols*2]).replace('\0', '-'));
		for(int i=0; i<this.rows; i++){
			for(int j=0; j<this.cols; j++){
				System.out.print(this.board[i][j]+" ");
			}
			System.out.println();
		}	
		System.out.println(new String(new char[this.cols*2]).replace('\0', '-'));
	}
	
	/* returns True/False if the agent(parameter) has won the game
	by checking all rows/columns/diagonals for a sequence of >=4 */
	public boolean isGoal(char agent){
	
		String find=""+agent+""+agent+""+agent+""+agent;
		
		//check rows
		for(int i=0; i<this.rows; i++)
			if(String.valueOf(this.board[i]).contains(find))
				return true;
		
		//check cols
		for(int j=0; j<this.cols; j++){
			String col="";
			for(int i=0; i<this.rows; i++)
				col+=this.board[i][j];
				
			if(col.contains(find))
				return true;
		}
		
		//check diags
		ArrayList<Point> pos_right=new ArrayList<Point>();
		ArrayList<Point> pos_left=new ArrayList<Point>();
		
		for(int j=0; j<this.cols-4+1; j++)
			pos_right.add(new Point(0,j));
		for(int j=4-1; j<this.cols; j++)
			pos_left.add(new Point(0,j));	
		for(int i=1; i<this.rows-4+1; i++){
			pos_right.add(new Point(i,0));
			pos_left.add(new Point(i,this.cols-1));
		}
	
		//check right diags
		for (Point p : pos_right) {
			String d="";
			int x=p.x, y=p.y;
			while(true){				
				if (x>=this.rows||y>=this.cols)
					break;
				d+=this.board[x][y];
				x+=1; y+=1;
			}
			if(d.contains(find))
				return true;
		}
		
		//check left diags
		for (Point p : pos_left) {
			String d="";
			int x=p.x, y=p.y;
			while(true){
				if(y<0||x>=this.rows||y>=this.cols)
					break;
				d+=this.board[x][y];
				x+=1; y-=1;
			}
			if(d.contains(find))
				return true;
		}
		
		return false;
		
	}
	
	//check the game results
	public char gameResult(){
		
		 int agentScore = 0, userScore = 0;
	        for(int i=5;i>=0;--i){
	            for(int j=0;j<=6;++j){
	                if(board[i][j]=='.') continue;
	                
	                //Checking cells to the right
	                if(j<=3){
	                    for(int k=0;k<4;++k){ 
	                            if(board[i][j+k]=='O') agentScore++;
	                            else if(board[i][j+k]=='X') userScore++;
	                            else break; 
	                    }
	                    if(agentScore==4)return 'O'; else if (userScore==4)return 'X';
	                    agentScore = 0; userScore = 0;
	                } 
	                
	                //Checking cells up
	                if(i>=3){
	                    for(int k=0;k<4;++k){
	                            if(board[i-k][j]=='O') agentScore++;
	                            else if(board[i-k][j]=='X') userScore++;
	                            else break;
	                    }
	                    if(agentScore==4)return 'O'; else if (userScore==4)return 'X';
	                    agentScore = 0; userScore = 0;
	                } 
	                
	                //Checking diagonal up-right
	                if(j<=3 && i>= 3){
	                    for(int k=0;k<4;++k){
	                        if(board[i-k][j+k]=='O') agentScore++;
	                        else if(board[i-k][j+k]=='X') userScore++;
	                        else break;
	                    }
	                    if(agentScore==4)return 'O'; else if (userScore==4)return 'X';
	                    agentScore = 0; userScore = 0;
	                }
	                
	                //Checking diagonal up-left
	                if(j>=3 && i>=3){
	                    for(int k=0;k<4;++k){
	                        if(board[i-k][j-k]=='O') agentScore++;
	                        else if(board[i-k][j-k]=='X') userScore++;
	                        else break;
	                    } 
	                    if(agentScore==4) return 'O'; else if (userScore==4)return 'X';
	                    agentScore = 0; userScore = 0;
	                }  
	            }
	        }
		 for(int j=0;j<7;++j){
	            //Game has not ended yet
	            if(this.board[0][j]=='.')
	            	return 'N'; //not finished
	        }
		 
	        //Game draw!
	        return 'D';//draw
        }
	

	/* returns the value of each state for minimax to min/max over at
	zero depth. Right now it's pretty trivial, looking for only goal states.
	(This would be perfect for infinite depth minimax. Not so great for d=2) */
	public double evaluationFunction(){
	
		if (this.isGoal('O'))
			return 1000.0;
		if (this.isGoal('X'))
			return -1000.0;
		
		return 0.0;
	}
	
    int calculateScore(int agentScore, int moreMoves){   
        int moveScore = 4 - moreMoves;
        if(agentScore==0) return 0;
        else if(agentScore==1)return 1*moveScore;
        else if(agentScore==2)return 10*moveScore;
        else if(agentScore==3)return 100*moveScore;
        else return 1000;
    }
    
	public int evaluateBoard(){
	      
        int agentScore=1;
        int score = 0;
        int blanks = 0;
        int k=0, moreMoves=0;
        
        for(int i=5;i>=0;--i){
            for(int j=0;j<=6;++j){
                
                if(board[i][j]=='.'|| board[i][j] == 'X') continue; 
                
                
              //check cells to the right
                if(j<=3){ 
                    for(k=1;k<4;++k){
                        if(board[i][j+k]=='O')
                        agentScore++;
                        else if(board[i][j+k]=='X'){
                        	      agentScore = 0;
                        	      blanks = 0;
                                  break; }
                        
                        else blanks++;
                    }
                     
                    moreMoves = 0; 
                    if(blanks>0) 
                        for(int c=1;c<4;++c){
                            int column = j+c;
                            for(int m=i; m<= 5;m++){
                             if( board[m][column]=='.')
                            	moreMoves++;
                                else break;
                            } 
                        } 
                    
                    if(moreMoves!=0) 
                    score += calculateScore(agentScore, moreMoves);
                    agentScore = 1;   
                    blanks = 0;
                } 
                
              
                if(i>=3){
                    for(k=1;k<4;++k){
                        if(board[i-k][j]=='O')
                        	agentScore++;
                        else if(board[i-k][j]=='X'){
                        	    agentScore=0;
                        	    break; } 
                    } 
                    
                    moreMoves = 0; 
                    if(agentScore>0){
                        int column = j;
                        for(int m=i-k+1; m<=i-1;m++){
                         if(board[m][column]=='.')
                        	 moreMoves++;
                            else break;
                        }  
                    }
                    if(moreMoves!=0) 
                    score += calculateScore(agentScore, moreMoves);
                    agentScore=1;  
                    blanks = 0;
                }
                
              //check cells up
                
                if(j>=3){
                    for(k=1;k<4;++k){
                        if(board[i][j-k]=='O')
                        	agentScore++;
                        else if(board[i][j-k]=='X')
                        { agentScore=0; 
                        blanks=0;
                        break;}
                        else blanks++;
                    }
                    moreMoves=0;
                    if(blanks>0) 
                        for(int c=1;c<4;++c){
                            int column = j- c;
                            for(int m=i; m<= 5;m++){
                             if(board[m][column]=='.')
                            	 moreMoves++;
                                else break;
                            } 
                        } 
                    
                    if(moreMoves!=0) score += calculateScore(agentScore, moreMoves);
                    agentScore=1; 
                    blanks = 0;
                }
                 
              //Checking diagonal up-right
                if(j<=3 && i>=3){
                    for(k=1;k<4;++k){
                        if(board[i-k][j+k]=='O')
                        	agentScore++;
                        else if(board[i-k][j+k]=='X'){
                        	agentScore=0;
                        	blanks=0;
                        	break;}
                        else blanks++;                        
                    }
                    
                    moreMoves=0;
                    if(blanks>0){
                        for(int c=1;c<4;++c){
                            int column = j+c, row = i-c;
                            for(int m=row;m<=5;++m){
                                if(board[m][column]=='.')
                                	moreMoves++;
                                else if(board[m][column]=='X');
                                else break;
                            }
                        } 
                        if(moreMoves!=0) 
                        score += calculateScore(agentScore, moreMoves);
                        agentScore=1;
                        blanks = 0;
                    }
                }
                 
              //Checking diagonal up-left
                if(i>=3 && j>=3){
                    for(k=1;k<4;++k){
                        if(board[i-k][j-k]=='.')
                           agentScore++;
                        else if(board[i-k][j-k]=='X'){
                        	agentScore=0;
                        	blanks=0;
                        	break;}
                        else blanks++;                        
                    }
                    moreMoves=0;
                    if(blanks>0){
                        for(int c=1;c<4;++c){
                            int column = j-c, row = i-c;
                            for(int m=row;m<=5;++m){
                                if(board[m][column]=='.')
                                	moreMoves++;
                                else if(board[m][column]=='X');
                                else break;
                            }
                        } 
                        if(moreMoves!=0) 
                        score += calculateScore( agentScore, moreMoves);
                        agentScore=1;
                        blanks = 0;
                    }
                } 
            }
        }
        return score;
    } 
	
}// end state

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class minimaxAgent {
	
	//this class represent the minimaxAgent
	
	int depth; 
	int nextMove;
	
	public minimaxAgent(int depth){ 
		this.depth = depth; }	
	
	public int getAction(State state) throws CloneNotSupportedException {   
		//the protagonist is max
		nextMove = -1;
		MAX_VALUE(state, 0);
		return nextMove;
		
	}
	
	public double MAX_VALUE(State state, int depth) throws CloneNotSupportedException {   
		
		
		char gameResult = state.gameResult();
        if(gameResult=='O')return 10000000/2;
        else if(gameResult=='X')return -10000000/2;
        else if(gameResult=='D')return 0; 
		
		//check if terminal -> use evaluation function
		if(depth == this.depth)
		return state.evaluateBoard();
		
		else{
			
		// else generate children
			ArrayList<Integer> children = new ArrayList<Integer>(); //Create children list
		    children = state.getLegalActions();
		    double value = -10000000; // initially -∞
		    double childrenValue;
		    
		//compare between the children values and value
		    for(int j =0; j<children.size();j++)
		        {
		        childrenValue = MIN_VALUE(state.generateSuccessor('O',children.get(j)),depth+1);// call MIN
		        
		        if(depth==0){
	               if (childrenValue > value )
	                   this.nextMove = j; }
		        
		        value = Math.max(childrenValue, value);//update the value to child's value (minimum), if it is larger
		        
		        }
		
		return value; //i.e maximum value
		}
	}
	
	public double MIN_VALUE(State state, int depth) throws CloneNotSupportedException {
		
		
		char gameResult = state.gameResult();
        if(gameResult=='O')return 10000000/2;
        else if(gameResult=='X')return -10000000/2;
        else if(gameResult=='D')return 0; 
		
		//check if terminal -> use evaluation function
		if(depth == this.depth)
		return state.evaluateBoard();
		
		else {
			ArrayList<Integer> children = new ArrayList<Integer>(); //Create children list	
		// else generate children
		    children = state.getLegalActions();
		    double value = 10000000; // initially ∞
		    double childrenValue;
		//compare between the children values and value
		    for(int i =0; i<children.size();i++)
		        {
		        childrenValue = MAX_VALUE(state.generateSuccessor('X',children.get(i)),depth+1);// call MAX
		        value = Math.min(childrenValue, value); //update the value to child's value (minimum), if it is larger    	
		        }//end for
		
		return value; //i.e minumum value
		}
		
	
}
	
}// end miniMaxAgent */

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class alphabetaAgent {
	
	//this class represent the alpha-beta Agent
	int depth;
	int nextMove;
	
	
	public alphabetaAgent(int depth)
	{
		this.depth = depth;
	}	
	
	public int getAction(State state) throws CloneNotSupportedException
	{   nextMove = -1;
		MAX_VALUE(state, 0 ,-10000000,10000000); // alpha and beta respectively initialized to -∞ and ∞
		return nextMove;
		
	}
	
	public double MAX_VALUE(State state, int depth, double alpha,double beta) throws CloneNotSupportedException
	{

		
		char gameResult = state.gameResult();
        if(gameResult=='O')return 10000000/2;
        else if(gameResult=='X')return -10000000/2;
        else if(gameResult=='D')return 0; 
		
		//check if terminal -> use evaluation function
		if(depth == this.depth)
		return state.evaluateBoard();
		
		else{
			
		//else -> generate children
		ArrayList<Integer> children = new ArrayList<Integer>();//Create children array list
		children = state.getLegalActions();
		double value = -10000000; // start at -∞
		double childrenValue;
		
		// Now compare the value and children's value and update it 
		for(int j = 0; j<children.size();j++)
		{
			childrenValue =  MIN_VALUE(state.generateSuccessor('O',children.get(j)),depth+1,alpha,beta);// call MIN
			
			if(depth==0){
               if (childrenValue > value )
                   this.nextMove = j;
               if( childrenValue == 10000000/2) break; }
			
			value = Math.max(childrenValue, value); //update the value to child's value (maximum), if it is smaller
			
			if ( value >= beta)//condition for pruning
				break; // break and return value 
			
			alpha = Math.max(alpha, value); //update alpha with the maximum of alpha and value
			
			if(childrenValue == 10000000|| childrenValue == -10000000) break;
		}
		return value; //i.e maximum value
		}
	}
	
	public double MIN_VALUE(State state, int depth,double alpha,double beta) throws CloneNotSupportedException
	{
		
		char gameResult = state.gameResult();
        if(gameResult=='O') return 10000000/2;
        else if(gameResult=='X')return -10000000/2;
        else if(gameResult=='D')return 0; 
        
		
		//check if terminal -> use evaluation function
		if(depth == this.depth)
			return state.evaluateBoard();
		else{
		ArrayList<Integer> children = new ArrayList<Integer>();//Create children array list
		// else -> generate children
		children = state.getLegalActions();
		double value = 10000000; // start at ∞
        double childrenValue;
        
        // Now compare the value and children's value and update it
		for(int j =0; j<children.size();j++)
		{
			childrenValue =  MAX_VALUE(state.generateSuccessor('X',children.get(j)),depth+1,alpha,beta);// call MAX
			
			value = Math.min(childrenValue, value);//update the value to child's value (minimum), if it is larger
			
			if ( value <= alpha)//condition for pruning
				break; // break and return value 
				
			beta = Math.min(beta, value); //update beta with the minimum of beta and value
		  
			if(childrenValue == 10000000|| childrenValue == -10000000) break;
		}
		return value;//i.e miniumum value
		}
	}
	 

}// end alpha-beta