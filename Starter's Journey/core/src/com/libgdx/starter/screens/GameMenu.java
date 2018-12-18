package com.libgdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.libgdx.starter.StarterJourney;

public class GameMenu implements Screen {
	
	private Viewport viewport;
	private Stage stage;
	
	//private BitmapFont titleFont, font;
	//private final String title = "Starter's Journey";
	
	//private int currentItem;
	//private String[] menuItems;
	
	private StarterJourney game;
	
	private Texture Title;
	private Texture[] playButtonActive = new Texture[2];
	//private Texture playButtonInActive;
	private Texture exitButtonActive;
	//private Texture exitButtonInActive;
	private Texture background;
	
	private int x, y;
	
	public static Music music;
	
	public GameMenu(StarterJourney game) {
		this.game = game;
		viewport = new FitViewport(StarterJourney.V_WIDTH, StarterJourney.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, ((StarterJourney) game).batch);
		
		music = StarterJourney.manager.get("audio/background_OST/Menu/1 - Piotr Musial - This War of Mine.ogg", Music.class);
        music.setLooping(true);
        music.play();
		
		Title = new Texture("image/MainMenu/StartersJourney 2.png");
		playButtonActive[0] = new Texture("image/MainMenu/StartButton1.png");
		playButtonActive[1] = new Texture("image/MainMenu/StartButton2.png");
		exitButtonActive = new Texture("image/MainMenu/x_button.png");
		background = new Texture("image/MainMenu/BG.jpg");
	}
	
	public void update(float dt) {
//
//		menuItems = new String[] {
//				"Play",
//				"Highscores",
//				"Quit"
//		};
//		
//		Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
//		
//		Table table = new Table();
//		table.center();
//		table.setFillParent(true);
//		
//		Label titleLabel = new Label(title, font);
//		table.add(titleLabel).expandX();
//		table.row();
//		
//		for(int i = 0; i < menuItems.length; i++) {
//			Label Label = new Label(menuItems[i], font);
//			if(currentItem == i)
//				Label.setColor(Color.RED);
//			table.add(Label).expandX().padTop(10f);
//			table.row();
//		}
//		
//		stage.addActor(table);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.getBatch().begin();
		
		// --- > Background
		stage.getBatch().draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		stage.getBatch().draw(Title, 
				StarterJourney.V_WIDTH / 2 - 500 / 2, 
				StarterJourney.V_HEIGHT / 2 - 120/ 2, 
				500, 
				154
				);
		// < --- Background
		
		// --- > Play Button
		x = StarterJourney.V_WIDTH / 2 - 200 / 2;
		y = StarterJourney.V_HEIGHT / 2 - 200;
		stage.getBatch().draw(playButtonActive[0], x, y, 200, 67);
		if(Gdx.input.getX() < (x + 200) * StarterJourney.V_SCALE 
				&& Gdx.input.getX() > x * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() < (y + 67) * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() > y * StarterJourney.V_SCALE) {
			stage.getBatch().draw(playButtonActive[1], x, y, 200, 67);}
		handleInput();
		// < --- Play Button
		
		// --- > Exit Button
		x = StarterJourney.V_WIDTH - 31;
		y = StarterJourney.V_HEIGHT - 31;
		stage.getBatch().draw(exitButtonActive, x, y, 21, 21);
		handleInput();
		// < ---  Exit Button
		stage.getBatch().end();
		stage.draw();
		
	}
	
	public void handleInput() {
//		if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
//			if(currentItem > 0)
//				currentItem--;
//		if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
//			if(currentItem < menuItems.length - 1)
//				currentItem++;
//		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
//			select();
		if(Gdx.input.getX() < (x + 200) * StarterJourney.V_SCALE 
				&& Gdx.input.getX() > x * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() < (y + 67) * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() > y * StarterJourney.V_SCALE) {
			if(Gdx.input.isTouched()) {
				game.setScreen(new LevelSelect((StarterJourney) game));
			}
		}
			
		if(Gdx.input.getX() < (x + 21) * StarterJourney.V_SCALE 
				&& Gdx.input.getX() > x * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() < (y + 21) * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() > y * StarterJourney.V_SCALE)
			if(Gdx.input.isTouched())
				Gdx.app.exit();
	}
	
	public void select() {
//		if(currentItem == 0) {
//			game.setScreen(new PlayScreen((StarterJourney) game));
//			dispose();
//		}
//		else if(currentItem == 1)
//			game.getState(game.PLAY);
//		else
//			Gdx.app.exit();
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		update(delta);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		music.dispose();
	}

}
