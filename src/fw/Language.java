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
 * ���ڱ༭���Ե��ࡣ
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
			bw.write("lobby_create: '�ɹ������µķ������������Ĭ�����á����ں�̨�����ļ����޸ģ�'\n");
			bw.write("lobby_create_failed: '������������Ѿ���ʹ�ù��ˣ�'\n");
			bw.write("lobby_remove: '�ɹ��Ƴ����������'\n");
			bw.write("lobby_remove_failed: 'û�з��������������֣�'\n");
			bw.write("lobby_showlist: '��ǰ���д���'\n");
			bw.write("group_create: '�ɹ������µķ������������Ĭ�����á����ں�̨�����ļ����޸ģ�'\n");
			bw.write("group_create_failed: '������������Ѿ���ʹ�ù��ˣ�'\n");
			bw.write("group_remove: '�ɹ��Ƴ����������'\n");
			bw.write("group_remove_failed: '�ɹ��Ƴ����������'\n");
			bw.write("notcomplete: 'û�б�������ȡ'\n");
			bw.write("joinerror_joinother: '����ʧ�ܣ����Ѿ������˱����Ϸ��'\n");
			bw.write("joinerror_full: '����ʧ�ܣ�����������'\n");
			bw.write("joinerror_inventory: '����ʧ�ܣ����������ı�����'\n");
			bw.write("joinerror_equipment: '����ʧ�ܣ�����������װ����'\n");
			bw.write("joinerror_playing: '����ʧ�ܣ���Ϸ���ڽ����У�'\n");
			bw.write("joinerror_loadfailed: '����ʧ�ܣ����������ǲ������ģ�'\n");
			bw.write("leaveerror_none: '�������κζ����У�'\n");
			bw.write("commanderror_debug: '����Ա���ڽ��в�����ԣ������ʱ�޷����롣�����½⡣'\n");
			bw.write("commanderror_unknown: 'ָ������(С��ʾ:ʹ��/fw help�鿴������)'\n");	
			bw.write("commanderror_nopermission: '��û��Ȩ����������'\n");
			bw.write("commanderror_console: '����̨�޷�ִ�и����'\n");
			bw.write("enable_noVault: '��⵽������û�а�װVaultǰ�ò�����޷�ʹ�þ��ù��ܣ�'\n");
			bw.write("enable_success: '��������ɹ�!'\n");
			bw.write("disable_success: '����رճɹ�!'\n");
			bw.write("disable_clear: '������֯����޴����뿪��Ϸ......'\n");
			bw.write("reload_success: '��������ء�'\n");	
			bw.write("load_succeed: '��ȡ<name>�ɹ���'\n");
			bw.write("load_failed: '��ȡ<name>ʧ�ܡ�'\n");
			bw.write("load_lostconfig: '<name>ȱ������<config>��(��Ĭ��Ϊ<default>)'\n");
			bw.write("load_lostimportantconfig: '<name>ȱ������<config>��(��������)'\n");
			bw.write("debug_on: '����ģʽ�ѿ�����'\n");
			bw.write("debug_off: '����ģʽ�ѹرա�'\n");
			bw.write("ban_pvp: '��ֹ�����ڻ���PVP!'\n");
			bw.write("ban_projectile: '�������������!'\n");
			bw.write("ban_potion: '���޷�����ҩˮ�����Ķ��ѣ�'\n");
			bw.write("ban_command: '�����н�ֹʹ�ñ�ָ�'\n");
			bw.write("sign_create_success: '���Ӵ����ɹ�!'\n");
			bw.write("sign_create_failed: '���Ӵ���ʧ��!'\n");
			bw.write("sign_destroyed: '����ɾ���ɹ�!'\n");
			bw.write("location_failed: '������Ч�����Ĵ����Ѿܾ���'\n");
			bw.write("location_unsafe: '������괫�Ͳ���ȫ��'\n");
			bw.write("signinfo:\n");
			bw.write("  freeJoin: '���ɼ���'\n");
			bw.write("  waiting: '�ȴ���'\n");
			bw.write("  ready: '<time>���ʼ'\n");
			bw.write("  started: '������Ϸ'\n");
			bw.write("lobby_statu:\n");
			bw.write("  playeramount: '��������'\n");
			bw.write("  skiplobby: 'ֱ�ӽ������'\n");
			bw.write("  statu: '��ǰ״̬'\n");
			bw.write("  group_statulist: '�����ڵ����ж���״̬'\n");
			bw.write("group_statu:\n");
			bw.write("  playerlist: '����б�'\n");
			bw.write("  playerlist_none: '�����'\n");		
			bw.write("  freejoin: '���ɼ���'\n");
			bw.write("  statu: '��ǰ״̬'\n");
			bw.close();
		} catch (IOException e) {
		}
		Data.LanguageFileConf = YamlConfiguration.loadConfiguration(Data.Language);
	}
	private static void English() {
		try {
			FileWriter bw = new FileWriter(Data.Language, false);
			bw.write("#���������ٸ�������,��ɾ�����ļ�,����Option.yml�и���'\n");
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
