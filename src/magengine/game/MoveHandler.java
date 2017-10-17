package magengine.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import application.Main;
import magengine.bullet.Bullet;
import magengine.element.Accelerated;
import magengine.element.BaseElement;
import magengine.element.DurationManage;
import magengine.element.LimitedByCanvas;
import magengine.element.Moveable;
import magengine.element.PolygonCollision;
import magengine.element.impl.Player;
import magengine.enemy.AEnemy;
import magengine.helper.Helper;
import magengine.launcher.Launcher;
import magengine.paint.MyCanvas;
import magengine.util.CollisionUtil;
import magengine.util.DI;
import magengine.util.ElementUtils;

/**
 * 单独一个线程，不断重复执行： 计算当前时刻所有Moveable元素的坐标 为渲染提供callRepaint方法
 * 
 * @see Moveable
 * @author MegaAstronic
 *
 */

public class MoveHandler implements Runnable {
	public static final double BULLET_EX_AREA_LENGTH = 100;
	public static final double ENEMY_EX_AREA_LENGTH = 100;
	public static final double DEFAULT_TIME_SPEED = 1;
	/**
	 * 全局速度
	 */
	public static double timeSpeed = DEFAULT_TIME_SPEED;
	public static final long SLEEP_TIME = 2;
	public static final long BLANK = 1000;

	private Map<String, Moveable> wantMoveMap = new ConcurrentHashMap<>();
	private ElementUtils mEU = null;

	public Map<String, Moveable> getWantMoveMap() {
		return wantMoveMap;
	}
	public void clear(){
		getWantMoveMap().clear();
	}
	private MyCanvas myCanvas = null;
	private MyCanvas sCanvas = null;// 辅助画布
	public boolean keepRun = true;

	private Moveable m;
	private Entry<String, Moveable> entry;
	private double nextX;
	private double nextY;
	int switchCount = 0;
	private double deltaTime = 0;
	private long current4Delta = System.currentTimeMillis();
	private LogicExecutor deltaExecutor= (LogicExecutor) DI.di().get("deltaExecutor");
	
	public MoveHandler(MyCanvas myCanvas, MyCanvas sCanvas) {
		this.myCanvas = myCanvas;
		this.sCanvas = sCanvas;
	}

	private long benchMax = 0;

	public void run() {
		Thread.currentThread().setName("moveHandlerThread");
		while (keepRun) {
			long currentTime = System.currentTimeMillis();
			deltaTime=currentTime-current4Delta;
			current4Delta = currentTime;
			deltaTime*=1;
			deltaExecutor.update(deltaTime);
			gameLogic();

			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void gameLogic(){
		Iterator<Entry<String, Moveable>> iter = wantMoveMap.entrySet().iterator();
		while (iter.hasNext()) {
			entry = iter.next();
			m = entry.getValue();
			m.modify();
			if (m instanceof BaseElement) {
				if (((BaseElement) m).getLambdaModify() != null) {
					((BaseElement) m).getLambdaModify().accept((BaseElement) m);
				}
				if (((BaseElement) m).wantBeRemoved == true) {
					removeElement(entry.getKey());
					continue;
				}
			}
			if (m instanceof Launcher) {
				Launcher l = (Launcher) m;
				if (LogicExecutor.gameTime() - l.getLastLaunchTime() > l.getInterval()) {
					l.setLastLaunchTime(LogicExecutor.gameTime());
					l.launch();
				}
			}

			if (m instanceof DurationManage) {
				DurationManage dm = (DurationManage) m;
				if (LogicExecutor.gameTime() - dm.getStartTime() > dm.getDuration()) {
					removeElement(entry.getKey());
					continue;
				}
			}

			if (Main.ACC_ENABLE) {
				// 使用加速度计算速度
				if (m instanceof Accelerated) {
					if (((Accelerated) m).getAccX() != 0) {
						m.setVelocityX(m.getVelocityX() + (deltaTime) * 1.0
								* ((Accelerated) m).getAccX() * timeSpeed * (1 / DEFAULT_TIME_SPEED) / BLANK);
					}
					if (((Accelerated) m).getAccY() != 0) {
						m.setVelocityY(m.getVelocityY() + (deltaTime) * 1.0
								* ((Accelerated) m).getAccY() * timeSpeed * (1 / DEFAULT_TIME_SPEED) / BLANK);
					}
				}
			}

			nextX = m.getX() + m.getVelocityX() * ((deltaTime) * 1.0 / BLANK) * timeSpeed;
			nextY = m.getY() + m.getVelocityY() * ((deltaTime) * 1.0 / BLANK) * timeSpeed;

			if (m instanceof Player) {

				// Player不受speed减速影响
				nextX = m.getX()
						+ m.getVelocityX() * ((deltaTime) * 1.0 / BLANK) * DEFAULT_TIME_SPEED;
				nextY = m.getY()
						+ m.getVelocityY() * ((deltaTime) * 1.0 / BLANK) * DEFAULT_TIME_SPEED;
			}
			if (checkAndMove(m, nextX, nextY)) {
				continue;
			}
			collisionCheck(m);
		}

		if (Main.DEBUG_BENCH) {
			if ((deltaTime) > benchMax)
				benchMax = (long) (deltaTime);
			System.out
					.println("1.游戏逻辑 " + (deltaTime) + "ms" + " benchMax=" + benchMax);
		}
	}
	
	/**
	 * 交替调用MyCanvas的repaint
	 */
	public void callRepaint() {
		if (switchCount == 0) {
			myCanvas.repaint();
			myCanvas.setVisible(true);
			sCanvas.setVisible(false);
		} else {
			sCanvas.repaint();
			sCanvas.setVisible(true);
			myCanvas.setVisible(false);
		}
		switchCount = (switchCount + 1) % 2;
	}

	public void removeElement(String key) {
		mEU.removeBoth(key);
	}

	public ElementUtils getmEU() {
		return mEU;
	}

	public void setmEU(ElementUtils mEU) {
		this.mEU = mEU;
	}

	private Map<String, PolygonCollision> collisionCheckMap = new ConcurrentHashMap<>();

	public void addCollisionElement(String key, PolygonCollision elem) {
		collisionCheckMap.put(key, elem);
	}

	public void removeCollisionElement(String key) {
		collisionCheckMap.remove(key);
	}

	private void collisionCheck(Moveable m) {
		if (m instanceof PolygonCollision) {
			collisionCheckMap.entrySet().forEach((entry) -> {

				PolygonCollision x = entry.getValue();
				if (!m.equals(x)) {
					// System.out.println(x);
					if (CollisionUtil.PolygonDetect(x, (PolygonCollision) m)) {
						x.onCollision((PolygonCollision) m);
						((PolygonCollision) m).onCollision(x);
						if (Main.DEBUG_COLLISION) {
							System.out.println("碰撞! x=" + m.getX() + " | y=" + m.getY());
						}
					}
				}
			});
		}
	}

	public int getSize() {
		return this.getWantMoveMap().size();
	}

	private boolean checkAndMove(Moveable m, double nextX, double nextY) {
		boolean needMove = false;
		boolean needDelete = false;
		if (m instanceof Bullet || m instanceof Helper || m instanceof Launcher) {
			if (nextX < MyCanvas.CANVAS_WIDTH + BULLET_EX_AREA_LENGTH && nextX > 0 - BULLET_EX_AREA_LENGTH
					&& nextY < MyCanvas.CANVAS_HEIGHT + BULLET_EX_AREA_LENGTH && nextY > 0 - BULLET_EX_AREA_LENGTH) {
				needMove=true;
			}else{
				needDelete=true;
			}
		} else if(m instanceof AEnemy) {
			if (nextX < MyCanvas.CANVAS_WIDTH + ENEMY_EX_AREA_LENGTH && nextX > 0 - ENEMY_EX_AREA_LENGTH
					&& nextY < MyCanvas.CANVAS_HEIGHT + ENEMY_EX_AREA_LENGTH && nextY > 0 - ENEMY_EX_AREA_LENGTH) {
				needMove = true;
			} else {
				needDelete = true;
			}
		}else {
			if (nextX < MyCanvas.CANVAS_WIDTH && nextX > 0 && nextY < MyCanvas.CANVAS_HEIGHT && nextY > 0) {
				needMove=true;
			}else{//出界了
				if (m instanceof LimitedByCanvas) {// 如果他是被边界限制的 不能移动

				} else {// 否则删除
					needDelete=true;
				}
			}
		} 
		if(needMove)
			move(m, nextX, nextY);
		if(needDelete){
			removeElement(entry.getKey());
			return true;
		}
		return false;
	}

	private void move(Moveable m, double nextX, double nextY) {
		if (m.getVelocityX() != 0)
			m.setX(nextX);
		if (m.getVelocityY() != 0)
			m.setY(nextY);
	}
	

}