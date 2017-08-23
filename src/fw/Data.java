package fw;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import fw.lobby.group.Group;
import net.milkbowl.vault.economy.Economy;

public class Data {

	public static String Version = "V4.6.3";
	
	public static boolean debug = false;
	public static String language;
	
	public static Fwmain fwmain;

	public static Economy economy;

	public static File groupDir;
	public static File lobbyDir;
	public static File itemDir;
	public static File optionFile;
	public static File Language;
	
	public static FileConfiguration optionFileConf;
	public static FileConfiguration LanguageFileConf;

	public static void ConsoleInfo(String info) {
		fwmain.getLogger().info(info);
	}

	public static void LoadOption() {
		try {
			debug = optionFileConf.getBoolean("Debug");
			language = optionFileConf.getString("Language");
		} catch (NullPointerException x) {
			return;
		}
	}
/**
 * ��a,b֮���ȡһ���������

 * @return �õ��������
 */
	public static int Random(int a, int b) {
		int s;
		Random random = new Random();
		int length;
		if (a > b) {
			length = a - b;
			s = random.nextInt(length) + b;
		} else if (a < b) {
			length = b - a;
			s = random.nextInt(length) + a;
		} else {
			return a;
		}
		return s;
	}

	public static String ColorChange(String str) {
		return str.replace("&", "��");
	}

	public static List<String> ColorChange(List<String> str) {
		for (int a = 0; a < str.size(); a++) {
			str.set(a, ColorChange(str.get(a)));
		}
		return str;
	}

	public static void save() {
		try {
			optionFileConf.save(optionFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/**
 * �ÿ���ִ̨��һ��ָ�
 * @param command
 */
	public static void ConsoleCommand(String command) {
		Bukkit.dispatchCommand(Data.fwmain.getServer().getConsoleSender(), command);
	}
/**
 * ���̨����һ��Debug��Ϣ��
 * ����Ϣ���Զ�����ǰ׺������ֻ��Debug����ʱ��Ч��
 * @param str ��Ҫ���͵���Ϣ
 */
	public static void Debug(String str){
		if(debug){
			ConsoleInfo("[Debug]"+str);
		}
	}
	
}
