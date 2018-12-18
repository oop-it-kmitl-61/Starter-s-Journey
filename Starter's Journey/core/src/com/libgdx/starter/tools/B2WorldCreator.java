package com.libgdx.starter.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Crystals;
import com.libgdx.starter.sprites.enemies.Bee;
import com.libgdx.starter.sprites.enemies.Boss;
import com.libgdx.starter.sprites.enemies.Enemies;
import com.libgdx.starter.sprites.enemies.Enemy;
import com.libgdx.starter.sprites.enemies.LeafBug;
import com.libgdx.starter.sprites.enemies.Turtle;
import com.libgdx.starter.sprites.items.Coin;
import com.libgdx.starter.sprites.items.Items;

public class B2WorldCreator {
	private Array<Enemy> enemy;
	private Array<Boss> boss;
	private Array<Items> coins;
	//private Array<HealthBar> health;
	
	
	public B2WorldCreator(PlayScreen screen) {
		World world = screen.getWorld();
		TiledMap map = screen.getMap();
        //create body and fixture variables
		BodyDef bdef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();
		Body body;
		
		//create ground bodies/fixtures
		for(MapObject object : map.getLayers().get("Ground").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / StarterJourney.PPM, (rect.getY() + rect.getHeight() / 2) / StarterJourney.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / StarterJourney.PPM, rect.getHeight() / 2 / StarterJourney.PPM);
			fdef.shape = shape;
			fdef.friction = 0.5f;
			body.createFixture(fdef);
		}
		
		//create under ground bodies/fixtures
		for(MapObject object : map.getLayers().get("UnderGround").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / StarterJourney.PPM, (rect.getY() + rect.getHeight() / 2) / StarterJourney.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / StarterJourney.PPM, rect.getHeight() / 2 / StarterJourney.PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = StarterJourney.UNDER_BIT;
			body.createFixture(fdef);
		}
		
		//create fire ground bodies/fixtures
		if(PlayScreen.numMap == 2)
			for(MapObject object : map.getLayers().get("FireGround").getObjects().getByType(RectangleMapObject.class)) {
				Rectangle rect = ((RectangleMapObject) object).getRectangle();
				
				bdef.type = BodyDef.BodyType.StaticBody;
				bdef.position.set((rect.getX() + rect.getWidth() / 2) / StarterJourney.PPM, (rect.getY() + rect.getHeight() / 2) / StarterJourney.PPM);
				
				body = world.createBody(bdef);
				
				shape.setAsBox(rect.getWidth() / 2 / StarterJourney.PPM, rect.getHeight() / 2 / StarterJourney.PPM);
				fdef.shape = shape;
				fdef.filter.categoryBits = StarterJourney.FIREGROUND_BIT;
				body.createFixture(fdef);
			}
		
		//create crystals bodies/fixtures
		for(MapObject object : map.getLayers().get("Crystal").getObjects().getByType(RectangleMapObject.class)) {
			//Rectangle rect = ((RectangleMapObject) object).getRectangle();
			new Crystals(screen, object);
		}
		
		//create reverse bodies/fixtures
		for(MapObject object : map.getLayers().get("Reverse").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / StarterJourney.PPM, (rect.getY() + rect.getHeight() / 2) / StarterJourney.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / StarterJourney.PPM, rect.getHeight() / 2 / StarterJourney.PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = StarterJourney.OBJECT_BIT;
			body.createFixture(fdef);
		}
		
		//create end point
		for(MapObject object : map.getLayers().get("EndPoint").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / StarterJourney.PPM, (rect.getY() + rect.getHeight() / 2) / StarterJourney.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / StarterJourney.PPM, rect.getHeight() / 2 / StarterJourney.PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = StarterJourney.ENDPOINT_BIT;
			body.createFixture(fdef);
		}
		
		//create all enemies
		enemy = new Array<Enemy>();
		for(MapObject object : map.getLayers().get("Enemy").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			if(PlayScreen.numMap == 0)
				enemy.add(new Enemies(screen, rect.getX() / StarterJourney.PPM, rect.getY() / StarterJourney.PPM, object));
			if(PlayScreen.numMap == 1)
				enemy.add(new LeafBug(screen, rect.getX() / StarterJourney.PPM, rect.getY() / StarterJourney.PPM, object));
			if(PlayScreen.numMap == 2)
				enemy.add(new Bee(screen, rect.getX() / StarterJourney.PPM, rect.getY() / StarterJourney.PPM, object));
		}
		
//		//create all enemy2
//		if(PlayScreen.numMap == 1) {
//			enemy2 = new Array<LeafBug>();
//			for(MapObject object : map.getLayers().get("Enemy2").getObjects().getByType(RectangleMapObject.class)) {
//				Rectangle rect = ((RectangleMapObject) object).getRectangle();
//				enemy2.add(new LeafBug(screen, rect.getX() / StarterJourney.PPM, rect.getY() / StarterJourney.PPM, object));
//			}
//		}
		
		//create boss
		boss = new Array<Boss>();
		for(MapObject object : map.getLayers().get("Boss").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			boss.add(new Boss(screen, rect.getX() / StarterJourney.PPM, rect.getY() / StarterJourney.PPM, object));
		}
		
		//create all coins
		coins = new Array<Items>();
		for(MapObject object : map.getLayers().get("Coin").getObjects()) {
			float x = object.getProperties().get("x", Float.class) / StarterJourney.PPM;
			float y = object.getProperties().get("y", Float.class) / StarterJourney.PPM;
			coins.add(new Coin(screen, x, y+0.08f));
		}
	}

	public Array<Enemy> getEnemies() {
		return enemy;
	}
	public static void removeTurtle(Turtle turtle) {
		//enemy2.removeValue(turtle, true);
	}
	public void removeEnemy(Enemy enemy) {
		if(enemy instanceof Enemies)
			this.enemy.removeValue((Enemies) enemy, true);
		if(enemy instanceof LeafBug)
			this.enemy.removeValue((LeafBug) enemy, true);
		if(enemy instanceof Bee)
			this.enemy.removeValue((Bee) enemy, true);
		if(enemy instanceof Boss)
			boss.removeValue((Boss) enemy, true);
	}
	public Array<Enemy> getEnemy() {
		Array<Enemy> enemy = new Array<Enemy>();
		enemy.addAll(this.enemy);
		enemy.addAll(boss);
		return enemy;
	}
	public Array<Items> getCoin() {
		return coins;
	}
}
