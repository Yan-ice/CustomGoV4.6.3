package fw.lobby.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTML.Tag;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;

import fw.Data;
import fw.location.FLocation;
/**
 * Ϊ�˱��ڽ��мƷְ����㣬
 * ����Player��Ĵ����ࡣ
 *
 */
public class FPlayer{
	
	Player player;
	
	int Score = 0;
	List<String> tag = new ArrayList<String>();
	private static Map<Player,FPlayer> map = new HashMap<Player,FPlayer>();
	/**
	 * ͨ��Player��ҵõ�������FPlayer��ҡ�
	 */
	public static FPlayer getFPlayer(Player player){
		if(map.containsKey(player)){
			return map.get(player);
		}
		return null;
	}
	/**
	 * ����һ��Player��ҵĴ�����FPlayer��
	 */
	public FPlayer(Player player){
		this.player = player;
		map.put(player, this);
	}
	/**
	 * ��ȡ��FPlayer�����Player��
	 */
	public Player ToMC(){
		return player;
	}
	/**
	 * ����һ��������
	 * @param ConsumeScore ���Ѷ�
	 * @return �Ƿ����ѳɹ�(�����Ƿ��㹻)
	 */
	public boolean ConsumeScore(int ConsumeScore){
		if(Score>=ConsumeScore){
			Score = Score-ConsumeScore;
			return true;
		}else{
			return false;
		}
	}
	/**
	 * ����һ��������
	 * @param AddScore ������
	 */
	public void AddScore(int AddScore){
		Score = Score+AddScore;
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
		}else{
			Data.ConsoleCommand("���"+player.getName()+"�ķ�������ͼ�޸�Ϊ����[����ֹ]");
		}

	}
	/**
	 * ��ȡ��ҵķ�����
	 */
	public int GetScore(){
		return Score;
	}
	/**
	 * �����ҵķ�����Ϣ����ң�
	 */
	public void lookScore(){
		player.sendMessage("���ķ���:"+Score);
	}
	
	
	public String getName(){
		return player.getName();
	}
	public void teleport(Location loc){
		player.teleport(loc);
	}
	public void sendMessage(String str){
		player.sendMessage(str);
	}
	public PlayerInventory getInventory(){
		return player.getInventory();
	}
	public void SetNameColor(ChatColor color){
		player.setCustomName(color+player.getCustomName());
	}
	public void SetNameColor(String color){
		player.setCustomName(color+player.getCustomName());
	}
	
	public void SetNameInv(boolean Invisible){
		player.setCustomNameVisible(Invisible);
	}
	public void chat(String chat){
		player.chat(chat);
	}
	/**
	 * ʹ�����ָ�������ڷ���һ��������Ϣ��
	 * @param chat ��������
	 * @param plist ָ������
	 */
	public void chatInList(String chat,List<FPlayer> plist){
		for(int a = 0;a<plist.size();a++){
			plist.get(a).sendMessage(ChatColor.YELLOW+"[��������]"+ChatColor.WHITE+"<"+ChatColor.RED+getName()+ChatColor.WHITE+"> "+chat);
		}
	}
	public EntityEquipment getEquipment() {
		return player.getEquipment();
	}
	public void addTag(String tag){
		if(!this.tag.contains(tag)){
			this.tag.add(tag);
		}
	}
	public void removeTag(String rtag){
		for(int a = 0;a<tag.size();a++){
			if(tag.get(a).equals(rtag)){
				tag.remove(a);
			}
		}
	}
	public boolean hasTag(String tag){
		return this.tag.contains(tag);
	}
	public void clearTag(){
		tag = new ArrayList<String>();
	}
}
