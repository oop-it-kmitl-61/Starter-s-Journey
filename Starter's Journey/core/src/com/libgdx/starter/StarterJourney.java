package com.libgdx.starter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.libgdx.starter.screens.GameMenu;
import com.libgdx.starter.screens.LevelSelect;
import com.libgdx.starter.screens.PlayScreen;

public class StarterJourney extends Game {
	public static final int V_SCALE = 2;
	public static final int V_WIDTH = 560;
	public static final int V_HEIGHT = 420;
	public static final float PPM = 100;
	
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short CRYSTALS_BIT = 4;
	public static final short OBJECT_BIT = 8;
	public static final short ENEMY_BIT = 16;
	public static final short ENEMY_HEAD_BIT = 32;
	public static final short ITEM_BIT = 64;
	public static final short PLAYER_HEAD_BIT = 128;
	public static final short UNDER_BIT = 256;
	public static final short FIREBALL_BIT = 512;
	public static final short ENDPOINT_BIT = 1024;
	public static final short FIREGROUND_BIT = 2048;
	
	public SpriteBatch batch;
	
	public static AssetManager manager;
	
    public static final int MENU = 83774392;
    public static final int PLAY = 388031654;
    public static final int LEVEL_SELECT = -9238732;
    
    public int state = MENU;
    public Screen screen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/background_OST/Menu/1 - Piotr Musial - This War of Mine.ogg", Music.class);
		manager.load("audio/background_OST/industrialized_scene/Dirk Valentine - Menu Theme.ogg", Music.class);
		manager.load("audio/background_OST/Cave_scene/Alone In The Cave.ogg", Music.class);
		manager.load("audio/background_OST/Jungle_scene/Dark Cave Music - Trickster Imps.ogg", Music.class);
		
		manager.load("audio/move/death/1yell6.wav", Sound.class);
		manager.load("audio/move/jump/jump.wav", Sound.class);
		manager.load("audio/move/attack/punch.wav", Sound.class);
		manager.load("audio/move/attack/swing.wav", Sound.class);
		
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/sounds/powerdown.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.load("audio/sounds/mariodie.wav", Sound.class);
		manager.finishLoading();
		
		setScreen(getState());
	}

	public void setState(int state) {
		if (state == MENU)
			screen = new GameMenu(this);
        if (state == PLAY) 
        	screen = new PlayScreen(this, PlayScreen.numMap);
        if (state == LEVEL_SELECT) 
        	screen = new LevelSelect(this);
	}
	
	public Screen getState() {
		if(screen == null) {
			screen = new GameMenu(this);
			return screen;
		}
		else
			return screen;
	}
	
	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		manager.dispose();
		batch.dispose();
		screen.dispose();
	}
	
}
