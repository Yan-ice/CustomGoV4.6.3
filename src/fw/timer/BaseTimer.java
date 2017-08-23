package fw.timer;

import org.bukkit.scheduler.BukkitRunnable;

import fw.Data;

public abstract class BaseTimer extends BukkitRunnable {
	protected int Maxtime;
	protected int time;
	private boolean isRunning;

	public int getTime() {
		return time;
	}
	public int getMaxtime(){
		return Maxtime;
	}
	/**
	 * ������ʱ����
	 */
	public void Start() {
		if (isRunning) {
			this.runTaskTimer(Data.fwmain, 20, 20);
		}
	}

	public void setMaxTime(int maxtime) {
		time = maxtime;
		Maxtime = maxtime;
	}
/**
 * ����ʹ���������������ʱ����
 * ���ΪStart()����!
 */
	@Override
	public void run() {
		isRunning = true;
		EveryTick();
		if (ShutDown()) {
			whenShutDown();
			isRunning = false;
			cancel();
			return;
		}
		if (time == 0) {
			cancel();
			TimeUp();
			return;
		}
		time--;
	}

	/**
	 * ÿ��ʱһ�룬����ִ��һ�θ÷��������Ƿ������ʱ��
	 * 
	 * @return ���Ϊtrue,��ֱ��ֹͣ��ʱ��
	 */
	public abstract boolean ShutDown();

	/**
	 * ����ΪShutDownʹ��ʱ��ֹͣʱ��ִ�е����
	 */
	public abstract void whenShutDown();

	public abstract void EveryTick();

	/**
	 * ��ʱ�䵽��ʱִ�еķ�����
	 */
	public abstract void TimeUp();
}
