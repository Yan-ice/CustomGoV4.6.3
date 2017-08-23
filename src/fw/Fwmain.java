package fw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fw.lobby.Basic;
import fw.lobby.Lobby;
import fw.lobby.JoinSign;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;
import fw.lobby.task.ItemTask;
import fw.location.FLocation;
import net.milkbowl.vault.economy.Economy;

public class Fwmain extends JavaPlugin implements Listener {

	public FileConfiguration load(File file) {
		if (!file.exists()) {
			saveResource(file.getName(), true);
		}
		return YamlConfiguration.loadConfiguration(file);

	}
	
	private void Fwcommands(Player player, String args[]) {
		if(args.length < 2){
			switch(args[0]){
			case "list":
				if (CheckPerm(player, "fw.list")) {
					showList(player);
				}
				break;
			case "debug":
				if (CheckPerm(player, "fw.admin")) {
					if(Data.debug){
						Data.debug = false;
						player.sendMessage(fw.Language.getMessage("debug_off"));
					}else{
						Data.debug = true;
						player.sendMessage(fw.Language.getMessage("debug_on"));
					}

				}
				break;	
			case "reload":
				if (CheckPerm(player, "fw.reload")) {
					Reload(player);
				}
				break;
			case "leave":
				if (CheckPerm(player, "fw.leave")) {
					Basic.AutoLeave(FPlayer.getFPlayer(player));
				}
				break;
			case "help":
				if (CheckPerm(player, "fw.help")) {
					Help.MainHelp(player);
					Help.LobbyHelp(player);
				}
				break;
			case "score":
				if (FPlayer.getFPlayer(player)!=null && CheckPerm(player, "fw.score")) {
					FPlayer.getFPlayer(player).lookScore();
				}
				break;
			default:
				player.sendMessage(fw.Language.getMessage("commanderror_unknown"));
			}
		}else{
			String Name = args[0];
			Lobby lobby = Lobby.getLobby(Name);
			new FLocation(player);
			if (args.length < 2) {
				Help.LobbyHelp(player);
			} else {
				if (args[1].equalsIgnoreCase("create")) {
					if (CheckPerm(player, "fw.create")) {
						Lobby.Create(player, Name);
					}
				}else if (args[1].equalsIgnoreCase("remove")) {
					if (CheckPerm(player, "fw.remove")) {
						Lobby.Remove(player, Name);
					}
				}else if (args[1].equalsIgnoreCase("load")) {
					if (CheckPerm(player, "fw.load")) {
						if(lobby.Load()){
							player.sendMessage(fw.Language.getMessage("load.succeed"));
						}else{
							player.sendMessage(fw.Language.getMessage("load.failed"));
						}
					}
				}else if (lobby != null && lobby.isComplete()) {
					switch (args[1]) {
					case "join":
						if (player.hasPermission("fw.join." + args[0]) || CheckPerm(player, "fw.join")) {
							FPlayer f;
							if(FPlayer.getFPlayer(player)!=null){
								f = FPlayer.getFPlayer(player);
							}else{
								f = new FPlayer(player);
							}

							lobby.JoinLobby(f);
						}
						break;
					case "statu":
						if (CheckPerm(player, "fw.statu")) {
							lobby.State(player);
						}

						break;
					case "addsign":
						if (player.getEyeLocation() != null) {
							if (lobby.NewSign(getTargetBlock(player).getLocation())) {
								player.sendMessage(fw.Language.getMessage("sign_create_success"));
							} else {
								player.sendMessage(fw.Language.getMessage("sign_create_failed"));
							}
						}
						break;
					case "addgroup":
						Group.Create(player, args[2], lobby);
						break;
					default:
						player.sendMessage(fw.Language.getMessage("commanderror_unknown"));
					}
						
				} else {
					player.sendMessage(ChatColor.RED +fw.Language.getMessage("notcomplete"));
				}
			}
		}
	}
/**
 * 发送大厅列表。
 * @param player 要发送的玩家
 */
	public void showList(CommandSender player) {
		String Glist = "";
		List<Lobby> Lobbylist = Lobby.getLobbyList();
		for (int a = 0; a < Lobbylist.size(); a++) {
			String Name;
			if (Lobbylist.get(a).isComplete()) {
				Name = ChatColor.GREEN + Lobbylist.get(a).Name + ChatColor.AQUA;
			} else {
				Name = ChatColor.RED + Lobbylist.get(a).Name + ChatColor.AQUA;
			}
			if (Glist != "") {
				Glist = Glist + "、" + Name;
			} else {
				Glist = Name;
			}
		}
		player.sendMessage(ChatColor.AQUA + fw.Language.getMessage("lobby_showlist")+":");
		player.sendMessage(Glist);
	}
/**
 * 检查一个玩家的一项权限。
 * fw.admin可以直接通过检查而无需验证是否有需要权限。
 * @param player 被检查的玩家
 * @param Permission 需要检查的权限
 * @return 是否通过检查
 */
	public static boolean CheckPerm(Player player, String Permission) {
		if (player.hasPermission("fw.admin") || player.hasPermission(Permission)) {
			return true;
		} else {
			player.sendMessage(fw.Language.getMessage("commanderror_nopermission"));
			return false;
		}
	}

	public void onEnable() {
		Data.fwmain = this;
		LoadFile();
		SendToData();
		fw.Language.SetLanguage(Data.language,fw.Language.getMessage("lobby_create").equals(""));
		Lobby.LoadAll(lobby);
		ItemTask.LoadAll(itemd);
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			isEco = setupEconomy();
		} else {
			getLogger().info(fw.Language.getMessage("enable_noVault"));
		}

		getServer().getPluginManager().registerEvents(this, this);
		if(lobby.listFiles().length==0){
			Lobby.Create(null, "ExampleLobby");
		}
		getLogger().info(fw.Language.getMessage("enable_success")+" [FW_Go " + Data.Version + " ]");
	}

	public void onDisable() {
		getLogger().info(fw.Language.getMessage("disable_clear"));
		List<Lobby> lobby = Lobby.getLobbyList();
		for(int a = 0;a<lobby.size();a++){
			List<FPlayer> pl = lobby.get(a).GetPlayerList();
			for(int b=0;b<pl.size();b++){
				pl.get(b).ToMC().teleport(lobby.get(a).getLeave().ToMC());
			}
		}
		ItemTask.UnLoadAll();
		HandlerList.unregisterAll();
		getLogger().info(fw.Language.getMessage("disable_success"));
	}
/**
 * 重载插件
 * @param sender 发送重载信息的对象
 */
	public void Reload(CommandSender sender) {
		Lobby.UnLoadAll();
		ItemTask.UnLoadAll();
		LoadFile();
		SendToData();
		fw.Language.SetLanguage(Data.language,fw.Language.getMessage("lobby_create").equals(""));
		Lobby.LoadAll(lobby);
		ItemTask.LoadAll(itemd);
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			isEco = setupEconomy();
		} else {
			getLogger().info(fw.Language.getMessage("enable_noVault"));
		}
		sender.sendMessage(fw.Language.getMessage("reload_success")+"[CustomGo " + Data.Version + " ]");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if(Data.debug){
					if(CheckPerm(player,"fw.debug")){
						switch(label){
						case "fw":
							Fwcommands(player, args);
							break;
						case "fwc":
							if(Group.SearchPlayerInGroup(FPlayer.getFPlayer(player))!=null){
								String v = "";
								for(int a = 0;a<args.length;a++){
									v = v+args[a]+" ";
								}
								FPlayer.getFPlayer(player).chatInList(v,Group.SearchPlayerInGroup(FPlayer.getFPlayer(player)).GetPlayerList());
							}
							break;
						}

					}else{
						player.sendMessage(fw.Language.getMessage("commanderror_debug"));
					}
				}else{
					switch(label){
					case "fw":
						Fwcommands(player, args);
						break;
					case "fwc":
						if(Group.SearchPlayerInGroup(FPlayer.getFPlayer(player))!=null){
							String v = "";
							for(int a = 0;a<args.length;a++){
								v = v+args[a]+" ";
							}
							FPlayer.getFPlayer(player).chatInList(v,Group.SearchPlayerInGroup(FPlayer.getFPlayer(player)).GetPlayerList());
						}
						break;
					}
				}
			} else {
				sender.sendMessage(fw.Language.getMessage("commanderror_console"));
			}
		} else {
			sender.sendMessage(fw.Language.getMessage("commanderror_unknown"));
		}
		return false;
	}

	public static Economy economy;

	protected File lobby = new File(getDataFolder(), "lobby");
	protected File itemd = new File(getDataFolder(), "itemtask");

	protected File option;
	protected static FileConfiguration optionfile;
	protected File Language;
	protected static FileConfiguration Languagefile;

	private void LoadFile() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		if (!lobby.exists()) {
			lobby.mkdir();
		}
		if (!itemd.exists()) {
			itemd.mkdir();
		}
		option = new File(getDataFolder(), "Option.yml");
		optionfile = load(option);
		
		Language = new File(getDataFolder(), "Language.yml");
		if(!Language.exists()){
			try {
				Language.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		Languagefile = load(Language);
	}

	private void SendToData() {
		Data.fwmain = this;

		Data.optionFile = option;
		Data.lobbyDir = lobby;
		Data.itemDir = itemd;

		Data.optionFileConf = optionfile;
		
		Data.Language = Language;
		Data.LanguageFileConf = Languagefile; 
		
		Data.economy = economy;
		Data.LoadOption();
	}

	private Block getTargetBlock(Player player) {
		HashSet<Byte> hs = new HashSet<>();
		hs.add((byte) 0);
		Block bl = player.getTargetBlock(hs, 6);
		return bl;
	}

	protected boolean isEco = false;

	protected boolean setupEconomy() {
		RegisteredServiceProvider<Economy> eco = getServer().getServicesManager().getRegistration(Economy.class);
		if (eco != null) {
			economy = (eco.getProvider());
		}
		return economy != null;
	}
}
