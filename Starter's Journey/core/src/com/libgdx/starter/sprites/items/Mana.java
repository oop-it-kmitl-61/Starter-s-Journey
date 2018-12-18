package com.libgdx.starter.sprites.items;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Player;

public class Mana extends Items {

	public Mana(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		setRegion(screen.getAtlas().findRegion("Bonus_Items"), 6*32, 0, 32, 32);
	}

	@Override
	public void defineItem() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY()-0.1f);
		bdef.type = BodyDef.BodyType.StaticBody;
		body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / StarterJourney.PPM);
		fdef.filter.categoryBits = StarterJourney.ITEM_BIT;
		fdef.filter.maskBits = StarterJourney.PLAYER_BIT |
				StarterJourney.OBJECT_BIT |
				StarterJourney.GROUND_BIT |
				StarterJourney.CRYSTALS_BIT;

		fdef.isSensor = true;
		fdef.shape = shape;
		body.createFixture(fdef).setUserData(this);
	}

	@Override
	public void use(Player player) {
		destroy();
		player.collect(this);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
	}
	
}
