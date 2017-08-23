package fw.lobby.group.trigger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class WalkOnBlock extends TriggerBaseExt {

	public WalkOnBlock(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void Listen(PlayerMoveEvent evt2) {
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			if (Id != null & Id.size()>0) {
				if (Id.get(0) == "0") {
					Strike(pl);
				} else {
					Location BlockWalk = new Location(pl.getLocation().getWorld(), pl.getLocation().getBlockX(),
							(pl.getLocation().getBlockY() - 1), pl.getLocation().getBlockZ());
					Block Blo = BlockWalk.getBlock();
					for (int a = 0; a < Id.size(); a++) {
						if (Blo.getTypeId() == Integer.valueOf(Id.get(a))) {
							Strike(pl);
							break;
						}
					}
				}
			}
		}
	}
}
