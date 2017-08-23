package fw.lobby;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fw.Data;
import fw.Fwmain;
import fw.Language;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;
import fw.location.FLocation;

public class JoinSign implements Listener {
	FLocation Loc;
	Lobby divider;
	boolean isLoad = false;
/**
 * 为一个分配大厅读取所有牌子。
 * @param divider 要读取的大厅
 * @return 读取成功的牌子列表。
 */
	public static List<JoinSign> DisposeLobbySign(Lobby divider){
		if(divider.getFileConf().contains("JoinSign")){
			List<String> stringlist = divider.getFileConf().getStringList("JoinSign");
			List<JoinSign> signlist = new ArrayList<JoinSign>();
			while(stringlist.size()>0){
				FLocation loc = new FLocation(stringlist.get(0));
				JoinSign sign = new JoinSign(loc,divider);
				signlist.add(sign);
				sign.LoadSign(divider);
				stringlist.remove(0);
			}
			return signlist;
		}
		return null;
	}
	public JoinSign(FLocation Loc, Lobby divider) {
		this.divider = divider;
		if (Loc == null || Loc.isComplete()) {
			return;
		}
		this.Loc = Loc;
		if (Loc.ToMC().getBlock().getState() instanceof Sign) {		
			Data.fwmain.getServer().getPluginManager().registerEvents(this, Data.fwmain);
		}
	}
/**
 * 重新读取木牌并刷新木牌的字。
 * @param divider 新的分配厅
 * @return
 */
	public boolean LoadSign(Lobby divider) {
		if (Loc == null || Loc.isComplete()) {
			return false;
		}
		HandlerList.unregisterAll(this);
		this.divider = divider;
		if (Loc.ToMC().getBlock().getState() instanceof Sign) {
			Sign sign = (Sign) Loc.ToMC().getBlock().getState();
			sign.setLine(0, ChatColor.BLUE + divider.GetDisplay());
			if(divider.isComplete()){
				sign.setLine(1,ChatColor.BLACK+ "(" + divider.GetPlayerAmount() +"/"+ChatColor.DARK_BLUE  + divider.GetMinPlayer() + ChatColor.BLACK+  "/" +ChatColor.DARK_RED+ divider.GetMaxPlayer() + ChatColor.BLACK+ ")");
				if(divider.isFreeJoin()){
					sign.setLine(3, ChatColor.GREEN + Language.getMessage("signinfo.freejoin"));
				}else{
					if (divider.CanJoin()) {
						if (divider.GetPlayerAmount() >= divider.GetMinPlayer()) {
							String start = ChatColor.GREEN + Language.getMessage("signinfo.ready");
							start = start.replaceAll("<time>", Integer.toString(divider.GetTimer().LobbyTimer().getTime()));
							sign.setLine(3, ChatColor.GREEN + start);
						} else {
							sign.setLine(3,
									ChatColor.GREEN + Language.getMessage("signinfo.waiting"));
						}
					} else {
						if(divider.GetPlayerAmount()!=0){
							sign.setLine(3, ChatColor.DARK_RED +  Language.getMessage("signinfo.started"));
						}else{
							sign.setLine(3,ChatColor.GREEN + Language.getMessage("signinfo.waiting"));
						}

					}
				}
			}else{
				sign.setLine(4, ChatColor.DARK_RED +Language.getMessage("signinfo.notcomplete"));
			}
			sign.update();
			Data.fwmain.getServer().getPluginManager().registerEvents(this, Data.fwmain);
			isLoad = true;
			return true;
		} else {
			return false;
		}
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void BreakListen(BlockBreakEvent evt) {
		if (evt.getBlock().getLocation().equals(Loc)) {
			if(divider.getFileConf().contains("JoinSign")){
				List<String> stringlist = divider.getFileConf().getStringList("JoinSign");
				stringlist.remove(
						Loc.ToMC().getBlockX() + "," + Loc.ToMC().getBlockY() + "," + Loc.ToMC().getBlockZ() + "," + Loc.getWorld().getName());
				divider.getFileConf().set("JoinSign",stringlist);
				try {
					divider.getFileConf().save(divider.GetOptionFile());
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				this.Loc = null;
				evt.getPlayer().sendMessage(Language.getMessage("sign_destroyed"));
				HandlerList.unregisterAll(this);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void InteractListen(PlayerInteractEvent evt) {
		if (evt.getClickedBlock() != null && evt.getClickedBlock().getState() != null
				&& evt.getClickedBlock().getState() instanceof Sign) {
			if (evt.getClickedBlock().getState().getLocation().equals(Loc)) {
				if (isLoad) {
					if(Data.debug){
						if(Fwmain.CheckPerm(evt.getPlayer(), "fw.debug")){
							divider.JoinLobby(new FPlayer(evt.getPlayer()));
							LoadSign(divider);
						}else{
							evt.getPlayer().sendMessage(Language.getMessage("commanderror_debug"));
						}
					}else{
						divider.JoinLobby(new FPlayer(evt.getPlayer()));
						LoadSign(divider);
					}
				} else {
					evt.getPlayer().sendMessage(ChatColor.RED + Language.getMessage("signinfo.notcomplete"));
				}
			}
		}

	}
}
