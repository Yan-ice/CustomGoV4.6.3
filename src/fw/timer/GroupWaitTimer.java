package fw.timer;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fw.Data;
import fw.lobby.group.Group;
import fw.lobby.task.GroupTaskRunner;

public class GroupWaitTimer extends BaseTimer {
	int FastTime;
	Group byGroup;

	public GroupWaitTimer(Group group, int time, int fasttime) {
		this.byGroup = group;
		this.time = time;
		this.Maxtime = time;
		this.FastTime = fasttime;
	}

	public void setGroup(Group group) {
		byGroup = group;
	}

	public boolean isComplete() {
		return byGroup != null && (time >= 0) && (FastTime >= 0);
	}

	public int getFastTime() {
		return FastTime;
	}

	public void setFastTime(int FastTime) {
		this.FastTime = FastTime;
	}

	@Override
	public void TimeUp() {
		byGroup.Start();
		time = Maxtime;
	}

	/**
	 * 该方法执行时，如果计时器的剩余时间多于调定时间，则将时间缩短至快进调定时间。（在构造函数时设定）
	 */
	public void FastStart() {
		if (isComplete()) {
			if (this.time > FastTime) {
				this.time = FastTime;
			}
		}
	}
	

	public boolean ShutDown() {
		return byGroup.GetPlayerAmount() < byGroup.GetMinPlayer();
	}

	@Override
	public void whenShutDown() {
		time = Maxtime;
	}

	@Override
	public void EveryTick() {
		if (byGroup.getFileConf().contains("ControlTask.onLobbyTimePast(" + time + ")")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onLobbyTimePast(" + time + ")");
			GroupTaskRunner runner = new GroupTaskRunner(task, byGroup, null);
			runner.runTask(Data.fwmain);
		}
	}
}
