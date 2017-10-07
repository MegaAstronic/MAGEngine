package magengine.helper;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import magengine.chapter.util.QuickDanmuku;
import magengine.element.Initializable;
import magengine.util.DI;

public class MoveToHelper extends Helper implements Initializable{
	
	private double sourceX,sourceY,targetX,targetY;
	private boolean accMode=true;
	private long timeCost;
	
	public MoveToHelper(double sourceX, double sourceY,double targetX,double targetY,long timeCost) {
		super(sourceX, sourceY);	
		this.sourceX=sourceX;
		this.sourceY=sourceY;
		this.targetX=targetX;
		this.targetY=targetY;
		this.timeCost=timeCost;
		
	}

	@Override
	public void initWhenAdd() {
		ScheduledExecutorService ses=(ScheduledExecutorService) DI.di().get("sES");
		if(accMode){
			double s = Math.sqrt((targetX-sourceX)*(targetX-sourceX)+(targetY-sourceY)*(targetY-sourceY));
			double accRate = s/((timeCost/1000.0/2)*(timeCost/1000.0/2));
			this.setAccX(accRate);
			QuickDanmuku.getQuickDanmuku().AccTo(this, targetX, targetY);
			ses.schedule(() -> {
				this.setAccX(-this.getAccX());
				this.setAccY(-this.getAccY());
			}, timeCost/2, TimeUnit.MILLISECONDS);
			ses.schedule(() -> {
				this.setAccX(0);
				this.setAccY(0);
				this.setVelocityX(0);
				this.setVelocityY(0);
				this.setDuration(timeCost-1);
			}, timeCost-1, TimeUnit.MILLISECONDS);
		}else{
			double s = Math.sqrt((targetX-sourceX)*(targetX-sourceX)+(targetY-sourceY)*(targetY-sourceY));
			double vRate = s/(timeCost/1000.0);
			this.setVelocityX(vRate);
			QuickDanmuku.getQuickDanmuku().VTo(this, targetX, targetY);
			ses.schedule(() -> {
				this.setAccX(0);
				this.setAccY(0);
				this.setVelocityX(0);
				this.setVelocityY(0);
				this.setDuration(timeCost-1);
			}, timeCost-1, TimeUnit.MILLISECONDS);
		}
	}
	
	public void setAccMode(boolean accMode) {
		this.accMode = accMode;
	}
	
	public long getTimeCost() {
		return timeCost;
	}

}