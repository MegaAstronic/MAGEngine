package magengine.element.impl;

import com.badlogic.gdx.math.Polygon;

import application.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import magengine.element.BaseElement;
import magengine.element.LimitedByCanvas;
import magengine.element.PolygonCollision;
import magengine.game.GameSession;
import magengine.game.LogicExecutor;
import magengine.mulplay.MulSync;
import magengine.paint.SpritePainter;
import magengine.util.CollisionTeam;
/**
 * 玩家控制的物体
 * 单例设计模式
 * 只能存在一个实例化对象
 * @author MegaAstronic
 *
 */
public class Player extends BaseElement implements LimitedByCanvas ,PolygonCollision,MulSync {
	private static volatile Player player1 = null;
	private static volatile Player player2 = null;
	public static boolean showCollision = true;
	public final int width = 10;
	public final int height = 10;
	private SpritePainter spritePainter = null;
	private int imgwidth = 60, imgheight = 131;
	private int checkPointLine = imgwidth/10;
	
	private boolean noHurtMode = false;
	public static final long NO_HURT_TIME = 3000;
	public boolean isShooting=false;
	
	public static void clear() {
		player1=null;
		player2=null;
	}
	
	/**
	 * 获取Player对象
	 * @param x 设定坐标x
	 * @param y 设定坐标y
	 * @return 返回Player对象
	 */
	public static Player getPlayer1(double x,double y){
		if(player1==null){
			player1=new Player(x,y);
			return player1;
		}else{
			if(!(x==-1&&y==-1)){
				player1.setX(x);
				player1.setY(y);
			}
			return player1;
		}
	}
	public static Player getPlayer1(){
		return getPlayer1(-1,-1);
	}
	public static Player getPlayer2(double x,double y){
		if(player2==null){
			player2=new Player(x,y);
			return player2;
		}else{
			if(!(x==-1&&y==-1)){
				player2.setX(x);
				player2.setY(y);
			}
			return player2;
		}
	}
	public static Player getPlayer2(){
		return getPlayer2(-1,-1);
	}
	private Player(double x, double y) {
		super(x, y);
		Image img=null;
		try {
			img = new Image(this.getClass().getResourceAsStream("/img/playerUpdatedSmallver.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		spritePainter=new SpritePainter(img, imgwidth, imgheight);
		setX(x);
		setY(y);
	}
	


	private int noHurtCountMax = 16;
	private int noHurtCount4Paint = 0;
	private int currentSpriteIndex=0;
	@Override
	public void paint(GraphicsContext gc) {
		noHurtCount4Paint=(noHurtCount4Paint+1)%noHurtCountMax;
		if(noHurtMode&&(noHurtCount4Paint<noHurtCountMax/2)){
			//do nothing
		}else{
			if(this.velocityX<0){
				spritePainter.paintSprite(8+(currentSpriteIndex), getX()-imgwidth/2,getY()-imgheight/2, gc);
				currentSpriteIndex=(currentSpriteIndex+1)%(4);
			}
			
			if(this.velocityX==0){
				spritePainter.paintSprite(0+(currentSpriteIndex), getX()-imgwidth/2, getY()-imgheight/2, gc);
				currentSpriteIndex=(currentSpriteIndex+1)%(4);
			}
				
			if(this.velocityX>0){
				spritePainter.paintSprite(4+(currentSpriteIndex), getX()-imgwidth/2, getY()-imgheight/2, gc);
				currentSpriteIndex=(currentSpriteIndex+1)%(4);
			}
			if(Main.DEBUG_COLLISION||showCollision){
				getPolygon();
				gc.setFill(Color.rgb(150, 100, 100,0.7));
				paintPolygon(vertices, gc);
//				gc.setFill(Color.WHITESMOKE);
			}
		}
	}
	public float[] vertices = new float[8*2];
	private Polygon polygon=new Polygon(vertices);
	
	@Override
	public Polygon getPolygon() {
		vertices[0]=(float) (getX()-checkPointLine/2);
		vertices[1]=(float) (getY()-checkPointLine/2/Math.tan(Math.PI/8));
		vertices[2]=(float) (getX()-checkPointLine/2/Math.tan(Math.PI/8));
		vertices[3]=(float) (getY()-checkPointLine/2);
		vertices[4]=(float) (getX()-checkPointLine/2/Math.tan(Math.PI/8));
		vertices[5]=(float) (getY()+checkPointLine/2);
		vertices[6]=(float) (getX()-checkPointLine/2);
		vertices[7]=(float) (getY()+checkPointLine/2/Math.tan(Math.PI/8));
		vertices[8]=(float) (getX()+checkPointLine/2);
		vertices[9]=(float) (getY()+checkPointLine/2/Math.tan(Math.PI/8));
		vertices[10]=(float) (getX()+checkPointLine/2/Math.tan(Math.PI/8));
		vertices[11]=(float) (getY()+checkPointLine/2);
		vertices[12]=(float) (getX()+checkPointLine/2/Math.tan(Math.PI/8));
		vertices[13]=(float) (getY()-checkPointLine/2);
		vertices[14]=(float) (getX()+checkPointLine/2);
		vertices[15]=(float) (getY()-checkPointLine/2/Math.tan(Math.PI/8));
		polygon.setVertices(vertices);
		return polygon;
	}
	@Override
	public CollisionTeam getTeam() {
		return CollisionTeam.PLAYER;
	}
	
	private void paintPolygon(float[] origin,GraphicsContext gc){
		double[] xpoint = new double[origin.length/2];
		double[] ypoint = new double[origin.length/2];
		for(int i=0;i<origin.length;i++){
			if(i%2==0){
				xpoint[i/2]=origin[i];
			}else{
				ypoint[i/2]=origin[i];
			}
		}
		gc.fillPolygon(xpoint, ypoint, origin.length/2);
		
	}
	
	@Override
	public void onCollision(PolygonCollision m) {
		if(m.getTeam().equals(CollisionTeam.ENEMY_BULLET)){
			if(!noHurtMode){
				if(GameSession.getGameSession().decHealthAndCheck()&&!Main.DEBUG_NO_FAILURE){
						GameSession.getGameSession().failure();
				}else{
					noHurtMode=true;
					LogicExecutor.getLogicExecutor().schedule(()->{
						noHurtMode=false;
					}, NO_HURT_TIME);
				}
					
			}else{
				//do nothing
			}
		}
	}

	public boolean getNoHurtMode() {
		return noHurtMode;
	}

	public void setNoHurtMode(boolean noHurtMode) {
		this.noHurtMode = noHurtMode;
	}

}
