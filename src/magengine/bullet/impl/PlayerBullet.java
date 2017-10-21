package magengine.bullet.impl;

import javafx.application.Platform;
import javafx.scene.effect.Bloom;
import magengine.paint.MyCanvasSwitcher;
import magengine.util.CollisionTeam;
import magengine.util.DI;

public class PlayerBullet extends DefaultBullet {

	public PlayerBullet(double x, double y) {
		super(x, y);
	}

	@Override
	public CollisionTeam getTeam() {
		return CollisionTeam.PLAYER_BULLET;
	}
	@Override
	public void initWhenAdd() {
		Platform.runLater(()->{
			((MyCanvasSwitcher) DI.di().get("switcher")).configCanvas(PlayerBullet.class, (canvas) -> {
				canvas.setEffect(new Bloom());
			});
		});
	}
}