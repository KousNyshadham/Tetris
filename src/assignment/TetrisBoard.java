package assignment;

import java.awt.*;

import assignment.Piece.PieceType;

/**
 * Represents a Tetris board -- essentially a 2-d grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */
public final class TetrisBoard implements Board {

	private Piece.PieceType[][] board;
	private Piece piece;
	private Point piecePosition;
	private Result lastResult;
	private Action lastAction;
	private int lastRowsCleared;
	private int width;
	private int height;
	private int maxHeight;
	private int[] columnHeights;
	private int[] rowWidths;
	
	
    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
    	if(height < 4 || width < 4) {
    		throw new IllegalArgumentException("width and height of board need to be greater than 3!");
    	}
    	board = new Piece.PieceType[height][width];
    	lastResult = Result.NO_PIECE;
    	lastAction = Action.NOTHING;
    	this.width = width;
    	this.height = height;
    	columnHeights = new int[width];
    	rowWidths = new int[height];
    }
    
    private TetrisBoard(Piece.PieceType[][] board, Piece piece, Point piecePosition, Result lastResult, Action lastAction, int lastRowsCleared, 
    		int width, int height, int maxHeight, int[] columnHeights, int[] rowWidths) {
    	this.board = new Piece.PieceType[height][width];
    	for(int i = 0; i < height; i++) {
    		this.board[i] = board[i].clone();
    	}
    	this.piece = piece;
    	this.piecePosition = (Point) piecePosition.clone();
    	this.lastResult = lastResult;
    	this.lastAction = lastAction;
    	this.lastRowsCleared = lastRowsCleared;
    	this.width = width;
    	this.height = height;
    	this.maxHeight = maxHeight;
    	this.columnHeights = columnHeights.clone();
    	this.rowWidths = rowWidths.clone();
    }

    public Result move(Action act) { 
    	lastAction = act;
    	lastRowsCleared = 0;
    	if(piece != null) {
    		Point[] body = piece.getBody();
    		int rotationIndex = piece.getRotationIndex();
    		switch(act) {
        	case LEFT:
        		for(int i = 0; i < body.length; i++) {
        			int xPos = body[i].x + piecePosition.x;
        			int yPos = body[i].y + piecePosition.y;
        			int row = (height - 1) - yPos;
        			int column = xPos;
        			int newColumn = column - 1;
        			if(newColumn < 0 || board[row][newColumn] != null) {
        				lastResult = Result.OUT_BOUNDS;
        				return lastResult;
        			}
        		}
        		piecePosition.setLocation(piecePosition.x - 1, piecePosition.y);
        		lastResult = Result.SUCCESS;
        		return lastResult;
        	case RIGHT:
        		for(int i = 0; i < body.length; i++) {
        			int xPos = body[i].x + piecePosition.x;
        			int yPos = body[i].y + piecePosition.y;
        			int row = (height - 1) - yPos;
        			int column = xPos;
        			int newColumn = column + 1;
        			if(newColumn >= width || board[row][newColumn] != null) {
        				lastResult = Result.OUT_BOUNDS;
        				return lastResult;
        			}
        		}
        		piecePosition.setLocation(piecePosition.x + 1, piecePosition.y);
        		lastResult = Result.SUCCESS;
        		return lastResult;
        	case DOWN:
        		boolean placed = false;
        		for(int i = 0; i < body.length; i++) {
        			int xPos = body[i].x + piecePosition.x;
        			int yPos = body[i].y + piecePosition.y;
        			int row = (height - 1) - yPos;
        			int column = xPos;
        			int newRow = row + 1;
        			if(newRow >= height || board[newRow][column] != null) {
        				placed = true;
        				break;
        			}
        		}
        		if(placed) {
        			for(int i = 0; i < body.length; i++) {
            			int xPos = body[i].x + piecePosition.x;
            			int yPos = body[i].y + piecePosition.y;
            			int row = (height - 1) - yPos;
            			int column = xPos;
            			board[row][column] = piece.getType();
            		}
        			calculate();
        			lastRowsCleared = clearRows();
        			calculate();
        			piece = null;
        			piecePosition = null;
        			lastResult = Result.PLACE;
        			return lastResult;
        		}
        		piecePosition.setLocation(piecePosition.x, piecePosition.y-1);
        		lastResult = Result.SUCCESS;
        		return lastResult;
        	case DROP:
        		int dropHeight;
        		dropHeight = dropHeight(piece, piecePosition.x);
        		for(int i = 0; i < body.length; i++) {
        			int xPos = body[i].x + piecePosition.x;
        			int yPos = body[i].y + dropHeight;
        			int row = (height - 1) - yPos;
        			int column = xPos;
        			board[row][column] = piece.getType();
        		}
        		calculate();
        		lastRowsCleared = clearRows();
        		calculate();
        		piece = null;
        		piecePosition = null;
        		lastResult = Result.PLACE;
        		return lastResult;
        	case CLOCKWISE:
        		Point[] clockwiseBody = piece.clockwisePiece().getBody();
        		if(piece.getType() == PieceType.SQUARE) {
        			lastResult = Result.SUCCESS;
        			return lastResult;
        		}
        		else if(piece.getType() == PieceType.STICK) {
        			for(Point test : Piece.I_CLOCKWISE_WALL_KICKS[rotationIndex]) {
            			boolean rotationWorked = true;
        				for(Point point: clockwiseBody) {
        					int xPos = point.x + test.x + piecePosition.x;
        					int yPos = point.y + test.y + piecePosition.y;
        					int row  = (height - 1) - yPos;
        					int column = xPos;
        					if(row < 0 || row >= height || column < 0 || column >= width || board[row][column] != null) {
        						rotationWorked = false;
        					}
        				}
        				if(rotationWorked) {
        					piece = piece.clockwisePiece();
        					piecePosition.setLocation(piecePosition.x + test.x, piecePosition.y + test.y);
        					lastResult = Result.SUCCESS;
        					return lastResult;
        				}
        			}
        			lastResult = Result.OUT_BOUNDS;
        			return lastResult;
        		}
        		else {
        			for(Point test: Piece.NORMAL_CLOCKWISE_WALL_KICKS[rotationIndex]) {
        				boolean rotationWorked = true;
        				for(Point point: clockwiseBody) {
        					int xPos = point.x + test.x + piecePosition.x;
        					int yPos = point.y + test.y + piecePosition.y;
        					int row  = (height - 1) - yPos;
        					int column = xPos;
        					if(row < 0 || row >= height || column < 0 || column >= width || board[row][column] != null) {
        						rotationWorked = false;
        					}
        				}
        				if(rotationWorked) {
        					piece = piece.clockwisePiece();
        					piecePosition.setLocation(piecePosition.x + test.x, piecePosition.y + test.y);
        					lastResult = Result.SUCCESS;
        					return lastResult;
        				}
        			}
        			lastResult = Result.OUT_BOUNDS;
        			return lastResult;
        		}
        	case COUNTERCLOCKWISE:
        		Point[] counterclockwiseBody = piece.counterclockwisePiece().getBody();
        		if(piece.getType() == PieceType.SQUARE) {
        			lastResult = Result.SUCCESS;
        			return lastResult;
        		}
        		else if(piece.getType() == PieceType.STICK) {
        			for(Point test : Piece.I_COUNTERCLOCKWISE_WALL_KICKS[rotationIndex]) {
            			boolean rotationWorked = true;
        				for(Point point: counterclockwiseBody) {
        					int xPos = point.x + test.x + piecePosition.x;
        					int yPos = point.y + test.y + piecePosition.y;
        					int row  = (height - 1) - yPos;
        					int column = xPos;
        					if(row < 0 || row >= height || column < 0 || column >= width || board[row][column] != null) {
        						rotationWorked = false;
        					}
        				}
        				if(rotationWorked) {
        					piece = piece.counterclockwisePiece();
        					piecePosition.setLocation(piecePosition.x + test.x, piecePosition.y + test.y);
        					lastResult = Result.SUCCESS;
        					return lastResult;
        				}
        			}
        			lastResult = Result.OUT_BOUNDS;
        			return lastResult;
        		}
        		else {
        			for(Point test: Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[rotationIndex]) {
        				boolean rotationWorked = true;
        				for(Point point: counterclockwiseBody) {
        					int xPos = point.x + test.x + piecePosition.x;
        					int yPos = point.y + test.y + piecePosition.y;
        					int row  = (height - 1) - yPos;
        					int column = xPos;
        					if(row < 0 || row >= height || column < 0 || column >= width || board[row][column] != null) {
        						rotationWorked = false;
        					}
        				}
        				if(rotationWorked) {
        					piece = piece.counterclockwisePiece();
        					piecePosition.setLocation(piecePosition.x + test.x, piecePosition.y + test.y);
        					lastResult = Result.SUCCESS;
        					return lastResult;
        				}
        			}
        			lastResult = Result.OUT_BOUNDS;
        			return lastResult;
        		}
        	case NOTHING:
        		lastResult = Result.SUCCESS;
        		return Result.SUCCESS;
        	case HOLD:
        		lastResult = Result.SUCCESS;
        		return Result.SUCCESS;
        	}
    	}
    	lastResult = Result.NO_PIECE;
    	return lastResult;
    }
    
    private void calculate() {
    	maxHeight = 0;
    	columnHeights = new int[width];
    	rowWidths = new int[height];
    	for(int j = 0; j < width; j++) {
    		for(int i = 0; i < height; i++) {
    			rowWidths[(height-1)-i] += (board[i][j]!=null) ? 1 : 0;
    			if(board[i][j] != null) {
    				if((height-1) - i + 1 > columnHeights[j]) {
    					columnHeights[j] = (height-1) - i + 1;
    				}
    			}
    		}
    		if(columnHeights[j] > maxHeight) {
    			maxHeight = columnHeights[j];
    		}
    	}
    }
    
    private int clearRows() {
    	int rowsCleared = 0;
    	for(int i = 0; i < height;) {
    		if(rowWidths[i] == width) {
    			rowsCleared++;
    			shiftBoard(i);
    			calculate();
    		}
    		else {
    			i++;
    		}
    	}
    	return rowsCleared;
    }
    
    private void shiftBoard(int i) {
    	for(int j = (height-1) - i;j > 0; j--) {
    		board[j] = board[j-1];
    	}
    	board[0] = new Piece.PieceType[width];
    }
    
    public Board testMove(Action act) { 
    	TetrisBoard tetrisBoard = new TetrisBoard(board, piece, piecePosition, lastResult, lastAction, 
    			lastRowsCleared, width, height, maxHeight, columnHeights, rowWidths);
    	tetrisBoard.move(act);
    	return tetrisBoard;
    }

    public Piece getCurrentPiece() { 
    	return piece;
    }

    public Point getCurrentPiecePosition() {
    	return piecePosition;
    }

    public void nextPiece(Piece p, Point spawnPosition) {
    	Point[] body = p.getBody();
    	for(int i = 0; i < body.length; i++) {
    		int xPos = body[i].x + spawnPosition.x;
    		int yPos = body[i].y + spawnPosition.y;
    		int row = (height - 1) - yPos;
    		int column = xPos;
    		if(row < 0 || row >= height || column < 0 || column >= width || board[row][column] != null) {
    			throw new IllegalArgumentException("piece cannot be placed!");
    		}
    	}
    	this.piece = p;
    	this.piecePosition = spawnPosition;
    }
    
    public boolean equals(Object other) { 
    	if(!(other instanceof Board)) {
    		return false;
    	}
    	Board otherBoard = (Board) other;
    	if(this.getHeight() != otherBoard.getHeight() || this.getWidth() != otherBoard.getWidth()) {
    		return false;
    	}
    	if((this.getCurrentPiecePosition() == null || otherBoard.getCurrentPiecePosition() == null)
    			&& (this.getCurrentPiecePosition() != otherBoard.getCurrentPiecePosition())) {
    		return false;
    	}
    	if((this.getCurrentPiecePosition() != null && otherBoard.getCurrentPiecePosition() != null) &&
    			!this.getCurrentPiecePosition().equals(otherBoard.getCurrentPiecePosition())) {
    		return false;
    	}
    	if(this.getCurrentPiece() != otherBoard.getCurrentPiece()) {
    		return false;
    	}
    	for(int i = 0; i < width; i++) {
    		for(int j = 0; j < height; j++) {
    			if(this.getGrid(i, j) != otherBoard.getGrid(i, j)) {
    				return false;
    			}
    		}
    	}
    	return true;
    }

    public Result getLastResult() { 
    	return lastResult; 
    }

    public Action getLastAction() { 
    	return lastAction; 
    }

    public int getRowsCleared() { 
    	return lastRowsCleared; 
    }

    public int getWidth() { 
    	return width;
    }

    public int getHeight() { 
    	return height; 
    }

    public int getMaxHeight() { 
    	return maxHeight; 
    }

    public int dropHeight(Piece piece, int x) { 
    	int dropHeight = Integer.MIN_VALUE;
    	int tempHeight = Integer.MIN_VALUE;
    	int[] skirt = piece.getSkirt();
    	for(int i = 0; i < skirt.length && i + x < width; i++) {
    		if(i + x < 0) {
    			continue;
    		}
    		tempHeight = columnHeights[i+x] - skirt[i];
    		if(tempHeight > dropHeight)
    			dropHeight = tempHeight;
    	}
    	return dropHeight;
    }

    public int getColumnHeight(int x) { 
    	return columnHeights[x]; 
    }

    public int getRowWidth(int y) { 
    	return rowWidths[y]; 
    }

    public Piece.PieceType getGrid(int x, int y) { 
    	int row = (height - 1) - y;
    	int column = x;
    	return board[row][column];
    }
}