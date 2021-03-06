package magengine.paint;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import magengine.element.Paintable;
/**
 * 本类的实例化对象负责完成绘画
 * @author MegaAstronic
 *
 */
public class MyCanvas extends Canvas {
	//画布大小
	public final static int CANVAS_WIDTH = 600;//600;
	public final static int CANVAS_HEIGHT = 700;//700;
	private GraphicsContext gc=null;
	private Map<String, Paintable> wantPaintMap = null;

	public Map<String, Paintable> getWantPaintMap() {
		return wantPaintMap;
	}
	
	
	public MyCanvas(){
		this(new ConcurrentHashMap<>());
	}
	public MyCanvas(Map<String, Paintable> wantPaintMap){
		super(CANVAS_WIDTH,CANVAS_HEIGHT);
		this.wantPaintMap = wantPaintMap;
		gc = super.getGraphicsContext2D();
	}
	public void clear(){
		gc.clearRect(0, 0, this.getWidth(), this.getHeight());
	}
	/**
	 * 将清空屏幕并调用wantPaintMap中所有元素的paint方法，完成绘制
	 */
	public void repaint(){
		//清空屏幕
		this.clear();
		//绘制wantPaint的内容
		for (Entry<String, Paintable> stringPaintableEntry : wantPaintMap.entrySet()) {
			stringPaintableEntry.getValue().paint(gc);
		}
	}
}
