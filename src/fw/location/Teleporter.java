package fw.location;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fw.Data;
import fw.Language;
import fw.lobby.player.FPlayer;

public class Teleporter {
	FPlayer player;

	/**
	 * 为一个玩家创建传送器。
	 * 传送器可以便捷地传送该玩家。
	 * @param player
	 */
	public Teleporter(FPlayer player) {
		this.player = player;
	}

	/**
	 * 自动检查坐标的存在性。如果存在将传送玩家。
	 * 
	 * @param Loc
	 *            将要传送的地点
	 * @param SafeTp
	 *            如果地点不安全，是否传送到附近安全区
	 */
	public void Teleport(FLocation Loc, boolean SafeTp) {
		if (Loc == null || (!Loc.isComplete())) {
			player.sendMessage(ChatColor.RED + Language.getMessage("location_failed"));
			return;
		}
		if (!SafeTp) {
			player.teleport(Loc.ToMC());
			player.teleport(Loc.ToMC());
		} else {
			if (FLocation.SafeLoc(Loc) != null) {
				player.teleport(FLocation.SafeLoc(Loc).ToMC());
				player.teleport(FLocation.SafeLoc(Loc).ToMC());
			} else {
				player.sendMessage(ChatColor.RED + Language.getMessage("location_unsafe"));
			}
		}
	}

	/**
	 * 自动检查坐标的存在性,并传送到随机一个坐标点。
	 */
	public void TeleportRandom(List<FLocation> Loc) {
		FLocation loc = FLocation.RandomLoc(Loc);
		if (loc != null) {
			player.teleport(loc.ToMC());
			player.teleport(loc.ToMC());
		} else {
			player.sendMessage(ChatColor.RED + Language.getMessage("location_unsafe"));
		}
		return;
	}
}
