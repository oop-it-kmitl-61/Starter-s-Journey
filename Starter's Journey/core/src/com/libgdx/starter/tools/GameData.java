package com.libgdx.starter.tools;

import java.io.Serializable;

public class GameData implements Serializable {

	private static final long serialVersionUID = 1;
	
	private int mapUnlock;
	
	public GameData() {}
	
	public void init() {
		//mapUnlock = PlayScreen.numMap;
	}
	
	public int getMapUnlock() {
		return mapUnlock;
	}

	public void setMapUnlock(int mapUnlock) {
		this.mapUnlock = mapUnlock;
	}
}
