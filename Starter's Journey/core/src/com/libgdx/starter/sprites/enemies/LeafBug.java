package com.libgdx.starter.sprites.enemies;

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

public class LeafBug extends Enemy {

	private float stateTime;
	private Animation<TextureRegion> walkAnimation;
	private Array<TextureRegion> frames;
	
	float count;

	Filter filter;
	
//	Texture blank;
//	Texture healthbar;
//	float maxHealth;
//	float currentHealth;
	
	public LeafBug(PlayScreen screen, float x, float y, MapObject object) {
		super(screen, x, y, object);
		frames = new Array<TextureRegion>();
		for(int i = 0; i < 2; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("LeafBug"), i * 64, 0, 64, 64));
		walkAnimation = new Animation<TextureRegion>(0.4f, frames);
		frames.clear();

		stateTime = 0;
		setBounds(getX(), getY(), 16 / StarterJourney.PPM, 16 / StarterJourney.PPM);
		setToDestroy = false;
		destroyed = false;
		
		maxHealth = 5;
		currentHealth = 5;
		
		filter = new Filter(); 
		filter.maskBits = StarterJourney.GROUND_BIT;
		
		velocity = new Vector2(0.6f, 0);
	}

	public void update(float dt) {
		stateTime += dt;
		
		if(setToDestroy && !destroyed) {
			if(count == 0)
				for(Fixture fixture : b2body.getFixtureList())
					fixture.setFilterData(filter);
			count += dt;
			
			setRegion(new TextureRegion(screen.getAtlas().findRegion("LeafBug"), 32 , 0, 16, 16));
			
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
	public void hitOnHead(Player player) {}

	@Override
	public void onEnemyHit(Enemy enemy) {}
	
}
