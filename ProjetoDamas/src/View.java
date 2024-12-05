import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {
	Board board;
	GameManager game;

	View() {
		int size = 6;
		game = new GameManager(size);
		board = new Board("Turno das " + (!game.getIsBlackTurn() ? "Pretas" : "Brancas"), size, size, 100);
		board.setIconProvider(this::icon);
		board.addMouseListener(this::click);
		board.setBackgroundProvider(this::background);
	}
	
	void click(int line, int col) {
		if (game.isGameOver())
			return;
		if (game.isRedSquare(new Position(line, col))) {
			game.setSelectedPawn(new Position(line, col));
			game.clearYellowSquares();
			Position[] newYellowSquares = game.getLegalMoves(game.getFieldPos(new Position(line, col)), line, col);
			for (int i = 0; i < newYellowSquares.length; i++)
				game.addYellowSquare(newYellowSquares[i]);
		}
		if (game.isYellowSquare(new Position(line, col)) && !game.isPawn(new Position(line, col))) {
			game.movePawn(game.getSelectedPawn(), new Position(line, col));
			game.clearYellowSquares();
			if (game.hasRedSquares() && game.canEat(game.getFieldPos(new Position(line, col)), line, col))
				game.refreshRedSquares();
			else 
				game.changeTurn();	
		}
		else if (!game.hasRedSquares()) {
			game.clearYellowSquares();
			if (game.isPawn(new Position(line, col))) {
				if (game.getIsBlackTurn() == (game.getFieldPos(new Position(line, col)).getColor() == "black.png")) {
					game.addYellowSquare(new Position(line, col));
					game.setSelectedPawn(new Position(line, col));
					Position[] newYellowSquares = game.getLegalMoves(game.getFieldPos(new Position(line, col)), line, col);
					for (int i = 0; i < newYellowSquares.length; i++)
						game.addYellowSquare(newYellowSquares[i]);
				}
			}
		}
		if (game.isGameOver())
			board.setTitle(game.getWinner()); 
		else
			board.setTitle("Turno das " + (game.getIsBlackTurn() ? "Pretas" : "Brancas"));
	}
	
	String icon(int line, int col) {	
		if (game.getFieldPos(new Position(line, col)) != null)
			return game.getFieldPos(new Position(line, col)).getColor();
		return null;
	}
	
	Color background(int line, int col) {
		if (game.isRedSquare(new Position(line, col)))
			return StandardColor.RED;
		if (game.isYellowSquare(new Position(line, col))) {
			if (game.isPawn(new Position(line, col)))
				return StandardColor.GRAY;
			return StandardColor.YELLOW;
		}
		else if ((line + col) % 2 == 0)
			return StandardColor.BLACK;
		return StandardColor.WHITE;
	}

	void start() {
		board.open();
		game.changeTurn();
	}
	
	public static void main(String[] args) {
		View gui = new View();
		gui.start();
		
	}
}