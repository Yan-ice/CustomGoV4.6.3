package fw.lobby.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.event.HandlerList;

import fw.Data;
import fw.lobby.group.trigger.Death;
import fw.lobby.group.trigger.Diss;
import fw.lobby.group.trigger.KillAllEntity;
import fw.lobby.group.trigger.KillEntity;
import fw.lobby.group.trigger.KillPlayer;
import fw.lobby.group.trigger.Offline;
import fw.lobby.group.trigger.PickupItem;
import fw.lobby.group.trigger.Sneak;
import fw.lobby.group.trigger.TimeUp;
import fw.lobby.group.trigger.TriggerBaseExt;
import fw.lobby.group.trigger.WalkOnBlock;

public class Trigger {
	Group byGroup;
	List<TriggerBaseExt> TriggerList = new ArrayList<TriggerBaseExt>();

	public Trigger(Group group) {
		byGroup = group;
	}

	public List<TriggerBaseExt> getTriggerList() {
		return TriggerList;
	}

	public void load(Group byGroup) {
		while (TriggerList.size() > 0) {
			HandlerList.unregisterAll(TriggerList.get(0));
			TriggerList.remove(0);
		}
		// unload
		Set<String> List = byGroup.getFileConf().getConfigurationSection("Trigger").getKeys(false);
		String[] TriggerList = List.toArray(new String[List.size()]);
		for (int a = 0; a < TriggerList.length; a++) {
			if (byGroup.getFileConf().contains("Trigger." + TriggerList[a] + ".Type")) {
				String type = byGroup.getFileConf().getString("Trigger." + TriggerList[a] + ".Type");
				TriggerBaseExt trigger;
				switch (type) {
				case "Death":
					trigger = new Death(TriggerList[a]);
					break;
				case "Sneak":
					trigger = new Sneak(TriggerList[a]);
					break;
				case "PickupItem":
					trigger = new PickupItem(TriggerList[a]);
					break;
				case "WalkOnBlock":
					trigger = new WalkOnBlock(TriggerList[a]);
					break;
				case "KillEntity":
					trigger = new KillEntity(TriggerList[a]);
					break;
				case "KillPlayer":
					trigger = new KillPlayer(TriggerList[a]);
					break;
				case "Diss":
					trigger = new Diss(TriggerList[a]);
					break;
				case "TimeUp":
					trigger = new TimeUp(TriggerList[a]);
					break;
				case "Offline":
					trigger = new Offline(TriggerList[a]);
					break;
				case "KillAllEntity":
					trigger = new KillAllEntity(TriggerList[a]);
					break;
				default:
					Data.Debug("读取" + byGroup.Name + "触发器" + TriggerList[a] + "类型无效。");
					return;
				}
				if (trigger.Load(byGroup)) {
					this.TriggerList.add(trigger);
				} else {
					Data.Debug(byGroup.Name + "触发器" + TriggerList[a] + "数据缺失。");
				}
			} else {
				Data.Debug(byGroup.Name + "触发器" + TriggerList[a] + "数据缺失。");
			}

		}
	}
}
