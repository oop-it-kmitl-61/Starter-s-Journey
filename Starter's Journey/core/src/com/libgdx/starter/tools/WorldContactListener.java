package com.libgdx.starter.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.sprites.FireBall;
import com.libgdx.starter.sprites.InteractiveTileObject;
import com.libgdx.starter.sprites.Player;
import com.libgdx.starter.sprites.enemies.Boss;
import com.libgdx.starter.sprites.enemies.Enemy;
import com.libgdx.starter.sprites.items.Items;

public class WorldContactListener implements ContactListener {

	private int numFootContacts, numDeadGround, numBossCt, numFireGround;
	StarterJourney game = new StarterJourney();
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
		
		switch (cDef) {
			case StarterJourney.PLAYER_BIT | StarterJourney.GROUND_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT)
					numFootContacts++;
				else
					numFootContacts++;
				break;
			case StarterJourney.PLAYER_BIT | StarterJourney.UNDER_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT)
					numDeadGround++;
				else
					numDeadGround++;
				break;
			case StarterJourney.PLAYER_BIT | StarterJourney.FIREGROUND_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT)
					numFireGround++;
                else
                	numFireGround++;
                break;
			case StarterJourney.PLAYER_HEAD_BIT | StarterJourney.CRYSTALS_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_HEAD_BIT)
					((InteractiveTileObject) fixB.getUserData()).onHeightHit((Player) fixA.getUserData());
				else
					((InteractiveTileObject) fixA.getUserData()).onHeightHit((Player) fixB.getUserData());
				break;
			case StarterJourney.ENEMY_HEAD_BIT | StarterJourney.PLAYER_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.ENEMY_HEAD_BIT)
					((Enemy)fixA.getUserData()).hitOnHead((Player) fixB.getUserData());
				else
					((Enemy)fixB.getUserData()).hitOnHead((Player) fixA.getUserData());
				break;
			case StarterJourney.ENEMY_BIT | StarterJourney.OBJECT_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.ENEMY_BIT)
					if(fixA.getUserData() instanceof Boss) {
						if(numBossCt == 0) {
							((Enemy)fixA.getUserData()).reverseVelocity(false, true);
							numBossCt = 1;
						}
						else {
							((Enemy)fixA.getUserData()).reverseVelocity(true, false);
							numBossCt = 0;
						}
					}
					else
						((Enemy)fixA.getUserData()).reverseVelocity(true, false);
				else
					if(fixB.getUserData() instanceof Boss) {
						if(numBossCt == 0) {
							((Enemy)fixB.getUserData()).reverseVelocity(false, true);
							numBossCt = 1;
						}
						else {
							((Enemy)fixB.getUserData()).reverseVelocity(true, false);
							numBossCt = 0;
						}
					}
					else
						((Enemy)fixB.getUserData()).reverseVelocity(true, false);
				break;
			case StarterJourney.PLAYER_BIT | StarterJourney.ENEMY_BIT:
//				if(((Player) fixA.getUserData()).currentState == Player.State.ATTACK1 || ((Player) fixB.getUserData()).currentState == Player.State.ATTACK1) {
//					if(fixA.getFilterData().categoryBits == StarterJourney.ENEMY_BIT)
//						((Enemy)fixA.getUserData()).onPlayerSword((Player) fixB.getUserData());
//					else
//						((Enemy)fixB.getUserData()).onPlayerSword((Player) fixA.getUserData());
//				}
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT) {
					if(fixB.getUserData() instanceof Boss) {
						if(numBossCt == 0) {
							((Enemy)fixB.getUserData()).reverseVelocity(false, true);
							numBossCt = 1;
						}
						else {
							((Enemy)fixB.getUserData()).reverseVelocity(true, false);
							numBossCt = 0;
						}
					}
					else if(fixB.getUserData() instanceof Enemy)
						((Enemy)fixB.getUserData()).reverseVelocity(true, false);
					
					if(((Player) fixA.getUserData()).currentState == Player.State.HANDATTACK
						| ((Player) fixA.getUserData()).currentState == Player.State.ATTACK1)
						((Enemy)fixB.getUserData()).onPlayerAtk((Player) fixA.getUserData());
					else
						((Player) fixA.getUserData()).hit((Enemy)fixB.getUserData());
				}
				else {
					if(fixA.getUserData() instanceof Boss) {
						if(numBossCt == 0) {
							((Enemy)fixA.getUserData()).reverseVelocity(false, true);
							numBossCt = 1;
						}
						else {
							((Enemy)fixA.getUserData()).reverseVelocity(true, false);
							numBossCt = 0;
						}
					}
					else if(fixA.getUserData() instanceof Enemy)
						((Enemy)fixA.getUserData()).reverseVelocity(true, false);
					
					if(((Player) fixB.getUserData()).currentState == Player.State.HANDATTACK
						| ((Player) fixB.getUserData()).currentState == Player.State.ATTACK1)
						((Enemy)fixA.getUserData()).onPlayerAtk((Player) fixB.getUserData());
					else
						((Player) fixB.getUserData()).hit((Enemy)fixA.getUserData());
				}
					
				break;
			case StarterJourney.ENEMY_BIT | StarterJourney.ENEMY_BIT:
//				((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
//				((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());
				((Enemy)fixA.getUserData()).reverseVelocity(true, false);
				((Enemy)fixB.getUserData()).reverseVelocity(true, false);
				break;
			case StarterJourney.ITEM_BIT | StarterJourney.OBJECT_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.ITEM_BIT)
					((Items)fixA.getUserData()).reverseVelocity(true, false);
				else
					((Items)fixB.getUserData()).reverseVelocity(true, false);
				break;
			case StarterJourney.ITEM_BIT | StarterJourney.PLAYER_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.ITEM_BIT)
					((Items)fixA.getUserData()).use((Player) fixB.getUserData());
				else
					((Items)fixB.getUserData()).use((Player) fixA.getUserData());
				break;
			case StarterJourney.FIREBALL_BIT | StarterJourney.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == StarterJourney.FIREBALL_BIT)
                    ((FireBall) fixA.getUserData()).setToDestroy();
                else
                    ((FireBall) fixB.getUserData()).setToDestroy();
                break;
			case StarterJourney.FIREBALL_BIT | StarterJourney.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == StarterJourney.FIREBALL_BIT)
                    ((FireBall) fixA.getUserData()).setToDestroy();
                else
                    ((FireBall) fixB.getUserData()).setToDestroy();
                break;
			case StarterJourney.FIREBALL_BIT | StarterJourney.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == StarterJourney.FIREBALL_BIT)
                    ((FireBall) fixA.getUserData()).setToDestroyWithEnemy();
                else
                    ((FireBall) fixB.getUserData()).setToDestroyWithEnemy();
                if(fixA.getFilterData().categoryBits == StarterJourney.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).onFireBallHit();
                else
                    ((Enemy) fixB.getUserData()).onFireBallHit();
                break;
			case StarterJourney.PLAYER_BIT | StarterJourney.ENDPOINT_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT)
                    ((Player) fixA.getUserData()).nextMap();
                else
                    ((Player) fixB.getUserData()).nextMap();
                break;
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
		
		switch (cDef) {
			case StarterJourney.PLAYER_BIT | StarterJourney.GROUND_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT)
					numFootContacts--;
				else
					numFootContacts--;
				break;
			case StarterJourney.PLAYER_BIT | StarterJourney.UNDER_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT)
					numDeadGround--;
				else
					numDeadGround--;
				break;
			case StarterJourney.PLAYER_BIT | StarterJourney.FIREGROUND_BIT:
				if(fixA.getFilterData().categoryBits == StarterJourney.PLAYER_BIT)
					numFireGround--;
                else
                	numFireGround--;
                break;
		}
	}
	
	public void setNumFoot() {
		numFootContacts = 1;
	}
	
	public void getNumFoot() {
		System.out.println(numFootContacts);
	}
	
	public boolean isPlayerOnFireGround() {
		if (numFireGround > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isPlayerOnGround() { 
		if (numFootContacts > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isPlayerOnDeadGround() { 
		if (numDeadGround > 0)
			return true;
		return false;
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
