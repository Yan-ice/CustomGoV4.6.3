package fw.lobby.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import fw.Data;

public class Death extends TriggerBaseExt {
	public Death(String ID) {
		super(ID);
	}

	@EventHandler(priority=EventPriority.LOW)
	void Listen(PlayerRespawnEvent evt2) {
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			Strike(pl);
		}
	}
}
