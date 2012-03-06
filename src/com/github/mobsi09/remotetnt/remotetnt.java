package com.github.mobsi09.remotetnt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.minecraft.server.EntityTNTPrimed;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftTNTPrimed;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



public class remotetnt extends JavaPlugin{
	
	public final String pluginName = "remotetnt";
	Logger log = Logger.getLogger("Minecraft");
	
	CraftTNTPrimed tntp;	
	
	public String playerkey = null;
    public Integer TNTnumber = 0;
    public Integer TNTbroken = 0;
    public Integer configTNTLimit = 128;
    public Integer configTNTreplaceLimit = 16;   
	
	public final LinkedList<Player> rtntEnabled = new LinkedList<Player>();
    public HashMap<Player, Integer> TNTcount = new HashMap<Player, Integer>();
	public HashMap<String, Block> TNTlocations = new HashMap<String, Block>();
	
    public void onEnable(){ 
    log.info("[" + pluginName + "]" + " - Version " + this.getDescription().getVersion() + " has been enabled.");
    
    this.getConfig().options().copyDefaults(true);
    this.saveConfig(); 
    
    rtntconfig();
  
    PluginManager pm = getServer().getPluginManager();
    
   
    pm.registerEvents(new TNTPlayerListener(this), this);
    pm.registerEvents(new TNTBlockListener(this), this); 

    }
     
    public void onDisable(){ 
    log.info("remotetnt has been disabled.");  
    }

    
    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    		Player player = null;
    		
    		if (sender instanceof Player) {
    			player = (Player) sender;
    		}
    		
    		if(command.getName().equalsIgnoreCase("rtnt")){
				if(player == null){
					return false;
				}else{
				toggleRemote(player);
				return true;                             
			    }
    		}else if (command.getName().equalsIgnoreCase("rtntreload")){ 
    			rtntreload(player);
    			return true;
    		}
    		return false;
	}
  
    public void rtntconfig(){
    	
        configTNTLimit = this.getConfig().getInt("TNTLimit", 128);
        configTNTreplaceLimit = this.getConfig().getInt("TNTReplaceLimit", 16);    	
    }
    
    public void rtntreload(Player player){
    	if(player.hasPermission("remotetnt.reload") || (player == null)){
    		this.reloadConfig();
    		this.getConfig().options().copyDefaults(true);
    		this.saveConfig();
    		rtntconfig();
    		log.info("remotetnt config has been reloaded."); 
    	}
    }
    
	public void toggleRemote(Player player){
		if (player.hasPermission("remotetnt.remote")){
				if(rtntEnabled.contains(player)){
					rtntEnabled.remove(player);
					player.sendMessage(ChatColor.DARK_PURPLE + "RemoteTNT stopped");
	                              TNTbroken = 0;
	                              if(TNTcount.containsKey(player)){
	                                  int i=TNTcount.get(player);
	                                  while(i>=1){
	                                      TNTlocations.remove(player.getName()+ i);
	                                      if(i==1)
	                                          TNTcount.remove(player);
	                                      i--;                       
	                                  }
	                              }
				} else {
					rtntEnabled.add(player);
					player.sendMessage(ChatColor.DARK_PURPLE + "RemoteTNT started");
	                              player.sendMessage(ChatColor.LIGHT_PURPLE + "Place TNT and detonate them by leftclicking in the air with a stonebutton");
	                              player.sendMessage(ChatColor.LIGHT_PURPLE + "The number of remotedetonated TNT is limited to " + configTNTLimit);
				}
			} 
		else{ player.sendMessage("You do not have permission to use RemoteTNT.");}
	}
	
    
	public void armRemote(Player player, Block block){
        if(TNTcount.containsKey(player)){                            
            TNTnumber = TNTcount.get(player);
            TNTnumber++;
            TNTcount.put(player, TNTnumber);
            playerkey = player.getName()+ TNTnumber;
            TNTlocations.put(playerkey, block);                       
            player.sendMessage(ChatColor.GRAY + "armed TNT number "+ TNTnumber +" placed");                             
        }else{
            TNTnumber = 1;
            TNTcount.put(player, TNTnumber);
            playerkey = player.getName()+ TNTnumber;
            TNTlocations.put(playerkey, block);  
            player.sendMessage(ChatColor.GRAY + "armed TNT number "+ TNTnumber+" placed"); 
        } 
    }
	
	public boolean isRemoteArmed(Player player){
		if(TNTcount.containsKey(player)){
			return true;
		}
		return false;
	}
	
	public void detonateTNT(Player player) {
		int i;
              Block TNTblock;
              i = TNTcount.get(player);
              Server server = this.getServer();
              while(i>=1){
                  TNTblock = TNTlocations.get(player.getName()+ i);
                  if(TNTblock.getType() == Material.TNT){
                      CraftWorld cWorld = (CraftWorld) TNTblock.getWorld();                
                      EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(), TNTblock.getLocation().getX(), TNTblock.getLocation().getY(), TNTblock.getLocation().getZ());
                      tnt.setPositionRotation(TNTblock.getLocation().getBlockX() + 0.5, TNTblock.getLocation().getBlockY(), TNTblock.getLocation().getBlockZ() + 0.5, 0, 0);
                      cWorld.getHandle().addEntity(tnt);
                      tntp = new CraftTNTPrimed((CraftServer) server, tnt);
                      TNTblock.setType(Material.AIR);
                  }
                  TNTlocations.remove(player.getName()+ i);
                  if(i==1)
                      TNTcount.remove(player);
                  i--;                   
              }
	}
	
	public boolean isrtntEnabled(Player player){
		return rtntEnabled.contains(player);
	}
}
