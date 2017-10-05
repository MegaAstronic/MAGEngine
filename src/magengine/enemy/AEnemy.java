package magengine.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import magengine.danmuku.ADanmuku;
import magengine.element.BaseElement;
import magengine.element.Initializable;
import magengine.util.SpritePainter;

public abstract class AEnemy extends BaseElement implements Initializable{
	public AEnemy(double x, double y, double velocityX, double velocityY, double ax, double ay) {
		super(x, y, velocityX, velocityY, ax, ay);
	}

	public AEnemy(double x, double y, double velocityX, double velocityY) {
		super(x, y, velocityX, velocityY);
	}

	public AEnemy(double x, double y) {
		super(x, y);	
	}
	
	private AtomicInteger HP=new AtomicInteger(100);
	private List<ADanmuku> danmukuList = new ArrayList<ADanmuku>();
	
	
	public void setHP(int HP){
		this.HP.set(HP);
	}
	
	public int getHP(){
		return this.HP.get();
	}
	
	public int addHP(int delta){
		return HP.addAndGet(delta);
	}

	public void addDanmuku(ADanmuku danmuku){
		danmukuList.add(danmuku);
	}
	
	@Override
	public void initWhenAdd() {
		danmukuList.forEach(x->{
			x.setSourceElement(this);
			x.delayExecute();
		});
	}
	
}