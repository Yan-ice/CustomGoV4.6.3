package fw.lobby.group;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import fw.Data;
import fw.Fwmain;
import fw.Language;
import fw.lobby.task.GroupTaskRunner;
import fw.lobby.Basic;
import fw.lobby.Lobby;
import fw.lobby.JoinSign;
import fw.lobby.group.trigger.TriggerBaseExt;
import fw.lobby.player.FPlayer;
import fw.location.FLocation;
import fw.location.Teleporter;

public class Group extends Basic implements Listener {
	private File file;

	private FLocation LobbyLoc;
	private List<FLocation> GroupLoc = new ArrayList<FLocation>();
	private GroupTimerControl timer;
	private PlayerRule rule = new PlayerRule();
	
	private boolean FreeJoin = false;
	private boolean canJoin = true;

	private int Score = 0;
	
	private Trigger trigger = new Trigger(this);
	
	private Lobby byLobby;
	
	protected Group(String Name, File file,Lobby byLobby) {
		this.Name = Name;
		this.file = file;
		this.byLobby = byLobby;
	}

	/**
	 * 创建一个队列。
	 * @param Pl 创建的玩家(输出创建信息,获取坐标提供为默认值)
	 * @param GroupName 队列名字
	 * @param byLobby 队列隶属于哪一个分配大厅
	 */
	public static void Create(Player Pl, String GroupName,Lobby byLobby){
		if (byLobby.hasGroup(GroupName)) {
			if(Pl !=null){
				Pl.sendMessage(ChatColor.RED +  Language.getMessage("group_create_failed"));
			}
			return;
		}
		try {
			File file = new File(byLobby.getDir(), GroupName + ".yml");
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
			bw.write("Display: " + GroupName + "\n");
			bw.write("#在这里编辑大厅显示名：在文字提示中有用到显示名。\n");
			bw.write("MaxPlayer: 4\n");
			bw.write("#队列最大人数。如果达到，队列将无法再加入更多人。\n");
			bw.write("MinPlayer: 2\n");
			bw.write("#队列最小人数。如果达到，队列开始计时器将启动。\n");
			bw.write("#如果开启freejoin,本项锁定为0。\n");
			bw.write("FreeJoin: false\n");
			bw.write("#是否允许自由加入。如果开启，\n");
			bw.write("#任何加入的队列将直接开始游戏，而不用在Lobby等待。\n");
			bw.write("ControlTask:\n");
			bw.write("#以下是队列任务控制器设置：\n");
			bw.write("  onPlayerJoin:\n");
			bw.write("  - tell{&e玩家<striker>加入了<display>！队列人数： (<playeramount>/<maxplayer>)} @a\n");
			bw.write("#在这里设置玩家加入队列(关闭FreeJoin)时执行的任务。<striker>在这里指加入的玩家。\n");
			bw.write("  onPlayerEnough:\n");
			bw.write("  - tell{&e队列人数已足够！开始60秒倒计时！} @a\n");
			bw.write("  onPlayerFull:\n");
			bw.write("  - tell{&e队列人数已满！倒计时快进到5秒！} @a\n");
			bw.write("  onPlayerFreeJoin:\n");
			bw.write("  - tell{&e玩家<striker>加入了<display>！队列人数： (<playeramount>/<maxplayer>)} @a\n");
			bw.write("  - tell{&c该队列为自由加入队列，您已被直接传送到目标地点！} @p\n");			
			bw.write("#在这里设置玩家自由加入(开启FreeJoin)时执行的任务。<striker>在这里指加入的玩家。\n");
			bw.write("  onPlayerLeave:\n");
			bw.write("  - tell{&e玩家<striker>离开了<display>！游戏人数： (<playeramount>/<maxplayer>)} @a\n");
			bw.write("  - tell{&e你离开了<display>！现在你回到了大厅！} @p\n");
			bw.write("  - tell{&e你可以观看游戏对战，或者输入/fw leave离开大厅。} @p\n");
			bw.write("#在这里设置玩家离开队列时执行的任务。<striker>在这里指离开的玩家。\n");
			bw.write("  onGroupStart:\n");
			bw.write("  - tell{&e<display>现在开始！} @a\n");
			bw.write("  - heal{20} @a\n");
			bw.write("#在这里设置队列开始时执行的任务。<player>在这里无效。\n");
			bw.write("  onTimePast(200):\n");
			bw.write("  - tell{&4<display>队列还有200秒将结束！} @a\n");
			bw.write("#在这里设置游戏时间剩余几秒时(括号内)时执行的任务。\n");
			bw.write("  onTimePast(100):\n");
			bw.write("  - tell{&4<display>队列还有100秒将结束！} @a\n");
			bw.write("  onTimePast(30):\n");
			bw.write("  - tell{&4<display>队列还有30秒将结束！} @a\n");
			bw.write("  onTimePast(10):\n");
			bw.write("  - tell{&4<display>队列还有10秒将结束！} @a\n");
			bw.write("  onLobbyTimePast(10):\n");
			bw.write("  - tell{&2<display>队列还有10秒将开始！} @a\n");
			bw.write("#在这里设置剩余多少玩家时(括号内)时执行的任务。\n");
			bw.write("  onPlayerRest(1):\n");
			bw.write("  - tell{&6恭喜你站到了最后！} @a\n");
			bw.write("  - command{eco give <player> 100} @a" + "\n");
			bw.write("  - leave{} @p" + "\n");
			bw.write("  onPlayerRest(2):\n");
			bw.write("  - tell{&6只剩下两名玩家！} @a\n");
			bw.write("#TimePast和PlayerRest可以设置多个。但@p无效。\n");
			bw.write("Timer: \n");
			bw.write("#在这里控制队列的阶段执行时间。\n");
			bw.write("  LobbyTime: 60" + "\n");
			bw.write("  #这里是等待厅Lobby的时间。达到最少人数开始计时，等待完毕后，游戏将开始。\n");
			bw.write("  Full_LobbyTime: 10" + "\n");
			bw.write("  #这里是Lobby满人后快进的时间。Lobby满人时，LobbyTime将快进到此时间继续计时。\n");
			bw.write("  ArenaTime: 300" + "\n");
			bw.write("  #这里是游戏时间。如果计时完毕队列仍未解散，将会强制解散。\n");
			bw.write("Locations:" + "\n");
			bw.write("  #以下是队列的各个坐标设置：\n");
			bw.write("  Lobby: " + X + "," + Y + "," + Z + "," + world + "\n");
			bw.write("  #这里是等待大厅坐标的设置。\n");
			bw.write("  Arena:\n");
			bw.write("  - " + X + "," + Y + "," + Z + "," + world + "\n");
			bw.write("  #这里是游戏地点的设置。\n");
			bw.write("  #玩家将会随机传送到其中一个地点。\n");
			bw.write("  #注意：玩家的队列离开地点为大厅的LobbyLoc。\n");		
			bw.write("Trigger:" + "\n");
			bw.write("#以下是队列任务触发器设置：\n");
			bw.write("#<player>代指执行者。可由玩家触发的触发器中，<striker>代指触发者。\n");
			bw.write("  DeathExample:" + "\n");
			bw.write("    Type: Death" + "\n");
			bw.write("    #这里设置本触发器类型。(下同)\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{玩家<striker>在游戏中阵亡了！} @a" + "\n");
			bw.write("    - tell{由于您的死亡,您被迫离开队列。} @p" + "\n");
			bw.write("    - heal{20} @p" + "\n");
			bw.write("    - leave{} @p" + "\n");
			bw.write("    #这里设置触发器触发时执行的任务。(下同)\n");
			bw.write("  PickupItemExample:" + "\n");
			bw.write("    Type: PickupItem" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - 264" + "\n");
			bw.write("    #这里设置会被触发的物品ID。\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{你获得了钻石！} @p" + "\n");
			bw.write("  AnotherPickupItemExample:" + "\n");
			bw.write("    Type: PickupItem" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - 265" + "\n");
			bw.write("    #这里设置会被触发的物品ID。\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{你获得了铁锭？} @p" + "\n");
			bw.write("  WalkOnBlockExample:" + "\n");
			bw.write("    Type: WalkOnBlock" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - 1" + "\n");
			bw.write("    #这里设置会被触发的物品ID。\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{你踩到了石头。} @p" + "\n");
			bw.write("  KillEntityExample:" + "\n");
			bw.write("    Type: KillEntity" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - DC大魔王" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{玩家<striker>成功击杀了终极BOSS！全队奖励100元！} @a" + "\n");
			bw.write("    - tell{恭喜你击杀了终极BOSS！！} @p" + "\n");
			bw.write("    - command{eco give <player> 100} @a" + "\n");
			bw.write("  KillAllEntityExample:" + "\n");
			bw.write("    Type: KillAllEntity" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - DC大魔王" + "\n");
			bw.write("    - 大魔王的护卫" + "\n");
			bw.write("    - 大魔王的密探" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{队伍成功击杀了最后一只怪物！任务完成！你们将在5秒内离开！} @a" + "\n");
			bw.write("    - heal{20} @a" + "\n");
			bw.write("    - delay{100}" + "\n");
			bw.write("    - leave{} @p" + "\n");
			bw.write("  #<striker>在这里代指击杀场内最后一个怪物的人。" + "\n");		
			bw.write("  KillPlayerExample:" + "\n");
			bw.write("    Type: KillPlayer" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{恭喜你击杀了一名玩家，赏金50元！} @p" + "\n");
			bw.write("    - command{eco give <player> 50} @p" + "\n");
			bw.write("  DissExample:" + "\n");
			bw.write("    Type: Diss" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{由于外部原因，队列被迫解散。} @a" + "\n");
			bw.write("    - leave{} @a" + "\n");
			bw.write("  TimeUpExample:" + "\n");
			bw.write("    Type: TimeUp" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{由于时间已到，队列被迫解散。} @a" + "\n");
			bw.write("    - leave{} @a" + "\n");
			bw.write("  OfflineExample:" + "\n");
			bw.write("    Type: Offline" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - leave{} @a" + "\n");
			bw.write("    #不建议修改这一项：离线玩家不退出队列容易导致BUG！\n");
			bw.write("PlayerRule:" + "\n");
			bw.write("  NameColor: WHITE" + "\n");
			bw.write("  #队内玩家的名字颜色。该项设置可以是：\n");
			bw.write("  #WHITE,YELLOW,GREEN,BLUE,RED,BLACK,GRAY,PURPLE\n");
			bw.write("  NameInvisible: true" + "\n");
			bw.write("  #队内玩家是否能看见名字。\n");
			bw.write("  PvP: false" + "\n");
			bw.write("  #队内是否允许PVP。\n");
			bw.write("  Potionhit: false" + "\n");
			bw.write("  #队内是否允许溅射药水影响。\n");
			bw.write("  Projectile: false" + "\n");
			bw.write("  #队内是否允许弹射物伤害。\n");
			bw.write("  WhiteListCommand:\n");
			bw.write("  - fw\n");
			bw.write("  - fwc\n");
			bw.write("  #这里设置玩家在队列中允许使用的指令。\n");
			bw.write("  DelGameItem: false\n");
			bw.write("  #是否删除游戏物品。如果开启，\n");
			bw.write("  #离开队列的人将会被清空背包。\n");
			bw.write("  ChatInGroup: false\n");
			bw.write("  #是否强制队伍聊天。\n");
			bw.write("  #如果开启，正常聊天将直接转换为队伍内聊天。\n");
			bw.write("  #如果关闭，玩家必须输入/fwc <内容> 进行队伍内聊天。\n");		
			bw.close();
			Load(byLobby,file);
		} catch (IOException e) {
		}
		if(Pl!=null){
			Pl.sendMessage(ChatColor.YELLOW +  Language.getMessage("group_create"));
		}
	}
/**
 * 获取一个玩家所在的队列
 * @param player 需要查看队列的玩家
 * @return 玩家所在的队列，如果不在队列则返回null。
 */
	public static Group SearchPlayerInGroup(FPlayer player){
		if(Lobby.SearchPlayerInLobby(player) != null){
			Lobby lobby = Lobby.SearchPlayerInLobby(player);
			
			for (int a = 0; a < lobby.getLobbyGroup().size(); a++) {
				if (lobby.getLobbyGroup().get(a).hasPlayer(player)) {
					return lobby.getLobbyGroup().get(a);
				}
			}
		}
		return null;
	}
	
	public File GetFile() {
		if (file != null) {
			return file;
		}
		return null;
	}

	public FileConfiguration getFileConf() {
		if (file != null) {
			return YamlConfiguration.loadConfiguration(file);
		} else {
			return null;
		}
	}

	public static File GetFile(String FileName) {
		if (Data.groupDir != null) {
			File[] list = Data.groupDir.listFiles();
			for (int a = 0; a < list.length; a++) {
				if (list[a].getName().equals(FileName)) {
					return list[a];
				}
			}
		}
		return null;
	}

	public static FileConfiguration getFileConf(String FileName) {
		if (GetFile(FileName) != null) {
			return YamlConfiguration.loadConfiguration(GetFile(FileName));
		} else {
			return null;
		}
	}
	/**
	 * 读取文件队列，无论是否成功都将自动添加到Lobby的Lobby列表中。
	 * @param byLobby 隶属的大厅(需要获取大厅文件夹)
	 * @param file 读取的文件
	 * @return 读取是否成功
	 */
	public static boolean Load(Lobby byLobby,File file) {
		if (file != null && file.isFile()) {
			String Name = file.getName().split("\\.")[0];
			if (byLobby.getGroup(Name) != null) {
				if(!byLobby.getGroup(Name).Load()){
					return false;
				}
			} else {
				Group gro = new Group(Name, file, byLobby);
				byLobby.getLobbyGroup().add(gro);
				if(!gro.Load()){
					return false;
				}
			}
		} else {
			Data.ConsoleInfo("文件出错。");
			return false;
		}
		return true;
	}

	public void SetDisplay(String dis) {
		Display = dis;
	}

	public boolean GetFreeJoin() {
		return FreeJoin;
	}

	public FLocation LobbyLoc() {
		return LobbyLoc;
	}

	public List<FLocation> AllArenaLoc() {
		return GroupLoc;
	}

	public FLocation ArenaLoc(int num) {
		if (GroupLoc.size() > num) {
			return GroupLoc.get(num);
		} else {
			return null;
		}
	}

	/**
	 * 是否可以加入。如果队列已经开始,canJoin就会为false.
	 * 
	 * @return
	 */
	public boolean canJoin() {
		return canJoin;
	}

	public void SetcanJoin(boolean canJoin) {
		this.canJoin = canJoin;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void UnLoadSign(){
		for(int a = 0;a<sign.size();a++){
			HandlerList.unregisterAll(sign.get(a));
		}
		sign.clear();
	}
	
	/**
	 * 读取/重载本队列,在debug开启时会发送详细重载信息。
	 * @return 读取/重载是否成功。
	 */
	public boolean Load() {
		if (isComplete()) {
			Dissolve();
		}
		isComplete = true;
		if(getFileConf().contains("Display")){
			Display = getFileConf().getString("Display");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Display",Name));
		}

		if(getFileConf().contains("FreeJoin")){
			FreeJoin = getFileConf().getBoolean("FreeJoin");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Freejoin","true"));
		}

		if(getFileConf().contains("MinPlayer")){
			MinPlayer = getFileConf().getInt("MinPlayer");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"MinPlayer",null));
			isComplete = false;
		}
		if(getFileConf().contains("MaxPlayer")){
			MaxPlayer = getFileConf().getInt("MaxPlayer");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"MaxPlayer",null));
			isComplete = false;
		}
		int LobbyTimer = 60;
		int Full_LobbyTimer = 10;
		int ArenaTimer = 300;
		if(getFileConf().contains("Timer.LobbyTime")){
			LobbyTimer = getFileConf().getInt("Timer.LobbyTime");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Timer.LobbyTime","60"));
		}
		if(getFileConf().contains("Timer.Full_LobbyTime")){
			Full_LobbyTimer = getFileConf().getInt("Timer.Full_LobbyTime");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Timer.FullLobbyTime","10"));
		}
		if(getFileConf().contains("Timer.ArenaTime")){
			ArenaTimer = getFileConf().getInt("Timer.ArenaTime");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),"Timer.ArenaTime","300"));
		}
		if(FreeJoin){
			MinPlayer = 0;
		}
		timer = new GroupTimerControl(this, LobbyTimer, Full_LobbyTimer, ArenaTimer);
		
		if(getFileConf().contains("Locations.Lobby")){
			LobbyLoc = new FLocation(getFileConf().getString("Locations.Lobby"));
			if(!LobbyLoc.isComplete()){
				Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Locations.Lobby",null));
				isComplete = false;
			}
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Locations.Lobby",null)); 
			isComplete = false;
		}
		
		if(getFileConf().contains("Locations.Arena")){
			List<String> Loclist = getFileConf().getStringList("Locations.Arena");
			for (int a = 0; a < Loclist.size(); a++) {
				FLocation l = new FLocation(Loclist.get(a));
				if(l.isComplete()){
					GroupLoc.add(l);
				}
			}
			if(GroupLoc.size()==0){
				Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Locations.Arena",null));
				isComplete = false;
			}
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostimportantconfig"),"Locations.Arena",null));
			isComplete = false;
		}

		getTrigger().load(this);

		if (!rule.Load(this)) {
			isComplete = false;
		}
		if(isComplete){
			Data.fwmain.getServer().getPluginManager().registerEvents(this, Data.fwmain);
			Data.ConsoleInfo(debugChange(Language.getMessage("load_succeed"),null,null));
		}else{
			Data.ConsoleInfo(debugChange(Language.getMessage("load_failed"),null,null));
		}
		return isComplete;
	}

	/**
	 * 让队列内所有玩家触发Diss条件。
	 */
	public void Dissolve() {
		List<TriggerBaseExt> list = getTrigger().getTriggerList();
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a) instanceof fw.lobby.group.trigger.Diss) {
				list.get(a).Strike(null);
			}
		}
	}
	/**
	 * 让队列内所有玩家离开，以达到清空队列目的。
	 * 该离开不会触发任何触发器。
	 */
	public void Clear(){
		while(playerList.size()>0){
			LeaveGroup(playerList.get(0));
		}
	}
	/**
	 * 检查本队列是否有这个玩家。
	 * 
	 * @param player
	 *            被检查的玩家。
	 * @return 是否检查到
	 */
	public boolean hasPlayer(FPlayer player) {
		for (int a = 0; a < playerList.size(); a++) {
			if (player == playerList.get(a)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 检查本队列是否有这个玩家。
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
	 * 获取队列所在的大厅。
	 */
	public Lobby byLobby() {
		return byLobby;
	}
	
	/**
	 * 让一名玩家加入队列。
	 * 该方法仅提供给大厅调用，不要随意调用！
	 * 
	 * @param player
	 *            将加入的玩家。
	 */
	public void JoinGroup(FPlayer player) {
		if (isComplete) {
			if (GetPlayerAmount() == GetMaxPlayer()) {
				return;
			}		
			player.SetNameColor(rule.NameColor());
			player.SetNameInv(rule.NameInv());
			if (FreeJoin) {
				playerList.add(player);
				onPlayerFreeJoin(player);
				Teleporter tel = new Teleporter(player);
				tel.TeleportRandom(GroupLoc);
			} else
				if (canJoin) {
					playerList.add(player);
					onPlayerJoin(player);
					Teleporter tel = new Teleporter(player);
					tel.Teleport(LobbyLoc, true);
					if (playerList.size() == MinPlayer) {// 人数达到最低人数
						timer.start(1);
						onPlayerEnough();
					}
					if (playerList.size() == MaxPlayer) {
						timer.LobbyTimer().FastStart();
						onPlayerFull();
					}
				} else {
					player.sendMessage(
							ChatColor.RED + Language.getMessage("joinerror_playing"));
				}
		} else {
			player.sendMessage(ChatColor.RED + Language.getMessage("joinerror_loadfailed"));
			Lobby.AutoLeave(player);
		}
	}
/**
 * 开始一个队列。该方法供计时器调用，请勿随意调用。
 */
	public void Start() {
		if (canJoin) {
			if (GetPlayerAmount() > 0) {
				onGroupStart();
				for (int aa = 0; aa < playerList.size(); aa++) {
					Teleporter tel = new Teleporter(playerList.get(aa));
					tel.TeleportRandom(GroupLoc);
				}
				timer.start(2);
				SetcanJoin(false);
			}
		}
	}

	/**
	 * 让玩家离开队列，
	 * 
	 * @param player
	 *            将离开的玩家。
	 */
	public void LeaveGroup(FPlayer player) {
		for (int a = 0; a < playerList.size(); a++) {
			if (player == playerList.get(a)) {
				onPlayerLeave(playerList.get(a));
				playerList.remove(a);
				if (rule.DelGameItem()) {
					player.getEquipment().clear();
					player.getInventory().clear();
				}
				onPlayerRest();
				if(byLobby.isFreeJoin()){
					Lobby.SearchPlayerInLobby(player).LeaveLobby(player);
				}else{
					Teleporter tel = new Teleporter(player);
					tel.Teleport(byLobby.getLobby(),true);
				}

				if (!FreeJoin) {
					if (isClear()) {
						onGroupClear();
						if(byLobby.getBind()){
							byLobby.Clear();
						}
					}
				}
			}
		}
		byLobby.CheckCanJoin();
	}

	/**
	 * 给玩家发送队列状态。
	 * 
	 * @param player
	 *            被发送的玩家
	 */
	public void state(Player player) {
		player.sendMessage(ChatColor.AQUA + Display + ChatColor.WHITE + Language.getMessage("group_statu.statu"));
		if (isComplete) {
			String pl = "";
			String statu = "";
			if (playerList.size() > 0) {
				for (int a = 0; a < playerList.size(); a++) {
					pl = pl + playerList.get(a).ToMC().getName() + " ";
				}
			}
			if (pl == "") {
				pl = Language.getMessage("group_statu.playerlist_none");
			}
			if(FreeJoin){
				statu = ChatColor.GREEN + Language.getMessage("group_statu.freejoin");
			}else{
				if(canJoin()){
					if(timer.LobbyTimer().getTime()!=timer.LobbyTimer().getMaxtime()){
						statu = ChatColor.GOLD+Language.getMessage("signinfo.ready");
					}else{
						statu = ChatColor.GREEN+Language.getMessage("signinfo.waiting");
					}
				}else{
					statu = ChatColor.RED+Language.getMessage("signinfo.started");
				}
			}
			player.sendMessage(ChatColor.YELLOW + Language.getMessage("group_statu.playerlist"));
			player.sendMessage(pl+ " (" + playerList.size() + "/" + MaxPlayer + ")");
			player.sendMessage(ChatColor.YELLOW + Language.getMessage("group_statu.statu")+ ": " + statu);
		} else {
			player.sendMessage(ChatColor.RED + Language.getMessage("notcomplete"));
		}
	}

	/**
	 * 检查队列是否为空。
	 */
	public boolean isClear() {
		return this.GetPlayerAmount() == 0;
	}

	@EventHandler
	private void PVPListen(EntityDamageByEntityEvent evt) {
		if (evt.getEntity() instanceof Player) {
			Player damaged = (Player) evt.getEntity();
			Player damager;
			if (evt.getDamager() instanceof Player) {
				damager = (Player) evt.getDamager();
			} else {
				return;
			}
			if (!rule.PvP()) {
				if (hasPlayer(damaged) && hasPlayer(damager)) {
					evt.setCancelled(true);
					damager.sendMessage(Language.getMessage("ban_pvp"));
				}
			}
		}
	}

	@EventHandler
	private void PotionListen(PotionSplashEvent evt2) {
		int s = 0;
		int b = 0;
		int c = 0;
		int no = 0;
		ThrownPotion pot = evt2.getPotion();
		if (pot.getShooter() instanceof Player) {
			Player shooter = (Player) pot.getShooter();
			while (b < playerList.size()) {
				if (shooter == playerList.get(b)) {
					if (!rule.Potionhit()) {
						evt2.setCancelled(true);
						List<Entity> damageds = pot.getNearbyEntities(3.0, 3.0, 3.0);
						while (s < damageds.size()) {
							if (damageds.get(s) instanceof Player) {
								Player damaged = (Player) damageds.get(s);
								while (c < playerList.size()) {
									if ((playerList.get(c) == damaged) && (shooter != damaged)) {
										shooter.sendMessage(Language.getMessage("ban_potion"));
									} else {
										no++;
										if (no == playerList.size()) {
											damaged.addPotionEffects(pot.getEffects());
										}
									}
									c++;
								}
								no = 0;
								c = 0;
							} else {
								if (damageds.get(s) instanceof Creature) {
									Creature damagedd = (Creature) damageds.get(s);
									damagedd.addPotionEffects(pot.getEffects());
								}
							}
							s++;
						}
						s = 0;
					}
				}
				b++;
			}
			b = 0;
		}
	}

	@EventHandler
	private void CommandListen(PlayerCommandPreprocessEvent evt) {
		if (hasPlayer(evt.getPlayer())) {
			String Command = evt.getMessage().split(" ")[0];
			for (int a = 0; a < rule.WhiteListCommand().size(); a++) {
				if (Command.equalsIgnoreCase("/fw")) {
					return;
				}
				if (Command.equalsIgnoreCase("/" + rule.WhiteListCommand().get(a))) {
					return;
				}
			}
			evt.setCancelled(true);
			evt.getPlayer().sendMessage(ChatColor.RED + Language.getMessage("ban_command"));
		}
	}
	
	@EventHandler
	private void ChatListen(PlayerChatEvent evt) {
		if(rule.chatInGroup()){
			if (!evt.getMessage().startsWith("/") ) {
				if(hasPlayer(evt.getPlayer())){
					evt.setCancelled(true);
					FPlayer.getFPlayer(evt.getPlayer()).chatInList(evt.getMessage(),GetPlayerList());
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	void Listen(PlayerRespawnEvent evt2) {
		Player pl = evt2.getPlayer();
		if (hasPlayer(pl)) {
			for (int a = 0; a < 50; a++) {
				FLocation f = GroupLoc.get(new Random().nextInt(GroupLoc.size()));
				if (FLocation.SafeLoc(f) != null) {
					evt2.setRespawnLocation(FLocation.SafeLoc(f).ToMC());
					return;
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	void PHitListen(EntityDamageByEntityEvent evt) {
		if(!rule.Projectile()){
			
			if(evt.getCause() == DamageCause.PROJECTILE){
				if(evt.getEntity() instanceof Player){
					if(hasPlayer((Player)evt.getEntity())){
						if(evt.getDamager() instanceof Projectile){
							if((Player)((Projectile)evt.getDamager()).getShooter()!=(Player)evt.getEntity()){
								if(hasPlayer((Player)((Projectile)evt.getDamager()).getShooter())){
									evt.setCancelled(true);
									((Player)((Projectile)evt.getDamager()).getShooter()).sendMessage(Language.getMessage("ban_projectile"));
								}
							}
						}
					}
				}
			}
		}
	}
	
	protected void onPlayerJoin(FPlayer player) {
		if (getFileConf().contains("ControlTask.onPlayerJoin")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerJoin"), this,
					player);
			task.runTask(Data.fwmain);
		}
	}
	protected void onPlayerFreeJoin(FPlayer player) {
		Data.Debug("玩家"+player.getName()+"加入自由队列"+Name+"。执行onPlayerFreeJoin。");
		if (getFileConf().contains("ControlTask.onPlayerFreeJoin")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerFreeJoin"), this,
					player);
			task.runTask(Data.fwmain);
		}
	}
	protected void onPlayerLeave(FPlayer player) {
		if (getFileConf().contains("ControlTask.onPlayerLeave")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerLeave"), this,
					player);
			task.runTask(Data.fwmain);
			//task.run();
		}
		player.SetNameColor(ChatColor.RESET);
		player.SetNameInv(false);

	}

	protected void onPlayerRest() {

		if (getFileConf().contains("ControlTask.onPlayerRest(" + playerList.size() + ")")) {
			GroupTaskRunner task = new GroupTaskRunner(
					getFileConf().getStringList("ControlTask.onPlayerRest(" + playerList.size() + ")"), this, null);
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
	protected void onGroupStart() {
		if (getFileConf().contains("ControlTask.onGroupStart")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onGroupStart"), this,
					null);
			task.runTask(Data.fwmain);
		}
	}

	protected void onGroupClear() {
		if (getFileConf().contains("ControlTask.onGroupClear")) {
			GroupTaskRunner task = new GroupTaskRunner(getFileConf().getStringList("ControlTask.onGroupClear"), this,
					null);
			task.runTask(Data.fwmain);
		}
		canJoin=true;
		blacklist = new ArrayList<Integer>();
		Score = 0;
	}
	
	private List<Integer> blacklist = new ArrayList<Integer>();
	protected void onScoreOver() {
		for(int a = 0;a<Score;a++){
			if (getFileConf().contains("ControlTask.onScoreOver(" + a + ")")) {
				if(!blacklist.contains(a)){
					GroupTaskRunner task = new GroupTaskRunner(
							getFileConf().getStringList("ControlTask.onScoreOver(" + a + ")"), this, null);
					task.runTask(Data.fwmain);
					blacklist.add(a);
				}

			}
		}

	}
	
	/**
	 * 增加一定分数。
	 * @param AddScore 增加量
	 */
	public void AddScore(int AddScore){
		Score = Score+AddScore;
		onScoreOver();
	}
	/**
	 * 扣除一定分数。(不可低于0)
	 * @param DelScore 减少量
	 */
	public void DelScore(int DelScore){
		Score = Score-DelScore;
		if(Score<0){
			Score=0;
		}
	}
	/**
	 * 设置玩家的分数。(不可低于0)
	 * @param NewScore 设置量
	 */
	public void SetScore(int NewScore){
		if(NewScore>=0){
			this.Score=NewScore;
			onScoreOver();
		}
	}
	/**
	 * 获取队列的分数。
	 */
	public int GetScore(){
		return Score;
	}
}
