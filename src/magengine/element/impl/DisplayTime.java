package magengine.element.impl;

import application.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import magengine.element.BaseElement;
import magengine.paint.MoveHandler;
import magengine.util.DI;

public class DisplayTime extends BaseElement {

	
	private long startTime;
	public DisplayTime(double x, double y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		startTime = System.currentTimeMillis();
	}

	String msg="";
	
	@Override
	public void paint(GraphicsContext gc) {
		if(Main.DEBUG){
			MoveHandler mh = (MoveHandler) DI.di().get("mh");
			msg=" Element count="+mh.getSize();
		}
		gc.setFill(Color.WHITE);
		gc.fillText("Time = "+(System.currentTimeMillis()-this.startTime)/1000+msg, getX(), getY());

	}

}
