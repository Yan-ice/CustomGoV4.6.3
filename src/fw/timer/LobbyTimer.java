package fw.timer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fw.Data;
import fw.lobby.Lobby;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;
import fw.lobby.task.GroupTaskRunner;

public class LobbyTimer extends BaseTimer {
	Lobby byDiv;
	int FastTime;

	public LobbyTimer(Lobby group, int time, int fasttime) {
		this.byDiv = group;
		this.time = time;
		this.Maxtime = time;
		this.FastTime = fasttime;
	}

	public void setDiv(Lobby group) {
		byDiv = group;
	}

	public boolean isComplete() {
		return byDiv != null && (time >= 0) && (FastTime >= 0);
	}

	public int getFastTime() {
		return FastTime;
	}

	public void setFastTime(int FastTime) {
		this.FastTime = FastTime;
	}
	
	public void FastStart() {
		if (isComplete()) {
			if (this.time > FastTime) {
				this.time = FastTime;
			}
		}
	}
	
	public boolean ShutDown() {
		return byDiv.GetPlayerAmount() < byDiv.GetMinPlayer();
	}

	public void whenShutDown() {
		time = Maxtime;
	}

	@Override
	public void TimeUp() {
		byDiv.onstart();
		for (int x = 0; x < byDiv.GetPlayerAmount(); x++) {// ´òÂÒË³Ðò
			int ran1 = (int) Math.round(Math.random() * ((byDiv.GetPlayerAmount() - 1) - 0) + 0);
			FPlayer ch = byDiv.GetPlayerList().get(ran1);
			byDiv.GetPlayerList().remove(ran1);
			byDiv.GetPlayerList().add(ch);
		}
		
		List<FPlayer> list = byDiv.GetPlayerList();
		List<FPlayer> playerlist = new ArrayList<FPlayer>();
		for(int a = 0;a<list.size();a++){
			playerlist.add(list.get(a));
		}

		List<Group> grouplist = byDiv.getLobbyGroup();

		int b = 0;
		Group group = grouplist.get(b);
		boolean skip = true;
		while (playerlist.size() > 0) {
			FPlayer player = playerlist.get(0);
			if (skip) {
				if (group.GetPlayerAmount() < group.GetMinPlayer()) {
					playerlist.remove(0);
					group.JoinGroup(player);
				} else {
					b++;
					if (b < grouplist.size()) {
						group = grouplist.get(b);
					} else {
						b = 0;
						skip = false;
					}
				}
			} else {
				group = grouplist.get(b);
				if (group.GetPlayerAmount() < group.GetMaxPlayer()) {
					playerlist.remove(0);
					group.JoinGroup(player);
				}
				if (b < grouplist.size()) {
					b++;
				} else {
					b = 0;
				}
			}
		}
		byDiv.SetCanJoin(false);
		time = getMaxtime();
	}

	@Override
	public void EveryTick() {
		byDiv.LoadSign();
		if (byDiv.getFileConf().contains("ControlTask.onTimePast(" + time + ")")) {
			List<String> task = byDiv.getFileConf().getStringList("ControlTask.onTimePast(" + time + ")");
			GroupTaskRunner runner = new GroupTaskRunner(task, byDiv, null);
			runner.runTask(Data.fwmain);
		}

	}
}
