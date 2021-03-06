package magengine.bullet.impl;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import magengine.bullet.APolygonBullet;

/**
 * 另一种子弹
 * @author Astronic
 *
 */
public class ArrowBullet extends APolygonBullet {

	public ArrowBullet(double x, double y) {
		super(x, y);
	}
	public ArrowBullet(double x,double y,double[] VCoodinate){
		super(x, y, VCoodinate[0], VCoodinate[1]);
	}
	public ArrowBullet(double x,double y,double vx, double vy){
		super(x, y,vx, vy);
	}
	
	public ArrowBullet(double x,double y,double vx, double vy,double[] ACoodinate) {
		super(x,y,vx, vy,ACoodinate[0], ACoodinate[1]);
	}
	
	public ArrowBullet(double x,double y,double[] VCoodinate,double ax,double ay) {
		super(x,y,VCoodinate[0], VCoodinate[1],ax,ay);
	}
	
	public ArrowBullet(double x,double y,double vx, double vy,double ax,double ay) {
		super(x,y,vx,vy,ax,ay);
	}
	
	public static final double[][] origin=new double[][]{
		{
			0,-9,0,9
		},
		{
			-13,12,5,12
		}
	};
	

@Override
public void paint(GraphicsContext gc) {
	gc.setFill(Color.SILVER);
	super.paint(gc);
}
	@Override
	protected double[][] getOrigin() {
		return origin;
	}
}


