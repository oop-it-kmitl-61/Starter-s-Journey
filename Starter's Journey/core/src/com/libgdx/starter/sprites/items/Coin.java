package com.libgdx.starter.sprites.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.Player;

public class Coin extends Items {

	private float stateTime;
	public Animation<TextureRegion> crystalAnimation;
	private Array<TextureRegion> frames;
	
	public Coin(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		frames = new Array<TextureRegion>();
		for(int i = 0; i < 28; i++)
			frames.add(new TextureRegion(screen.getAtlas().findRegion("crystal"), i * 16, 0, 16, 16));
		crystalAnimation = new Animation<TextureRegion>(0.2f, frames);
		stateTime = 0;
	}

	@Override
	public void defineItem() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY()-0.1f);
		bdef.type = BodyDef.BodyType.StaticBody;
		body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(8 / StarterJourney.PPM);
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
		player.coin();
	}

	public void update(float dt) {
		super.update(dt);
		stateTime += dt;
		setRegion(crystalAnimation.getKeyFrame(stateTime, true));
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
	}
	
}
