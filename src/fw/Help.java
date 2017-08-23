package fw;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
/**
 * ���ڱ༭���ָ��������ࡣ
 *
 */
public class Help {
	private static String title(String str) {
		return ChatColor.RED + "-------[" + ChatColor.YELLOW + str + ChatColor.RED + "]-------";
	}
/**
 * ���Ͳ����ָ�������
 * @param sender Ҫ���͵Ķ���
 */
	public static void MainHelp(CommandSender sender) {
		if(Data.language.equals("Chinese")){
			sender.sendMessage(title("CustomGo "+Data.Version+" ����"));
			sender.sendMessage("/fw reload              ���ز��");
			sender.sendMessage("/fw list                �鿴���ж���/�������");
			sender.sendMessage("/fw leave               �뿪һ������Ĵ���");
			sender.sendMessage("/fw <��������> <����>   ������ָ��[��ϸ��������һ��]");
		}else{
			sender.sendMessage(title("CustomGo "+Data.Version+" Help"));
			sender.sendMessage("/fw reload            "+ChatColor.YELLOW+"reload the Plugin");
			sender.sendMessage("/fw list              "+ChatColor.YELLOW+"show the lobby list");
			sender.sendMessage("/fw leave             "+ChatColor.YELLOW+"leave a lobby");
			sender.sendMessage("/fw <lobby> <value> "+ChatColor.YELLOW+"commands about lobby");
		}
	}
	/**
	 * ���Ͳ��lobby����������
	 * @param sender Ҫ���͵Ķ���
	 */
	public static void LobbyHelp(CommandSender sender) {
		if(Data.language.equals("Chinese")){
			sender.sendMessage(title("����ָ���������"));
			sender.sendMessage("join             ����һ�����д���");
			sender.sendMessage("info             �鿴����������еĲ�����Ϣ");
			sender.sendMessage("statu            �鿴����������е�״̬");
			sender.sendMessage("create           ����һ������");
			sender.sendMessage("remove           �Ƴ�һ�����д���");
			sender.sendMessage("addsign          ��׼�ǶԵ���������Ϊ��������");
			sender.sendMessage("addgroup <������>  ���������½�һ�����С�");	
			sender.sendMessage("load             ��ȡһ������������");
			sender.sendMessage("unload           ȡ����ȡ����[�ɽ�ֹ����]");
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
