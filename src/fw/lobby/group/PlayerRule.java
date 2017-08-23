package fw.lobby.group;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import fw.Data;
import fw.Language;

public class PlayerRule {
	private boolean PvP = false;
	private boolean Potionhit = false;
	private boolean NameInv = true;
	private ChatColor color;
	private boolean DelGameItem = false;
	private List<String> WhiteListCommand = new ArrayList<String>();
	private boolean chatInGroup = true;
	private boolean Projectile = false;
	protected String debugChange(String Message,String Name,String config,String defaultConfig){
		Message = Message.replaceAll("<name>", Name);
		if(config!=null){
			Message = Message.replaceAll("<config>", config);
		}
		if(defaultConfig!=null){
			Message = Message.replaceAll("<default>", defaultConfig);
		}
		return Message;
	}
	
	public boolean Load(Group group) {
		if(group.getFileConf().contains("PlayerRule.NameColor")){
			String namecolor = group.getFileConf().getString("PlayerRule.NameColor");
			switch(namecolor){
			case "WHITE":
				color = ChatColor.WHITE;
				break;
			case "RED":
				color = ChatColor.RED;
				break;
			case "BLUE":
				color = ChatColor.BLUE;
				break;
			case "YELLOW":
				color = ChatColor.YELLOW;
				break;
			case "GREEN":
				color = ChatColor.GREEN;
				break;
			case "BLACK":
				color = ChatColor.BLACK;
				break;
			case "GRAY":
				color = ChatColor.GRAY;
				break;
			case "PURPLE":
				color = ChatColor.LIGHT_PURPLE;
				break;
			default:
				color = ChatColor.WHITE;
				Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.NameColor","WHITE"));
			}
		}else{
			color = ChatColor.WHITE;
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.NameColor","WHITE"));
		}
		if(group.getFileConf().contains("PlayerRule.NameInvisible")){
			this.NameInv = group.getFileConf().getBoolean("PlayerRule.NameInvisible");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.NameInvisible","true"));
		}
		
		if(group.getFileConf().contains("PlayerRule.PvP")){
			this.PvP = group.getFileConf().getBoolean("PlayerRule.PvP");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.PVP","false"));
		}
		if(group.getFileConf().contains("PlayerRule.Potionhit")){
			this.PvP = group.getFileConf().getBoolean("InGroup.Potionhit");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.PotionHit","false"));
		}
		if(group.getFileConf().contains("PlayerRule.WhiteListCommand")){
			this.WhiteListCommand = group.getFileConf().getStringList("PlayerRule.WhiteListCommand");
			if(!WhiteListCommand.contains("fw")){
				WhiteListCommand.add("fw");
			}
			if(!WhiteListCommand.contains("fwc")){
				WhiteListCommand.add("fwc");
			}
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.WhiteListCommand","fw,fwc"));
			WhiteListCommand.add("fw");
			WhiteListCommand.add("fwc");
		}
		if(group.getFileConf().contains("PlayerRule.DelGameItem")){
			DelGameItem = group.getFileConf().getBoolean("PlayerRule.DelGameItem");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.DelGameItem","false"));
		}
		if(group.getFileConf().contains("PlayerRule.ChatInGroup")){
			chatInGroup = group.getFileConf().getBoolean("PlayerRule.ChatInGroup");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.ChatInGroup","true"));
		}
		if(group.getFileConf().contains("PlayerRule.Projectile")){
			chatInGroup = group.getFileConf().getBoolean("PlayerRule.Projectile");
		}else{
			Data.Debug(debugChange(Language.getMessage("load_lostconfig"),group.Name,"PlayerRule.Projectile","false"));
		}
		return true;
	}

	boolean PvP() {
		return PvP;
	}

	boolean Potionhit() {
		return Potionhit;
	}
	
	ChatColor NameColor() {
		return color;
	}
	List<String> WhiteListCommand(){
		return WhiteListCommand;
	}
	
	boolean NameInv(){
		return NameInv;
	}
	boolean DelGameItem(){
		return DelGameItem;
	}

	public boolean chatInGroup() {
		return chatInGroup;
	}
	public boolean Projectile(){
		return Projectile;
	}
}
