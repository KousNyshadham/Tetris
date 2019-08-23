package assignment;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import javax.swing.KeyStroke;
import assignment.Board.Action;

public class JBrainTetris extends JTetris{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		createGUI(new JBrainTetris());
	}

	public JBrainTetris() {
		super();
        setPreferredSize(new Dimension(WIDTH*PIXELS+2, (HEIGHT+TOP_SPACE)*PIXELS+2));
        gameOn = false;
        board = new TetrisBoard(WIDTH, HEIGHT + TOP_SPACE);
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	double a = -0.4910981485621495;
            	double b = 0.5690509314816721;
            	double c = -0.6374131843042231;
            	double d = -0.16943458423185362;
            	Queue<Board.Action> nextMove = nextMove(board, a, b, c , d);
            	while(!nextMove.isEmpty()) {
            		tick(nextMove.poll());
            	}
            }
        });
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	while(gameOn) {
            		double a = -0.4910981485621495;
                	double b = 0.5690509314816721;
                	double c = -0.6374131843042231;
                	double d = -0.16943458423185362;
                	Queue<Board.Action> nextMove = nextMove(board, a, b, c , d);
            		while(!nextMove.isEmpty()) {
            			tick(nextMove.poll());
            		}
            	}
            }
        },
        "computeAI", KeyStroke.getKeyStroke('c'), WHEN_IN_FOCUSED_WINDOW);	
	}
	
	public Queue<Board.Action> nextMove(Board currentBoard, double a, double b, double c, double d){
		ArrayList<BoardState> options = enumerateOptions(currentBoard);
		double best = 0;
        int bestIndex = 0;
		for(int i = 0; i < options.size(); i++) {
			double score = scoreBoard(options.get(i).board, a, b, c , d);
			if(score > best) {
				best = score;
				bestIndex = i;
			}
		}
		return options.get(bestIndex).moves;
	}
	
	private ArrayList<BoardState> enumerateOptions(Board currentBoard){
		ArrayList<BoardState> states = new ArrayList<BoardState>();
		ArrayList<Board> boards = new ArrayList<Board>();
		ArrayList<Queue<Board.Action>> moves = new ArrayList<Queue<Board.Action>>();
		//no movement
		boards.add(currentBoard.testMove(Action.DROP));
		Queue<Board.Action> nothing = new LinkedList<Board.Action>(); 
		nothing.add(Action.DROP);
		moves.add(nothing);
		//left, no rotations
		Board left = currentBoard.testMove(Action.LEFT);
		int count = 1;
        while (left.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(left.testMove(Action.DROP));
        	Queue<Board.Action> lefts = new LinkedList<Board.Action>();
            int i = 0;
            while(i < count) {
            	lefts.add(Action.LEFT);
            	i++;
            }
            lefts.add(Action.DROP);
            moves.add(lefts);
            left.move(Action.LEFT);
            count++;
        }
        //right, no rotations
        Board right = currentBoard.testMove(Action.RIGHT);
        count = 1;
        while(right.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(right.testMove(Action.DROP));
        	Queue<Board.Action> rights = new LinkedList<Board.Action>();
        	int i = 0;
        	while(i < count) {
        		rights.add(Action.RIGHT);
        		i++;
        	}
        	rights.add(Action.DROP);
        	moves.add(rights);
        	right.move(Action.RIGHT);
        	count++;
        }
        //clockwise rotation
        Board clockwise = currentBoard.testMove(Action.CLOCKWISE);
        boards.add(clockwise.testMove(Action.DROP));
        Queue<Board.Action> clockwiseQueue = new LinkedList<Board.Action>();
        clockwiseQueue.add(Action.CLOCKWISE);
        clockwiseQueue.add(Action.DROP);
        moves.add(clockwiseQueue);
        //left, clockwise rotation
        clockwise.move(Action.LEFT);
        count = 1;
        while(clockwise.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(clockwise.testMove(Action.DROP));
        	Queue<Board.Action> lefts = new LinkedList<Board.Action>();
        	lefts.add(Action.CLOCKWISE);
            int i = 0;
            while(i < count) {
            	lefts.add(Action.LEFT);
            	i++;
            }
            lefts.add(Action.DROP);
            moves.add(lefts);
            clockwise.move(Action.LEFT);
            count++;
        }
        //right, clockwise rotation
        clockwise = currentBoard.testMove(Action.CLOCKWISE);
        clockwise.move(Action.RIGHT);
        count = 1;
        while(clockwise.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(clockwise.testMove(Action.DROP));
        	Queue<Board.Action> rights = new LinkedList<Board.Action>();
        	rights.add(Action.CLOCKWISE);
        	int i = 0;
        	while(i < count) {
        		rights.add(Action.RIGHT);
        		i++;
        	}
        	rights.add(Action.DROP);
        	moves.add(rights);
        	clockwise.move(Action.RIGHT);
        	count++;
        }
        //counterclockwise rotation
        Board counterclockwise = currentBoard.testMove(Action.COUNTERCLOCKWISE);
        boards.add(counterclockwise.testMove(Action.DROP));
        Queue<Board.Action> counterclockwiseQueue = new LinkedList<Board.Action>();
        counterclockwiseQueue.add(Action.COUNTERCLOCKWISE);
        counterclockwiseQueue.add(Action.DROP);
        moves.add(counterclockwiseQueue);
        //left, counterclockwise rotation
        counterclockwise.move(Action.LEFT);
        count = 1;
        while(counterclockwise.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(counterclockwise.testMove(Action.DROP));
        	Queue<Board.Action> lefts = new LinkedList<Board.Action>();
        	lefts.add(Action.COUNTERCLOCKWISE);
            int i = 0;
            while(i < count) {
            	lefts.add(Action.LEFT);
            	i++;
            }
            lefts.add(Action.DROP);
            moves.add(lefts);
            counterclockwise.move(Action.LEFT);
            count++;
        }
        //right, counterclockwise rotation
        counterclockwise = currentBoard.testMove(Action.COUNTERCLOCKWISE);
        counterclockwise.move(Action.RIGHT);
        count = 1;
        while(counterclockwise.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(counterclockwise.testMove(Action.DROP));
        	Queue<Board.Action> rights = new LinkedList<Board.Action>();
        	rights.add(Action.COUNTERCLOCKWISE);
        	int i = 0;
        	while(i < count) {
        		rights.add(Action.RIGHT);
        		i++;
        	}
        	rights.add(Action.DROP);
        	moves.add(rights);
        	counterclockwise.move(Action.RIGHT);
        	count++;
        }
        //two rotations
        Board two = currentBoard.testMove(Action.CLOCKWISE).testMove(Action.CLOCKWISE);
        boards.add(two.testMove(Action.DROP));
        Queue<Board.Action> twoQueue = new LinkedList<Board.Action>();
        twoQueue.add(Action.CLOCKWISE);
        twoQueue.add(Action.CLOCKWISE);
        twoQueue.add(Action.DROP);
        moves.add(twoQueue);
        //left, two rotations
        two.move(Action.LEFT);
        count = 1;
        while(two.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(two.testMove(Action.DROP));
        	Queue<Board.Action> lefts = new LinkedList<Board.Action>();
        	lefts.add(Action.CLOCKWISE);
        	lefts.add(Action.CLOCKWISE);
            int i = 0;
            while(i < count) {
            	lefts.add(Action.LEFT);
            	i++;
            }
            lefts.add(Action.DROP);
            moves.add(lefts);
            two.move(Action.LEFT);
            count++;
        }
        //right, two rotations
        two = currentBoard.testMove(Action.CLOCKWISE).testMove(Action.CLOCKWISE);
        two.move(Action.RIGHT);
        count = 1;
        while(two.getLastResult() == Board.Result.SUCCESS) {
        	boards.add(two.testMove(Action.DROP));
        	Queue<Board.Action> rights = new LinkedList<Board.Action>();
        	rights.add(Action.CLOCKWISE);
        	rights.add(Action.CLOCKWISE);
        	int i = 0;
        	while(i < count) {
        		rights.add(Action.RIGHT);
        		i++;
        	}
        	rights.add(Action.DROP);
        	moves.add(rights);
        	two.move(Action.RIGHT);
        	count++;
        }
        for(int i = 0; i < boards.size(); i++) {
        	states.add(new BoardState(moves.get(i), boards.get(i)));
        }
		return states;
	}
	
	private int aggregateHeight(Board newBoard) {
    	int aggregateHeight = 0;
    	for(int i = 0; i < newBoard.getWidth(); i++) {
    		aggregateHeight += newBoard.getColumnHeight(i);
    	}
    	return aggregateHeight;
    }
    
    private int holes(Board newBoard) {
    	int holes = 0;
    	for(int i = 0; i < newBoard.getWidth(); i++) {
    		int columnHeight = newBoard.getColumnHeight(i);
    		for(int j = columnHeight-2; j >= 0; j--) {
    			if(newBoard.getGrid(i, j) == null) {
    				holes++;
    			}
    		}
    	}
    	return holes;
    }
    
    private int bumpiness(Board newBoard) {
    	int bumpiness = 0;
    	for(int i = 0; i < newBoard.getWidth()-1; i++) {
    		bumpiness += Math.abs(newBoard.getColumnHeight(i) - newBoard.getColumnHeight(i+1));
    	}
    	return bumpiness;
    }
    
    private double scoreBoard(Board newBoard, double a, double b, double c, double d) {
        return 100 + (a * aggregateHeight(newBoard) + b * newBoard.getRowsCleared() + 
        		c * holes(newBoard) + d * bumpiness(newBoard));
    }
    
    public class BoardState{
		Queue<Board.Action> moves = new LinkedList<Board.Action>();
		Board board;
		double fitness;
		public BoardState(Queue<Board.Action> moves, Board board) {
			this.moves = moves;
			this.board = board;
		}
	}
}