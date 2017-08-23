package fw.lobby.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fw.Data;
import fw.lobby.Lobby;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;

public class ItemTask implements Listener {
	public static List<ItemTask> l = new ArrayList<ItemTask>();

	ItemStack Item;
	List<String> Task;
	String Name;
	int Cost;
	boolean isComplete = false;
	boolean consume = false;

	public File GetFile() {
		File[] list = Data.itemDir.listFiles();
		for (int a = 0; a < list.length; a++) {
			if (list[a].getName().equals(Name + ".yml")) {
				return list[a];
			}
		}
		return null;
	}

	public static ItemTask getItemTask(String name) {
		for (int a = 0; a < l.size(); a++) {
			if (l.get(a).Name.equals(name)) {
				return l.get(a);
			}
		}
		return null;
	}

	public ItemTask(String Name) {
		this.Name = Name;
	}

	public void SetValue(int ID, String ItemName, List<String> ItemLore, List<String> Task) {
		try {
			Item = new ItemStack(ID);
			ItemMeta meta = Item.getItemMeta();
			meta.setDisplayName(ItemName);
			meta.setLore(ItemLore);
			Item.setItemMeta(meta);
			this.Task = Task;
		} catch (NullPointerException e) {
			return;
		}
		isComplete = true;
		Data.fwmain.getServer().getPluginManager().registerEvents(this, Data.fwmain);
	}

	public void LoadFile() {
		try {
			if (GetFile() != null) {
				File f = GetFile();
				FileConfiguration fil = YamlConfiguration.loadConfiguration(f);
				SetValue(fil.getInt("id"), Data.ColorChange(fil.getString("ItemName")),
						Data.ColorChange(fil.getStringList("Lore")), fil.getStringList("Task"));
				if(fil.contains("Consume")){
					consume = fil.getBoolean("Consume");
				}
			} else {
				Data.ConsoleInfo("文件没有找到！");
			}
		} catch (NullPointerException e) {
			return;
		}

	}

	public static void UnLoadAll() {
		while (l.size() > 0) {
			HandlerList.unregisterAll(l.get(0));
			l.remove(0);
		}
	}

	public static void LoadAll(File dir) {
		UnLoadAll();
		File[] list = dir.listFiles();
		List<ItemTask> l = new ArrayList<ItemTask>();
		if (list.length > 0) {
			for (int a = 0; a < list.length; a++) {
				ItemTask i = new ItemTask(list[a].getName().split("\\.")[0]);
				i.LoadFile();
				l.add(i);
			}
			ItemTask.l = l;
		}
	}

	public void Give(FPlayer player) {
		if (isComplete) {
			if(Cost>0){
				if(!player.ConsumeScore(Cost)){
					player.sendMessage(ChatColor.YELLOW+"您花费了"+Cost+"点数来购买它！");
					return;
				}else{
					player.sendMessage(ChatColor.RED+"您没有足够的点数购买它！");
				}
			}
			Inventory inv = player.getInventory();
			for (int a = 0; a < inv.getSize(); a++) {
				if (inv.getItem(a) == null) {
					inv.setItem(a, Item);
					return;
				}
			}
		} else {
			player.sendMessage(ChatColor.RED+"您无法获得物品" + Name + "。因为它的设置是错误的。");
		}

	}

	@EventHandler
	private void IListen(PlayerInteractEvent evt) {
		if (evt.hasItem()) {
			if (evt.getItem().equals(Item)) {
				if (FPlayer.getFPlayer(evt.getPlayer())!=null && Group.SearchPlayerInGroup(FPlayer.getFPlayer(evt.getPlayer())) != null) {
					if(consume){
						if(evt.getItem().getAmount()>1){
							evt.getItem().setAmount(evt.getItem().getAmount()-1);
						}else{
							evt.getPlayer().getInventory().clear(evt.getPlayer().getInventory().getHeldItemSlot());
						}
					}
					GroupTaskRunner task = new GroupTaskRunner(Task, Group.SearchPlayerInGroup(FPlayer.getFPlayer(evt.getPlayer())),
							FPlayer.getFPlayer(evt.getPlayer()));
					task.run();
				}
			}
		}
	}
}
