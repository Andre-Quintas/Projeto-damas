import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {
	Board board;
	GameManager game;

	View() {
		game = new GameManager();
		board = new Board("Turno das " + (game.getIsBlackTurn() ? "Pretas" : "Brancas"), 8, 8, 100);
		board.setIconProvider(this::icon);
		board.addMouseListener(this::click);
		board.setBackgroundProvider(this::background);
	}
	
	void click(int line, int col) {
		if (game.isYellowSquare(new Position(line, col)) && !game.isPawn(new Position(line, col))) {
			game.movePawn(game.getSelectedPawn(), new Position(line, col));
			game.changeTurn();
			game.clearYellowSquares();
		}
		else {
			game.clearYellowSquares();
			if (game.isPawn(new Position(line, col))) {
				if (game.getIsBlackTurn() == (game.getFieldPos(line, col).getColor() == "black.png")) {
					game.addYellowSquare(new Position(line, col));
					game.setSelectedPawn(new Position(line, col));
					Position[] newYellowSquares = game.getLegalMoves(game.getFieldPos(line, col), line, col);
					for (int i = 0; i < newYellowSquares.length; i++)
						game.addYellowSquare(newYellowSquares[i]);
				}
			}
		}
		board.setTitle("Turno das " + (game.getIsBlackTurn() ? "Pretas" : "Brancas"));
	}
	
	String icon(int line, int col) {	
		if (game.getFieldPos(line, col) != null)
			return game.getFieldPos(line, col).getColor();
		return null;
	}
	
	Color background(int line, int col) {
		if (game.isYellowSquare(new Position(line, col)))
			return StandardColor.YELLOW;
		else if ((line + col) % 2 == 0)
			return StandardColor.BLACK;
		return StandardColor.WHITE;
	}

	void start() {
		board.open();
	}
	
	public static void main(String[] args) {
		View gui = new View();
		gui.start();
		
	}
}