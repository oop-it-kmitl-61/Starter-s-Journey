package com.libgdx.starter.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.scenes.Hud;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.enemies.Enemy;
import com.libgdx.starter.sprites.enemies.Turtle;
import com.libgdx.starter.sprites.items.Items;
import com.libgdx.starter.sprites.items.Mana;
import com.libgdx.starter.sprites.items.Potion;

public class Player extends Sprite {

	public enum State {FALLING, JUMPING, STANDING, RUNNING, SLIDING, GROWING, DEAD, ATTACK1, ATTACKFIREBALL, ENEMYHIT, HANDATTACK};
	public State currentState;
	public State previousState;
	public World world;
	public Body b2body;
	private PlayScreen screen;
	
	//private TextureRegion playerStand;
	private Animation<TextureRegion> playerRun;
	private Animation<TextureRegion> playerJump;
	private Animation<TextureRegion> playerDie;
	private Animation<TextureRegion> playerSlide;
	private Animation<TextureRegion> playerIdle;
	private Animation<TextureRegion> playerHitedEnemy;
	private Animation<TextureRegion> playerHandAttack;
	
	//private Animation<TextureRegion> playerAttack;
	private Animation<TextureRegion> playerToSword;
	private Animation<TextureRegion> playerSwordIdle;
	private Animation<TextureRegion> playerSwordAttack;
	private Animation<TextureRegion> playerSwordDie;
	private Animation<TextureRegion> playerSwordRun;
	private Animation<TextureRegion> playerSwordJump;
	public static Animation<TextureRegion> playerFireBallAttack;
	
	private float stateTimer;
	private boolean runningRight;
	private boolean playerIsSword;
	private boolean swordAnimation;
	private boolean timeToDefineSwordPlayer;
	private boolean timeToRedefinePlayer;
	private boolean playerIsDead;
	private boolean enemyHitPlayer;
	
	private boolean attack;
	private int countTypedKeyAttack;
	
	public Array<FireBall> fireballs;
	public FireBall currentFireBall;
	public int numFireBall;
	
	Texture blank;
	Texture manaBar;
	float maxMana = 3;
	float currentMana = 0;
	
	public Player(PlayScreen screen) {
		this.screen = screen;
		this.world = screen.getWorld();
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		runningRight = true;
		attack = false;
		enemyHitPlayer = false;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		//PLayer State : Normal
		for(int i = 0; i < 4; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), i * 50, 0*37, 50, 37));
		playerIdle = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 1; i < 7; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), i * 50, 1*37, 50, 37));
		playerRun = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 1; i < 4; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), i * 50, 2*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 1 * 50, 3*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 2 * 50, 3*37, 50, 37));
		playerJump = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 3; i < 7; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), i * 50, 3*37, 50, 37));
		playerSlide = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 4; i < 7; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-hand-combat"), i * 50, 4*37, 50, 37));
		for(int i = 0; i < 4; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-hand-combat"), i * 50, 5*37, 50, 37));
		playerDie = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 0; i < 4; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 2*50, 10*37, 50, 37));
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 3*50, 0*37, 50, 37));
		}
		playerHitedEnemy = new Animation<TextureRegion>(0.2f, frames);
		frames.clear();
		
		for(int i = 0; i < 4; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-hand-combat"), i*50, 0*37, 50, 37));
		}
		playerHandAttack = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		//Player State : Change to Sword
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 3*50, 5*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 3*50, 0*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 3*50, 5*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 3*50, 0*37, 50, 37));
		playerToSword = new Animation<TextureRegion>(0.2f, frames);
		frames.clear();
		
		//PLayer State : Sword
		for(int i = 3; i < 7; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), i * 50, 5*37, 50, 37));
		playerSwordIdle = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 0; i < 5; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), i * 50, 6*37, 50, 37));
		playerSwordAttack = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 1; i < 7; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-sword"), i * 50, 1*37, 50, 37));
		playerSwordRun = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 1; i < 4; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-sword"), i * 50, 2*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-sword"), 1 * 50, 3*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-sword"), 2 * 50, 3*37, 50, 37));
		playerSwordJump = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 0; i < 6; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-hand-combat"), i * 50, 9*37, 50, 37));
		playerSwordDie = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for(int i = 1; i < 7; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-sword"), i * 50, 12*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-sword"), 0 * 50, 13*37, 50, 37));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("adventurer-sword"), 1 * 50, 13*37, 50, 37));
		playerFireBallAttack = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		//playerStand = new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 0, 37, 50, 37);
		//playerSwordStand = new TextureRegion(screen.getAtlas().findRegion("adventurer-normal"), 3*50, 5*37, 50, 37);
		
		definePlayer();
		
		setBounds(0, 0, 30 / StarterJourney.PPM, 22.2f / StarterJourney.PPM);
		setRegion(playerIdle.getKeyFrame(stateTimer, true));
		
		fireballs = new Array<FireBall>();
		
		blank = new Texture("manabg.png");
		manaBar = new Texture("manafg.png");
	}
	
	public void handleInput() {
		
		if(isSword() && countTypedKeyAttack == 0) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
				StarterJourney.manager.get("audio/move/attack/swing.wav", Sound.class).play();
				showAttack();
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.X) && currentMana == 3)
				showAttack();
		}
		else if(Hud.extraLives != 0 && !enemyHitPlayer && countTypedKeyAttack == 0) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
				StarterJourney.manager.get("audio/move/attack/punch.wav", Sound.class).play();
				showAttack();
			}
		}
	}
	
	public void update(float dt) {
		//update our sprite to correspond with the position of our Box2D body
		setPosition(b2body.getPosition().x  - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 4 / StarterJourney.PPM);
		
		//update player attack
		handleInput();
		
		if(attack) {
			stateTimer += dt;
			setRegion(getAttackFrame(dt));
			if(playerSwordAttack.isAnimationFinished(stateTimer)) {
				attack = false;
				countTypedKeyAttack = 0;
			}
		}
		else
			//update sprite with the correct frame depending on player current action
			setRegion(getFrame(dt));

		if(timeToDefineSwordPlayer)
			defineSwordPlayer();
		if(timeToRedefinePlayer)
			redefinePlayer();
		
		for(FireBall ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
		
		if(b2body.getLinearVelocity().y == 0)
			screen.setNumFoot();
		
		//player died
		if(Hud.extraLives == 0) {
			Filter filter = new Filter();
			filter.maskBits = StarterJourney.GROUND_BIT | StarterJourney.UNDER_BIT;
			for(Fixture fixture : b2body.getFixtureList())
				fixture.setFilterData(filter);
			
			playerIsDead = true;
			filter = new Filter();
			filter.maskBits = StarterJourney.GROUND_BIT;
			for(Fixture fixture : b2body.getFixtureList())
				fixture.setFilterData(filter);
			b2body.applyLinearImpulse(new Vector2(0, 0), b2body.getWorldCenter(), true);
		}
	}
	
	public void showAttack() {
		this.attack = true;
		stateTimer = 0;
		countTypedKeyAttack = 1;
		if(Gdx.input.isKeyJustPressed(Input.Keys.Z))
			currentState = State.ATTACK1;
		if(Gdx.input.isKeyJustPressed(Input.Keys.X))
			currentState = State.ATTACKFIREBALL;
		if(Gdx.input.isKeyJustPressed(Input.Keys.C))
			currentState = State.HANDATTACK;
	}
	
	public TextureRegion getAttackFrame(float dt) {
		
		TextureRegion region;
		switch(currentState) {
			case ATTACK1:
				region = playerSwordAttack.getKeyFrame(stateTimer);
				break;
			case ATTACKFIREBALL:
				region = playerFireBallAttack.getKeyFrame(stateTimer);
				break;
			case HANDATTACK:
				region = playerHandAttack.getKeyFrame(stateTimer);
				break;
			default:
				region = playerIsSword ? playerSwordIdle.getKeyFrame(stateTimer, true) : playerIdle.getKeyFrame(stateTimer, true);
				break;
		}
		
		if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		}
		else if((b2body.getLinearVelocity().x> 0 || runningRight) && region.isFlipX()) {
			region.flip(true, false);
			runningRight = true;
		}
		
		return region;
	}
	
	public TextureRegion getFrame(float dt) {
		currentState = getState();
		
		TextureRegion region;
		switch(currentState) {
			case DEAD:
				region = playerIsSword ? playerSwordDie.getKeyFrame(stateTimer) : playerDie.getKeyFrame(stateTimer);
				break;
			case GROWING:
				region = playerToSword.getKeyFrame(stateTimer);
				if(playerToSword.isAnimationFinished(stateTimer))
					swordAnimation = false;
				break;
			case ENEMYHIT:
				region = playerHitedEnemy.getKeyFrame(stateTimer);
				if(playerHitedEnemy.isAnimationFinished(stateTimer)) {
					enemyHitPlayer = false;
					timeToRedefinePlayer = true;
				}
				break;
			case JUMPING:
				region = playerIsSword ? playerSwordJump.getKeyFrame(stateTimer, true) : playerJump.getKeyFrame(stateTimer);
				break;
			case RUNNING:
				region = playerIsSword ? playerSwordRun.getKeyFrame(stateTimer, true) : playerRun.getKeyFrame(stateTimer, true);
				break;
			case SLIDING:
				region = playerSlide.getKeyFrame(stateTimer);
				break;
			default:
				region = playerIsSword ? playerSwordIdle.getKeyFrame(stateTimer, true) : playerIdle.getKeyFrame(stateTimer, true);
				break;
		}
		
		if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		}
		else if((b2body.getLinearVelocity().x> 0 || runningRight) && region.isFlipX()) {
			region.flip(true, false);
			runningRight = true;
		}
		
		stateTimer = currentState == previousState ? stateTimer + dt : 0;
		previousState = currentState;
		return region;
	}
	
	public State getState() {
		if(playerIsDead)
			return State.DEAD;
		else if(enemyHitPlayer)
			return State.ENEMYHIT;
		else if(swordAnimation)
			return State.GROWING;
		else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
			return State.JUMPING;
		else if(b2body.getLinearVelocity().y < 0)
			return State.FALLING;
		else if(b2body.getLinearVelocity().x != 0)
			return State.RUNNING;
		else
			return State.STANDING;
	}
	
	
	public void grow() {
		if( !isSword() ) {
			swordAnimation = true;
			playerIsSword = true;
			timeToDefineSwordPlayer = true;
			setBounds(getX(), getY(), getWidth(), getHeight());
			StarterJourney.manager.get("audio/sounds/powerup.wav", Sound.class).play();
		}
	}
	
	public void collect(Items item) {
		if(item instanceof Potion)
			Hud.addLives(1);
		else if(item instanceof Mana)
			if(currentMana != 3)
				currentMana += 1;
	}
	
	public void coin() {
		StarterJourney.manager.get("audio/sounds/coin.wav", Sound.class).play();
		Hud.addScore(200);
	}
	
	public boolean isDead() {
		return playerIsDead;
	}
	
	public float getStateTimer() {
		return stateTimer;
	}
	
	public boolean isSword() {
		return playerIsSword;
	}
	
	public void hit(Enemy enemy) {
		if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
			((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
		} else {
			if(currentState == State.ATTACK1)
				return;
			if(playerIsSword) {
				playerIsSword = false;
				timeToRedefinePlayer = true;
				setBounds(getX(), getY(), getWidth(), getHeight());
				StarterJourney.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
			}
			else {
				Hud.setLives(1);
				enemyHitPlayer = true;
				if(Hud.extraLives == 0) {
					if(PlayScreen.numMap == 0)
						StarterJourney.manager.get("audio/background_OST/industrialized_scene/Dirk Valentine - Menu Theme.ogg", Music.class).stop();
			        else if(PlayScreen.numMap == 1)
			        	StarterJourney.manager.get("audio/background_OST/Cave_scene/Alone In The Cave.ogg", Music.class).stop();
			        else
			        	StarterJourney.manager.get("audio/background_OST/Jungle_scene/Dark Cave Music - Trickster Imps.ogg", Music.class).stop();
					//StarterJourney.manager.get("audio/music/mario_music.ogg", Music.class).stop();
					StarterJourney.manager.get("audio/move/death/1yell6.wav", Sound.class).play();
				}
			}
		}
	}
	
	public void setRedifine() {
		timeToDefineSwordPlayer = true;
	}
	
	public void redefinePlayer() {
		Vector2 position = b2body.getPosition();
		world.destroyBody(b2body);
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / StarterJourney.PPM);
		fdef.filter.categoryBits = StarterJourney.PLAYER_BIT;
		fdef.filter.maskBits = StarterJourney.GROUND_BIT |
				StarterJourney.CRYSTALS_BIT |
				StarterJourney.ENEMY_BIT |
				StarterJourney.ENEMY_HEAD_BIT |
				StarterJourney.ITEM_BIT | 
				StarterJourney.UNDER_BIT |
				StarterJourney.ENDPOINT_BIT |
				StarterJourney.FIREGROUND_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2 / StarterJourney.PPM, 8 / StarterJourney.PPM), new Vector2(2 / StarterJourney.PPM, 8 / StarterJourney.PPM));
		fdef.filter.categoryBits = StarterJourney.PLAYER_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData(this);
		
		timeToRedefinePlayer = false;
	}
	
	public void defineSwordPlayer() {
		Vector2 currentPosition = b2body.getPosition();
		world.destroyBody(b2body);
		
		BodyDef bdef = new BodyDef();
		bdef.position.set(currentPosition);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / StarterJourney.PPM);
		fdef.filter.categoryBits = StarterJourney.PLAYER_BIT;
		fdef.filter.maskBits = StarterJourney.GROUND_BIT |
				StarterJourney.CRYSTALS_BIT |
				StarterJourney.ENEMY_BIT |
				StarterJourney.ENEMY_HEAD_BIT |
				StarterJourney.ITEM_BIT | 
				StarterJourney.UNDER_BIT |
				StarterJourney.ENDPOINT_BIT |
				StarterJourney.FIREGROUND_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2 / StarterJourney.PPM, 8 / StarterJourney.PPM), new Vector2(2 / StarterJourney.PPM, 8 / StarterJourney.PPM));
		fdef.filter.categoryBits = StarterJourney.PLAYER_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData(this);
		timeToDefineSwordPlayer = false;
	}
	
	public void definePlayer() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(320 / StarterJourney.PPM, 128 / StarterJourney.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / StarterJourney.PPM);
		fdef.filter.categoryBits = StarterJourney.PLAYER_BIT;
		fdef.filter.maskBits = StarterJourney.GROUND_BIT |
				StarterJourney.CRYSTALS_BIT |
				StarterJourney.ENEMY_BIT |
				StarterJourney.ENEMY_HEAD_BIT |
				StarterJourney.ITEM_BIT | 
				StarterJourney.UNDER_BIT |
				StarterJourney.ENDPOINT_BIT |
				StarterJourney.FIREGROUND_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2 / StarterJourney.PPM, 8 / StarterJourney.PPM), new Vector2(2 / StarterJourney.PPM, 8 / StarterJourney.PPM));
		fdef.filter.categoryBits = StarterJourney.PLAYER_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData(this);
	}
	
	public void fire(){
		currentFireBall = new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false);
        fireballs.add(currentFireBall);
        numFireBall = 1;
        currentMana = 0;
    }
	
	public float getCurrentMana() {
		return currentMana;
	}
	
	public FireBall getCurrentFireBall() {
		return currentFireBall;
	}
	
	public int getNumFireBall() {
		return numFireBall;
	}

	public void setNumFireBall(int numFireBall) {
		this.numFireBall = numFireBall;
	}

	public void nextMap() {
		if(screen.getBossDied())
			screen.nextScreen();
	}
	
	public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
        batch.draw(blank, getX(), getY() + 0.2f, getWidth(), 5 / StarterJourney.PPM);
		batch.draw(manaBar, getX(), getY() + 0.2f, getWidth() * (currentMana / maxMana), 5 / StarterJourney.PPM);
    }

}
