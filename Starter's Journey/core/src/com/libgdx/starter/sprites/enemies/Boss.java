package com.libgdx.starter.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.scenes.Hud;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Player;

public class Boss extends Enemy {

	private float stateTime;
	private Animation<TextureRegion> walkAnimation;
	private Animation<TextureRegion> atkAnimation;
	private Animation<TextureRegion> fireGround;
	private Array<TextureRegion> frames;
	private Array<TextureRegion> atkFrames;
	
	float count;

	Filter filter;
	
	public Boss(PlayScreen screen, float x, float y, MapObject object) {
		super(screen, x, y, object);
		frames = new Array<TextureRegion>();
		atkFrames = new Array<TextureRegion>();
		if(PlayScreen.numMap == 0) {
			for(int i = 0; i < 2; i++)
				frames.add(new TextureRegion(screen.getAtlas().findRegion("LeafBug"), i * 64, 0, 64, 64));
			maxHealth = 10;
			currentHealth = 10;
		}
		if(PlayScreen.numMap == 1) {
			for(int i = 0; i < 8; i++)
				frames.add(new TextureRegion(screen.getAtlas().findRegion("Bee"), i * 96, 0, 96, 96));
			maxHealth = 15;
			currentHealth = 15;
		}
		if(PlayScreen.numMap == 2) {
			for(int i = 0; i < 2; i++)
				frames.add(new TextureRegion(screen.getAtlas().findRegion("Fire_Ground"), i * 32, 0, 32, 32));
			fireGround = new Animation<TextureRegion>(0.4f, frames);
			frames.clear();
			
			TextureRegion region;
			for(int i = 8; i < 12; i++) {
				region = new TextureRegion(screen.getAtlas().findRegion("Dragon"), i * 80, 0, 80, 80);
				region.flip(true, false);
				frames.add(region);
			}
			for(int i = 0; i < 4; i++) {
				region = new TextureRegion(screen.getAtlas().findRegion("Dragon"), i * 80, 0, 80, 80);
				region.flip(true, false);
				atkFrames.add(region);
			}
			atkAnimation = new Animation<TextureRegion>(0.8f, atkFrames);
			atkFrames.clear();
			maxHealth = 20;
			currentHealth = 20;
		}
		
		walkAnimation = new Animation<TextureRegion>(0.4f, frames);
		frames.clear();
		
		stateTime = 0;
		setBounds(getX(), getY(), 64 / StarterJourney.PPM, 64 / StarterJourney.PPM);
		setToDestroy = false;
		destroyed = false;
		
		velocity = new Vector2(1f, 1f);
		
//		blank = new Texture("enemyhealthbg.png");
//		healthbar = new Texture("enemyhealthfg.png");
		filter = new Filter(); 
		filter.maskBits = StarterJourney.GROUND_BIT;
	}

	public void update(float dt) {
		stateTime += dt;
		
		if(setToDestroy && !destroyed) {
			if(count == 0)
				for(Fixture fixture : b2body.getFixtureList())
					fixture.setFilterData(filter);
			count += dt;
			if(PlayScreen.numMap == 0)
				for(int i = 0; i < 2; i++) {
					setRegion(new TextureRegion(screen.getAtlas().findRegion("LeafBug"), 3 * 64, 0, 64, 64));
					setRegion(new TextureRegion(screen.getAtlas().findRegion("LeafBug"), 2 * 64, 0, 64, 64));
				}
			if(PlayScreen.numMap == 1)
				for(int i = 0; i < 2; i++) {
					setRegion(new TextureRegion(screen.getAtlas().findRegion("Bee"), 8 * 96, 0, 96, 96));
					setRegion(new TextureRegion(screen.getAtlas().findRegion("Bee"), 7 * 96, 0, 96, 96));
				}
			if(PlayScreen.numMap == 2)
				for(int i = 4; i < 8; i++) {
					setRegion(new TextureRegion(screen.getAtlas().findRegion("Dragon"), i * 80, 0, 80, 80));
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
			if(Hud.getWorldTimer() % 50 == 0 && PlayScreen.numMap == 2)
				setRegion(atkAnimation.getKeyFrame(stateTime, true));
			else
				setRegion(walkAnimation.getKeyFrame(stateTime, true));
		}
		
		if(destroyed) {
			Hud.addScore(2000);
			randomItems();
			screen.setBossDied(true);
		}
		
	}

	public void defineEnemy() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(20 / StarterJourney.PPM);
		fdef.filter.categoryBits = StarterJourney.ENEMY_BIT;
		fdef.filter.maskBits = StarterJourney.GROUND_BIT |
				StarterJourney.CRYSTALS_BIT |
				StarterJourney.PLAYER_BIT |
				StarterJourney.OBJECT_BIT |
				StarterJourney.ENEMY_BIT |
				StarterJourney.FIREBALL_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
	}
	
	public void draw(Batch batch) {
		if(!destroyed || stateTime < 1) {
			super.draw(batch);
			batch.draw(blank, getX(), getY() + 0.6f, getWidth(), 5 / StarterJourney.PPM);
			batch.draw(healthbar, getX(), getY() + 0.6f, getWidth() * (currentHealth / maxHealth), 5 / StarterJourney.PPM);
		}
		if(PlayScreen.numMap == 2 && atkAnimation.isAnimationFinished(stateTime)) {
			if(Hud.getWorldTimer() % 50 == 0) {
				float x = 23.44f;
				for(int i = 0; i < 16; i++) {
					batch.draw(fireGround.getKeyFrame(stateTime, true), x, 0.18f, 0.5f, 0.5f);
					x += 0.22f;
				}
			}
		}
	}
	
	@Override
	public void hitOnHead(Player player) {}

	@Override
	public void onEnemyHit(Enemy enemy) {}
	
}
