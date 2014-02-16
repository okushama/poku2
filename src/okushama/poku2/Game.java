package okushama.poku2;
import java.util.ArrayList;

import okushama.engine.core.SimpleGame;
import okushama.engine.util.Colour;
import okushama.engine.util.Registry;
import okushama.engine.util.Renderer;
import okushama.engine.util.TrueTypeFont;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.Audio;

public class Game extends SimpleGame {

	public static void main(String[] args) {
		new Game();
	}

	public EntityPlayer playerOne = null, playerTwo = null;
	public ArrayList<EntityBall> balls = null;
	public EntityBall mainBall;
	public boolean gamePaused = false, gameStarted = false, gameFinished = false, twoPlayer = false, aiPlayerOne = false;
	public static final int scoreToWin = 21;
	public int winner = -1;
	public TrueTypeFont fontLarge, fontSmall;
	public Audio sfxHit1, sfxHit2, sfxMiss, sfxHitWall, sfxPause, sfxGameOver, bgmMain;
	private static final String dirTexture = "assets/texture/", dirFont = "assets/font/", dirSound = "assets/sound/";

	public Game() {
		super("Poku 2", 400, 400, 2, dirTexture + "icon.png");
	}

	@Override
	public void registerAssets() {
		fontLarge = Registry.registerFont(dirFont + "font.TTF", 24f);
		fontSmall = Registry.registerFont(dirFont + "font.TTF", 10f);
		sfxHit1 = Registry.registerSound(dirSound + "hitone.wav");
		sfxHit2 = Registry.registerSound(dirSound + "hittwo.wav");
		sfxMiss = Registry.registerSound(dirSound + "miss.wav");
		sfxHitWall = Registry.registerSound(dirSound + "hitwall.wav");
		sfxPause = Registry.registerSound(dirSound + "pause.wav");
		sfxGameOver = Registry.registerSound(dirSound + "gameover.wav");
		bgmMain = Registry.registerSound(dirSound + "bg.ogg");

		playerOne = new EntityPlayer(this, 0);
		playerTwo = new EntityPlayer(this, 1);
		balls = new ArrayList<EntityBall>();
		mainBall = new EntityBall(this);
		this.resetGame(true);
		bgmMain.playAsSoundEffect(1f, 0.02f, true);
	}

	public boolean gameInAction() {
		return gameStarted && !gameFinished && !gamePaused;
	}

	public void resetBalls() {
		balls.clear();
		mainBall.reset();
		balls.add(mainBall);
	}

	@Override
	public void onRenderTick(float pt) {
		if (gameInAction()) {
			playerOne.render(pt);
			playerTwo.render(pt);
			for (EntityBall ball : balls) {
				ball.render(pt);
			}
		}
		Renderer.drawRect(0, displayHeight - 30, displayWidth, 30, new float[] {
				0f, 0f, 0f, 1f });
		Renderer.drawRect(0, displayHeight - 30, displayWidth, 2, new float[] {
				1f, 1f, 1f, 1f });
		Renderer.drawRect(0, 0, displayWidth, 2, new float[] { 1f, 1f, 1f, 1f });
		String sep = " - ";
		if (playerOne.score == 0 && playerTwo.score == 0) {
			sep = "___";
		}
		String scores = playerOne.score + sep + playerTwo.score;
		Renderer.drawString(fontLarge, scores,
				displayWidth / 2 - (fontLarge.getWidth(scores) / 2),
				displayHeight - fontLarge.getHeight(), Colour.white);
		String[] line = { "" };
		if (gamePaused) {
			line = new String[] { "PAUSED" };
		}
		if (!gameStarted) {
			line = new String[] { "---POKU---" };
			if (System.currentTimeMillis() % 1000 > 440)
				Renderer.drawCenteredString(fontSmall,
						"PRESS 'SPACE' TO BEGIN", 35, Colour.white,
						displayWidth);
		}
		if (gameFinished) {
			if (winner == 1) {
				line = new String[] { "YOU LOSE!" };
			} else {
				line = new String[] { "YOU WIN!" };
			}
			if (twoPlayer || aiPlayerOne) {
				line = new String[] { "P" + (winner + 1) + " WINS!" };
			}
		}
		for (int i = 0; i < line.length; i++) {
			Renderer.drawString(fontLarge,line[i],
					displayWidth / 2 - (fontLarge.getWidth(line[i]) / 2),
					displayHeight / 2 - (fontLarge.getHeight() * i) + 40, Colour.white);
		}

		String[] pauseText2 = { "CONTROLS:", "P1 MOVEMENT: Arrows ",
				"P2 MOVEMENT: Z and X", "EXTRA BALLS: B      ",
				"TWO PLAYERS: SPACE  ", "SPECTATE AI: ENTER  ",
				"PAUSE GAME : ESC    ", "RESET GAME : R      " };
		if (gameInAction()) {
			long tick = 2000;
			String p1id = "P1", p2id = "P2";
			if (System.currentTimeMillis() % tick >= tick / 4) {
				if (aiPlayerOne) {
					p1id = "CPU";
				}
				if (!twoPlayer) {
					p2id = "CPU";
				}
				Renderer.drawString(fontSmall, p1id, 2, displayHeight - 28,
						Colour.white);
				Renderer.drawString(fontSmall, p2id, displayWidth - 1
						- fontSmall.getWidth(p2id), displayHeight - 28,
						Colour.white);
			}

		}
		if (!gameInAction()) {
			for (int i = 0; i < pauseText2.length; i++) {
				Renderer.drawCenteredString(fontSmall, pauseText2[i],
						(displayHeight / 2 + 30) - (fontSmall.getHeight() * i),
						Colour.white, displayWidth);
			}

			Renderer.drawCenteredString(fontSmall, "(C) Mr_okushama 2014", 3,
					Colour.white, displayWidth);
		}
	}

	@Override
	public void onLogicTick(float pt) {
		if (winner > -1) {
			gameFinished = true;
		}

		if (gameInAction()) {
			playerOne.logic(pt);
			if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
				playerOne.magnetizeBalls();
			}
			playerTwo.logic(pt);
			for (int i = 0; i < balls.size(); i++) {
				EntityBall ball = balls.get(i);
				ball.logic(pt);
			}
			if (playerOne.score >= scoreToWin) {
				winner = 0;
				return;
			}
			if (playerTwo.score > scoreToWin) {
				winner = 1;
				return;
			}
		}
	}

	@Override
	public void onMouseClick(int btn) {

	}

	@Override
	public void onKeyPress(int key) {
		if(key == Keyboard.KEY_RETURN){
			if(gameInAction()){
				if(!aiPlayerOne){
					aiPlayerOne = true;
					twoPlayer = false;
				}else{
					aiPlayerOne = false;
				}
			}
		}
		if(key == Keyboard.KEY_R){
			this.resetGame(true);
		}
		if (key == Keyboard.KEY_B){
			EntityBall ball = new EntityBall(this);
			ball.reset();
			balls.add(ball);
		}
		
		if (key == Keyboard.KEY_SPACE) {
			if (gameInAction()){
				if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
					aiPlayerOne = !aiPlayerOne;
					return;
				}
				twoPlayer = !twoPlayer;
			}
			if (gameFinished) {
				sfxGameOver.playAsSoundEffect(1f, 0.05f, false);
				resetGame(true);
				return;
			}
			if (!gameStarted) {
				sfxPause.playAsSoundEffect(1f, 0.05f, false);
				resetGame(false);
			}
		}
		if (key == Keyboard.KEY_ESCAPE) {
			if (gameFinished) {
				resetGame(true);
				return;
			}
			sfxPause.playAsSoundEffect(1f, 0.05f, false);
			gamePaused = !gamePaused;
		}
	}

	public void resetGame(boolean toMenu) {
		if (!toMenu) {
			gameStarted = true;
		} else {
			gameStarted = false;
		}
		gamePaused = false;
		resetBalls();
		winner = -1;
		playerOne.score = 0;
		playerTwo.score = 0;
		gameFinished = false;
	}

	public EntityBall getClosestBall(EntityPlayer player) {
		float closest = 9999f;
		EntityBall closestObj = null;

		for (EntityBall ball : balls) {
			if (player.playerSlot == 0) {
				if (ball.posY - (player.posY + player.sizeY) < closest) {
					closest = (ball.posY - player.posY);
					closestObj = ball;
					closestObj.distanceToClosestPlayer = closest;
					closestObj.closestPlayerSlot = 0;

				}
			} else {
				if (player.posY - (ball.posY + ball.sizeY) < closest) {
					closest = player.posY - (ball.posY + ball.sizeY);
					closestObj = ball;
					closestObj.distanceToClosestPlayer = closest;
					closestObj.closestPlayerSlot = 1;
				}
			}
		}
		return closestObj;
	}

}
