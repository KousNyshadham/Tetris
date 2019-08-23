package assignment;

import java.awt.*;
import java.util.*;
/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for it's
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do precomputation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {

    /**
     * Construct a tetris piece of the given type. The piece should be in it's spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */
	
	private PieceType type;
	private ArrayList<Point[]> bodies = new ArrayList<Point[]>();
	private int rotationIndex;
	private int[][] skirts;
	private TetrisPiece[] pieces = new TetrisPiece[4];
	
    public TetrisPiece(PieceType type) {
    	this.type = type;
    	bodies.add(type.getSpawnBody());
    	generateBodies();
    	rotationIndex = 0;
    	generateSkirts();
    	generatePieces();
    	
    }
    
    private TetrisPiece(PieceType type, ArrayList<Point[]> bodies, int rotationIndex, int[][] skirts) {
    	this.type = type;
    	this.bodies = bodies;
    	this.rotationIndex = rotationIndex;
    	this.skirts = skirts;
    }
    
    private void generatePieces() {
    	pieces[0] = this;
    	for(int i = 1; i < 4; i++) {
    		pieces[i] = new TetrisPiece(type, bodies, i, skirts);
    	}
    	for(int i = 1; i < 4; i++) {
    		pieces[i].pieces = this.pieces;
    	}
    }
    
    private void generateBodies() {
    	for(int i = 1; i < 4; i++) {
    		Point[] previousBody = bodies.get(i-1);
    		Point[] newBody = new Point[4];
    		for(int j = 0; j < 4; j++) {
    			newBody[j] = new Point(previousBody[j].y, (type.getBoundingBox().height-1) - previousBody[j].x);
    		}
    		bodies.add(newBody);
    	}
    }
    
    private void generateSkirts() {
    	int width = type.getBoundingBox().width;
    	skirts = new int[4][width];
    	for(int i = 0; i < 4; i++) {
    		for(int j = 0; j < width; j++) {
    			skirts[i][j] = Integer.MAX_VALUE;
    		}
    	}
    	for(int i = 0; i < 4; i++) {
    		for(int j = 0; j < 4; j++) {
    			Point point = bodies.get(i)[j];
    			if(point.y < skirts[i][point.x]) {
    				skirts[i][point.x] = point.y;
    			}
    		}
    	}
    }
    
    @Override
    public PieceType getType() {
        return type;
    }

    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    @Override
    public Piece clockwisePiece() {
    	return pieces[(rotationIndex+1) % 4];
    }

    @Override
    public Piece counterclockwisePiece() {
    	return pieces[(rotationIndex+3) % 4];
    }

    @Override
    public int getWidth() {
        return type.getBoundingBox().width;
    }

    @Override
    public int getHeight() {
        return type.getBoundingBox().height;
    }

    @Override
    public Point[] getBody() {
        return bodies.get(rotationIndex);
    }

    @Override
    public int[] getSkirt() {
        return skirts[rotationIndex];
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;
        if(otherPiece.type == type && otherPiece.rotationIndex == rotationIndex) {
        	return true;
        }
        return false;
    }
}