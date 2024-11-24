// posição na grelha
package rawr;

record Position(int line, int col) {
	
}

class Player {
	
	private char[][] board = new char[3][3];
	private Position[] winPos;
	private boolean isX = true;
	
	Player() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j<3; j++)
				board[i][j] = ' ';
	}
	
	public void updateBoard(Position pos, char c) {
		if (this.board[pos.line()][pos.col()] == ' ') {
			this.board[pos.line()][pos.col()] = c;
			this.changePlayer();
		}
	}
	
	public String s() {
		String d = "";
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j<3; j++)
				d += "" + getBoardPos(new Position(i, j)) + ", ";
			d += "\n";
		}
		return d;
	}
	
	public char getBoardPos(Position pos) {
		return this.board[pos.line()][pos.col()];
	}
	
	public void changePlayer() {
		this.isX = !this.isX;
	}
	
	public boolean getIsX() {
		return this.isX;
	}
	
	public boolean gameWon() {
		return winPos != null;
	}
	
	public boolean isWinningPos(Position pos) {
		if (!this.gameWon())
			return false;
		
		for (int i = 0; i<winPos.length; i++)
			if (winPos[i] == pos)
				return true;
		return false;
	}
	
	public char checkVictory() {
		for (int i = 0; i<3; i++) {
			if (getBoardPos(new Position(0, i)) == getBoardPos(new Position(1, i)) && getBoardPos(new Position(0, i)) == getBoardPos(new Position(2, i))) {
				//if (getBoardPos(new Position(0, i)) != '\0') {
					winPos = new Position[] {new Position(0, i), new Position(1, i), new Position(2, i)};
					return getBoardPos(new Position(0, i));
				}
				//}
		}
		for (int i = 0; i<3; i++) {
			if (getBoardPos(new Position(i, 0)) == getBoardPos(new Position(i, 1)) && getBoardPos(new Position(i, 0)) == getBoardPos(new Position(i, 2)))
				//winPos = new Position[] {new Position(i, 0), new Position(i, 1), new Position(i, 2)};
				return getBoardPos(new Position(i, 0));
		}
		if (getBoardPos(new Position(0, 0)) == getBoardPos(new Position(1, 1)) && getBoardPos(new Position(0, 0)) == getBoardPos(new Position(2, 2))) {
			//winPos = new Position[] {new Position(0, 0), new Position(1, 1), new Position(2, 2)};
			return getBoardPos(new Position(0, 0));
		}
		if (getBoardPos(new Position(2, 0)) == getBoardPos(new Position(1, 1)) && getBoardPos(new Position(2, 0)) == getBoardPos(new Position(0, 2))) {
			//winPos = new Position[] {new Position(2, 0), new Position(1, 1), new Position(0, 2)};
			return getBoardPos(new Position(2, 0));
		}
		return ' ';
	}
}





