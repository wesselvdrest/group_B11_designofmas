import java.util.ArrayList;
import java.util.Collections;
import java.awt.Point;

public class SolverDoubleDealing extends GameSolver {

    @Override
    public Edge getNextMove(final Board board, int color) {
		boolean Endgame = false;
		
		if(board.getBoxCount(0)==0 && board.getBoxCount(1)==0) { //Endgame means all boxes have at least 2 sides
			Endgame = true; 
		}
		
		if(!Endgame) {//If not in Endgame yet, play like Heuristic
			referenceColor = color;
			ArrayList<Edge> moves = board.getAvailableMoves();
			Collections.shuffle(moves);
			int moveCount = moves.size();
			int value[] = new int[moveCount];
        
			for(int i=0;i<moveCount;i++) {
				Board nextBoard = board.getNewBoard(moves.get(i), color);
				value[i] = heuristic(nextBoard, (nextBoard.getScore(color) > board.getScore(color) ? color : Board.toggleColor(color)));
			}

			int maxValueIndex=0;
			for(int i=1;i<moveCount;i++){
				if(value[i]>value[maxValueIndex])
					maxValueIndex=i;
			}
			return moves.get(maxValueIndex);
		}
		else { //Endgame = true
			ArrayList<Edge> moves = board.getAvailableMoves();
			ReturnValues chainValues = board.getChainInformation();
			ArrayList<Point> Shorties = chainValues.shortest; //Return the array with the boxes that are part of the shortest chain (ideally single blocks)
			ArrayList<Point> singleBoxes = chainValues.singleBoxes;
			ArrayList<Point> diadChains = chainValues.diadChains;
			
			ArrayList<Edge> singleMoves = board.getAvailableMoves(singleBoxes);
			int moveCount = moves.size();
			int singleCount = singleMoves.size();
			int score = board.getScore(color);
			boolean canTakeBox = false;
			int boxCounter = 0;
			boolean flag = true;
			Board LastBoard = board;
			
			for(int i=0;i<moveCount;i++) { // if you can double cross, do so.
				Board nextBoard = board.getNewBoard(moves.get(i), color);
				if(nextBoard.getScore(color) == score+2) {
					return moves.get(i);
				}
			}
			for(int i=0;i<singleCount;i++) { // if you can take a single box, do so.
				Board nextBoard = board.getNewBoard(singleMoves.get(i), color);
				if(nextBoard.getScore(color) > score) {
					return singleMoves.get(i);
				}
			}
			
			
			
			while(flag == true){ // check how many boxes can be taken
				flag = false;
				for(int i=0;i<moveCount;i++) { 
					Board nextBoard = LastBoard.getNewBoard(moves.get(i), color);
					if(nextBoard.getScore(color) > LastBoard.getScore(color)) {
						boxCounter++;
						flag = true;
						LastBoard = nextBoard;
						moves = LastBoard.getAvailableMoves();
						moveCount = moves.size();
						break;
					}
				}
			}
			if(boxCounter > 0){
				canTakeBox = true;
			}
			int BoxesLeft = board.amountBoxesLeft();
			if(boxCounter == BoxesLeft){ //If you can take all boxes that are left on the board, do so (less think time)
				moves = board.getAvailableMoves();
				moveCount = moves.size();
				score = board.getScore(color);
        
				for(int i=0;i<moveCount;i++) { // if boxes can be taken, take boxes.
					Board nextBoard = board.getNewBoard(moves.get(i), color);
					if(nextBoard.getScore(color) > score) {
						return moves.get(i);
					}
				}
			}
			
			int[] Histogram = chainValues.Histogram;
			int HistSize = Histogram.length;
			int currentcount = 0; //end score if we don't double deal
			int reversecount = 0; //if we do double deal, what could we score
			if(boxCounter>1){
				Histogram[boxCounter]--;
				currentcount += boxCounter;
				reversecount += boxCounter-2; //you lose 2 boxes from double dealing
			}
			boolean countIN = false;
			for(int i=0; i<HistSize; i++){ //count score if we assume the smallest chain will be given free
				if(Histogram[i] != 0){
					Histogram[i]--;
					i--;
					if(countIN){
						currentcount += i;
						countIN = false;
					} else {
						reversecount += i;
						countIN = true;
					}
					
				}
			}
			
			
			if(currentcount >= reversecount){
				// you will gain the most chains by not double dealing: take boxes if you can and open the smallest chain
				moves = board.getAvailableMoves();
				moveCount = moves.size();
				score = board.getScore(color);
        
				for(int i=0;i<moveCount;i++) { // if boxes can be taken, take boxes.
					Board nextBoard = board.getNewBoard(moves.get(i), color);
					if(nextBoard.getScore(color) > score) {
						return moves.get(i);
					}
				}
				ArrayList<Edge> bMoves = board.getAvailableMoves(Shorties); //Get the open edges of these boxes
				Collections.shuffle(bMoves);
				return bMoves.get(0);//Pick one of these edges as the next move (thus opening the shortest chain).
			}

			if(reversecount > currentcount && canTakeBox){
				// score will be higher if you double deal (by opening the outside of a diad chain (which does not result in increased score))
				if(!diadChains.isEmpty()){
					ArrayList<Edge> dMoves = board.getAvailableMoves(diadChains); // get empty sides of the boxes in the diad
					int DiadSize = dMoves.size();
					score = board.getScore(color);
					for(int i=0;i<DiadSize;i++) { // find a move that does not take a box if possible
						Board nextBoard = board.getNewBoard(dMoves.get(i), color);
						if(nextBoard.getScore(color) == score) {
							return dMoves.get(i);
						}
					}
					return dMoves.get(0);
				} else { // take the box
					score = board.getScore(color);
					moves = board.getAvailableMoves();
					moveCount = moves.size();
					for(int i=0;i<moveCount;i++) {
						Board nextBoard = board.getNewBoard(moves.get(i), color);
						if(nextBoard.getScore(color) > score) {
							return moves.get(i);
						}
					}
				}
			}
			
			if(reversecount > currentcount && canTakeBox == false){
				//if double dealing is better and there are no boxes to take, try a half-hearted handout (outside of diad chain)
				// we are going to try to not open the chain, by having the opponent make a mistake
				int n = board.getSize();
				if(!diadChains.isEmpty()){
					ArrayList<Edge> dMoves = board.getAvailableMoves(diadChains); // get empty sides of the boxes in the diad
					for(Edge edge : dMoves) { // find an edge that is on a border
						if((edge.isHorizontal() && edge.getY()==0) || (edge.isHorizontal() && edge.getY()==n-1) ||
						(!(edge.isHorizontal()) && edge.getX()==0) || (!(edge.isHorizontal()) && edge.getX()==n-1) ){
							return edge;
						}
					}
				} else { // open smallest chain
					ArrayList<Edge> bMoves = board.getAvailableMoves(Shorties); //Get the open edges of these boxes
					Collections.shuffle(bMoves);
					return bMoves.get(0);//Pick one of these edges as the next move (thus opening the shortest chain).
				}
				
			}
			//Default
			moves = board.getAvailableMoves();
			return moves.get(0);
		}
    }
}