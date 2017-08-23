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
	 * �÷���ִ��ʱ�������ʱ����ʣ��ʱ����ڵ���ʱ�䣬��ʱ���������������ʱ�䡣���ڹ��캯��ʱ�趨��
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
