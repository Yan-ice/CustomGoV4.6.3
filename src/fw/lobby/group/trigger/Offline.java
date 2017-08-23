package fw.lobby.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import fw.lobby.Basic;
import fw.lobby.player.FPlayer;

public class Offline extends TriggerBaseExt {

	public Offline(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void Listen(PlayerQuitEvent evt2) {
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			Strike(pl);
			Basic.AutoLeaveAll(FPlayer.getFPlayer(pl));
		}
	}
}
