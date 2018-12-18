package com.libgdx.starter.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.libgdx.starter.StarterJourney;

public class HowToPlay implements Screen {

	private Viewport viewport;
	private Stage stage;
	
	private Game game;
	
	private Texture background;
	private Image bg;
	
	private int numMap;
	
	public HowToPlay(StarterJourney game, int numMap) {
		this.game = game;
		this.numMap = numMap;
		viewport = new FitViewport(StarterJourney.V_WIDTH, StarterJourney.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, ((StarterJourney) game).batch);
		
		background = new Texture("image/HowToPlay/BG.png");
		bg = new Image(background);
		
		bg.setBounds(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		
		stage.addActor(bg);
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			game.setScreen(new PlayScreen((StarterJourney) game, numMap));
			dispose();
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
