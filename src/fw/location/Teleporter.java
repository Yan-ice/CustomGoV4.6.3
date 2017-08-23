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
	 * Ϊһ����Ҵ�����������
	 * ���������Ա�ݵش��͸���ҡ�
	 * @param player
	 */
	public Teleporter(FPlayer player) {
		this.player = player;
	}

	/**
	 * �Զ��������Ĵ����ԡ�������ڽ�������ҡ�
	 * 
	 * @param Loc
	 *            ��Ҫ���͵ĵص�
	 * @param SafeTp
	 *            ����ص㲻��ȫ���Ƿ��͵�������ȫ��
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
	 * �Զ��������Ĵ�����,�����͵����һ������㡣
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
