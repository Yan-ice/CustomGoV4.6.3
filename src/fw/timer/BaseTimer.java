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
	 * 启动计时器。
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
 * 请勿使用这个方法启动计时器！
 * 请改为Start()方法!
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
	 * 每计时一秒，都会执行一次该方法决定是否继续计时。
	 * 
	 * @return 如果为true,将直接停止计时。
	 */
	public abstract boolean ShutDown();

	/**
	 * 当因为ShutDown使计时器停止时，执行的命令。
	 */
	public abstract void whenShutDown();

	public abstract void EveryTick();

	/**
	 * 当时间到点时执行的方法。
	 */
	public abstract void TimeUp();
}
