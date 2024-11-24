// posição na grelha
record Position(int line, int col) {
	
	// é adjacente outra posição?
	boolean isAdjacent(Position p) {
		return Math.abs(line - p.line()) + Math.abs(col - p.col()) == 1;
	}
}

class PositionTrace {
	
	// caminho (posicoes visitadas)
	private Position[] path;
	
	// número de posições visitadas
	private int visited;
	
	// inicializado com o número de passos que podem ser dados
	PositionTrace(int energy) {
		assert energy >= 0;
		path = new Position[1 + energy];
		path[0] = new Position(0, 0);
		visited = 1;
	}

	// localização atual (corresponde ao último passo)
	Position last() {
		return path[visited - 1];
	}

	// energia disponível
	int energy() {
		return path.length - visited;
	}
	
	// a posição já foi visitada?
	boolean isVisited(Position p) {
		for(int i = 0; i < visited; i++)
			if(path[i].equals(p))
				return true;
		return false;
	}
	
	// mover para uma posição adjacente (se houver energia)
	void moveTo(Position p) {
		if(energy() > 0 && p.isAdjacent(path[visited - 1]) && !this.isVisited(p)) {
			path[visited] = p;
			visited++;
		}
	}
}