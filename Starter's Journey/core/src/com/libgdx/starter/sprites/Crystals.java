package com.libgdx.starter.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.scenes.Hud;
import com.libgdx.starter.screens.PlayScreen;
import com.libgdx.starter.sprites.items.ItemDef;
import com.libgdx.starter.sprites.items.Sword;

public class Crystals extends InteractiveTileObject {
	private static TiledMapTileSet tileSet;
	private final int BLANK_COIN = 2934;
	public Crystals(PlayScreen screen, MapObject object) {
		super(screen, object);
		tileSet = map.getTileSets().getTileSet("gutter");
		fixture.setUserData(this);
		setCategoryFilter(StarterJourney.CRYSTALS_BIT);
	}

	@Override
	public void onHeightHit(Player player) {
		Gdx.app.log("Coin", getCell().getTile().getId()+"");
		if(getCell().getTile().getId() == BLANK_COIN)
			StarterJourney.manager.get("audio/sounds/bump.wav", Sound.class).play();
		else {
			if(object.getProperties().containsKey("sword")) {
				screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / StarterJourney.PPM), 
						Sword.class));
				StarterJourney.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
			}
			else
				StarterJourney.manager.get("audio/sounds/coin.wav", Sound.class).play();
			Hud.addScore(200);
		}
		getCell().setTile(tileSet.getTile(BLANK_COIN));
		
		
	}
}
