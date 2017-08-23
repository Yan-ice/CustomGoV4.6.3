package fw.lobby.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Sneak extends TriggerBaseExt {

	public Sneak(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void Listen(PlayerToggleSneakEvent evt2) {
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {

			evt2.setCancelled(true);
			Strike(pl);

		}
	}
}
