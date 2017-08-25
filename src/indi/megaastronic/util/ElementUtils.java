package indi.megaastronic.util;

import java.util.Map;

import indi.megaastronic.element.BaseElement;
import indi.megaastronic.element.Initializable;
import indi.megaastronic.element.Moveable;
import indi.megaastronic.element.Paintable;
import indi.megaastronic.element.impl.Bullet;
import indi.megaastronic.paint.MoveHandler;
import indi.megaastronic.paint.MyCanvas;
import javafx.scene.layout.StackPane;
/**
 * 统一处理元素的运动与绘画
 * @author Astronic
 *
 */
public class ElementUtils {
	private MoveHandler mh = null;
	private MyCanvas myCanvas =null;
	private MyCanvasSwitcher switcher=null;
	public ElementUtils(MoveHandler mh, MyCanvas myCanvas,StackPane root) {
		super();
		this.mh = mh;
		this.myCanvas = myCanvas;
		this.switcher=new MyCanvasSwitcher(root, mh);
	}

	/**
	 * 让元素可以运动与被绘画
	 * 如果元素需要初始化，会在此初始化  {@link Initializable}
	 * @param name
	 * @param value
	 */
	public void add(String name,Object value){
		if(value instanceof Initializable){
			((Initializable) value).init();
		}
		if(mh!=null)
			if(value instanceof Moveable)
				mh.getWantMoveMap().put(name, (Moveable) value);
		if(myCanvas!=null)
			if(value instanceof Paintable)
				if(value instanceof Bullet){
					switcher.addElement(name,(BaseElement)value);
				}else{
					myCanvas.getWantPaintMap().put(name, (Paintable) value);
				}
	}
	

	public void removeBoth(String key){
		
		Object obj = getWantMoveMap().get(key);
		if(obj instanceof Bullet){
			switcher.removeElement(key);
		}else{
			myCanvas.getWantPaintMap().remove(key);
		}
		if(obj instanceof BaseElement){
			((BaseElement)obj).setDeleted(true);
		}
		mh.getWantMoveMap().remove(key);


	}
	
	private Map<String, Moveable> getWantMoveMap(){
		return mh.getWantMoveMap();
	}
	private Map<String,Paintable> getWantPaintMap(){
		return myCanvas.getWantPaintMap();
	}

	public MyCanvasSwitcher getSwitcher() {
		return switcher;
	}
	public MoveHandler getMh() {
		return mh;
	}

	public void setMh(MoveHandler mh) {
		this.mh = mh;
	}

	public MyCanvas getMyCanvas() {
		return myCanvas;
	}

	public void setMyCanvas(MyCanvas myCanvas) {
		this.myCanvas = myCanvas;
	}
	
	
}
