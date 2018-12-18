package com.libgdx.starter.screens;

import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.libgdx.starter.StarterJourney;
import com.libgdx.starter.scenes.Hud;
import com.libgdx.starter.sprites.Player;
import com.libgdx.starter.sprites.enemies.Enemy;
import com.libgdx.starter.sprites.items.ItemDef;
import com.libgdx.starter.sprites.items.Items;
import com.libgdx.starter.sprites.items.Mana;
import com.libgdx.starter.sprites.items.Potion;
import com.libgdx.starter.sprites.items.Sword;
import com.libgdx.starter.tools.B2WorldCreator;
import com.libgdx.starter.tools.SaveMap;
import com.libgdx.starter.tools.WorldContactListener;

public class PlayScreen implements Screen {
	//Reference to our Game, used to set Screens
	private StarterJourney game;
	private TextureAtlas atlas;
	
	private OrthographicCamera gameCam;
	private Viewport gamePort;
	private Hud hud;
	
	//Tiled map variables
	public static int numMap;
	public static int totalMap = 3;
	private TmxMapLoader maploader;
	private TiledMap[] map = new TiledMap[totalMap];
	private OrthogonalTiledMapRenderer renderer;
	
	//Box2d variables
	private World world;
	private Box2DDebugRenderer b2dr;
	private B2WorldCreator creator;
	
	//Sprite
	private Player player;
	//private Enemies enemies;
	
	private Music music;
	
	private Array<Items> items;
	private LinkedBlockingQueue<ItemDef> itemsToSpawn;
	
	private WorldContactListener cl;
	
	//game over layer
	private Image semiTL;
	private Texture[] playAgainButton = new Texture[2];
	private Texture[] menuButton = new Texture[2];
	private Texture background;
	private Image bgOver;
	private Image[] playOver = new Image[2];
	private Image[] menuOver = new Image[2];
	
	private Filter filter;
	
	boolean bossDied;
	
	public PlayScreen(StarterJourney game, int numMap) {
		PlayScreen.numMap = numMap;
		
		atlas = new TextureAtlas("characters_packer/Adventure_and_Enemies.pack");
		
		this.game = game;
		//create cam used to follow player through cam world
		gameCam = new OrthographicCamera();
		
		//create a FitViewport to maintain virtual aspect ratio despite screen
		gamePort = new FitViewport(StarterJourney.V_WIDTH / StarterJourney.PPM, StarterJourney.V_HEIGHT / StarterJourney.PPM, gameCam);
		
		//create our game HUD for scores/timers/level info
		hud = new Hud(game.batch);
		
		//'Load our map and setup our map renderer
		createTiles();
		
		//initially set our game cam to be centered correctly at the start of map
		gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
		
		//create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
		world = new World(new Vector2(0, -10), true);
		//allows for debug lines of our box2d world.
		b2dr = new Box2DDebugRenderer();
		
		creator = new B2WorldCreator(this);

		//create player in our game world
        player = new Player(this);
        
        cl = new WorldContactListener();
        world.setContactListener(cl);
        
        if(numMap == 0)
        	music = StarterJourney.manager.get("audio/background_OST/industrialized_scene/Dirk Valentine - Menu Theme.ogg", Music.class);
        else if(numMap == 1)
        	music = StarterJourney.manager.get("audio/background_OST/Cave_scene/Alone In The Cave.ogg", Music.class);
        else
        	music = StarterJourney.manager.get("audio/background_OST/Jungle_scene/Dark Cave Music - Trickster Imps.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.05f);
        music.play();
        
        items = new Array<Items>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
		
//        enemies = new Enemies(this, .64f, .64f);
        
        //game over draw transparent layer
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fillRectangle(0, 0, 1, 1);
		Texture texture1=new Texture(pixmap);
		pixmap.dispose();

		semiTL= new Image(texture1);
		semiTL.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		semiTL.getColor().a=.5f;
		
		//game over util
		playAgainButton[0] = new Texture("image/GameOver/PlayAgain.png");
		playAgainButton[1] = new Texture("image/GameOver/PlayAgain2.png");
		menuButton[0] = new Texture("image/GameOver/MainMenu.png");
		menuButton[1] = new Texture("image/GameOver/MainMenu2.png");
		background = new Texture("image/GameOver/BG.png");
		
		bgOver = new Image(background);
		playOver[0] = new Image(playAgainButton[0]);
		playOver[1] = new Image(playAgainButton[1]);
		menuOver[0] = new Image(menuButton[0]);
		menuOver[1] = new Image(menuButton[1]);
		
		bgOver.setSize(533, 400);
		playOver[0].setSize(80, 80);
		playOver[1].setSize(80, 80);
		menuOver[0].setSize(80, 80);
		menuOver[1].setSize(80, 80);
		
		bgOver.setPosition(StarterJourney.V_WIDTH / 2 - 533 / 2, StarterJourney.V_HEIGHT / 2 - 400 / 2);
		playOver[0].setPosition(StarterJourney.V_WIDTH / 2 - 100, StarterJourney.V_HEIGHT / 2 - 150);
		playOver[1].setPosition(StarterJourney.V_WIDTH / 2 - 100, StarterJourney.V_HEIGHT / 2 - 150);
		menuOver[0].setPosition(StarterJourney.V_WIDTH / 2 + 20, StarterJourney.V_HEIGHT / 2 - 150);
		menuOver[1].setPosition(StarterJourney.V_WIDTH / 2 + 20, StarterJourney.V_HEIGHT / 2 - 150);
		
		filter = new Filter(); 
		filter.maskBits = StarterJourney.GROUND_BIT;
		
		bossDied = false;
		
	}
	
	public void spawnItem(ItemDef idef) {
		itemsToSpawn.add(idef);
	}
	
	public void handleSpawningItems() {
		if(!itemsToSpawn.isEmpty()) {
			ItemDef idef = itemsToSpawn.poll();
			if(idef.type == Sword.class)
				items.add(new Sword(this, idef.position.x, idef.position.y));
			if(idef.type == Potion.class)
				items.add(new Potion(this, idef.position.x, idef.position.y));
			if(idef.type == Mana.class)
				items.add(new Mana(this, idef.position.x, idef.position.y));
			
		}
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}
	
	@Override
	public void show() {
		
	}
	
	public void setNumMap(int numMap) {
		PlayScreen.numMap = numMap;
	}

	public void createTiles() {
		maploader = new TmxMapLoader();
		map[0] = maploader.load("maps/IndustrialScene/Industrial_scene.tmx");
		map[1] = maploader.load("maps/CaveScene/CaveMap.tmx");
		map[2] = maploader.load("maps/JungleScene/JungleMap.tmx");
		renderer = new OrthogonalTiledMapRenderer(map[numMap], 1 / StarterJourney.PPM);
	}
	
	public void handleInput(float dt) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
			hud.setPreviousScore(hud.getScore());
			game.setScreen(new PlayScreen((StarterJourney) game, PlayScreen.numMap));
		}
		if(player.currentState != Player.State.DEAD) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && cl.isPlayerOnGround()) {
				StarterJourney.manager.get("audio/move/jump/jump.wav", Sound.class).play(0.2f);
				player.b2body.applyLinearImpulse(new Vector2(0, 3f), player.b2body.getWorldCenter(), true);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
				player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
				player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
			if(Gdx.input.isKeyJustPressed(Input.Keys.X) && player.isSword() && player.getCurrentMana() == 3)
                player.fire();
		}
	}
	
	public void update(float dt) {
		//handle user input first
		handleInput(dt);
		handleSpawningItems();
		
		//take 1 step in the physics simulation (60 times per second)
		world.step(1/60f, 6, 2);
		
		player.update(dt);

		//enemies.update(dt);
		for(Enemy enemy : creator.getEnemy()) {
			enemy.update(dt); 
			
			//FireBall hit Enemy
			if(player.getNumFireBall() == 1) {
				if(enemy.b2body.getPosition().y > player.getCurrentFireBall().getY() 
					&& (enemy.b2body.getPosition().y - player.getCurrentFireBall().getY()) <= 0.15f) {
//					enemy.b2body.getPosition().x <=  player.getCurrentFireBall().getX()
					if(player.getCurrentFireBall().isFireRight()) {
						if((player.getCurrentFireBall().getX() - enemy.b2body.getPosition().x) > 0
						&& player.getCurrentFireBall().getX() < enemy.b2body.getPosition().x + 0.1f) {
							enemy.onFireBallHit();
							
							player.getCurrentFireBall().setToDestroyWithEnemy();
							player.setNumFireBall(0);
							player.getCurrentFireBall().setEnemyPosition(enemy.b2body.getPosition().x, enemy.b2body.getPosition().y);
						}
					}
					else {
						if((enemy.b2body.getPosition().x - player.getCurrentFireBall().getX()) > 0
							&& enemy.b2body.getPosition().x < player.getCurrentFireBall().getX() + 0.1f) {
							enemy.onFireBallHit();
							
							player.getCurrentFireBall().setToDestroyWithEnemy();
							player.setNumFireBall(0);
							player.getCurrentFireBall().setEnemyPosition(enemy.b2body.getPosition().x, enemy.b2body.getPosition().y);
						}
					}
					
				}
			}
			
			if(enemy.getHealth() == 0 && enemy.isDestroyed())
				creator.removeEnemy(enemy);
			if(enemy.getX() < player.getX() + 300 / StarterJourney.PPM)
				enemy.b2body.setActive(true);
		}
		
		for(Items coin : creator.getCoin())
			coin.update(dt);
		
		for(Items item : items)
			item.update(dt);
		
		hud.update(dt);
		
		//attach our game cam to our player.x coordinate
		if(player.currentState != Player.State.DEAD) {
			gameCam.position.x = player.b2body.getPosition().x;
		}
		
		//update our game cam with correct coordinates after changes
		gameCam.update();
		//tell our renderer to draw only what our camera can see in our game world
		renderer.setView(gameCam);

	}
	
	@Override
	public void render(float delta) {
		
        //separate our update logic from render
		if(!(gameOver() | cl.isPlayerOnDeadGround()))
			update(delta);
        
		//Clear the game screen with Black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//render our game map
		renderer.render();
		
		//renderer our Box2DDebugLines
		//b2dr.render(world, gameCam.combined);
		
		game.batch.setProjectionMatrix(gameCam.combined);
		game.batch.begin();
		player.draw(game.batch);
		//enemies.draw(game.batch);
		for(Enemy enemy : creator.getEnemy())
			enemy.draw(game.batch);
		for(Items coin : creator.getCoin())
			coin.draw(game.batch);
		for(Items item : items)
			item.draw(game.batch);
		game.batch.end();
		
		//Set our batch to now draw what the Hud camera sees.
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

		if(gameOver() | cl.isPlayerOnDeadGround() | (cl.isPlayerOnFireGround() && Hud.getWorldTimer() % 50 == 0)) {
			if(PlayScreen.numMap == 0)
				StarterJourney.manager.get("audio/background_OST/industrialized_scene/Dirk Valentine - Menu Theme.ogg", Music.class).stop();
	        else if(PlayScreen.numMap == 1)
	        	StarterJourney.manager.get("audio/background_OST/Cave_scene/Alone In The Cave.ogg", Music.class).stop();
	        else
	        	StarterJourney.manager.get("audio/background_OST/Jungle_scene/Dark Cave Music - Trickster Imps.ogg", Music.class).stop();
			gameOverInit();
		}
			

		hud.stage.draw();

	}
	
	public boolean playerOnFireGround() {
		return cl.isPlayerOnFireGround();
	}
	
	public boolean gameOver() {
		if(Hud.worldTimer == 0)
			return true;
		if(cl.isPlayerOnDeadGround())
			return true;
		if(player.currentState == Player.State.DEAD && player.getStateTimer() > 3) {
			return true;
		}
		return false;
	}
	
	public void gameOverInit() {
		
		BitmapFont fo = new BitmapFont(Gdx.files.internal("font/upheavtt.fnt"));
    	fo.getData().setScale(0.8f);
    	
    	Label.LabelStyle font = new Label.LabelStyle(fo, Color.valueOf("C2B2BE"));
    	Table table = new Table();
		table.center();
		table.setFillParent(true);
		
		Label scoreLabel = new Label(hud.getScore()+"", font);
		
		table.add(scoreLabel).expandX();
		table.row();
		scoreLabel = new Label("POINTS", font);
		table.add(scoreLabel).expandX().padTop(10);
		
		hud.stage.addActor(semiTL);
		hud.stage.addActor(table);
		hud.stage.addActor(bgOver);
		hud.stage.addActor(playOver[0]);
    	hud.stage.addActor(menuOver[0]);

    	if(Gdx.input.getX() < (playOver[0].getX() + 80) * StarterJourney.V_SCALE 
				&& Gdx.input.getX() > playOver[0].getX() * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() < (StarterJourney.V_HEIGHT / 2 - 150 + 80) * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() > StarterJourney.V_HEIGHT / 2 - 150 * StarterJourney.V_SCALE)
    		if(Gdx.input.justTouched()) {
    			hud.setPreviousScore(hud.getScore());
    			game.setScreen(new PlayScreen((StarterJourney) game, PlayScreen.numMap));
    		}
    	if(Gdx.input.getX() < (menuOver[0].getX() + 80) * StarterJourney.V_SCALE 
				&& Gdx.input.getX() > menuOver[0].getX() * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() < (StarterJourney.V_HEIGHT / 2 - 150 + 80) * StarterJourney.V_SCALE 
				&& StarterJourney.V_HEIGHT * StarterJourney.V_SCALE - Gdx.input.getY() > StarterJourney.V_HEIGHT / 2 - 150 * StarterJourney.V_SCALE)
    		if(Gdx.input.justTouched()) {
    			game.setScreen(new LevelSelect((StarterJourney) game));
    		}
    	if(numMap > SaveMap.gd.getMapUnlock()) {
    		SaveMap.gd.setMapUnlock(numMap);
    		SaveMap.save();
    	}
	}
	
	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
	}

	public TiledMap getMap() {
		return map[numMap];
	}
	
	public void nextScreen() {
		if(numMap > SaveMap.gd.getMapUnlock()) {
    		SaveMap.gd.setMapUnlock(numMap);
    		SaveMap.save();
    	}
		PlayScreen.numMap += 1;
		if(PlayScreen.numMap == map.length)
			game.setScreen(new LevelSelect((StarterJourney) game));
		else {
			game.setScreen(new PlayScreen((StarterJourney) game, numMap));
		}
	}
	
	public void setBossDied(boolean bossDied) {
		this.bossDied = bossDied;
	}
	
	public boolean getBossDied() {
		return bossDied;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void setNumFoot() {
		cl.setNumFoot();
	}
	
	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		map[numMap].dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
		hud.dispose();
		background.dispose();
		playAgainButton[0].dispose();
		playAgainButton[1].dispose();
		menuButton[0].dispose();
		menuButton[1].dispose();
	}

}
