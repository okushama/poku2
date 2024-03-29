package okushama.poku2;

import java.util.Random;

import okushama.engine.util.Renderer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class EntityPlayer extends Entity {

	public int playerSlot = -1;
	public int score = 0;
	public int[][] keys = { new int[] { Keyboard.KEY_LEFT, Keyboard.KEY_RIGHT },
			new int[] { Keyboard.KEY_Z, Keyboard.KEY_X } };
	public float lastX = -999f;

	public EntityPlayer(Game g, int slot) {
		super(g);
		sizeX = 23;
		sizeY = 20;
		playerSlot = slot;
	}

	@Override
	public void render(float partialTick) {
		float[] c = {0.8f,0.8f,0.8f,1f};
		if(playerSlot == 0){
			if(!theGame.aiPlayerOne)
			c = new float[]{0.8f, 0.3f, 0.3f, 1f};
		}else{
			if(theGame.twoPlayer)
				c = new float[]{0.3f, 0.5f, 0.8f, 1f};
		}
		Renderer.drawRect(posX, posY, sizeX, sizeY, c);
	}

	@Override
	public void logic(float partialTick) {
		lastX = posX;
		if((playerSlot == 0 && !theGame.aiPlayerOne) || theGame.twoPlayer){
			
		}
		
		float spd = 3f;
		if (Keyboard.isKeyDown(keys[playerSlot][0])
				&& !Keyboard.isKeyDown(keys[playerSlot][1])) {
			posX -= spd * partialTick;
		} else if (Keyboard.isKeyDown(keys[playerSlot][1])
				&& !Keyboard.isKeyDown(keys[playerSlot][0])) {
			posX += spd * partialTick;
		}
		if (posX < 0) {
			posX = 0;
		}
		if (posX > theGame.displayWidth - sizeX) {
			posX = theGame.displayWidth - sizeX;
		}
		posY = playerSlot == 0 ? -12 : theGame.displayHeight - 36;

		// Ai Portion
		boolean topHalf = playerSlot == 1;
		boolean shouldMove = false;
		EntityBall ball = theGame.getClosestBall(this);
		if(ball != null){
			if(topHalf){
				if(ball.posY+(ball.sizeY/2) > (theGame.displayHeight/2) - 15 && (ball.posY+ball.sizeY) < posY + sizeY)
				{
					shouldMove = true;
				}
			}else{
				if(ball.posY+(ball.sizeY/2) < (theGame.displayHeight/2) - 30 && ball.posY > posY)
				{
					shouldMove = true;
				}
			}
			if(playerSlot == 0 && !theGame.aiPlayerOne) shouldMove = false;
			if(playerSlot == 1 && theGame.twoPlayer) shouldMove = false;
			if (shouldMove)
			{
				float speed = 3f;
				if (posX + (sizeX / 2) + 5 < ball.posX + (ball.sizeX / 2)) {
					posX += speed * partialTick;
				}
				if (posX + (sizeX / 2) - 5 > ball.posX + (ball.sizeX / 2)) {
					posX -= speed * partialTick;
				}
			}
		}
	}
	
	public void magnetizeBalls(){
		float maxDist = 50f;
		float strength = 0.002f;
		EntityBall nearest = theGame.getClosestBall(this);
		//if(nearest.posX > posX-(sizeX/2) && nearest.posX + nearest.sizeX < posX+(sizeX*1.5f))
		if(nearest.distanceToClosestPlayer < maxDist && nearest.closestPlayerSlot == playerSlot && nearest.motionY < 0){
			Vector2f pos = new Vector2f(posX+(sizeX/2), posY+sizeY);
			Vector2f ballpos = new Vector2f(nearest.posX+(nearest.sizeX/2), nearest.posY);
			Vector2f dist = new Vector2f();
			Vector2f.sub(pos, ballpos, dist);
			//nearest.motionX = 0;
			//nearest.motionY = 1f;
			nearest.motionX += dist.getX() * strength;
			//nearest.posY += dist.getY() * strength;
		}
		
	}

}
