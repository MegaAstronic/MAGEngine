package magengine.bullet.impl;

import java.util.function.Function;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import magengine.bullet.APolygonBullet;
import magengine.bullet.RadiusSupplier;

/**
 * 画矩形子弹 继承多边形
 * 
 * 
 */
public class SquareBullet extends APolygonBullet implements RadiusSupplier {


	public SquareBullet(double x, double y, double vx, double vy, double ax, double ay) {
		super(x, y, vx, vy, ax, ay);
		// TODO Auto-generated constructor stub
	}

	public SquareBullet(double x, double y, double vx, double vy, double[] ACoodinate) {
		super(x, y, vx, vy, ACoodinate);
		// TODO Auto-generated constructor stub
	}

	public SquareBullet(double x, double y, double[] VCoodinate, double ax, double ay) {
		super(x, y, VCoodinate, ax, ay);
		// TODO Auto-generated constructor stub
	}

	public SquareBullet(double x, double y, double[] VCoodinate) {
		super(x, y, VCoodinate);
		// TODO Auto-generated constructor stub
	}

	public SquareBullet(double x, double y, double vx, double vy) {
		super(x, y, vx, vy);
		// TODO Auto-generated constructor stub
	}

	public SquareBullet(double x, double y) {
		super(x, y); // 中心坐标
		// TODO Auto-generated constructor stub
	}


	public static final double[][] origin = new double[][] { 
		{ -7, -7, 7,  7,  -7},
		{ -5.3,  5.3, 5.3, -5.3,  -5.3} 
	};

	@Override
	protected double[][] getOrigin() {
		return origin;
	}
	
	private Function<RadiusSupplier,Paint> colorSupplier = (cirb)->{
		return Color.WHITESMOKE;
	};

	public void setColorSupplier(Function<RadiusSupplier, Paint> colorSupplier) {
		this.colorSupplier = colorSupplier;
	}
	
	public Function<RadiusSupplier, Paint> getColorSupplier() {
		return colorSupplier;
	}
	
	private double r = 1;

	private double[][] martix ;
	public void setR(double r) {
		this.r = r;
		martix[0][0]=r;
		martix[1][1]=r;
	}
	/**
	 * 取得缩放后的半径
	 * @return
	 */
	@Override
	public double getR() {
		return r*super.getScale();
	}
	/**
	 * 取得缩放后前的半径
	 * @return
	 */
	public double getRealR() {
		return r;
	}
	

	public void paint(GraphicsContext gc) {
		gc.setFill(colorSupplier.apply(this));
		super.paint(gc);
	}

}
