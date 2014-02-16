

import java.util.Random;

import okushama.engine.util.Renderer;

import org.lwjgl.opengl.Display;

public class EntityBall extends Entity {

	public float motionX, motionY;

	public float distanceToClosestPlayer = 0f;
	public int closestPlayerSlot = -1;
	public int lastHittingPlayer = -1;

	public EntityBall(Game g) {
		super(g);
		sizeX = sizeY = 6;
		reset();
	}

	public void reset() {
		if (this != theGame.mainBall) {
			theGame.balls.remove(this);
		}
		posX = (theGame.displayWidth / 2) - (sizeX / 2);
		posY = (theGame.displayHeight / 2) - (sizeY / 2);
		boolean left = new Random().nextBoolean();
		float dir = left ? -new Random().nextFloat() : new Random().nextFloat();
		motionX = dir;
		motionY = left ? 0.5f : -0.5f;
	}

	@Override
	public void render(float partialTick) {
		float r = 1f, g = 1f, b = 1f;
		float hot = 6f;
		if (this != theGame.mainBall) {
			r = g = b = 0.5f;
		}
		if (motionY > hot || motionY < -hot || motionX > hot || motionX < -hot) {
			if ((int) posY % 2 > 0) {
				r = 1f;
				b = 0f;
				g = 1f;
			}
		}
		Renderer.drawRect(posX, posY, sizeX, sizeY, new float[] { r, g, b, 1f });
	}

	public void hit(float partialTick, boolean changeY) {
		if (this.posY > theGame.displayHeight / 2) {
			theGame.sfxHit2.playAsSoundEffect(1f, 0.05f, false);
		} else {
			theGame.sfxHit1.playAsSoundEffect(1f, 0.05f, false);
		}
		float accel = 0.15f;
		float newMotionY = -(motionY - (accel * partialTick));
		if (lastHittingPlayer == 1) {
			newMotionY = -(motionY + (accel * partialTick));
		}

		if (changeY)
			motionY = newMotionY;
		float max = 12f;
		if (motionY > max) {
			motionY = max;
		}
		if (motionY < -max) {
			motionY = -max;
		}
	}

	public void miss() {
		theGame.sfxMiss.playAsSoundEffect(1f, 0.05f, false);

		if (posY < theGame.displayHeight / 2) {
			theGame.playerTwo.score++;
		} else {
			theGame.playerOne.score++;
		}

		reset();
	}

	@Override
	public void logic(float partialTick) {
		// move based on motion
		posY += motionY * partialTick;
		posX += motionX * partialTick;

		// Wall bounce
		if (posX < 0) {
			posX = 0;
			motionX = -motionX;
			theGame.sfxHitWall.playAsSoundEffect(1f, 0.05f, false);
		}
		if (posX > theGame.displayWidth - sizeX) {
			posX = theGame.displayWidth - sizeX;
			motionX = -motionX;
			theGame.sfxHitWall.playAsSoundEffect(1f, 0.05f, false);
		}

		// ball detection
		for (EntityBall otherBall : theGame.balls) {
			if (otherBall == this)
				continue;
			if (this.posX + this.sizeX > otherBall.posX
					&& this.posX < otherBall.posX + otherBall.sizeX) {
				if (this.posY + this.sizeY > otherBall.posY
						&& this.posY < otherBall.posY + otherBall.sizeY) {
					if (this.posY < otherBall.posY) {
						motionY = -motionY;
						otherBall.motionY = -motionY;
					}
					motionX = -motionX;
					otherBall.motionX = -otherBall.motionX;
					break;
				}

			}
		}

		// player detection
		if (posY < theGame.playerOne.posY + theGame.playerOne.sizeY
				&& posY > theGame.playerOne.posY) {
			if (posX + sizeX > theGame.playerOne.posX
					&& posX < theGame.playerOne.posX + theGame.playerOne.sizeX) {
				boolean left = theGame.playerOne.lastX > theGame.playerOne.posX;
				float dir = left ? -rand.nextFloat() : rand.nextFloat();
				// dir = dir * (new Random().nextInt(3)+1);
				boolean changeY = true;
				//if (posY >= theGame.playerOne.posY + theGame.playerOne.sizeY - 5) 
				{
					posY = theGame.playerOne.posY + theGame.playerOne.sizeY + 1;
				}
				// motionX = 0;
				motionX = (dir * 2);
				lastHittingPlayer = 0;
				hit(partialTick, changeY);
				return;
			}
		}
		if (posY + sizeY > theGame.playerTwo.posY + 1
				&& posY + sizeY < theGame.playerTwo.posY + theGame.playerTwo.sizeY) {
			if (posX + sizeX > theGame.playerTwo.posX
					&& posX < theGame.playerTwo.posX + theGame.playerTwo.sizeX) {
				boolean left = theGame.playerTwo.lastX > theGame.playerTwo.posX;
				float dir = left ? -rand.nextFloat() : rand.nextFloat();
				boolean changeY = true;
				//if (posY + sizeY < theGame.playerTwo.posY - 5) 
				{
					posY = theGame.playerTwo.posY - sizeY - 1;
				}
				//posY = theGame.playerTwo.posY - sizeY - 3;
				// motionX = 0;
				motionX = (dir * 2);
				lastHittingPlayer = 1;
				hit(partialTick, changeY);
				return;
			}
		}
		// motionX = motionX * partialTick;

		// back wall detection
		if (posY > theGame.displayHeight - 30) {
			miss();
		}
		if (posY + (sizeY * 2) <= 0) {
			miss();
		}
	}

}
