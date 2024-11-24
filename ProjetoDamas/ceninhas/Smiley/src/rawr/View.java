package rawr;
import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {
	Player player;
	Board board;

	View(Player model) {
		this.player = model;
		board = new Board("Player: " + (player.getIsX() ? 'X' : 'O'), 3, 3, 100);
		board.setIconProvider(this::icon);
		board.addMouseListener(this::click);
		board.setBackgroundProvider(this::background);
		board.addAction("new", this::action);
	}

	// qual o ícone na posição (line, col)?
	// null significa não haver ícone
	String icon(int line, int col) {
		char c = player.getBoardPos(new Position(line, col));
		if (c == ' ')
			return null; 
		return (c == 'x' ? "x.png" : "o.png");
	}

	// executa cada vez que é clicada uma posição do tabuleiro (line, col)
	void click(int line, int col) {
		player.updateBoard(new Position(line, col), (player.getIsX() ? 'x' : 'o'));
		board.setTitle("Player: " + (player.getIsX() ? 'X' : 'O'));
		char winChar = player.checkVictory();
		board.showMessage("r: " + winChar);
		board.showMessage("" + player.s());
		if (winChar != ' ')
			board.setTitle("WINNER: " + Character.toUpperCase(winChar));
	}

	// qual a cor de fundo da posição (line, col)?
	Color background(int line, int col) {
		if (player.isWinningPos(new Position(line, col)))
			return StandardColor.YELLOW;
		return StandardColor.WHITE;
	}

	// executa quando o botão é primido
	void action() {
		Player newModel = new Player();
		View gui = new View(newModel);
		gui.start();
	}

	void start() {
		board.open();
	}

	public static void main(String[] args) {
		View gui = new View(new Player());
		gui.action();
	}
}