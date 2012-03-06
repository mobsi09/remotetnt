package com.github.mobsi09.remotetnt;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class TNTBlockListener implements Listener {
	
    public remotetnt plugin;
    public TNTBlockListener(remotetnt instance){
	plugin = instance;
    }
        
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
                Block block = event.getBlock();
                Player player = event.getPlayer();
		if (block.getType() == Material.TNT && plugin.isrtntEnabled(player)){
                            if(plugin.TNTcount.containsKey(player)){
                                if(plugin.TNTcount.get(player)<=(plugin.configTNTLimit - 1 + plugin.TNTbroken)){
                                    plugin.armRemote(player, block);
                                }else{
                                    
                                    event.setCancelled(true);                                    
                                }
                            }else{
                                plugin.armRemote(player, block);                            
                            }
                }
	}
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
          Block block = event.getBlock();
          Player player = event.getPlayer();
          if (block.getType() == Material.TNT && plugin.isrtntEnabled(player) && plugin.TNTcount.containsKey(player)){
                if(plugin.TNTbroken <= plugin.configTNTreplaceLimit && (plugin.TNTcount.get(player)- plugin.TNTbroken) >= 1 )
                    plugin.TNTbroken++;                    
          }
    }
}

