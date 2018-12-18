package com.libgdx.starter.sprites.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.scenes.Hud;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Player;

public class Bee extends Enemy {

	private float stateTime;
	private Animation<TextureRegion> walkAnimation;
	private Array<TextureRegion> frames;
	
	float count;

	Filter filter;
	
	public Bee(PlayScreen screen, float x, float y, MapObject object) {
		super(screen, x, y, object);
		frames = new Array<TextureRegion>();
		for(int i = 0; i < 8; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("Bee"), i * 96, 0, 96, 96));
		walkAnimation = new Animation<TextureRegion>(0.4f, frames);
		frames.clear();

		stateTime = 0;
		setBounds(getX(), getY(), 16 / StarterJourney.PPM, 16 / StarterJourney.PPM);
		setToDestroy = false;
		destroyed = false;
		
		maxHealth = 10;
		currentHealth = 10;

		filter = new Filter(); 
		filter.maskBits = StarterJourney.GROUND_BIT;
		
		velocity = new Vector2(0.7f, 0);
	}

	public void update(float dt) {
		stateTime += dt;
		
		if(setToDestroy && !destroyed) {
			if(count == 0)
				for(Fixture fixture : b2body.getFixtureList())
					fixture.setFilterData(filter);
			count += dt;
			
			for(int i = 0; i < 2; i++) {
				setRegion(new TextureRegion(screen.getAtlas().findRegion("Bee"), 8 * 96, 0, 96, 96));
				setRegion(new TextureRegion(screen.getAtlas().findRegion("Bee"), 7 * 96, 0, 96, 96));
			}
			
			if(count >= 2) {
				destroyed = true;
				world.destroyBody(b2body);
			}
			stateTime = 0;
			
		}
		else if(!destroyed) {
			b2body.setLinearVelocity(velocity);
			setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 0 / StarterJourney.PPM);
			setRegion(walkAnimation.getKeyFrame(stateTime, true));
		}
		
		if(destroyed) {
			Hud.addScore(500);
			randomItems();
		}
	}

	public void draw(Batch batch) {
		if(!destroyed || stateTime < 1) {
			super.draw(batch);
			batch.draw(blank, getX(), getY() + 0.2f, getWidth(), 5 / StarterJourney.PPM);
			batch.draw(healthbar, getX(), getY() + 0.2f, getWidth() * (currentHealth / maxHealth), 5 / StarterJourney.PPM);
		}
	}
	
	@Override
	public void hitOnHead(Player player) {
		setToDestroy = true;
		StarterJourney.manager.get("audio/sounds/stomp.wav", Sound.class).play();
	}

	@Override
	public void onEnemyHit(Enemy enemy) {
		if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL)
			setToDestroy = true;
		else
			reverseVelocity(true, false);
	}
	
}