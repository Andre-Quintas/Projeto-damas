
public class Pawn {
	
	private String color; //"black.png" ou "white.png"
	
	public Pawn(String color) {
		this.color = color;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public String toString() {
		return (color == "black.png" ? "B" : "W");
	}
	
}
