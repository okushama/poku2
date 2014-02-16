package okushama.poku2;


import java.util.Random;

public abstract class Entity {

	public float posX, posY;
	public float sizeX = 1, sizeY = 1;
	public final Random rand = new Random();
	public Game theGame;
	public Entity(Game g){
		posX = 0;
		posY = 0;
		theGame = g;
		
	}
	
	public abstract void render(float partialTick);
	
	public abstract void logic(float partialTick);
	
}
