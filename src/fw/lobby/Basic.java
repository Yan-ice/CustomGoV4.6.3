package fw.lobby;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fw.Language;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;
public abstract class Basic {
	public String Name;
	public String Display;
	protected int MinPlayer;
	protected int MaxPlayer;
	protected boolean isComplete = false;
	protected List<FPlayer> playerList = new ArrayList<FPlayer>();
	protected List<JoinSign> sign = new ArrayList<JoinSign>();

	protected String debugChange(String Message,String config,String defaultConfig){
		if(Message !=null){
			if(Name!=null){
				Message = Message.replaceAll("<name>", Name);
			}
			if(config!=null){
				Message = Message.replaceAll("<config>", config);
			}
			if(defaultConfig!=null){
				Message = Message.replaceAll("<default>", defaultConfig);
			}
			return Message;
		}else{
			return "";
		}
	}
	
	public boolean isComplete() {
		return isComplete;
	}

	public String GetDisplay() {
		return Display;
	}

	public List<JoinSign> getSign() {
		return sign;
	}

	public int GetMaxPlayer() {
		return MaxPlayer;
	}

	public int GetMinPlayer() {
		return MinPlayer;
	}

	/**
	 * 获取当前玩家列表的复制。
	 * （您无法对玩家列表进行修改）
	 * @return
	 */
	public List<FPlayer> GetPlayerList() {
		List<FPlayer> list = new ArrayList<FPlayer>();
		for(int a = 0;a<playerList.size();a++){
			list.add(playerList.get(a));
		}
		return list;
	}

	public int GetPlayerAmount() {
		return playerList.size();
	}
	
	public static boolean AutoLeave(FPlayer player){
		if(Lobby.SearchPlayerInLobby(player)!=null){
			if(Group.SearchPlayerInGroup(player)!=null){
				Group.SearchPlayerInGroup(player).LeaveGroup(player);
			}else{
				if(Lobby.SearchPlayerInLobby(player)!=null){
					Lobby.SearchPlayerInLobby(player).LeaveLobby(player);
				}
			}
			return true;
		}
		player.sendMessage(ChatColor.RED+Language.getMessage("leaveerror-none"));
		return false;
	}
	public static boolean AutoLeaveAll(FPlayer player){
		if(Lobby.SearchPlayerInLobby(player)!=null){
			if(Group.SearchPlayerInGroup(player)!=null){
				Group.SearchPlayerInGroup(player).LeaveGroup(player);
				if(Lobby.SearchPlayerInLobby(player)!=null){
					Lobby.SearchPlayerInLobby(player).LeaveLobby(player);
				}
			}else{
				if(Lobby.SearchPlayerInLobby(player)!=null){
					Lobby.SearchPlayerInLobby(player).LeaveLobby(player);
				}
			}
			return true;
		}
		player.sendMessage(ChatColor.RED+Language.getMessage("leaveerror-none"));
		return false;
	}
	
	/**
	 * 给队内所有玩家发送信息。
	 * 
	 * @param Message
	 */
	public void SendNotice(String Message) {
		for (int a = 0; a < playerList.size(); a++) {
			playerList.get(a).sendMessage(Message);
		}
	}
	
}
