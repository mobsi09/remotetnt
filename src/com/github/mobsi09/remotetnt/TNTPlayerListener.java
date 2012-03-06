package com.github.mobsi09.remotetnt;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TNTPlayerListener implements Listener{

	public remotetnt plugin;
    public TNTPlayerListener(remotetnt instance){
    	plugin = instance;
        }      

	@EventHandler 
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
        
		if(event.getAction().equals(Action.LEFT_CLICK_AIR) && player.getItemInHand().getType() == Material.STONE_BUTTON && plugin.isRemoteArmed(player)){
                plugin.detonateTNT(player);
                plugin.TNTbroken = 0;
        }
    }
	
	@EventHandler 
	public void PlayerQuitEvent(PlayerQuitEvent event){
		Player player = event.getPlayer();
 		if(plugin.rtntEnabled.contains(player)){
			plugin.rtntEnabled.remove(player);
		}
    }
	
	@EventHandler 
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
 		if(plugin.rtntEnabled.contains(player)){
			plugin.rtntEnabled.remove(player);
		}
    }	
	
                
}
