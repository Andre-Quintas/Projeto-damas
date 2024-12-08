import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

/*
 record que combina uma representacao do campo com uma bollean que indica se é a vez das pecas pretas
 para facilitar o processo de salvar e carregar um jogo atraves do ficheiro
 */
record LoadData(Pawn[][] newField, boolean isBlack) {
	
}

//record que representa um posicao(x, y)
record Position(int line, int col) {
	
	//retorna a quantidade de casas entre a posicao atual e uma posicao "target"
	public int distance(Position target) {
		int d = Math.abs(target.line() - this.line) + Math.abs(target.col() - this.col);
		return d;
	}
	
	//retorna a casa entre a posicao atual e uma posicao "target"
	public Position getMiddle(Position target) {
		int line = this.line + ((this.line - target.line()) > 0 ? -1 : 1);
		int col =  this.col + ((this.col - target.col()) > 0 ? -1 : 1);
		return new Position(line, col);
	}
}

//Class principal, responsavel pelas funcoes do jogo
public class GameManager {
	
	private Pawn[][] field;
	private Position[] yellowSquares = new Position[16]; //Array que vai conter todas as casas com movimentos possiveis + casa da peca selecionada
	private Position[] redSquares = new Position[16]; //Array que vai conter todas as casas com jogadas obrigatorias
	private Position selectedPawn;
	private boolean isBlackTurn = true;
	private boolean gameOver = false;
	
	//contrutor default de tamanho 8
	public GameManager() {
		fieldInit(8);
	}
	
	//contrutor de tamanho dependente da variavel size
	public GameManager(int size) {
		fieldInit(size);
	}
	
	//contrutor que depende de um ficheiro com os dados do jogo
	public GameManager(Pawn[][] newField, boolean isBlack) {
		isBlackTurn = isBlack;
		field = newField;
	}
	
	//funcao que verifica se uma peca tem movimentos possiveis
	public boolean canMove(Pawn pawn, int line, int col) {
		return getLegalMoves(pawn, line, col).length > 0;
	}
	
	//funcao que verifica se existe alguma peca da cor "color" que se possa mover
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
	
	//funcao que retorna a quantidade de peoes restantes da cor "color"
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
	
	//funcao que retorna "true" caso seja a vez das pecas pretas jogarem, caso contrario "false"
	public boolean getIsBlackTurn() {
		return this.isBlackTurn;
	}
	
	//funcao que retorna uma string que representa se algum dos lados ganhou ou se houve um empate
	public String getWinner() {
		int blackCount = getPawnCount("black.png");
		int whiteCount = getPawnCount("white.png");
		
		if (blackCount == whiteCount)
			return "EMPATE";
		return blackCount > whiteCount ? "VENCEDOR: PRETO" : "VENCEDOR: BRANCO"; 
	}
	
	//funcao que define o final de um jogo
	private void gameOver() {
		this.gameOver = true;
		clearRedSquares();
		clearYellowSquares();
	}
	
	//funcao que retorna "true" caso o jogo já tenha acabado, caso contrario "false"
	public boolean isGameOver() {
		return this.gameOver;
	}
	
	//funcao responsavel por mudar o turno para a cor oposta e verificar é possivel terminar o jogo
	public void changeTurn() {
		this.isBlackTurn = !this.isBlackTurn;
		refreshRedSquares();
		if (getPawnCount("black.png") == 0 || getPawnCount("white.png") == 0)
			gameOver();
		if ((!canAnyMove("black.png") && this.isBlackTurn) || (!canAnyMove("white.png") && !this.isBlackTurn))
			gameOver();
	}
	
	//funcao responsavel por repor os quadrados vermelhos caso existam
	public void refreshRedSquares() {
		clearRedSquares();
		redSquares = getMandatoryMoves();
	}
	
	//funcao que retorna uma casa vermelha aleatoria
	public Position getRandomRed() {
		Position[] cleanRedSquares = removeNulls(redSquares);
		int r = (int)(Math.random() * cleanRedSquares.length);
		return cleanRedSquares[r];
	}
	
	//funcao que retorna uma casa amarela aleatoria
	public Position getRandomYellow() {
		Position[] cleanYellowSquares = removeNulls(yellowSquares);
		int r = (int)(Math.random() * cleanYellowSquares.length);
		return !cleanYellowSquares[r].equals(selectedPawn) ? cleanYellowSquares[r] : getRandomYellow();
	}
	
	//funcao que retorna um peao aleatorio desde que este tenha movimentos possiveis
	public Position getRandomPlayablePawnPos(String color) {
		int line = (int)(Math.random() * field.length);
		int col = (int)(Math.random() * field[0].length);
		
		Pawn pawn = getFieldPos(new Position(line, col));
		if (pawn != null && pawn.getColor() == color)
			if (canMove(pawn, line, col) || canEat(pawn, line, col))
				return new Position(line, col);
		return getRandomPlayablePawnPos(color);
	}
	
	//funcao que retorna o peao selecionado (click num peao mais recente do utilizador)
	public Position getSelectedPawn() {
		return this.selectedPawn;
	}
	
	//funcao que recebe a posicao de um peao e define a posicao como o peao selecionado
	public void setSelectedPawn(Position pos) {
		this.selectedPawn = pos;
	}
	
	//funcao que recebe um array de posicoes e retorna esse mesmo array sem casa nulas
	private Position[] removeNulls(Position[] array) {
		int maxIndex = firstAvailableIndex(array);
		Position[] newArray = new Position[maxIndex];
		for (int i = 0; i < newArray.length; i++)
			newArray[i] = array[i];
		return newArray;
	}
	
	//funcao responsavel por realizar o movimento de um peao
	public void movePawn(Position origin, Position target) {
		if (origin.distance(target) > 2) {
			Position middle = origin.getMiddle(target);
			this.field[middle.line()][middle.col()] = null;
		}
		this.field[target.line()][target.col()] = this.field[origin.line()][origin.col()];
		this.field[origin.line()][origin.col()] = null;
	}
	
	//funcao retorna as casas de jogadas obrigatorias
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
	
	//funcao que recebe um peao e devolve o valor "true" caso este peao consiga comer algum outro peao, caso contrario "false"
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
	
	//funcao retorna as casas de jogadas possiveis
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
	
	//funcao que recebe um array de posicoes e devolve o primeiro index nulo
	private int firstAvailableIndex(Position[] array) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == null)
				return i;
		return array.length;
	}
	
	//funcao para adicionar uma posicao ao array de quadrados amarelos
	public void addYellowSquare(Position pos) {
		this.yellowSquares[firstAvailableIndex(this.yellowSquares)] = new Position(pos.line(), pos.col());
	}
	
	//funcao para limpar o array de quadrados amarelos
	public void clearYellowSquares() {
		this.yellowSquares = new Position[16];
	}
	
	//funcao para limpar o array de quadrados vermelhos
	public void clearRedSquares() {
		this.redSquares = new Position[16];
	}
	
	//funcao que recebe uma posicao e verifica se essa posicao faz parte do array de quadrados amarelos
	public boolean isYellowSquare(Position pos) {
		for (int i = 0; i<this.yellowSquares.length; i++) {
			if (yellowSquares[i] == null)
				break;
			else if (yellowSquares[i].equals(pos))
				return true;
		}
		return false;
	}
	
	//funcao que recebe uma posicao e verifica se essa posicao faz parte do array de quadrados vermelhos
	public boolean isRedSquare(Position pos) {
		for (int i = 0; i<this.redSquares.length; i++) {
			if (redSquares[i] == null)
				break;
			else if (redSquares[i].equals(pos))
				return true;
		}
		return false;
	}
	
	//funcao que verifica se existem quadrados vermelhos
	public boolean hasRedSquares() {
		return firstAvailableIndex(redSquares) > 0;
	}
	
	//funcao que verifica se existem quadrados amarelos
	public boolean hasYellowSquares() {
		return firstAvailableIndex(yellowSquares) > 0;
	}
	
	//funcao que recebe uma posicao e verifica se é uma posicao valida dentro do campo atual
	private boolean isInsideField(Position pos) {
		boolean res = (pos.line() >= 0 && pos.line() < field.length);
		res = res && (pos.col() >= 0 && pos.col() < field[0].length);
		return res;
	}
	
	//funcao para inicializar o campo com uma tamanho definido "size"
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
	
	//funcao que recebe um posicao e verifica se essa posicao é um peao no tabuleiro
	public boolean isPawn(Position pos) {
		return field[pos.line()][pos.col()] != null;
	}
	
	//funcao que recebe uma posicao e retorna a casa dessa posicao no tabuleiro
	public Pawn getFieldPos(Position pos) {
		return this.field[pos.line()][pos.col()];
	}
	
	//funcao para guardar o jogo atual num ficheiro
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
	
	//funcao para carregar o jogo atual a partir de um ficheiro
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