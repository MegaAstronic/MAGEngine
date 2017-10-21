package magengine.bullet;

import com.badlogic.gdx.math.Polygon;

import javafx.scene.canvas.GraphicsContext;
import magengine.element.PolygonCollision;
import magengine.util.CollisionTeam;
import magengine.util.Transform;

public abstract class APolygonBullet extends Bullet implements PolygonCollision{

	public APolygonBullet(double x, double y, double vx, double vy, double ax, double ay) {
		super(x, y, vx, vy, ax, ay);
		// TODO Auto-generated constructor stub
	}

	public APolygonBullet(double x, double y, double vx, double vy, double[] ACoodinate) {
		super(x, y, vx, vy, ACoodinate);
		// TODO Auto-generated constructor stub
	}

	public APolygonBullet(double x, double y, double vx, double vy) {
		super(x, y, vx, vy);
		// TODO Auto-generated constructor stub
	}

	public APolygonBullet(double x, double y, double[] VCoodinate, double ax, double ay) {
		super(x, y, VCoodinate, ax, ay);
		// TODO Auto-generated constructor stub
	}

	public APolygonBullet(double x, double y, double[] VCoodinate) {
		super(x, y, VCoodinate);
		// TODO Auto-generated constructor stub
	}

	public APolygonBullet(double x, double y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	protected abstract double[][] getOrigin();
	protected volatile float[] vertices=new float[getOrigin()[0].length*2];
	protected volatile Polygon polygon = new Polygon(vertices);
	
	
	protected double[][] transformVAndDelta(double[][] origin){
		double s = Math.sqrt(velocityX*velocityX+velocityY*velocityY);
		Transform t= new Transform(new double[][] {
			{-velocityY/s , velocityX/s }, 
			{-velocityX/s,-velocityY/s } });
		double[][] ans= t.transform(origin);
		Transform.delta(ans, getX(), getY());
		return ans;
	}
	
	protected void toVertices(double[][] in,float[] target){
		for(int i=0;i<in[0].length;i++){
			target[i*2]=(float) in[0][i];
			target[i*2+1]=(float) in[1][i];
		}
	}
	@Override
	public CollisionTeam getTeam() {
		return CollisionTeam.ENEMY_BULLET;
	}
	@Override
	public Polygon getPolygon() {
		return polygon;
	}
	
	protected double[][] handleCollision(){
		double[][] ans=transformVAndDelta(getOrigin());
		toVertices(ans, vertices);
		polygon.setVertices(vertices);
		return ans;
	}
	@Override
	public void paint(GraphicsContext gc){
		double[][] ans=handleCollision();
		gc.fillPolygon(ans[0],ans[1], getOrigin()[0].length);
	}
	@Override
	public void onCollision(PolygonCollision m) {
		this.setWantBeRemoved(true);
	}
	
}