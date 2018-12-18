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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Player;

public class Turtle extends Enemy {

	public static final int KICK_LEFT_SPEED = -2;
	public static final int KICK_RIGHT_SPEED = 2;
	public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD};
	public State currentState;
	public State previousState;
	private float stateTime;
	private Animation<TextureRegion> walkAnimation;
	private Array<TextureRegion> frames;
	private TextureRegion shell;
	private float deadRotationDegrees;
	private boolean destroyed;
	
	public Turtle(PlayScreen screen, float x, float y, MapObject object) {
		super(screen, x, y, object);
		frames= new Array<TextureRegion>();
		frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
		shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
		walkAnimation = new Animation<TextureRegion>(0.2f, frames);
		currentState = previousState = State.WALKING;
		deadRotationDegrees = 0;
		
		setBounds(getX(), getY(), 16 / StarterJourney.PPM, 24 / StarterJourney.PPM);
	}
	
	public TextureRegion getFrame(float dt) {
		TextureRegion region;
		
		switch(currentState) {
		case MOVING_SHELL:
			region = shell;
			break;
		case STANDING_SHELL:
		case WALKING:
		default:
			region = walkAnimation.getKeyFrame(stateTime, true);
			break;
		}
		
		if(velocity.x > 0 && region.isFlipX() == false) {
			region.flip(true, false);
		}
		if(velocity.x < 0 && region.isFlipX() == true) {
			region.flip(true, false);
		}
		
		stateTime = currentState == previousState ? stateTime + dt : 0;
		previousState = currentState;
		return region;
	}

	@Override
	public void update(float dt) {
		stateTime += dt;
		setRegion(getFrame(dt));
		if(currentState == State.STANDING_SHELL && stateTime > 5) {
			currentState = State.WALKING;
			velocity.x = 0.5f;
		}
		
		setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 6 / StarterJourney.PPM);
		
		if(currentState == State.DEAD) {
			deadRotationDegrees += 3;
			rotate(deadRotationDegrees);
			if(stateTime > 5 &&  !destroyed) {
				world.destroyBody(b2body);
				destroyed = true;
			}
		}
		else
			b2body.setLinearVelocity(velocity);
		
		if(setToDestroy && !destroyed)  {
			world.destroyBody(b2body);
			destroyed = true;
			setRegion(new TextureRegion(screen.getAtlas().findRegion("turtle"), 2*16 , 0, 16, 24));
			stateTime = 0;
		}
	}

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
		
		//Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-6, 10).scl(1 / StarterJourney.PPM);
        vertice[1] = new Vector2(6, 10).scl(1 / StarterJourney.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / StarterJourney.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / StarterJourney.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 1.5f;
        fdef.filter.categoryBits = StarterJourney.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
	}
	
	@Override
	public void onEnemyHit(Enemy enemy) {
		if(enemy instanceof Turtle) {
			if(((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
				killed();
			}
			else if(currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING)
				return;
			else
				reverseVelocity(true, false);
		}
		else if(currentState != State.MOVING_SHELL)
			reverseVelocity(true, false);
	}
	
	@Override
	public void hitOnHead(Player player) {
		if(currentState != State.STANDING_SHELL) {
			currentState = State.STANDING_SHELL;
			velocity.x = 0;
		} else {
			kick(player.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
		}
	}
	
	public void draw(Batch batch) {
		if(!destroyed)
			super.draw(batch);
	}
	
	public void kick(int speed) {
		velocity.x = speed;
		currentState = State.MOVING_SHELL;
	}

	public State getCurrentState() {
		return currentState;
	}
	
	public void killed() {
		currentState = State.DEAD;
		Filter filter = new Filter();
		filter.maskBits = StarterJourney.NOTHING_BIT;
		for(Fixture fixture : b2body.getFixtureList())
			fixture.setFilterData(filter);
		b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
	}

}
