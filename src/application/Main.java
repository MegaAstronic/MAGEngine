package application;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import magengine.ui.SceneManager;
import magengine.util.DI;


public class Main extends Application {
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_ElementCreate = false;
	public static final boolean DEBUG_BENCH=false;
	public static final boolean DEBUG_COLLISION=false;
	public static final boolean ACC_ENABLE=true;
	@Override
	public void start(Stage primaryStage) {
		try {
			SceneManager.startGame(primaryStage);
//			SceneManager.shutdownGame();
//			SceneManager.startGame(primaryStage);	
//			ScheduledExecutorService ses = (ScheduledExecutorService) DI.di().get("sES");
//			ses.schedule(()->{
//				SceneManager.shutdownGame();
//				SceneManager.startGame(primaryStage);
//			}, 3000, TimeUnit.MILLISECONDS);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
