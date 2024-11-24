
record Position(int line, int col) {
	
}

public class GameManager {
	
	private Pawn[][] field;
	private Position[] yellowSquares = new Position[32];
	private Position selectedPawn;
	private boolean isBlackTurn = false;
	
	public GameManager() {
		fieldInit();
	}
	
	public boolean getIsBlackTurn() {
		return this.isBlackTurn;
	}
	
	public void changeTurn() {
		this.isBlackTurn = !this.isBlackTurn;
	}
	
	public Position getSelectedPawn() {
		return this.selectedPawn;
	}
	
	public void setSelectedPawn(Position pos) {
		this.selectedPawn = pos;
	}
	
	private Position[] removeNulls(Position[] array) {
		int maxIndex = firstAvailableIndex(array);
		Position[] newArray = new Position[maxIndex];
		for (int i = 0; i < newArray.length; i++)
			newArray[i] = array[i];
		return newArray;
	}
	
	public void movePawn(Position origin, Position target) {
		this.field[target.line()][target.col()] = this.field[origin.line()][origin.col()];
		this.field[origin.line()][origin.col()] = null;
	}
	
	public Position[] getLegalMoves(Pawn pawn, int line, int col) {
		Position[] positions = new Position[32];
		String color = pawn.getColor();
		
		if (color == "black.png") {
			if (line == 7)
				return new Position[] {};
			if (col != 0)
				if (!isPawn(new Position(line+1, col-1)))
					positions[firstAvailableIndex(positions)] = new Position(line+1, col-1);
			if (col != 7)
				if (!isPawn(new Position(line+1, col+1)))
					positions[firstAvailableIndex(positions)] = new Position(line+1, col+1);
		}
		else {
			if (line == 0)
				return new Position[] {};
			if (col != 0)
				if (!isPawn(new Position(line-1, col-1)))
					positions[firstAvailableIndex(positions)] = new Position(line-1, col-1);
			if (col != 7)
				if (!isPawn(new Position(line-1, col+1)))
					positions[firstAvailableIndex(positions)] = new Position(line-1, col+1);
		}
		return removeNulls(positions);
	}
	
	private int firstAvailableIndex(Position[] array) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == null)
				return i;
		return array.length;
	}
	
	public void addYellowSquare(Position pos) {
		this.yellowSquares[firstAvailableIndex(this.yellowSquares)] = new Position(pos.line(), pos.col());
	}
	
	public void clearYellowSquares() {
		this.yellowSquares = new Position[32];
	}
	
	public boolean isYellowSquare(Position pos) {
		for (int i = 0; i<this.yellowSquares.length; i++) {
			if (yellowSquares[i] == null)
				break;
			else if (yellowSquares[i].equals(pos))
				return true;
		}
		return false;
	}
	
	private void fieldInit() {
		field = new Pawn[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Pawn newPawn = null;
				if ((i + j) % 2 == 0) {
					if (i < 3)
						newPawn = new Pawn("black.png");
					else if (i > 4)
						newPawn = new Pawn("white.png");
				}
				field[i][j] = newPawn;
			}
		}
	}
	
	public boolean isPawn(Position pos) {
		return !(field[pos.line()][pos.col()] == null);
	}
	
	public Pawn getFieldPos(int line, int col) {
		return this.field[line][col];
	}
	
	public String showField() {
		String text = "";
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				text += (this.field[i][j] == null ? "  " : this.field[i][j].toString()) + ",";
			}
			text += "\n";
		}
		return text;
	}
}
