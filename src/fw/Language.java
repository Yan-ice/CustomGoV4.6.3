package fw;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fw.lobby.Lobby;
/**
 * 用于编辑语言的类。
 *
 */
public class Language {
	private static String Lang;
	public static String getMessage(String key) {
		String a = "";
		if(Data.LanguageFileConf.contains(key)){
			a = Data.LanguageFileConf.getString(key);
		}
		return a;
	}
	public static void SetLanguage(String Language,boolean ChangeYML){
		switch(Language){
		case "Chinese":
			Lang = "Chinese";
			if(ChangeYML){
				Chinese();
			}
			break;
		case "English":
			Lang = "English";
			if(ChangeYML){
				English();
			}
			break;
		default:
			Lang = "English";
			if(ChangeYML){
				English();
			}
			break;
		}
	}
	public static String getLanguage(){
		return Lang;
	}
	private static void Chinese() {

		try {
			FileWriter bw = new FileWriter(Data.Language, false);
			bw.write("#To change language quickly, delete this file and set the Language configuration in Option.yml\n");
			bw.write("lobby_create: '成功创建新的分配大厅并生成默认配置。请在后台配置文件中修改！'\n");
			bw.write("lobby_create_failed: '这个大厅名字已经被使用过了！'\n");
			bw.write("lobby_remove: '成功移除分配大厅！'\n");
			bw.write("lobby_remove_failed: '没有分配大厅叫这个名字！'\n");
			bw.write("lobby_showlist: '当前所有大厅'\n");
			bw.write("group_create: '成功创建新的分配大厅并生成默认配置。请在后台配置文件中修改！'\n");
			bw.write("group_create_failed: '这个队列名字已经被使用过了！'\n");
			bw.write("group_remove: '成功移除分配大厅！'\n");
			bw.write("group_remove_failed: '成功移除分配大厅！'\n");
			bw.write("notcomplete: '没有被正常读取'\n");
			bw.write("joinerror_joinother: '加入失败！您已经加入了别的游戏！'\n");
			bw.write("joinerror_full: '加入失败！人数已满！'\n");
			bw.write("joinerror_inventory: '加入失败！请先清空你的背包！'\n");
			bw.write("joinerror_equipment: '加入失败！请先清空你的装备！'\n");
			bw.write("joinerror_playing: '加入失败！游戏正在进行中！'\n");
			bw.write("joinerror_loadfailed: '加入失败！它的配置是不完整的！'\n");
			bw.write("leaveerror_none: '您不在任何队列中！'\n");
			bw.write("commanderror_debug: '管理员正在进行插件测试，玩家暂时无法加入。敬请谅解。'\n");
			bw.write("commanderror_unknown: '指令有误！(小提示:使用/fw help查看帮助！)'\n");	
			bw.write("commanderror_nopermission: '您没有权限这样做！'\n");
			bw.write("commanderror_console: '控制台无法执行该命令！'\n");
			bw.write("enable_noVault: '检测到服务器没有安装Vault前置插件！无法使用经济功能！'\n");
			bw.write("enable_success: '插件启动成功!'\n");
			bw.write("disable_success: '插件关闭成功!'\n");
			bw.write("disable_clear: '正在组织玩家无触发离开游戏......'\n");
			bw.write("reload_success: '插件已重载。'\n");	
			bw.write("load_succeed: '读取<name>成功。'\n");
			bw.write("load_failed: '读取<name>失败。'\n");
			bw.write("load_lostconfig: '<name>缺少配置<config>！(已默认为<default>)'\n");
			bw.write("load_lostimportantconfig: '<name>缺少配置<config>！(致命错误)'\n");
			bw.write("debug_on: '测试模式已开启。'\n");
			bw.write("debug_off: '测试模式已关闭。'\n");
			bw.write("ban_pvp: '禁止队伍内互相PVP!'\n");
			bw.write("ban_projectile: '不允许射击队友!'\n");
			bw.write("ban_potion: '您无法作用药水于您的队友！'\n");
			bw.write("ban_command: '队列中禁止使用本指令！'\n");
			bw.write("sign_create_success: '牌子创建成功!'\n");
			bw.write("sign_create_failed: '牌子创建失败!'\n");
			bw.write("sign_destroyed: '牌子删除成功!'\n");
			bw.write("location_failed: '坐标无效！您的传送已拒绝！'\n");
			bw.write("location_unsafe: '这个坐标传送不安全！'\n");
			bw.write("signinfo:\n");
			bw.write("  freeJoin: '自由加入'\n");
			bw.write("  waiting: '等待中'\n");
			bw.write("  ready: '<time>秒后开始'\n");
			bw.write("  started: '正在游戏'\n");
			bw.write("lobby_statu:\n");
			bw.write("  playeramount: '加入人数'\n");
			bw.write("  skiplobby: '直接进入队列'\n");
			bw.write("  statu: '当前状态'\n");
			bw.write("  group_statulist: '大厅内的所有队列状态'\n");
			bw.write("group_statu:\n");
			bw.write("  playerlist: '玩家列表'\n");
			bw.write("  playerlist_none: '无玩家'\n");		
			bw.write("  freejoin: '自由加入'\n");
			bw.write("  statu: '当前状态'\n");
			bw.close();
		} catch (IOException e) {
		}
		Data.LanguageFileConf = YamlConfiguration.loadConfiguration(Data.Language);
	}
	private static void English() {
		try {
			FileWriter bw = new FileWriter(Data.Language, false);
			bw.write("#如果你想快速更换语言,请删除本文件,并在Option.yml中更改'\n");
			bw.write("divider_create: 'You create a new divider and generate the default configuration successfully.Please option it in the backstage!'\n");
			bw.write("divider_create_failed: 'this name has been used!'\n");
			bw.write("divider_remove: 'You removed a divider successfully!'\n");
			bw.write("divider_remove_failed: 'Cannot find any divider with this name!'\n");
			bw.write("divider_showlist: 'Divider list'\n");
			bw.write("group_create: 'You create a new group and generate the default configuration successfully.Please option it in the backstage!'\n");
			bw.write("group_create_failed: 'this name has been used!'\n");
			bw.write("group_remove: 'You removed a group successfully!'\n");
			bw.write("group_remove_failed: 'Cannot find any group with this name!'\n");
			bw.write("notcomplete: 'load failed'\n");
			bw.write("joinerror_joinother: 'You has been in another game!'\n");
			bw.write("joinerror_full: 'the player was full!'\n");
			bw.write("joinerror_inventory: 'Please empty your inventory first!'\n");
			bw.write("joinerror_equipment: 'Please empty your equipments first!'\n");
			bw.write("joinerror_playing: 'this game has started!'\n");
			bw.write("joinerror_loadfailed: 'Cannot load this game!'\n");
			bw.write("leaveerror_none: 'You are not in any games!'\n");
			bw.write("commanderror_debug: 'the operator has opened Debug Mode.Please wait for testing!'\n");
			bw.write("commanderror_unknown: 'Unknown command! (Use \"/fw help\" to get help!)'\n");	
			bw.write("commanderror_nopermission: 'You donnot have permission to do this!'\n");
			bw.write("commanderror_console: 'the console cannot run this command!'\n");
			bw.write("enable_noVault: 'the server did not install the Vault plugin!Unable to use economic function!'\n");
			bw.write("enable_success: 'Plugin has Enabled successfully!'\n");
			bw.write("disable_success: 'Plugin has Disabled successfully!'\n");
			bw.write("disable_clear: 'Player is leaving group without any task.......'\n");
			bw.write("reload_success: 'Reload complete.'\n");	
			bw.write("load_succeed: 'Read <name> successfully.'\n");
			bw.write("load_failed: 'Failed to read <name>.'\n");
			bw.write("load_lostconfig: 'Cannot read <config> in <name>!(The default is <default>)'\n");
			bw.write("load_lostimportantconfig: 'Cannot read <config> in <name>!(Fatal error!)'\n");
			bw.write("debug_on: 'You open the Debug Mode.'\n");
			bw.write("debug_off: 'You close the Debug Mode.'\n");
			bw.write("ban_pvp: 'Attack teammate is not allowed!'\n");
			bw.write("ban_projectile: 'Shoot teammate is not allowed!'\n");
			bw.write("ban_potion: 'You cannot apply effects to your teammates!'\n");
			bw.write("ban_command: 'You cannot use this command now!'\n");
			bw.write("sign_create_success: 'You create the JoinSign successfully!'\n");
			bw.write("sign_create_failed: 'Failed to create the JoinSign!'\n");
			bw.write("sign_destroyed: 'You removed the JoinSign successfully!'\n");
			bw.write("location_failed: 'Cannot load the location in Configuration!'\n");
			bw.write("location_unsafe: 'This location is unsafe!'\n");
			bw.write("signinfo:\n");
			bw.write("  freeJoin: 'Free to join'\n");
			bw.write("  waiting: 'waiting...'\n");
			bw.write("  ready: 'start in <time>s'\n");
			bw.write("  started: 'playing'\n");
			bw.write("divider_statu:\n");
			bw.write("  playeramount: 'player amount'\n");
			bw.write("  skipdivider: 'Direct access to Group'\n");
			bw.write("  statu: 'Statu'\n");
			bw.write("  group_statulist: 'Statu about all groups in divider'\n");
			bw.write("group_statu:\n");
			bw.write("  playerlist: 'players list'\n");
			bw.write("  playerlist_none: 'none'\n");		
			bw.write("  statu: 'Statu'\n");
			bw.write("  freejoin: 'Free to join'\n");	
			bw.close();
		} catch (IOException e) {
		}
		Data.LanguageFileConf = YamlConfiguration.loadConfiguration(Data.Language);
		
	}
}
