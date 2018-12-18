package com.libgdx.starter.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;

public class FireBall extends Sprite {

    PlayScreen screen;
    World world;
    Array<TextureRegion> frames;
    Animation<TextureRegion> fireAnimation;
    Animation<TextureRegion> fireFlipAnimation;
    Animation<TextureRegion> explosion;
    float stateTime;
    float exploTime;
    boolean destroyed;
    boolean setToDestroy;
    boolean fireRight;
    boolean setToDestroyWithEnemy;

    Body b2body;
    
    float enemyPositionX;
    float enemyPositionY;
    
    TextureRegion region;
    
    public FireBall(PlayScreen screen, float x, float y, boolean fireRight){
        this.fireRight = fireRight;
        this.screen = screen;
        this.world = screen.getWorld();
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Fireball"), i * 32, 0, 32, 32));
        }
        fireAnimation = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();
        setRegion(fireAnimation.getKeyFrame(0));
        setBounds(x, y - 0.1f, 20 / StarterJourney.PPM, 20 / StarterJourney.PPM);
        
        for(int i = 0; i < 3; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("explosion"), i * 32, 0, 32, 32));
        }
        explosion = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();
        
        for(int i = 0; i < 2; i++){
        	region = new TextureRegion(screen.getAtlas().findRegion("Fireball"), i * 32, 0, 32, 32);
        	region.flip(true, false);
            frames.add(region);
        }
        fireFlipAnimation = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();
        
        setToDestroy = false;
		destroyed = false;
		setToDestroyWithEnemy = false;
		
        //defineFireBall();
		
		
        
    }

    public void defineFireBall(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(fireRight ? getX() + 12 /StarterJourney.PPM : getX() - 12 /StarterJourney.PPM, getY() + 0.1f);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / StarterJourney.PPM);
        fdef.filter.categoryBits = StarterJourney.FIREBALL_BIT;
        fdef.filter.maskBits = StarterJourney.GROUND_BIT |
                StarterJourney.ENEMY_BIT;

        fdef.shape = shape;
        fdef.friction = 0;
        b2body.setGravityScale(0);
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 0));
    }

    public void update(float dt){
        stateTime += dt;
        
        if(fireRight) {
        	setPosition(getX() + 0.012f, getY());
        	setRegion(fireAnimation.getKeyFrame(stateTime, true));
        }
        else {
        	setPosition(getX() - 0.012f, getY());
        	setRegion(fireFlipAnimation.getKeyFrame(stateTime, true));
        }
        //setRegion(region);

        if((stateTime > 3 || setToDestroy || setToDestroyWithEnemy) && !destroyed) {
        	exploTime += dt;
        	
        	setRegion(explosion.getKeyFrame(exploTime));
        	setPosition(enemyPositionX, enemyPositionY);

        	if(explosion.isAnimationFinished(exploTime)) {
            	destroyed = true;
    		}
//        	if(setToDestroyWithEnemy) {
//        		if(world.getBodyCount() > 10)
//        			world.destroyBody(b2body);
//        		setPosition(getX(), getY());
//        		if(explosion.isAnimationFinished(exploTime)) {
//                	destroyed = true;
//        		}
//        	} else {
//        		if(explosion.isAnimationFinished(exploTime)) {
//                	world.destroyBody(b2body);
//                	destroyed = true;
//                }
//        	}
        }
//        if(b2body.getLinearVelocity().y > 2f)
//            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 2f);
//        if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0))
//            setToDestroy();
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public void setToDestroyWithEnemy() {
    	setToDestroyWithEnemy = true;
    }
    
    public boolean isDestroyed(){
        return destroyed;
    }

	public void setEnemyPosition(float enemyPositionX, float enemyPositionY) {
		this.enemyPositionX = enemyPositionX;
		this.enemyPositionY = enemyPositionY;
	}

	public boolean isFireRight() {
		return fireRight;
	}

}
