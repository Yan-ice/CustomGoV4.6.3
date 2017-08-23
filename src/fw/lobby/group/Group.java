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
	 * ����һ�����С�
	 * @param Pl ���������(���������Ϣ,��ȡ�����ṩΪĬ��ֵ)
	 * @param GroupName ��������
	 * @param byLobby ������������һ���������
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
			bw.write("#������༭������ʾ������������ʾ�����õ���ʾ����\n");
			bw.write("MaxPlayer: 4\n");
			bw.write("#�����������������ﵽ�����н��޷��ټ�������ˡ�\n");
			bw.write("MinPlayer: 2\n");
			bw.write("#������С����������ﵽ�����п�ʼ��ʱ����������\n");
			bw.write("#�������freejoin,��������Ϊ0��\n");
			bw.write("FreeJoin: false\n");
			bw.write("#�Ƿ��������ɼ��롣���������\n");
			bw.write("#�κμ���Ķ��н�ֱ�ӿ�ʼ��Ϸ����������Lobby�ȴ���\n");
			bw.write("ControlTask:\n");
			bw.write("#�����Ƕ���������������ã�\n");
			bw.write("  onPlayerJoin:\n");
			bw.write("  - tell{&e���<striker>������<display>������������ (<playeramount>/<maxplayer>)} @a\n");
			bw.write("#������������Ҽ������(�ر�FreeJoin)ʱִ�е�����<striker>������ָ�������ҡ�\n");
			bw.write("  onPlayerEnough:\n");
			bw.write("  - tell{&e�����������㹻����ʼ60�뵹��ʱ��} @a\n");
			bw.write("  onPlayerFull:\n");
			bw.write("  - tell{&e������������������ʱ�����5�룡} @a\n");
			bw.write("  onPlayerFreeJoin:\n");
			bw.write("  - tell{&e���<striker>������<display>������������ (<playeramount>/<maxplayer>)} @a\n");
			bw.write("  - tell{&c�ö���Ϊ���ɼ�����У����ѱ�ֱ�Ӵ��͵�Ŀ��ص㣡} @p\n");			
			bw.write("#����������������ɼ���(����FreeJoin)ʱִ�е�����<striker>������ָ�������ҡ�\n");
			bw.write("  onPlayerLeave:\n");
			bw.write("  - tell{&e���<striker>�뿪��<display>����Ϸ������ (<playeramount>/<maxplayer>)} @a\n");
			bw.write("  - tell{&e���뿪��<display>��������ص��˴�����} @p\n");
			bw.write("  - tell{&e����Թۿ���Ϸ��ս����������/fw leave�뿪������} @p\n");
			bw.write("#��������������뿪����ʱִ�е�����<striker>������ָ�뿪����ҡ�\n");
			bw.write("  onGroupStart:\n");
			bw.write("  - tell{&e<display>���ڿ�ʼ��} @a\n");
			bw.write("  - heal{20} @a\n");
			bw.write("#���������ö��п�ʼʱִ�е�����<player>��������Ч��\n");
			bw.write("  onTimePast(200):\n");
			bw.write("  - tell{&4<display>���л���200�뽫������} @a\n");
			bw.write("#������������Ϸʱ��ʣ�༸��ʱ(������)ʱִ�е�����\n");
			bw.write("  onTimePast(100):\n");
			bw.write("  - tell{&4<display>���л���100�뽫������} @a\n");
			bw.write("  onTimePast(30):\n");
			bw.write("  - tell{&4<display>���л���30�뽫������} @a\n");
			bw.write("  onTimePast(10):\n");
			bw.write("  - tell{&4<display>���л���10�뽫������} @a\n");
			bw.write("  onLobbyTimePast(10):\n");
			bw.write("  - tell{&2<display>���л���10�뽫��ʼ��} @a\n");
			bw.write("#����������ʣ��������ʱ(������)ʱִ�е�����\n");
			bw.write("  onPlayerRest(1):\n");
			bw.write("  - tell{&6��ϲ��վ�������} @a\n");
			bw.write("  - command{eco give <player> 100} @a" + "\n");
			bw.write("  - leave{} @p" + "\n");
			bw.write("  onPlayerRest(2):\n");
			bw.write("  - tell{&6ֻʣ��������ң�} @a\n");
			bw.write("#TimePast��PlayerRest�������ö������@p��Ч��\n");
			bw.write("Timer: \n");
			bw.write("#��������ƶ��еĽ׶�ִ��ʱ�䡣\n");
			bw.write("  LobbyTime: 60" + "\n");
			bw.write("  #�����ǵȴ���Lobby��ʱ�䡣�ﵽ����������ʼ��ʱ���ȴ���Ϻ���Ϸ����ʼ��\n");
			bw.write("  Full_LobbyTime: 10" + "\n");
			bw.write("  #������Lobby���˺�����ʱ�䡣Lobby����ʱ��LobbyTime���������ʱ�������ʱ��\n");
			bw.write("  ArenaTime: 300" + "\n");
			bw.write("  #��������Ϸʱ�䡣�����ʱ��϶�����δ��ɢ������ǿ�ƽ�ɢ��\n");
			bw.write("Locations:" + "\n");
			bw.write("  #�����Ƕ��еĸ����������ã�\n");
			bw.write("  Lobby: " + X + "," + Y + "," + Z + "," + world + "\n");
			bw.write("  #�����ǵȴ�������������á�\n");
			bw.write("  Arena:\n");
			bw.write("  - " + X + "," + Y + "," + Z + "," + world + "\n");
			bw.write("  #��������Ϸ�ص�����á�\n");
			bw.write("  #��ҽ���������͵�����һ���ص㡣\n");
			bw.write("  #ע�⣺��ҵĶ����뿪�ص�Ϊ������LobbyLoc��\n");		
			bw.write("Trigger:" + "\n");
			bw.write("#�����Ƕ������񴥷������ã�\n");
			bw.write("#<player>��ִָ���ߡ�������Ҵ����Ĵ������У�<striker>��ָ�����ߡ�\n");
			bw.write("  DeathExample:" + "\n");
			bw.write("    Type: Death" + "\n");
			bw.write("    #�������ñ����������͡�(��ͬ)\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{���<striker>����Ϸ�������ˣ�} @a" + "\n");
			bw.write("    - tell{������������,�������뿪���С�} @p" + "\n");
			bw.write("    - heal{20} @p" + "\n");
			bw.write("    - leave{} @p" + "\n");
			bw.write("    #�������ô���������ʱִ�е�����(��ͬ)\n");
			bw.write("  PickupItemExample:" + "\n");
			bw.write("    Type: PickupItem" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - 264" + "\n");
			bw.write("    #�������ûᱻ��������ƷID��\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{��������ʯ��} @p" + "\n");
			bw.write("  AnotherPickupItemExample:" + "\n");
			bw.write("    Type: PickupItem" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - 265" + "\n");
			bw.write("    #�������ûᱻ��������ƷID��\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{������������} @p" + "\n");
			bw.write("  WalkOnBlockExample:" + "\n");
			bw.write("    Type: WalkOnBlock" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - 1" + "\n");
			bw.write("    #�������ûᱻ��������ƷID��\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{��ȵ���ʯͷ��} @p" + "\n");
			bw.write("  KillEntityExample:" + "\n");
			bw.write("    Type: KillEntity" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - DC��ħ��" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{���<striker>�ɹ���ɱ���ռ�BOSS��ȫ�ӽ���100Ԫ��} @a" + "\n");
			bw.write("    - tell{��ϲ���ɱ���ռ�BOSS����} @p" + "\n");
			bw.write("    - command{eco give <player> 100} @a" + "\n");
			bw.write("  KillAllEntityExample:" + "\n");
			bw.write("    Type: KillAllEntity" + "\n");
			bw.write("    Id:" + "\n");
			bw.write("    - DC��ħ��" + "\n");
			bw.write("    - ��ħ���Ļ���" + "\n");
			bw.write("    - ��ħ������̽" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{����ɹ���ɱ�����һֻ���������ɣ����ǽ���5�����뿪��} @a" + "\n");
			bw.write("    - heal{20} @a" + "\n");
			bw.write("    - delay{100}" + "\n");
			bw.write("    - leave{} @p" + "\n");
			bw.write("  #<striker>�������ָ��ɱ�������һ��������ˡ�" + "\n");		
			bw.write("  KillPlayerExample:" + "\n");
			bw.write("    Type: KillPlayer" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{��ϲ���ɱ��һ����ң��ͽ�50Ԫ��} @p" + "\n");
			bw.write("    - command{eco give <player> 50} @p" + "\n");
			bw.write("  DissExample:" + "\n");
			bw.write("    Type: Diss" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{�����ⲿԭ�򣬶��б��Ƚ�ɢ��} @a" + "\n");
			bw.write("    - leave{} @a" + "\n");
			bw.write("  TimeUpExample:" + "\n");
			bw.write("    Type: TimeUp" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - tell{����ʱ���ѵ������б��Ƚ�ɢ��} @a" + "\n");
			bw.write("    - leave{} @a" + "\n");
			bw.write("  OfflineExample:" + "\n");
			bw.write("    Type: Offline" + "\n");
			bw.write("    Task:" + "\n");
			bw.write("    - leave{} @a" + "\n");
			bw.write("    #�������޸���һ�������Ҳ��˳��������׵���BUG��\n");
			bw.write("PlayerRule:" + "\n");
			bw.write("  NameColor: WHITE" + "\n");
			bw.write("  #������ҵ�������ɫ���������ÿ����ǣ�\n");
			bw.write("  #WHITE,YELLOW,GREEN,BLUE,RED,BLACK,GRAY,PURPLE\n");
			bw.write("  NameInvisible: true" + "\n");
			bw.write("  #��������Ƿ��ܿ������֡�\n");
			bw.write("  PvP: false" + "\n");
			bw.write("  #�����Ƿ�����PVP��\n");
			bw.write("  Potionhit: false" + "\n");
			bw.write("  #�����Ƿ�������ҩˮӰ�졣\n");
			bw.write("  Projectile: false" + "\n");
			bw.write("  #�����Ƿ����������˺���\n");
			bw.write("  WhiteListCommand:\n");
			bw.write("  - fw\n");
			bw.write("  - fwc\n");
			bw.write("  #������������ڶ���������ʹ�õ�ָ�\n");
			bw.write("  DelGameItem: false\n");
			bw.write("  #�Ƿ�ɾ����Ϸ��Ʒ�����������\n");
			bw.write("  #�뿪���е��˽��ᱻ��ձ�����\n");
			bw.write("  ChatInGroup: false\n");
			bw.write("  #�Ƿ�ǿ�ƶ������졣\n");
			bw.write("  #����������������콫ֱ��ת��Ϊ���������졣\n");
			bw.write("  #����رգ���ұ�������/fwc <����> ���ж��������졣\n");		
			bw.close();
			Load(byLobby,file);
		} catch (IOException e) {
		}
		if(Pl!=null){
			Pl.sendMessage(ChatColor.YELLOW +  Language.getMessage("group_create"));
		}
	}
/**
 * ��ȡһ��������ڵĶ���
 * @param player ��Ҫ�鿴���е����
 * @return ������ڵĶ��У�������ڶ����򷵻�null��
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
	 * ��ȡ�ļ����У������Ƿ�ɹ������Զ���ӵ�Lobby��Lobby�б��С�
	 * @param byLobby �����Ĵ���(��Ҫ��ȡ�����ļ���)
	 * @param file ��ȡ���ļ�
	 * @return ��ȡ�Ƿ�ɹ�
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
			Data.ConsoleInfo("�ļ�����");
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
	 * �Ƿ���Լ��롣��������Ѿ���ʼ,canJoin�ͻ�Ϊfalse.
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
	 * ��ȡ/���ر�����,��debug����ʱ�ᷢ����ϸ������Ϣ��
	 * @return ��ȡ/�����Ƿ�ɹ���
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
	 * �ö�����������Ҵ���Diss������
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
	 * �ö�������������뿪���Դﵽ��ն���Ŀ�ġ�
	 * ���뿪���ᴥ���κδ�������
	 */
	public void Clear(){
		while(playerList.size()>0){
			LeaveGroup(playerList.get(0));
		}
	}
	/**
	 * ��鱾�����Ƿ��������ҡ�
	 * 
	 * @param player
	 *            ��������ҡ�
	 * @return �Ƿ��鵽
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
	 * ��鱾�����Ƿ��������ҡ�
	 * 
	 * @param player
	 *            ��������ҡ�
	 * @return �Ƿ��鵽
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
	 * ��ȡ�������ڵĴ�����
	 */
	public Lobby byLobby() {
		return byLobby;
	}
	
	/**
	 * ��һ����Ҽ�����С�
	 * �÷������ṩ���������ã���Ҫ������ã�
	 * 
	 * @param player
	 *            ���������ҡ�
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
					if (playerList.size() == MinPlayer) {// �����ﵽ�������
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
 * ��ʼһ�����С��÷�������ʱ�����ã�����������á�
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
	 * ������뿪���У�
	 * 
	 * @param player
	 *            ���뿪����ҡ�
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
	 * ����ҷ��Ͷ���״̬��
	 * 
	 * @param player
	 *            �����͵����
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
	 * �������Ƿ�Ϊ�ա�
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
		Data.Debug("���"+player.getName()+"�������ɶ���"+Name+"��ִ��onPlayerFreeJoin��");
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
	 * ����һ��������
	 * @param AddScore ������
	 */
	public void AddScore(int AddScore){
		Score = Score+AddScore;
		onScoreOver();
	}
	/**
	 * �۳�һ��������(���ɵ���0)
	 * @param DelScore ������
	 */
	public void DelScore(int DelScore){
		Score = Score-DelScore;
		if(Score<0){
			Score=0;
		}
	}
	/**
	 * ������ҵķ�����(���ɵ���0)
	 * @param NewScore ������
	 */
	public void SetScore(int NewScore){
		if(NewScore>=0){
			this.Score=NewScore;
			onScoreOver();
		}
	}
	/**
	 * ��ȡ���еķ�����
	 */
	public int GetScore(){
		return Score;
	}
}
