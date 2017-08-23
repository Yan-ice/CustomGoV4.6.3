package fw.lobby.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fw.lobby.Basic;
import fw.lobby.group.Group;
import fw.lobby.player.FPlayer;

public class Calculater {
	Basic group;
	FPlayer player;
	FPlayer striker;
	Calculater(Basic group, FPlayer player, FPlayer striker){
		this.group = group;
		this.player = player;
		this.striker = striker;
	}
	public String ValueChange(String str) {
		str = BaseChange(str);
		str = ScoreChange(str);
		str = CalChange(str);


		return str;
	}
	
	private String BaseChange(String str){
		str = str.replace("&", "§");
		if (player != null) {
			str = str.replace("<player>", player.getName());
			str = str.replace("<player.score>", ""+player.GetScore());
		} else {
			str = str.replace("<player>", "[控制台]");
			str = str.replace("<player.score>", "0");
		}
		if (striker != null) {
			str = str.replace("<striker>", striker.getName());
			str = str.replace("<striker.score>", ""+striker.GetScore());
		} else {
			str = str.replace("<striker>", "[未知触发者]");
			str = str.replace("<striker.score>", "0");
		}
		str = str.replace("<name>", group.Name);
		str = str.replace("<display>", group.GetDisplay());
		str = str.replace("<playeramount>", "" + group.GetPlayerAmount());
		str = str.replace("<maxplayer>", "" + group.GetMaxPlayer());
		str = str.replace("<minplayer>", "" + group.GetMinPlayer());

		if(group instanceof Group){
			Group gro = (Group)group;
			str = str.replace("<groupscore>","" + gro.GetScore());
		}
		return str;
	}
	private String ScoreChange(String str){
		List<FPlayer> list = group.GetPlayerList();
		List<FPlayer> plist = new ArrayList<FPlayer>();
		for(int a = 0;a<list.size();a++){
			plist.add(list.get(a));
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
		//到这里是队列排序过程
		if(newlist.size()>0){
			for(int a = 0;a<newlist.size();a++){
				if(newlist.get(a)!=null){
					str = str.replace("<scorelist."+(a+1)+">",newlist.get(a).getName());
					str = str.replace("<scorelist."+(a+1)+".score>",""+newlist.get(a).GetScore());
				}
			}
		}
		return str;
	}
	private String CalChange(String str){
		while(str.lastIndexOf("[cal=")!=-1 && str.indexOf("]")!=-1){
			String replace = str.substring(str.indexOf("[cal="),str.indexOf("]")+1);
			String cal = str.substring(str.indexOf("[cal=")+5,str.indexOf("]"));
			if(Check(cal)){
				str = str.replace(replace, ""+Calculate(cal));
			}else{
				str = str.replace(replace, "-1");
			}
		}
		return str;
	}
	private double Calculate(String C){
		double p1;
		double p2;
		String suf;
		String pre;
		if(C.indexOf("+")!=-1 || C.indexOf("-")!=-1){
			if(C.lastIndexOf("+")>C.lastIndexOf("-")){
				suf = C.substring(C.lastIndexOf("+")+1);
				pre = C.substring(0,C.lastIndexOf("+"));	
			}else{
				suf = C.substring(C.lastIndexOf("-")+1);
				pre = C.substring(0,C.lastIndexOf("-"));
			}
			if(!isNum(suf)){
				p2 = Calculate(suf);
			}else{
				p2 = Double.parseDouble(suf);
			}
			if(!isNum(pre)){
				p1 = Calculate(pre);
			}else{
				p1 = Double.parseDouble(pre);
			}
			
			if(C.lastIndexOf("+")>C.lastIndexOf("-")){
				return p1+p2;	
			}else{
				return p1-p2;
			}

		}else if(C.indexOf("*")!=-1 || C.indexOf("/")!=-1){
			if(C.lastIndexOf("*")>C.lastIndexOf("/")){
				suf = C.substring(C.lastIndexOf("*")+1);
				pre = C.substring(0,C.lastIndexOf("*"));
			}else{
				suf = C.substring(C.lastIndexOf("/")+1);
				pre = C.substring(0,C.lastIndexOf("/"));
			}
			if(!isNum(suf)){
				p2 = Calculate(suf);
			}else{
				p2 = Double.parseDouble(suf);
			}
			if(!isNum(pre)){
				p1 = Calculate(pre);
			}else{
				p1 = Double.parseDouble(pre);
			}
			
			if(C.lastIndexOf("*")>C.lastIndexOf("/")){
				return p1*p2;	
			}else{
				return p1/p2;
			}
		}else{
			return Double.parseDouble(C);
		}
	}
	
	String num = "-.1234567890";
	public boolean isNum(String str){
		for(int a = 0;a<str.length()-1;a++){
			if(num.indexOf(str.substring(a,a+1))==-1){
				return false;
			}
		}
		return true;
	}
	
	String math = "+-*/-.1234567890";
	public boolean Check(String str){
		for(int a = 0;a<str.length()-1;a++){
			if(math.indexOf(str.substring(a,a+1))==-1){
				return false;
			}
		}
		return true;
	}
}
