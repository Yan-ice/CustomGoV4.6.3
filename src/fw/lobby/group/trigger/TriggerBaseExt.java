package fw.lobby.group.trigger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import org.bukkit.event.Listener;

import fw.Data;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;
import fw.lobby.task.GroupTaskRunner;

public abstract class TriggerBaseExt implements Listener {
	String Name;
	Group byGroup;
	List<String> Task;
	List<String> Id = new ArrayList<String>();

	public TriggerBaseExt(String Name) {
		this.Name = Name;
		Data.fwmain.getServer().getPluginManager().registerEvents(this, Data.fwmain);
	}

	public List<String> getTask() {
		return Task;
	}

	public Group fromGroup() {
		return byGroup;
	}

	void setId(List<String> a) {
		Id = a;
	}

	public List<String> getId() {
		return Id;
	}

	public boolean Load(Group byGroup) {
		this.byGroup = byGroup;
		try {
			if (byGroup.getFileConf().contains("Trigger." + Name + ".Id")) {
				Id = byGroup.getFileConf().getStringList("Trigger." + Name + ".Id");
			}
			Task = byGroup.getFileConf().getStringList("Trigger." + Name + ".Task");
		} catch (NullPointerException a) {
			return false;
		}
		return true;
	}

	public void Strike(Player player) {
		if(player != null){
			Data.Debug("玩家"+player.getName()+"触发了"+Name+"。");
			GroupTaskRunner task = new GroupTaskRunner(Task, byGroup, FPlayer.getFPlayer(player));
			task.runTask(Data.fwmain);
		}else{
			Data.Debug("触发了"+Name+"。");
			GroupTaskRunner task = new GroupTaskRunner(Task, byGroup, null);
			task.runTask(Data.fwmain);
		}

	}
}
