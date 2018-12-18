package com.libgdx.starter.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;

public class Hud implements Disposable {

	public Stage stage;
	private Viewport viewport;
	
	public static Integer worldTimer;
	private float timeCount;
	private static Integer score = 0;
	public static int extraLives;
	
	int previousScore;
	
	//Scene2D widgets
	private Label countdownLabel;
	private static Label scoreLabel;
	private Label timeLabel;
	private Label levelLabel;
	private Label worldLabel;
	private Label starterLabel;
	private Label livesLabel;
	public static Label numLivesLabel;
	
	private static Image[] lifes;
	public static Group grp;
	
	public Hud(SpriteBatch sb) {
		//define our tracking variables
		worldTimer = 300;
		timeCount = 0;
		extraLives = 3;
		
		//setup the HUD viewport using a new camera seperate from our gamecam
		//define our stage using that viewport and our games spritebatch
		viewport = new FitViewport(StarterJourney.V_WIDTH, StarterJourney.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, sb);
		
		//define a table used to organize our hud's labels
		Table table = new Table();
		//Top-Align table
		table.top();
		//make the table fill the entire stage
		table.setFillParent(true);
		
		//define our labels using the String, and a Label style consisting of a font and color
		countdownLabel= new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		levelLabel = new Label(PlayScreen.numMap+"", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		starterLabel = new Label("STARTER", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		livesLabel = new Label("LIVES", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		numLivesLabel = new Label(extraLives+"", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		
		//lifes
		lifes = new Image[3];
		lifes[0] = new Image(new Texture("PIxelantasy/Bonus items/PNG/heart.png"));
		lifes[0].setSize(16, 16);
		lifes[1] = new Image(new Texture("PIxelantasy/Bonus items/PNG/heart.png"));
		lifes[1].setSize(16, 16);
		lifes[1].setX(16);
		lifes[2] = new Image(new Texture("PIxelantasy/Bonus items/PNG/heart.png"));
		lifes[2].setSize(16, 16);
		lifes[2].setX(32);
		
		grp = new Group();
		grp.addActor(lifes[0]);
		grp.addActor(lifes[1]);
		grp.addActor(lifes[2]);
		
		//define our labels using the String, and a Label style consisting of a font and color
		table.add(starterLabel).expandX().padTop(10);
		table.add(worldLabel).expandX().padTop(10);
		table.add(livesLabel).expandX().padTop(10);
		table.add(timeLabel).expandX().padTop(10);
		//add a second row to our table
		table.row();
		table.add(scoreLabel).expandX();
		table.add(levelLabel).expandX();
		table.add(grp).fillY().padLeft(-48);
		table.add(countdownLabel).expandX();
		
		//add our table to the stage
		stage.addActor(table);
	}

	public void update(float dt) {
		timeCount += dt;
		if(timeCount >= 1) {
			worldTimer--;
			countdownLabel.setText(String.format("%03d", worldTimer));
			timeCount = 0;
		}
	}
	
	public static void addScore(int value) {
		score += value;
		scoreLabel.setText(String.format("%06d", score));
	}
	
	public static void addLives(int value) {
		if(extraLives == 3)
			return;
		grp.addActor(lifes[extraLives]);
		extraLives += value;
	}
	
	public static void setLives(int value) {
		extraLives -= value;
		numLivesLabel.setText(extraLives+"");
		grp.removeActor(lifes[extraLives]);
	}
	
	public void setPreviousScore(int previousScore) {
		score = this.previousScore;
		scoreLabel.setText(String.format("%06d", score));
		this.previousScore = previousScore;
	}
	
	public void setScore() {
		score = previousScore;
		scoreLabel.setText(String.format("%06d", score));
	}
	
	public int getScore() {
		return score;
	}

	public static Integer getWorldTimer() {
		return worldTimer;
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
	
}
