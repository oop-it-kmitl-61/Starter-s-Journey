package com.libgdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.tools.SaveMap;

public class LevelSelect implements Screen {

	private Viewport viewport;
	private Stage stage;
	
	private StarterJourney game;
	
	private Texture[] playButtonActive = new Texture[3];
	private Texture exitButtonActive;
	private Texture background, locked;
	
	private Image[] semiTL = new Image[3], lock = new Image[3];
	private Image[] map = new Image[PlayScreen.totalMap];
	private Image bg, exit;
	
	private int mapUnlock;
	
	private int[] x = new int[3], y = new int[3];
	
	public LevelSelect(StarterJourney game) {
		this.game = game;
		viewport = new FitViewport(StarterJourney.V_WIDTH, StarterJourney.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, ((StarterJourney) game).batch);

		x[0] = StarterJourney.V_WIDTH / 2 - 200;
		y[0] = StarterJourney.V_HEIGHT / 2 - 20;
		x[1] = StarterJourney.V_WIDTH / 2 + 26;
		y[1] = StarterJourney.V_HEIGHT / 2 - 20;
		x[2] = StarterJourney.V_WIDTH / 2 - 170 / 2;
		y[2] = StarterJourney.V_HEIGHT / 2 - 175;
		
		//draw transparent layer
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fillRectangle(0, 0, 1, 1);
		Texture texture1 = new Texture(pixmap);
		pixmap.dispose();

		semiTL[0] = new Image(texture1);
		semiTL[0].setSize(170, 139);
		semiTL[0].getColor().a=.2f;
		semiTL[0].setBounds(x[0], y[0], 170, 139);
		semiTL[1] = new Image(texture1);
		semiTL[1].setSize(170, 139);
		semiTL[1].getColor().a=.2f;
		semiTL[1].setBounds(x[1], y[1], 170, 139);
		semiTL[2] = new Image(texture1);
		semiTL[2].setSize(170, 139);
		semiTL[2].getColor().a=.2f;
		semiTL[2].setBounds(x[2], y[2], 170, 139);
		
		//game over util
		playButtonActive[0] = new Texture("image/ChooseScene/Asset 1.png");
		playButtonActive[1] = new Texture("image/ChooseScene/Asset 2.png");
		playButtonActive[2] = new Texture("image/ChooseScene/Asset 3.png");
		exitButtonActive = new Texture("image/ChooseScene/x_button.png");
		background = new Texture("image/ChooseScene/BG.jpg");
		locked = new Texture("image/ChooseScene/locked.png");
		
		bg = new Image(background);
		exit = new Image(exitButtonActive);
		map[0] = new Image(playButtonActive[0]);
		map[1] = new Image(playButtonActive[1]);
		map[2] = new Image(playButtonActive[2]);
		lock[0] = new Image(locked);
		lock[1] = new Image(locked);
		lock[2] = new Image(locked);
		
		bg.setBounds(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		exit.setBounds(StarterJourney.V_WIDTH - 31, StarterJourney.V_WIDTH - 31, 21, 21);
		map[0].setBounds(x[0], y[0], 170, 139);
		map[1].setBounds(x[1], y[1], 170, 139);
		map[2].setBounds(x[2], y[2], 170, 139);
		lock[0].setBounds(x[0] + 24, y[0], 120, 120);
		lock[1].setBounds(x[1] + 24, y[1], 120, 120);
		lock[2].setBounds(x[2] + 24, y[2], 120, 120);

		SaveMap.load();
		mapUnlock = SaveMap.gd.getMapUnlock();
		GameMenu.music.play();
	}

	@Override
	public void show() {}

	public void update(float dt) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
//		stage.getBatch().begin();
//		// --- > Background
//		stage.getBatch().draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
//		// < --- Background
//		
//		// --- > Play Button
//		x = StarterJourney.V_WIDTH / 2 - 200;
//		y = StarterJourney.V_HEIGHT / 2 - 20;
//		stage.getBatch().draw(playButtonActive[0], x, y, 170, 139);
//		handleInput(0);
//		
//		x = StarterJourney.V_WIDTH / 2 + 26;
//		stage.getBatch().draw(playButtonActive[1], x, y, 170, 139);
//		handleInput(2);
//		
//		x = StarterJourney.V_WIDTH / 2 - 170 / 2;
//		y = StarterJourney.V_HEIGHT / 2 - 175;
//		stage.getBatch().draw(playButtonActive[2], x, y, 170, 139);
//		handleInput(1);
//		// < --- Play Button
//		
//		// --- > Exit Button
//		x = StarterJourney.V_WIDTH - 31;
//		y = StarterJourney.V_HEIGHT - 31;
//		stage.getBatch().draw(exitButtonActive, x, y, 21, 21);
//		handleInput(3);
//		// < ---  Exit Button
//		stage.getBatch().end();
		
		stage.addActor(bg);
		stage.addActor(exit);
		stage.addActor(map[0]);
		stage.addActor(map[1]);
		stage.addActor(map[2]);
		
		for(int i = (mapUnlock+1); i < PlayScreen.totalMap; i++) {
			stage.addActor(semiTL[i]);
			stage.addActor(lock[i]);
		}
		
		stage.draw();
		
		for(int i = 0; i < mapUnlock + 1; i++)
			handleInput(i, x[i], y[i]);
	}
	
	public void handleInput(int numMap, int x, int y) {
		if(Gdx.input.getX() < (x + 170) * StarterJourney.V_SCALE 
				&& Gdx.input.getX() > x * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() < (y + 139) * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() > y * StarterJourney.V_SCALE)
			if(Gdx.input.isTouched()) {
				if(numMap == 0)
					game.setScreen(new HowToPlay((StarterJourney) game, numMap));
				else
					game.setScreen(new PlayScreen((StarterJourney) game, numMap));
				GameMenu.music.stop();
			}
		if(Gdx.input.getX() < (x + 21) * StarterJourney.V_SCALE 
				&& Gdx.input.getX() > x * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() < (y + 21) * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() > y * StarterJourney.V_SCALE)
			if(Gdx.input.isTouched())
				Gdx.app.exit();
	}
	
	@Override
	public void render(float delta) {
		update(delta);
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}
	
}
