package me.schmausio.deli;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import me.schmausio.deli.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("project_delivery");
		config.setResizable(false);
		config.setWindowedMode(Main.SCREEN_WIDTH * 2,Main.SCREEN_HEIGHT * 2);
		new Lwjgl3Application(new Main(), config);
	}
}
