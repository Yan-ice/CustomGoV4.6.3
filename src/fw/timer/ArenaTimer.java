package fw.timer;

import java.util.List;

import fw.Data;
import fw.lobby.group.Group;
import fw.lobby.group.trigger.TriggerBaseExt;
import fw.lobby.task.GroupTaskRunner;

public class ArenaTimer extends BaseTimer {
	Group byGroup;

	public ArenaTimer(Group group, int time) {
		byGroup = group;
		this.time = time;
		this.Maxtime = time;
	}

	public void setGroup(Group group) {
		byGroup = group;
	}

	public boolean isComplete() {
		return byGroup != null && (time >= 0);
	}

	@Override
	public void TimeUp() {
		List<TriggerBaseExt> list = byGroup.getTrigger().getTriggerList();
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a) instanceof fw.lobby.group.trigger.TimeUp) {
				list.get(a).Strike(null);
			}
		}
		time = Maxtime;
	}

	public boolean ShutDown() {
		return byGroup.canJoin() | byGroup.isClear();
	}

	public void EveryTick() {
		if (byGroup.getFileConf().contains("ControlTask.onTimePast(" + time + ")")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onTimePast(" + time + ")");
			GroupTaskRunner runner = new GroupTaskRunner(task, byGroup, null);
			runner.runTask(Data.fwmain);
		}

	}

	@Override
	public void whenShutDown() {
		time = Maxtime;
		byGroup.SetcanJoin(true);
	}
}
