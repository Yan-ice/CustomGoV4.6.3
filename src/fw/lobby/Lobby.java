package fw.lobby;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import fw.Data;
import fw.Fwmain;
import fw.Language;
import fw.lobby.task.GroupTaskRunner;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;
import fw.location.FLocation;
import fw.location.Teleporter;

public class Lobby extends Basic {

	private static List<Lobby> list = new ArrayList<Lobby>();

	public static List<Lobby> getLobbyList() {
		return list;
	}

	private List<Group> Divgroup = new ArrayList<Group>();
	private LobbyTimerControl timer = new LobbyTimerControl(this);
	private FLocation LobbyLoc;
	private FLocation LeaveLoc;
	private boolean Bind = false;
	private int JoinPrice = 0;
	private boolean FreeJoin = false;
	private boolean CanBringItem = true;
	
	private boolean CanJoin;
	
	/**
	 * 通过分配厅名获取一个大厅。
	 * @param groupa 大厅名
	 * @return 得到的大厅，如果不存在则返回null。
	 */
	public static Lobby getLobby(String groupa) {
		if (list != null) {
			for (int cc = 0; cc < list.size(); cc++) {
				if (list.get(cc).Name.equalsIgnoreCase(groupa)) {
					return list.get(cc);
				}
			}
		}
		return null;
	}

	/**
	 * 取消掉所有分配大厅。
	 */
	public static void UnLoadAll() {
		while (list.size() > 0) {
			list.get(0).Unload();
		}
	}
/**
 * 创建一个大厅.
 * @param Pl 创建的玩家(输出创建信息,获取坐标提供为默认值)
 * @param LobbyName 大厅名字
 */
	public static void Create(Player Pl, String LobbyName){
		if (getDir(LobbyName) != null) {
			Pl.sendMessage(ChatColor.RED + Language.getMessage("lobby_create_failed"));
			return;
		}
		try {
			File dir = new File(Data.lobbyDir, LobbyName);
			dir.mkdir();
			File file = new File(dir,"LobbyOption.yml");
			file.createNewFile();
			FileWriter bw = new FileWriter(file, true);
			String world;
			double X;
			double Y;
			double Z;
			if(Pl != null){
				world = Pl.getWorld().getName();
				X = Pl.getLocation().getX();
				Y = Pl.getLocation().getY();
				Z = Pl.getLocation().getZ();
			}else{
				world = Data.fwmain.getServer().getWorlds().get(0).getName();
				X=0;
				Y=0;
				Z=0;
			}
			X = (double) ((int) (X * 100)) / 100;
			Z = (double) ((int) (Z * 100)) / 100;
			if(Language.getLanguage().equals("Chinese")){
				bw.write("Display: "+LobbyName+"大厅\n");
				bw.write("#在这里编辑显示名：在牌子上，文字提示中都有用到显示名。\n");
				bw.write("Timer:\n");
				bw.write("  LobbyTime: 60\n");
				bw.write("  Full_LobbyTime: 10\n");
				bw.write("#在这里编辑大厅的等待时间。\n");
				bw.write("ControlTask:\n");
				bw.write("#以下是队列任务控制器设置，与队列同理。\n");
				bw.write("  onPlayerJoin:\n");
				bw.write("  - tell{&e<striker>加入了<display>！} @a\n");
				bw.write("  onPlayerFreeJoin:\n");
				bw.write("  - tell{&c该队列为自由加入队列，您已被直接分配！} @p\n");	
				bw.write("  onPlayerLeave:\n");
				bw.write("  - tell{&e<striker>离开了<display>！} @a\n");
				bw.write("  - tell{&e你离开了<display>！} @p\n");	
				bw.write("  onTimePast(30):\n");
				bw.write("  - tell{<display>将在30秒后开始！} @a\n");
				bw.write("  onTimePast(10):\n");
				bw.write("  - tell{<display>将在10秒后开始！} @a\n");
				bw.write("  onLobbyStart:\n");
				bw.write("  - tell{&c<display>队列现在开始分配玩家！} @a\n");
				bw.write("JoinCost:\n");
				bw.write("  Price: 0\n");
				bw.write("#在这里设置加入的花费，设置为0以关闭花费。\n");
				bw.write("  Pay_Message: 您花费了<money>$加入了<name>！\n");	
				bw.write("  NotEnough_Message: 您没有足够金钱加入<name>！(<money>$)\n");		
				bw.write("SkipLobby: false\n");
				bw.write("#在这里设置是否跳过大厅的干预。\n");
				bw.write("#如果开启，加入时直接分配到队列，离开时直接离开到大厅离开点。\n");	
				bw.write("#该项设置便于不需要大厅分配的队列。\n");			
				bw.write("#注意:如果分配的队列中有任何一个是自由加入队列，\n");
				bw.write("#那么这个大厅会锁定开启自由加入！\n");		
				bw.write("Bind: false\n");
				bw.write("#注意:自由加入大厅锁定Bind为false!\n");		
				bw.write("Lobby: " + X + "," + Y + "," + Z + "," + world + "\n");
				bw.write("Leave: " + X + "," + Y + "," + Z + "," + world + "\n");
				bw.write("JoinSign:\n");
				bw.write("- 0,0,0,"+world+"\n");
			}else{
				bw.write("Display: "+LobbyName+" Lobby\n");
				bw.write("#Edit the display name here.\n");
				bw.write("Timer:\n");
				bw.write("  LobbyTime: 60\n");
				bw.write("  Full_LobbyTime: 10\n");
				bw.write("#Edit the timer about Lobby。\n");
				bw.write("ControlTask:\n");
				bw.write("#The task controller here.Same as the group task controller.\n");
				bw.write("  onPlayerJoin:\n");
				bw.write("  - tell{&ePlayer <striker> joined the <display>!} @a\n");
				bw.write("  onPlayerFreeJoin:\n");
				bw.write("  - tell{&cThis is free to join.You join the group now.} @p\n");	
				bw.write("  onPlayerLeave:\n");
				bw.write("  - tell{&ePlayer <striker> leaved the <display>!} @a\n");
				bw.write("  - tell{&eYou have leaved <display>!} @p\n");	
				bw.write("  onTimePast(30):\n");
				bw.write("  - tell{<display> will start in 30 seconds!} @a\n");
				bw.write("  onTimePast(10):\n");
				bw.write("  - tell{<display> will start in 10 seconds!} @a\n");
				bw.write("  onLobbyStart:\n");
				bw.write("  - tell{&c<display> is started!} @a\n");
				bw.write("JoinCost:\n");
				bw.write("  Price: 0\n");
				bw.write("  #Edit the payment to join。\n");
				bw.write("  Pay_Message: You paid <money>$ to join <name>!\n");	
				bw.write("  NotEnough_Message: You don't have enough money to join <name>!(<money>$)\n");		
				bw.write("SkipLobby: false\n");
				bw.write("#Set whether to skip the Lobby here\n");
				bw.write("#If enabled,the player don't need to wait for this Lobby.\n");	
				bw.write("#This setting is convenient for groups that don't need the Lobby.\n");			
				bw.write("#Warning:If any group in Lobby is free to join,\n");
				bw.write("#This setting will lock in \"true\".\n");		
				bw.write("Bind: false\n");
				bw.write("#Warning:If the SkipLobby setting is \"true\",This setting will lock in \"false\".\n");		
				bw.write("Lobby: " + X + "," + Y + "," + Z + "," + world + "\n");
				bw.write("Leave: " + X + "," + Y + "," + Z + "," + world + "\n");
				bw.write("JoinSign:\n");
				bw.write("- 0,0,0,"+world+"\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto_generated catch block
			e.printStackTrace();
		}
		if(Pl!=null){
			Pl.sendMessage(ChatColor.YELLOW +Language.getMessage("lobby_create"));
		}

		Lobby Lobby = new Lobby(LobbyName);
		list.add(Lobby);
		Group.Create(Pl, "ExampleGroup",Lobby);
		Lobby.Load();
	}

	/**
	 * 将该大厅从大厅列表中移除,但不删除配置。
	 */
	public void Unload() {
		if (isComplete) {
			Clear();
		}
		list.remove(this);
	}
/**
 * 删除一个指定的大厅。
 * @param Pl 执行删除的玩家(发送信息)
 * @param LobbyName 要删除的分配厅名
 */
	public static void Remove(Player Pl, String LobbyName) {
		getLobby(LobbyName).Clear();
		list.remove(LobbyName);
		GetOptionFile(LobbyName + ".yml").delete();
		Pl.sendMessage(ChatColor.YELLOW + Language.getMessage("lobby_remove"));
	}

	/**
	 * 重载所有在大厅列表中的队列。
	 */
	public static Lobby ReloadAll(String groupa) {
		if (list != null && list.size() > 0) {
			for (int cc = 0; cc < list.size(); cc++) {
				if (list.get(cc).Name.equalsIgnoreCase(groupa)) {
					return list.get(cc);
				}
			}
		}
		return null;
	}

	/**
	 * 读取所有大厅文件。
	 * 
	 * @param dir
	 *            指定的文件夹。
	 */
	public static void LoadAll(File dir) {
		File[] list;
		if (dir != null && dir.isDirectory()) {
			list = dir.listFiles();
			if(list.length==0){
				Create(null,"ExampleLobby");
			}
			for (int a = 0; a < list.length; a++) {
				Load(list[a]);
			}

		}

	}

	/**
	 * 读取一个指定的大厅文件。
	 * 
	 * @param file
	 *            指定的队列。
	 */
	public static void Load(File file) {
		if (file != null) {
			String Name = file.getName();
			Lobby gro = new Lobby(Name);
			gro.Load();
			list.add(gro);
		}
	}
	/**
	 * 获取一个玩家所在的大厅。
	 * @param player 需要查看大厅的玩家
	 * @return 玩家所在的大厅，如果不在队列则返回null。
	 */
	public static Lobby SearchPlayerInLobby(FPlayer player) {
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a).hasPlayer(player)) {
				return list.get(a);
			}
		}
		return null;
	}


	public File getDir(){
		if (Data.lobbyDir != null) {
			File[] list = Data.lobbyDir.listFiles();
			for (int a = 0; a < list.length; a++) {
				if (list[a].getName().equals(Name) && list[a].isDirectory()) {
					return list[a];
				}
			}
		}
		return null;
	}
	public static File getDir(String LobbyName){
		if (Data.lobbyDir != null) {
			File[] list = Data.lobbyDir.listFiles();
			for (int a = 0; a < list.length; a++) {
				if (list[a].getName().equals(LobbyName) && list[a].isDirectory()) {
					return list[a];
				}
			}
		}
		return null;
	}
	public File GetOptionFile() {
		if(getDir() != null){
			File[] FileList = getDir().listFiles();
			for(int b = 0;b<FileList.length;b++){
				if(FileList[b].getName().equals("LobbyOption.yml")){
					return FileList[b];
				}
			}
		}
		return null;
	}

	public FileConfiguration getFileConf() {
		if (GetOptionFile() != null) {
			return YamlConfiguration.loadConfiguration(GetOptionFile());
		} else {
			return null;
		}

	}

	public static File GetOptionFile(String FileName) {
		if(getDir(FileName) != null){
			File[] FileList = getDir(FileName).listFiles();
			for(int b = 0;b<FileList.length;b++){
				if(FileList[b].getName().equals("LobbyOption。yml")){
					return FileList[b];
				}
			}
		}
		return null;
	}

	public static FileConfiguration getFileConf(String FileName) {
		if (GetOptionFile(FileName) != null) {
			return YamlConfiguration.loadConfiguration(GetOptionFile(FileName));
		} else {
			return null;
		}
	}

	public Lobby(String Name) {
		this.Name = Name;
	}

	public Lobby(String Name, String Display, LobbyTimerControl timer, List<Group> Lobby, FLocation Loc, boolean Bind) {
		this.Name = Name;
		this.Divgroup = Lobby;
		this.Bind = Bind;
		this.LobbyLoc = Loc;
		int MaxPlayer = 0;
		int MinPlayer = 0;
		for (int a = 0; a < Lobby.size(); a++) {
			MaxPlayer = MaxPlayer + Lobby.get(a).GetMaxPlayer();
			MinPlayer = MinPlayer + Lobby.get(a).GetMinPlayer();
		}
		this.MaxPlayer = MaxPlayer;
		this.MinPlayer = MinPlayer;
		this.timer = timer;
	}

	/**
	 * 读取一个分配大厅。
	 * 
	 * @return 是否重载成功。
	 */
	public boolean Load() {
			if(isComplete){
				Clear();
			}
			isComplete = true;
			if(getFileConf().contains("Display")){
				Display = getFileConf().getString("Display");
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Display",Name));
				isComplete = false;
			}
			
			if(getFileConf().contains("Bind")){
				Bind = getFileConf().getBoolean("Bind");
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Bind","true"));
			}
			if(getFileConf().contains("JoinCost.Price")){
				JoinPrice = getFileConf().getInt("JoinCost.Price");
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"JoinCost.Price","0"));
			}
			if(getFileConf().contains("SkipLobby")){
				FreeJoin = getFileConf().getBoolean("SkipLobby");
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"SkipLobby","false"));
			}
			if(getFileConf().contains("CanBringItem")){
				CanBringItem = getFileConf().getBoolean("CanBringItem");
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"CanBringItem","true"));
			}
			if(getFileConf().contains("Lobby")){
				LobbyLoc = new FLocation(getFileConf().getString("Lobby"));
				if(!LobbyLoc.isComplete()){
					Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Lobby",null));
					isComplete = false;
				}
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Lobby",null));
				isComplete = false;
			}
			if(getFileConf().contains("Leave")){
				LeaveLoc = new FLocation(getFileConf().getString("Leave"));
				if(!LeaveLoc.isComplete()){
					Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Leave",null));
					isComplete = false;
				}
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Leave",null));
				isComplete = false;
			}
			
			int LobbyTime= 60;
			int FullLobbyTime = 10;
			
			if(getFileConf().contains("Timer.LobbyTime")){
				LobbyTime = getFileConf().getInt("Timer.LobbyTime");
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"LobbyTime","60"));
			}
			if(getFileConf().contains("Timer.Full_LobbyTime")){
				FullLobbyTime = getFileConf().getInt("Timer.Full_LobbyTime");
			}else{
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Full_LobbyTime","10"));
			}
			timer = new LobbyTimerControl(this, LobbyTime, FullLobbyTime);

			File[] filelist = getDir().listFiles();
			Divgroup.clear();
			for(int a = 0;a<filelist.length;a++){
				if(!filelist[a].getName().equals("LobbyOption.yml")){
					if(!Group.Load(this,filelist[a])){
					}
				}
			}
			for(int a = 0;a<Divgroup.size();a++){
				if(Divgroup.get(a).isComplete()&&Divgroup.get(a).GetFreeJoin()){
					FreeJoin = true;
					CanJoin = true;
					Bind = false;
				}
			}
			if (Divgroup.size() > 0) {
				int MaxPlayer = 0;
				int MinPlayer = 0;
				for (int a = 0; a < Divgroup.size(); a++) {
					if(Divgroup.get(a).isComplete()){
						MaxPlayer = MaxPlayer + Divgroup.get(a).GetMaxPlayer();
						MinPlayer = MinPlayer + Divgroup.get(a).GetMinPlayer();
					}
				}
				this.MaxPlayer = MaxPlayer;
				this.MinPlayer = MinPlayer;
			} else {
				isComplete = false;
				return false;
			}
			if(isComplete){
				CanJoin = true;
				Data.ConsoleInfo(debugChange(Language.getMessage("load_succeed"),null,null));
			}else{
				CanJoin = false;
				Data.ConsoleInfo(debugChange(Language.getMessage("load_failed"),null,null));
			}
			sign = JoinSign.DisposeLobbySign(this);
			return isComplete;
	}
/**
 * 读取该大厅所有加入牌子。
 */
	public void LoadSign() {
		if(getSign()!=null && !getSign().isEmpty()){
			for (int a = 0; a < getSign().size(); a++) {
				getSign().get(a).LoadSign(this);
			}
		}

	}
	/**
	 * 取消该大厅的所有加入牌子。
	 */
	public void UnLoadSign(){
		for(int a = 0;a<sign.size();a++){
			HandlerList.unregisterAll(sign.get(a));
		}
		sign.clear();
	}
	/**
	 * 取消所有大厅的加入牌子。
	 */
	public static void UnLoadAllSign(){
		if (list.size() > 0) {
			for (int cc = 0; cc < list.size(); cc++) {
				if (list.get(cc).sign != null) {
					list.get(cc).UnLoadSign();
				}
			}
		}
	}

	public boolean CanJoin(){
		return CanJoin;
	}
	/**
	 * 获得该大厅的队列列表。
	 * @return 队列列表
	 */
	public List<Group> getLobbyGroup() {
		return Divgroup;
	}

	public boolean getBind() {
		return Bind;
	}
	public boolean isFreeJoin(){
		return FreeJoin;
	}
	public FLocation getLobby() {
		return LobbyLoc;
	}
	public FLocation getLeave() {
		return LeaveLoc;
	}
	public Group getGroup(String groupName){
		for(int a = 0;a<Divgroup.size();a++){
			if(Divgroup.get(a).Name.equals(groupName)){
				return Divgroup.get(a);
			}
		}
		return null;
	}

	public void setBind(boolean Bind) {
		this.Bind = Bind;
	}

	public LobbyTimerControl GetTimer() {
		return timer;
	}



	/**
	 * 清空该分配厅内玩家(包括队列)。
	 */
	public void Clear() {
		for(int a = 0;a<Divgroup.size();a++){
			Divgroup.get(a).Clear();
		}
		for(int a = 0;a<playerList.size();a++){
			LeaveLobby(playerList.get(a));
		}
		onLobbyClear();
	}
	/**
	 * 检查大厅内是否有这个玩家。
	 * 
	 * @param player
	 *            被检查的玩家。
	 * @return 是否检查到
	 */
	public boolean hasPlayer(FPlayer player) {
		for (int a = 0; a < playerList.size(); a++) {
			if (player==playerList.get(a)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 检查大厅内是否有这个玩家。
	 * 
	 * @param player
	 *            被检查的玩家。
	 * @return 是否检查到
	 */
	public boolean hasPlayer(Player player) {
		for (int a = 0; a < playerList.size(); a++) {
			if (player == playerList.get(a).ToMC()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 检查大厅内是否有这个队列。
	 * 
	 * @param groupName
	 *            被检查的队列名字
	 * @return 是否检查到
	 */
	public boolean hasGroup(String groupName){
		for(int a = 0;a<getLobbyGroup().size();a++){
			if(getLobbyGroup().get(a).Name.equals(groupName)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 创建一个加入牌子。
	 * 注意：在创建位置必须存在一个牌子才能创建！
	 * 
	 * @param Loc
	 *            创建牌子的位置。
	 * @return 创建是否成功
	 */
	public boolean NewSign(Location Loc) {
		File file = GetOptionFile();
		FileConfiguration fileConf = getFileConf();
		List<String> stringlist = fileConf.getStringList("JoinSign");
		if(!stringlist.contains(Loc.getBlockX() + "," + Loc.getBlockY() + "," + Loc.getBlockZ() + "," + Loc.getWorld().getName())){
			stringlist.add(
					Loc.getBlockX() + "," + Loc.getBlockY() + "," + Loc.getBlockZ() + "," + Loc.getWorld().getName());
			fileConf.set("JoinSign",stringlist);
			try {
				fileConf.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			JoinSign a = new JoinSign(new FLocation(Loc), this);
			if (a.LoadSign(this)) {
				sign.add(a);
				return true;
			}
		}
		return false;
	}

	private boolean joinCost(FPlayer pl) {
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			if (!(pl.ToMC().hasPermission("fw.free"))) {
				String name = pl.getName();
				if (JoinPrice > 0) {
					double money = Data.economy.getBalance(name);
					if (money < JoinPrice) {
						pl.sendMessage(ChatColor.RED + getFileConf().getString("JoinCost.Pay_Message"));
						return false;
					} else {
						Data.economy.withdrawPlayer(name, JoinPrice);
						pl.sendMessage(ChatColor.DARK_GREEN + getFileConf().getString("JoinCost.NotEnough_Message"));
						return true;
					}
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
	/**
	 * 让一名玩家加入大厅。
	 * 
	 * @param player
	 *            将加入的玩家。
	 */
	public void JoinLobby(FPlayer player) {
		if (isComplete) {
			List<Lobby> Lobbylist = getLobbyList();
			for (int a = 0; a < Lobbylist.size(); a++) {
				if (Lobbylist.get(a).hasPlayer(player)) {
					player.sendMessage(ChatColor.RED + Language.getMessage("joinerror_joinother"));
					return;
				}
			}
			if(GetPlayerAmount() == MaxPlayer){
				player.sendMessage(ChatColor.RED + Language.getMessage("joinerror_full"));
				return;
			}
			
			if (!CanBringItem) {
				for (int a = 0; a < player.getInventory().getSize(); a++) {
					if (player.getInventory().getItem(a) != null) {
						player.sendMessage(ChatColor.RED + Language.getMessage("joinerror_inventory"));
						return;
					}
				}
				if ((player.getInventory().getHelmet() != null) | (player.getInventory().getLeggings() != null)
						| (player.getInventory().getBoots() != null)
						| (player.getInventory().getChestplate() != null)) {
					player.sendMessage(ChatColor.RED + Language.getMessage("joinerror_equipment"));
					return;
				}
			}
			
			if(FreeJoin){
				if (joinCost(player)) {
					onPlayerFreeJoin(player);
				}
			}else{
				if(CanJoin){
					if (joinCost(player)) {
						onPlayerJoin(player);
						if (GetPlayerAmount() == MinPlayer) {
							onPlayerEnough();
							timer.start();
						}
						
						if(GetPlayerAmount()==MaxPlayer){
							timer.LobbyTimer().FastStart();
							onPlayerFull();
						}
					}

				}else{
					player.sendMessage(ChatColor.RED + Language.getMessage("joinerror_playing"));
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + Language.getMessage("joinerror_loadfailed"));
		}
	}
	/**
	 * 让一名玩家离开大厅。
	 * 
	 * @param player
	 *            将加入的玩家。
	 */
	public void LeaveLobby(FPlayer player) {
		for (int b = 0; b < playerList.size(); b++) {
			if (playerList.get(b) == player) {
				if(FreeJoin){
					onPlayerLeave(player,true);
				}else{
					onPlayerLeave(player,false);
				}
				LoadSign();
			}
		}
		CheckCanJoin();
	}

	/**
	 * 给玩家发送大厅以及大厅内队列状态。
	 * 
	 * @param player
	 *            被发送的玩家。
	 */
	public void State(Player player){
		player.sendMessage(ChatColor.BLUE + Display + ChatColor.RESET + ":");
		if (isComplete) {
			player.sendMessage(ChatColor.YELLOW + Language.getMessage("lobby_statu.playeramount") +": " +ChatColor.WHITE+ "(" + GetPlayerAmount() +"/"+ChatColor.DARK_BLUE  + GetMinPlayer() + ChatColor.BLACK+  "/" +ChatColor.DARK_RED+ GetMaxPlayer() + ChatColor.WHITE+ ")");
			String statu = "";
			if(FreeJoin){
				statu = ChatColor.GREEN + Language.getMessage("lobby_statu.skiplobby");
			}else{
				if(CanJoin()){
					if(timer.LobbyTimer().getTime()!=timer.LobbyTimer().getMaxtime()){
						String start = ChatColor.GREEN + Language.getMessage("signinfo.ready");
						start = start.replaceAll("<time>", Integer.toString(GetTimer().LobbyTimer().getTime()));
						statu = ChatColor.GOLD+start;
					}else{
						statu = ChatColor.GREEN+ Language.getMessage("signinfo.waiting");
					}
				}else{
					statu = ChatColor.RED+Language.getMessage("signinfo.started");
				}
			}

			player.sendMessage(ChatColor.YELLOW +Language.getMessage("lobby_statu.statu")+": " + statu);
			player.sendMessage(ChatColor.YELLOW + Display +" "+ Language.getMessage("lobby_statu.group_statulist")+":");
			for (int a = 0; a < Divgroup.size(); a++) {
				Divgroup.get(a).state(player);
			}
		} else {
			player.sendMessage(ChatColor.RED + Language.getMessage("notcomplete"));
		}
	}
	/**
	 * 检查大厅是否可以成为加入状态：
	 * 如果所有队列都被清空，将检查成功，大厅将自动清空并允许加入。
	 */
	public void CheckCanJoin(){
		for(int a = 0;a<Divgroup.size();a++){
			if(!(Divgroup.get(a).canJoin())){
				return;
			}
		}
		Clear();
		CanJoin=true;
	}
	/**
	 * 仅供计时器调用。请勿随意调用！
	 */
	public void onstart() {
		onLobbyStart();
	}

	protected void onPlayerJoin(FPlayer player) {
		player.SetScore(0);
		playerList.add(player);
		Teleporter tel = new Teleporter(player);
		tel.Teleport(LobbyLoc, true);
		if (getFileConf().contains("ControlTask.onPlayerJoin")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerJoin"), this,
					player);
			task.runTask(Data.fwmain);
		}
	}
	protected void onPlayerEnough() {
		if (getFileConf().contains("ControlTask.onPlayerEnough")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerEnough"), this,
					null);
			task.runTask(Data.fwmain);
		}
	}
	protected void onPlayerFull() {
		if (getFileConf().contains("ControlTask.onPlayerFull")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerFull"), this,
					null);
			task.runTask(Data.fwmain);
		}
	}
	protected void onPlayerFreeJoin(FPlayer player) {
		player.SetScore(0);
		playerList.add(player);
		Random random = new Random();
		int r = 0;
		do{
			r = random.nextInt(Divgroup.size());
		}while(!Divgroup.get(r).canJoin());
		Divgroup.get(r).JoinGroup(player);	
		if (getFileConf().contains("ControlTask.onPlayerFreeJoin")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerFreeJoin"), this,
					player);
			task.runTask(Data.fwmain);
		}else{
		}
	}
	protected void onPlayerLeave(FPlayer player,boolean withoutTask) {
		if(!withoutTask){
			if (getFileConf().contains("ControlTask.onPlayerLeave")) {	
				GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerLeave"), this,
					player);
				task.runTask(Data.fwmain);
			
			}
		}
		Teleporter tel = new Teleporter(player);
		tel.Teleport(LeaveLoc, true);
		playerList.remove(player);
	}

	protected void onLobbyStart() {
		if (getFileConf().contains("ControlTask.onLobbyStart")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onLobbyStart"), this,
					null);
			task.runTask(Data.fwmain);
		}

	}


	protected void onLobbyClear() {
		if (getFileConf().contains("ControlTask.onLobbyClear")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onLobbyClear"), this,
					null);
			task.runTask(Data.fwmain);
		}

	}
	public void SetCanJoin(boolean canjoin) {
		this.CanJoin = canjoin;
		
	}

}
