package com.libgdx.starter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.libgdx.starter.StarterJourney;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = StarterJourney.V_WIDTH*StarterJourney.V_SCALE;
		config.height = StarterJourney.V_HEIGHT*StarterJourney.V_SCALE;
		config.resizable = false;
		new LwjglApplication(new StarterJourney(), config);
	}
}
