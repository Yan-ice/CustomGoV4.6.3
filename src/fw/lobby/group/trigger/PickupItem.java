package fw.lobby.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PickupItem extends TriggerBaseExt {

	public PickupItem(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.MONITOR)
	void Listen(PlayerPickupItemEvent evt2) {
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			if (Id != null) {
				if (Id.get(0) == "0") {
					Strike(pl);
				} else {
					for (int a = 0; a < Id.size(); a++) {
						if (evt2.getItem().getItemStack().getTypeId() == Integer.valueOf(Id.get(a))) {
							Strike(pl);
							break;
						}
					}
				}
			}

		}
	}
}
