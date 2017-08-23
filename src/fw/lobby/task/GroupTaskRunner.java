package fw.lobby.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fw.Data;
import fw.lobby.Basic;
import fw.lobby.Lobby;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;
import fw.location.FLocation;
import fw.location.Teleporter;

public class GroupTaskRunner extends BukkitRunnable {
	List<String> list = new ArrayList<String>();
	List<FPlayer> playerlist = new ArrayList<FPlayer>();
	Basic group;
	FPlayer striker;

	
	public GroupTaskRunner(List<String> TaskList, Basic byGroup, FPlayer striker) {
		this.group = byGroup;
		this.striker = striker;
		for (int a = 0; a < TaskList.size(); a++) {
			list.add(TaskList.get(a));
		}
		playerlist = byGroup.GetPlayerList();

	}

	public GroupTaskRunner clone() {
		return new GroupTaskRunner(list, group, striker);
	}

	@Override
	public void run() {
		while (list.size() > 0) {
			String task = list.get(0);
			list.remove(0);
			if (!Execute(task)) {
				Data.Debug("一项Task已被取消！队列:" + group.Name + " 被取消任务:" + task);
			}
		}
		ShutDown();
	}

	public boolean Execute(String args) {
		
		String label = null;
		String value = null;
		String target = "";
		if (args.indexOf("{") != -1) {
			label = args.split("\\{")[0];
		} else {
			return false;
		}
		if (args.indexOf("}") != -1) {
			value = args.split("\\{")[1];
			value = value.split("\\}")[0];
			if (value == "" || value == " ") {
				value = null;
			}
		} else {
			return false;
		}
		List<FPlayer> list;
		if (args.indexOf("@") != -1) {
			target = ("@" + args.split("@")[1]);
			PlayerChooser chooser = new PlayerChooser(playerlist,striker);
			list = chooser.Choose(target);
		}else{
			list = null;
		}

		if(list!=null && list.size()>0){
			for (int a = 0; a < list.size(); a++) {
				Calculater cal = new Calculater(group,list.get(a),striker);
				String valuen = cal.ValueChange(value);
				if (!RunTask(label, valuen, list.get(a))) {
					return false;
				}
			}
		}else{
			Calculater cal = new Calculater(group,null,striker);
			value = cal.ValueChange(value);
			if (!RunTask(label, value, null)) {
				return false;
			}
		}

		return true;
	}

	private boolean RunTask(String label, String value, FPlayer target) {
		Player Target = null;
		if(target!=null){
			Target = target.ToMC();
		}

		if (label == null) {
			return false;
		}
		switch (label) {
		case "leave":
			if (Target == null) {
				return false;
			}
			Basic.AutoLeave(target);
			break;
		case "command":
			if (value == null) {
				return false;
			}
			CommandRunner(value, Target);
			break;
		case "tell":
			if (value == null | Target == null) {
				return false;
			}
			Target.sendMessage(value);
			break;
		case "title":
			if (value == null | Target == null || value.indexOf(",")==-1) {
				return false;
			}
			Target.sendTitle(value.split(",")[0],value.split(",")[1]);
			break;
		case "delay":
			if (value == null) {
				return false;
			}
			try {
				Delay(Integer.valueOf(value));
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "heal":
			if (Target == null) {
				return false;
			}
			if ((value != null) && (value.trim() != "")) {
				try {
					Heal(Target, Integer.valueOf(value));
				} catch (NumberFormatException a) {
					return false;
				}
			} else {
				Heal(Target, null);
			}
			break;
		case "maxhp":
			if (Target == null) {
				return false;
			}
			if ((value != null) && (value.trim() != "")) {
				try {
					Target.setMaxHealth(Double.parseDouble(value));
				} catch (NumberFormatException a) {
					return false;
				}
			} else {
				Target.setMaxHealth(20);
			}
			break;
		case "food":
			if (Target == null) {
				return false;
			}
			if (value != null) {
				try {
					Food(Target, Integer.valueOf(value));
				} catch (NumberFormatException a) {
					return false;
				}
			} else {
				Food(Target, null);
			}
			break;
		case "damage":
			if (Target == null || value == null) {
				return false;
			}
			try {
				Damage(Target, Integer.valueOf(value));
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "potion":
			if (Target == null || value == null) {
				return false;
			}
			Potion(Target, value);
			break;	
		case "taskitem":
			if (Target == null || value == null) {
				return false;
			}
			GiveTaskItem(target, value);
			break;
		case "teleport":
			if (Target == null || value == null) {
				return false;
			}
			Teleport(target, value);
			break;
		case "addscore":
			if (Target == null || value == null) {
				return false;
			}
			try {
			target.AddScore(Integer.valueOf(value));
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "delscore":
			if (Target == null || value == null) {
				return false;
			}
			try {
			target.DelScore(Integer.valueOf(value));
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "setscore":
			if (Target == null || value == null) {
				return false;
			}
			try {
			target.SetScore(Integer.valueOf(value));
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "addgroupscore":
			if (value == null) {
				return false;
			}
			try {
				if(group instanceof Group){
					((Group)group).AddScore(Integer.valueOf(value));
				}
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "delgroupscore":
			if (value == null) {
				return false;
			}
			try {
				if(group instanceof Group){
					((Group)group).DelScore(Integer.valueOf(value));
				}
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "setgroupscore":
			if (value == null) {
				return false;
			}
			try {
				if(group instanceof Group){
					((Group)group).SetScore(Integer.valueOf(value));
				}
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "gronotice":
			if (value == null) {
				return false;
			}
			if(group instanceof Lobby){
				for(int a = 0;a<((Lobby)group).getLobbyGroup().size();a++){
					((Lobby)group).getLobbyGroup().get(a).SendNotice(value);
				}
			}else if(group instanceof Group){
				((Group)group).SendNotice(value);
			}
			break;
		case "divnotice":
			if (value == null) {
				return false;
			}
			if(group instanceof Lobby){
				((Lobby)group).SendNotice(value);
			}else if(group instanceof Group){
				((Group)group).byLobby().SendNotice(value);
			}
			
			break;
		case "addtag":
			if (target == null || value == null) {
				return false;
			}
			target.addTag(value);
			break;
		case "deltag":
			if (target == null || value == null) {
				return false;
			}
			target.removeTag(value);
			break;
		case "cleartag":
			if (target == null || value == null) {
				return false;
			}
			target.clearTag();
			break;
		default:
			Data.ConsoleInfo("出现无法识别的Task指令"+label+"？");
			return false;
		}
		return true;
	}

	private void Potion(Player target, String value) {
		String[] args = value.split(",");
		if(args.length>2){
			PotionEffectType eff;
			switch(args[0]){
			case "speed":
			eff = PotionEffectType.SPEED;
			break;
			case "slow":
			eff = PotionEffectType.SLOW;
			break;
			case "invisibility":
			eff = PotionEffectType.INVISIBILITY;
			break;
			case "damage_resistance":
			eff = PotionEffectType.DAMAGE_RESISTANCE;
			break;
			case "wither":
			eff = PotionEffectType.WITHER;
			break;
			case "poison":
			eff = PotionEffectType.POISON;
			break;
			case "harm":
			eff = PotionEffectType.HARM;
			break;
			case "heal":
			eff = PotionEffectType.HEAL;
			break;
			case "night_vision":
			eff = PotionEffectType.NIGHT_VISION;
			break;
			case "fire_resistance":
			eff = PotionEffectType.FIRE_RESISTANCE;
			break;
			case "confusion":
			eff = PotionEffectType.CONFUSION;
			break;
			case "jump":
			eff = PotionEffectType.JUMP;
			break;
			case "blindness":
			eff = PotionEffectType.BLINDNESS;
			break;
			case "increase_damage":
			eff = PotionEffectType.INCREASE_DAMAGE;
			break;
			case "health_boost":
			eff = PotionEffectType.HEALTH_BOOST;
			break;
			default:
			eff = PotionEffectType.SPEED;
			break;
			}
			try {
				target.addPotionEffect(new PotionEffect(eff,Integer.valueOf(args[1]),Integer.valueOf(args[2])));
			} catch (NumberFormatException a) {
				
			}
		}
		return;
	}

	private void CommandRunner(String command, Player Target) {
		if (Target != null) {
			if (Target.isOp()) {
				Target.chat("/" + command);
			} else {
				Target.setOp(true);
				Target.chat("/" + command);
				Target.setOp(false);
			}
		} else {
			Data.ConsoleCommand(command);
		}
	}

	private void Delay(int delay) {
		List<String> newl = new ArrayList<String>();
		while (list.size() > 0) {
			newl.add(list.get(0));
			list.remove(0);
		}
		GroupTaskRunner newRunner = new GroupTaskRunner(newl, group, striker);
		newRunner.runTaskLater(Data.fwmain, delay);
	}

	private void Heal(Player player, Integer amount) {
		if (amount != null) {
			if (((Damageable)player).getHealth() + amount <= ((Damageable)player).getMaxHealth()) {
				player.setHealth(((Damageable)player).getHealth() + amount);
			} else {
				player.setHealth(((Damageable)player).getMaxHealth());
			}
		} else {
			player.setHealth(((Damageable)player).getMaxHealth());
		}
	}

	private void Food(Player player, Integer amount) {
		if (amount != null) {
			if (player.getFoodLevel() + amount <= 20) {
				player.setFoodLevel(player.getFoodLevel() + amount);
			} else {
				player.setFoodLevel(20);
			}
		} else {
			player.setFoodLevel(20);
		}
	}

	private void GiveTaskItem(FPlayer player, String Name) {
		if (ItemTask.getItemTask(Name) != null) {
			ItemTask.getItemTask(Name).Give(player);
		} else {
			Data.ConsoleInfo("任务执行物品不存在。");
		}
	}

	private void Damage(Player player, Integer amount) {
		if (((Damageable)player).getHealth() > amount) {
			player.setHealth(player.getHealthScale()*20 - amount);
		} else {
			player.setHealth(0);
		}
	}

	private void Teleport(FPlayer player, String Location) {
		Teleporter tel = new Teleporter(player);
		FLocation Loc = new FLocation(Location);
		tel.Teleport(Loc, true);
	}

	public void ShutDown() {
		cancel();
	}

}
