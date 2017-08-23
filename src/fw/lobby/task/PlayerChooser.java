package fw.lobby.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import fw.Data;
import fw.lobby.player.FPlayer;

public class PlayerChooser {
	List<FPlayer> player = new ArrayList<FPlayer>();
	
	FPlayer Target;
	
	PlayerChooser(List<FPlayer> player,FPlayer Target){
		for(int a = 0;a<player.size();a++){
			this.player.add(player.get(a));
		}
		this.Target = Target;
	}
	public List<FPlayer> Choose(String Chooser){
		if(Chooser.indexOf("[") !=-1){
			GroupConditionChoose("["+ Chooser.split("\\[")[1]);
			RangeChoose(Chooser.split("\\[")[0]);
		}else{
			RangeChoose(Chooser);
		}
		return player;
	}
	protected void RangeChoose(String Range){
		if (Range.indexOf("@") != -1) {
			String target = (Range.split("@")[1]);
			List<FPlayer> newplayer = new ArrayList<FPlayer>();
			switch (target) {
			case "a":
				if(player!=null && player.size()>0){
					return;
				}else{
					player=null;
					return;
				}
			case "p":
				if(player!=null && player.size()>0){
					if(player.contains(Target)){
						newplayer.add(Target);
					}else{
						player=null;
						return;
					}
				}else{
					player=null;
					return;
				}

				break;
			case "r":
				if(player!=null && player.size()>0){
					int a = Data.Random(0, player.size() - 1);
					newplayer.add(player.get(a));
					break;
				}else{
					player=null;
					return;
				}
			case "c":
				player=null;
				return;
			default:
				if(player!=null && player.size()>0){
					if(player.contains(Target)){
						newplayer.add(Target);
					}else{
						player=null;
						return;
					}
				}else{
					player=null;
					return;
				}
				break;
			}
			player=newplayer;
		}

	}
	
	/**
	 * 处理的条件格式：[XXX,XXX,XXX]
	 */
	protected void GroupConditionChoose(String ConditionGroup){
		String[] condition = ConditionGroup.split("\\[")[1].split("\\]")[0].split(",");
		for(int a = 0;a<condition.length;a++){
			ConditionChoose(condition[a]);
		}
	}
	protected void ConditionChoose(String condition){
		if(condition.split("=").length>1){
			String value = condition.split("=")[1];
			switch(condition.split("=")[0]){
			case "minLevel":
				MinLevel(Integer.valueOf(value));
				break;
			case "maxLevel":
				MaxLevel(Integer.valueOf(value));
				break;
			case "minLife":
				MinLife(Integer.valueOf(value));
				break;
			case "maxLife":
				MaxLife(Integer.valueOf(value));
				break;
			case "minScore":
				MinScore(Integer.valueOf(value));
				break;
			case "maxScore":
				MaxScore(Integer.valueOf(value));
				break;
			case "highScoreList":
				highScoreList(Integer.valueOf(value));
				break;
			case "lowScoreList":
				lowScoreList(Integer.valueOf(value));
				break;
			case "Tag":
				Tag(value);
				break;
			case "Permission":
				Permission(value);
				break;	
			default:
				return;
			}
		}
		return;
	}
	
	
	private void MinLevel(int level){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).ToMC().getExpToLevel()>=level){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	private void MaxLevel(int level){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).ToMC().getExpToLevel()<=level){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	private void MinLife(int life){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).ToMC().getHealthScale()*20>=life){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	private void MaxLife(int life){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(((Damageable)player.get(a).ToMC()).getHealth()<=life){
				newplayer.add(player.get(a));
			}
		}
		
		player = newplayer;
	}
	
	private void MaxScore(int score){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).GetScore()<=score){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	
	private void MinScore(int score){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).GetScore()>=score){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	
	private void highScoreList(int l){
		List<FPlayer> plist = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			plist.add(player.get(a));
		}
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		if(plist.size()>0){
			FPlayer pl = null;
			while(plist.size()>0){
				int score = 0;
				int remove = 0;
				for(int a = 0;a<plist.size();a++){
					if(plist.get(a).GetScore()>score){
						score = plist.get(a).GetScore();
						remove = a;
						pl=plist.get(a);
					}
				}
				newplayer.add(pl);
				plist.remove(remove);
			}
		}
		while(newplayer.size()>l){
			newplayer.remove(newplayer.get(l));
		}
		player = newplayer;
		
	}
	private void lowScoreList(int l){
		List<FPlayer> plist = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			plist.add(player.get(a));
		}
		List<FPlayer> newlist = new ArrayList<FPlayer>();
		if(plist.size()>0){
			FPlayer pl = null;
			while(plist.size()>0){
				int score = 0;
				int remove = 0;
				for(int a = 0;a<plist.size();a++){
					if(plist.get(a).GetScore()>score){
						score = plist.get(a).GetScore();
						remove = a;
						pl=plist.get(a);
					}
				}
				newlist.add(pl);
				plist.remove(remove);
			}
		}
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = l;a<newlist.size();a++){
			newplayer.add(newlist.get(l));
		}
		player = newplayer;
		
	}
	
	private void Tag(String Tag){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(Tag.startsWith("\\-")){
				if(!player.get(a).hasTag(Tag.substring(1))){
					newplayer.add(player.get(a));
				}
			}else{
				if(player.get(a).hasTag(Tag)){
					newplayer.add(player.get(a));
				}
			}

		}
		player = newplayer;
	}
	
	private void Permission(String Perm){
		List<FPlayer> newplayer = new ArrayList<FPlayer>();
		for(int a = 0;a<player.size();a++){
			if(Perm.startsWith("\\-")){
				if(!player.get(a).ToMC().hasPermission(Perm.substring(1))){
					newplayer.add(player.get(a));
				}
			}else{
				if(player.get(a).ToMC().hasPermission(Perm)){
					newplayer.add(player.get(a));
				}
			}
		}
		player = newplayer;
	}
}
