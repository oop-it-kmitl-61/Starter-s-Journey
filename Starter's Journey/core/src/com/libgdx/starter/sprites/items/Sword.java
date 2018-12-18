package com.libgdx.starter.sprites.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Player;

public class Sword extends Items {

	public Sword(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		setRegion(screen.getAtlas().findRegion("weapon"), 48, 0, 16, 16);
		velocity = new Vector2(0.7f, 0);
	}

	@Override
	public void defineItem() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / StarterJourney.PPM);
		fdef.filter.categoryBits = StarterJourney.ITEM_BIT;
		fdef.filter.maskBits = StarterJourney.PLAYER_BIT |
				StarterJourney.OBJECT_BIT |
				StarterJourney.GROUND_BIT |
				StarterJourney.CRYSTALS_BIT;

		fdef.shape = shape;
		body.createFixture(fdef).setUserData(this);
	}

	@Override
	public void use(Player player) {
		destroy();
		player.grow();
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
		velocity.y = body.getLinearVelocity().y;
		body.setLinearVelocity(velocity);
	}

}
