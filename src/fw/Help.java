package fw;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
/**
 * 用于编辑插件指令帮助的类。
 *
 */
public class Help {
	private static String title(String str) {
		return ChatColor.RED + "-------[" + ChatColor.YELLOW + str + ChatColor.RED + "]-------";
	}
/**
 * 发送插件总指令帮助。
 * @param sender 要发送的对象。
 */
	public static void MainHelp(CommandSender sender) {
		if(Data.language.equals("Chinese")){
			sender.sendMessage(title("CustomGo "+Data.Version+" 帮助"));
			sender.sendMessage("/fw reload              重载插件");
			sender.sendMessage("/fw list                查看所有队列/分配大厅");
			sender.sendMessage("/fw leave               离开一个加入的大厅");
			sender.sendMessage("/fw <分配厅名> <参数>   分配厅指令[详细帮助见下一栏]");
		}else{
			sender.sendMessage(title("CustomGo "+Data.Version+" Help"));
			sender.sendMessage("/fw reload            "+ChatColor.YELLOW+"reload the Plugin");
			sender.sendMessage("/fw list              "+ChatColor.YELLOW+"show the lobby list");
			sender.sendMessage("/fw leave             "+ChatColor.YELLOW+"leave a lobby");
			sender.sendMessage("/fw <lobby> <value> "+ChatColor.YELLOW+"commands about lobby");
		}
	}
	/**
	 * 发送插件lobby参数帮助。
	 * @param sender 要发送的对象。
	 */
	public static void LobbyHelp(CommandSender sender) {
		if(Data.language.equals("Chinese")){
			sender.sendMessage(title("大厅指令参数帮助"));
			sender.sendMessage("join             加入一个已有大厅");
			sender.sendMessage("info             查看大厅及其队列的部分信息");
			sender.sendMessage("statu            查看大厅及其队列的状态");
			sender.sendMessage("create           创建一个大厅");
			sender.sendMessage("remove           移除一个已有大厅");
			sender.sendMessage("addsign          将准星对的牌子设置为大厅牌子");
			sender.sendMessage("addgroup <队列名>  给分配厅新建一个队列。");	
			sender.sendMessage("load             读取一个大厅的配置");
			sender.sendMessage("unload           取消读取配置[可禁止加入]");
		}else{
			sender.sendMessage(title("lobby Help"));
			sender.sendMessage("join              "+ChatColor.YELLOW+"join a lobby");
			sender.sendMessage("statu             "+ChatColor.YELLOW+"look a lobby's statu");
			sender.sendMessage("create            "+ChatColor.YELLOW+"create a new lobby");
			sender.sendMessage("remove            "+ChatColor.YELLOW+"remove a lobby");
			sender.sendMessage("addsign           "+ChatColor.YELLOW+"change a sign(player aimed) to joinsign");
			sender.sendMessage("addgroup <group>  "+ChatColor.YELLOW+"create a new group in this lobby");	
			sender.sendMessage("load              "+ChatColor.YELLOW+"load a lobby separately");
		}
	}
}
