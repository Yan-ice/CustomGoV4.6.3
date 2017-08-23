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
 * 为了便于进行计分板运算，
 * 代替Player类的代理类。
 *
 */
public class FPlayer{
	
	Player player;
	
	int Score = 0;
	List<String> tag = new ArrayList<String>();
	private static Map<Player,FPlayer> map = new HashMap<Player,FPlayer>();
	/**
	 * 通过Player玩家得到代理类FPlayer玩家。
	 */
	public static FPlayer getFPlayer(Player player){
		if(map.containsKey(player)){
			return map.get(player);
		}
		return null;
	}
	/**
	 * 创建一个Player玩家的代理类FPlayer。
	 */
	public FPlayer(Player player){
		this.player = player;
		map.put(player, this);
	}
	/**
	 * 获取该FPlayer的玩家Player。
	 */
	public Player ToMC(){
		return player;
	}
	/**
	 * 消费一定分数。
	 * @param ConsumeScore 消费额
	 * @return 是否消费成功(分数是否足够)
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
	 * 增加一定分数。
	 * @param AddScore 增加量
	 */
	public void AddScore(int AddScore){
		Score = Score+AddScore;
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
		}else{
			Data.ConsoleCommand("玩家"+player.getName()+"的分数被企图修改为负数[已阻止]");
		}

	}
	/**
	 * 获取玩家的分数。
	 */
	public int GetScore(){
		return Score;
	}
	/**
	 * 输出玩家的分数信息给玩家，
	 */
	public void lookScore(){
		player.sendMessage("您的分数:"+Score);
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
	 * 使玩家在指定队伍内发送一条聊天信息。
	 * @param chat 聊天内容
	 * @param plist 指定队伍
	 */
	public void chatInList(String chat,List<FPlayer> plist){
		for(int a = 0;a<plist.size();a++){
			plist.get(a).sendMessage(ChatColor.YELLOW+"[队伍聊天]"+ChatColor.WHITE+"<"+ChatColor.RED+getName()+ChatColor.WHITE+"> "+chat);
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
