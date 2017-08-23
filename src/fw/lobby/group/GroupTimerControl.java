package fw.lobby.group;

import fw.Data;
import fw.timer.ArenaTimer;
import fw.timer.GroupWaitTimer;

public class GroupTimerControl {
	public int LobbyTime;
	public int Full_LobbyTime;
	public int ArenaTime;
	Group group;

	public GroupTimerControl(Group group, int LobbyTime, int Full_LobbyTime, int ArenaTime) {
		this.group = group;
		this.LobbyTime = LobbyTime;
		this.Full_LobbyTime = Full_LobbyTime;
		this.ArenaTime = ArenaTime;

		Ltimer = new GroupWaitTimer(group, LobbyTime, Full_LobbyTime);
		Atimer = new ArenaTimer(group, ArenaTime);
	}

	public boolean isComplete() {
		return this.LobbyTime > 0 & this.Full_LobbyTime >= 0 & this.ArenaTime > 0;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	private GroupWaitTimer Ltimer;
	private ArenaTimer Atimer;

	public GroupWaitTimer LobbyTimer() {
		return Ltimer;
	}

	public ArenaTimer ArenaTimer() {
		return Atimer;
	}

	public void start(int a) {
		if (isComplete()) {
			switch (a) {
			case 1:
				Ltimer = new GroupWaitTimer(group, LobbyTime, Full_LobbyTime);
				Ltimer.runTaskTimer(Data.fwmain, 20, 20);
				break;
			case 2:
				Atimer = new ArenaTimer(group, ArenaTime);
				Atimer.runTaskTimer(Data.fwmain, 20, 20);
				break;
			}
		}
	}
}
