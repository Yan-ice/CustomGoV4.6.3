package fw.lobby;

import fw.Data;
import fw.timer.LobbyTimer;

public class LobbyTimerControl {
	public int LobbyTime;
	public int Full_LobbyTime;
	Lobby Lobby;

	public LobbyTimerControl(Lobby div, int LobbyTime, int Full_LobbyTime) {
		this.Lobby = div;
		this.LobbyTime = LobbyTime;
		this.Full_LobbyTime = Full_LobbyTime;
		
		 Dtimer = new LobbyTimer(Lobby, LobbyTime, Full_LobbyTime);
	}

	public LobbyTimerControl(Lobby Lobby) {
		this.Lobby = Lobby;
	}

	public boolean isComplete() {
		return this.LobbyTime > 0 & this.Full_LobbyTime >= 0;
	}

	public void setGroup(Lobby Lobby) {
		this.Lobby = Lobby;
	}

	private LobbyTimer Dtimer;

	public LobbyTimer LobbyTimer() {
		return Dtimer;
	}

	public void start() {
		if (isComplete()) {
			Dtimer = new LobbyTimer(Lobby, LobbyTime, Full_LobbyTime);
			Dtimer.runTaskTimer(Data.fwmain, 20, 20);
		}
	}
}
