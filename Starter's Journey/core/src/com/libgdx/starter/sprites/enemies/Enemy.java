package com.libgdx.starter.sprites.enemies;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Player;
import com.libgdx.starter.sprites.items.ItemDef;
import com.libgdx.starter.sprites.items.Mana;
import com.libgdx.starter.sprites.items.Potion;
import com.libgdx.starter.sprites.items.Sword;

public abstract class Enemy extends Sprite {
	protected World world;
	protected PlayScreen screen;
	protected MapObject object;
	protected TiledMap map;
	protected TiledMapTile tile;
	public Body b2body;
	public Vector2 velocity;
	
	Texture blank;
	Texture healthbar;
	float maxHealth;
	float currentHealth;
	
	protected boolean setToDestroy;
	protected boolean destroyed;
	
	protected String[] items;
	
	public Enemy(PlayScreen screen, float x, float y, MapObject object) {
		this.world = screen.getWorld();
		this.screen = screen;
		this.object = object;
		this.map = screen.getMap();
		setPosition(x, y);
		
		blank = new Texture("enemyhealthbg.png");
		healthbar = new Texture("enemyhealthfg.png");
		
		defineEnemy();
		velocity = new Vector2(0.8f, 0);
		b2body.setActive(false);
		
		setToDestroy = false;
		destroyed = false;
		
		items = new String[] {"potion", "mana", "none", "sword"};
	}
	
	public abstract void update(float dt);
	public abstract void hitOnHead(Player player);
	public abstract void onEnemyHit(Enemy enemy);
	
	public void defineEnemy() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(8 / StarterJourney.PPM);
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
	
	public void reverseVelocity(boolean x, boolean y) {
		if(x)
			velocity.x = - velocity.x;
		if(y)
			velocity.y = - velocity.y;
	}
	
	public float getHealth() {
		return currentHealth;
	}
	
	public void onPlayerAtk(Player player) {
		setHealth(1);
		if(currentHealth == 0)
			setToDestroy = true;
	}
	
	public void onFireBallHit() {
		setHealth(3);
		if(currentHealth == 0) {
			setToDestroy = true;
		}
	}
	
	public void setHealth(int value) {
		if(currentHealth - value <= 0) {
			currentHealth = 0;
		}
		else {
			currentHealth -= value;
		}
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public void randomItems() {
		int rnd = new Random().nextInt(items.length);
		String item = items[rnd];
		//object.getProperties().containsKey(item)
		if(item == "potion")
			screen.spawnItem(new ItemDef(new Vector2(b2body.getPosition().x, b2body.getPosition().y + 16 / StarterJourney.PPM), Potion.class));
		else if(item == "sword")
			screen.spawnItem(new ItemDef(new Vector2(b2body.getPosition().x, b2body.getPosition().y + 16 / StarterJourney.PPM), Sword.class));
		else if(item == "mana")
			screen.spawnItem(new ItemDef(new Vector2(b2body.getPosition().x, b2body.getPosition().y + 16 / StarterJourney.PPM), Mana.class));
	}
	
	public TiledMapTileLayer.Cell getCell() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("background");
		return layer.getCell((int)(b2body.getPosition().x * StarterJourney.PPM / 15), 
				(int)(b2body.getPosition().y * StarterJourney.PPM / 15));
	}
	
}
