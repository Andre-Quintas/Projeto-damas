import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

record LoadData(Pawn[][] newField, boolean isBlack) {
	
}

record Position(int line, int col) {
	
	public int distance(Position pos) {
		int d = Math.abs(pos.line() - this.line) + Math.abs(pos.col() - this.col);
		return d;
	}
	
	public Position getMiddle(Position pos) {
		int line = this.line + ((this.line - pos.line()) > 0 ? -1 : 1);
		int col =  this.col + ((this.col - pos.col()) > 0 ? -1 : 1);
		return new Position(line, col);
	}
}

public class GameManager {
	
	private Pawn[][] field;
	private Position[] yellowSquares = new Position[16];
	private Position[] redSquares = new Position[16];
	private Position selectedPawn;
	private boolean isBlackTurn = true;
	private boolean gameOver = false;
	
	public GameManager() {
		fieldInit(8);
	}
	
	public GameManager(int size) {
		fieldInit(size);
	}
	
	public GameManager(Pawn[][] newField, boolean isBlack) {
		isBlackTurn = isBlack;
		field = newField;
	}
	
	public boolean canMove(Pawn pawn, int line, int col) {
		return getLegalMoves(pawn, line, col).length > 0;
	}
	
	public boolean canAnyMove(String color) {
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[0].length; j++) {
				Pawn pawn = getFieldPos(new Position(i, j));
				if (pawn != null && color == pawn.getColor() && canMove(pawn, i, j))
					return true;
			}
		}
		return false;
	}
	
	public int getPawnCount(String color) {
		int count = 0;
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[0].length; j++) {
				Pawn pawn = getFieldPos(new Position(i, j));
				if (pawn != null && color == pawn.getColor())
					count++;
			}
		}
		return count;
	}
	
	public boolean getIsBlackTurn() {
		return this.isBlackTurn;
	}
	
	public String getWinner() {
		int blackCount = getPawnCount("black.png");
		int whiteCount = getPawnCount("white.png");
		
		if (blackCount == whiteCount)
			return "EMPATE";
		return blackCount > whiteCount ? "VENCEDOR: PRETO" : "VENCEDOR: BRANCO"; 
	}
	
	private void gameOver() {
		this.gameOver = true;
		clearRedSquares();
		clearYellowSquares();
	}
	
	public boolean isGameOver() {
		return this.gameOver;
	}
	
	public void changeTurn() {
		this.isBlackTurn = !this.isBlackTurn;
		refreshRedSquares();
		if (getPawnCount("black.png") == 0 || getPawnCount("white.png") == 0)
			gameOver();
		if ((!canAnyMove("black.png") && this.isBlackTurn) || (!canAnyMove("white.png") && !this.isBlackTurn))
			gameOver();
	}
	
	public void refreshRedSquares() {
		clearRedSquares();
		redSquares = getMandatoryMoves();
	}
	
	public Position getRandomRed() {
		Position[] cleanRedSquares = removeNulls(redSquares);
		int r = (int)(Math.random() * cleanRedSquares.length);
		return cleanRedSquares[r];
	}
	
	public Position getRandomYellow() {
		Position[] cleanYellowSquares = removeNulls(yellowSquares);
		int r = (int)(Math.random() * cleanYellowSquares.length);
		return !cleanYellowSquares[r].equals(selectedPawn) ? cleanYellowSquares[r] : getRandomYellow();
	}
	
	public Position getRandomPlayablePawnPos(String color) {
		int line = (int)(Math.random() * field.length);
		int col = (int)(Math.random() * field[0].length);
		
		Pawn pawn = getFieldPos(new Position(line, col));
		if (pawn != null && pawn.getColor() == color)
			if (canMove(pawn, line, col) || canEat(pawn, line, col))
				return new Position(line, col);
		return getRandomPlayablePawnPos(color);
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
		if (origin.distance(target) > 2) {
			Position middle = origin.getMiddle(target);
			this.field[middle.line()][middle.col()] = null;
		}
		this.field[target.line()][target.col()] = this.field[origin.line()][origin.col()];
		this.field[origin.line()][origin.col()] = null;
	}
	
	private Position[] getMandatoryMoves() {
		Position[] redSquares = new Position[16];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[0].length; j++) {
				if (isPawn(new Position(i, j))) {
					boolean blackPawn = getFieldPos(new Position(i, j)).getColor() == "black.png";
					redSquares[firstAvailableIndex(redSquares)] = (blackPawn == isBlackTurn) && (canEat(getFieldPos(new Position(i, j)), i, j)) ? (new Position(i, j)) : null;
				}
			}
		}
		return removeNulls(redSquares);
	}
	
	public boolean canEat(Pawn pawn, int line, int col) {
		String color = pawn.getColor();
		Position left = new Position(color == "black.png" ? line+1 : line-1, col-1);
		Position right = new Position(color == "black.png" ? line+1 : line-1, col+1);
		
		Position[] pos = {left, right};
		
		for (int i = 0; i < 2; i++) {
			if (isInsideField(pos[i])) {
				if (isPawn(pos[i])) {
					Position next = new Position(color == "black.png" ? line+2 : line-2, col + (i==0 ? -2 : 2));
					if (isInsideField(next) && !isPawn(next) && getFieldPos(pos[i]).getColor() != color)
						return true;
				}
			}
		}
		return false;
	}
	
	public Position[] getLegalMoves(Pawn pawn, int line, int col) {
		Position[] positions = new Position[16];
		String color = pawn.getColor();
		Position left = new Position(color == "black.png" ? line+1 : line-1, col-1);
		Position right = new Position(color == "black.png" ? line+1 : line-1, col+1);
		
		Position[] pos = {left, right};
		
		for (int i = 0; i < 2; i++) {
			if (isInsideField(pos[i])) {
				if (isPawn(pos[i])) {
					Position next = new Position(color == "black.png" ? line+2 : line-2, col + (i==0 ? -2 : 2));
					if (isInsideField(next) && !isPawn(next) && getFieldPos(pos[i]).getColor() != color)
						positions[firstAvailableIndex(positions)] = next;
				}
				else if (!hasRedSquares()){
					positions[firstAvailableIndex(positions)] = pos[i];
				}
			}
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
		this.yellowSquares = new Position[16];
	}
	
	public void clearRedSquares() {
		this.redSquares = new Position[16];
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
	
	public boolean isRedSquare(Position pos) {
		for (int i = 0; i<this.redSquares.length; i++) {
			if (redSquares[i] == null)
				break;
			else if (redSquares[i].equals(pos))
				return true;
		}
		return false;
	}
	
	public boolean hasRedSquares() {
		return firstAvailableIndex(redSquares) > 0;
	}
	
	public boolean hasYellowSquares() {
		return firstAvailableIndex(yellowSquares) > 0;
	}
	
	private boolean isInsideField(Position pos) {
		boolean res = (pos.line() >= 0 && pos.line() < field.length);
		res = res && (pos.col() >= 0 && pos.col() < field[0].length);
		return res;
	}
	
	private void fieldInit(int size) {
		field = new Pawn[size][size];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[0].length; j++) {
				Pawn newPawn = null;
				if ((i + j) % 2 == 0) {
					if (i < (size - 2)/2)
						newPawn = new Pawn('b');
					else if (i > size/2)
						newPawn = new Pawn('w');
				}
				field[i][j] = newPawn;
			}
		}
	}
	
	public boolean isPawn(Position pos) {
		return field[pos.line()][pos.col()] != null;
	}
	
	public Pawn getFieldPos(Position pos) {
		return this.field[pos.line()][pos.col()];
	}
	
	public void save(String fileName) {
		try {
			PrintWriter writer = new PrintWriter(new File(fileName));
			writer.println(field.length);
			writer.println(isBlackTurn);
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[0].length; j++) {
					writer.print(field[i][j] == null ? ' ' : field[i][j].getChar() );
				}
				writer.println();
			}
			writer.close();
		
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
		}
	}
	
	public LoadData load(String fileName) {
		try {
			Scanner scanner = new Scanner(new File(fileName));
			int size = scanner.nextInt();
			Pawn[][] newField = new Pawn[size][size];
			boolean isBlack = scanner.nextBoolean();
			scanner.nextLine();
			for (int i = 0; i < size; i++) {
				String line = scanner.nextLine();
				for (int j = 0; j < size; j++) {
					char c = line.charAt(j);
					newField[i][j] = c == ' ' ? null : new Pawn(c);
				}
			}
			scanner.close();
			return new LoadData(newField, isBlack);
			
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}
}