
public class Pawn {
	
	private String color; //"black.png" ou "white.png"
	
	public Pawn(char color) {
		this.color = color == 'b' ? "black.png" : "white.png";
	}
	
	public String getColor() {
		return this.color;
	}
	
	public char getChar() {
		return (color == "black.png" ? 'b' : 'w');
	}
	
}
