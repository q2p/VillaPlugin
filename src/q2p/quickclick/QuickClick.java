package q2p.quickclick;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import q2p.quickclick.client.Authorization;
import q2p.quickclick.client.ClientInfo;
import q2p.quickclick.client.ClientPool;

/*
TODO:
блокировать ip которые не смогли за логинится на 10 минут
замениеть System.out.println() на встроенную функцию bukkit'а (logger)
		// p.player.playEffect(pop, Effect.LAVADRIP, null);
*/

public class QuickClick extends JavaPlugin implements Listener {
	private static JavaPlugin instance = null;
	public static JavaPlugin getPluginInstance() {
		assert instance != null;
		return instance;
	}

	private static final String unknownCmd = "Unknown command.";

	public static void registerListener(final Listener listener) {
		instance.getServer().getPluginManager().registerEvents(listener, instance);
	}
	
	public void onEnable() {
		instance = this;
		HubStatus.initilize();
		BannedCommands.initialize();
		OutsideInfo.initialize();
		ClientPool.initialize();
		registerListener(this);
	}
	
	public void onDisable() {
		ClientPool.deinitialize();
		OutsideInfo.deinitialize();
		BannedCommands.deinitialize();
		HubStatus.deinitilize();
		instance = null;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Authorization.checkCommand(command, sender, args)) return true;
		if(Modifications.checkCommand(command, sender, args)) return true;
		if(HubStatus.logicTick.benchmarkCommand(command, sender)) return true;
		if(AdminManager.checkCommand(command, sender, args)) return true;
		sender.sendMessage(unknownCmd);
		return true;
	}
	
	// Восстановление здоровья
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent event) {
		event.setCancelled(true);
	}
	// Изменение уровня
	@EventHandler
	public void onLevelChange(final PlayerLevelChangeEvent event) {
		if(event.getNewLevel() != 0)
			event.getPlayer().setLevel(0);
	}
	// MOTD
	@EventHandler
	public void onPing(ServerListPingEvent event) {
		OutsideInfo.onPing(event);
	}
	// Нажатие лкм/пкм
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

		}
	}
	// При подкулючении к серверу
	@EventHandler
	public void onLogin(final PlayerLoginEvent event) {
		// TODO: full, whitelist, blacklist
		ClientPool.acceptPlayer(event);
	}
	// Передвижение
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		ClientInfo cli = ClientInfo.getFromPlayer(player);
		if(!cli.isLoggedIn()) {
			event.setTo(WorldManager.getLoginLocation());
		} else {
			if(WorldManager.outOfBoundaries(event.getTo()))
				event.setTo(WorldManager.getSpawnLocation());
		}
	}
	// При входе в игру (без логина)
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Authorization.onJoin(event.getPlayer());
	}
	// Отключение от сервера
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		ClientPool.onExit(event);
	}
	// Запрет крафта
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		event.setCancelled(true);
	}
	// Уничтожение блока
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(!Modifications.isInModificationMode() || event.getBlock().getY() == 0)
			event.setCancelled(true);
	}
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		if(!Modifications.isInModificationMode() || event.getBlock().getY() == 0)
			event.setCancelled(true);
	}
	// Постановка блока
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!Modifications.isInModificationMode()) {
			event.setBuild(false);
			event.setCancelled(true);
		}
	}
	// Декоративный огонь
	@EventHandler
	public void onBurn(final BlockBurnEvent event) {
		event.setCancelled(true);
	}
	@EventHandler
	public void onIgnite(final BlockIgniteEvent event) {
		if(event.getCause() != IgniteCause.FLINT_AND_STEEL || event.getPlayer() == null || !Modifications.isInModificationMode())
			event.setCancelled(true);
	}
	// Огонь не гаснет
	@EventHandler
	public void onFade(BlockFadeEvent event) {
		if(event.getBlock().getType() == Material.FIRE)
			event.setCancelled(true);
	}
	// Смерть
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.setDroppedExp(0);
		event.setKeepInventory(false);
		event.setKeepLevel(false);
		event.setDeathMessage(null);
		// TODO: сообщение на разных языках
	}
	// Возрождение
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final ClientInfo clientInfo = ClientInfo.getFromPlayer(player);
		player.setGameMode(Modifications.isInModificationMode() ? GameMode.CREATIVE : GameMode.ADVENTURE);
		player.teleport(WorldManager.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
		event.setRespawnLocation(clientInfo.isLoggedIn() ? WorldManager.getSpawnLocation() : WorldManager.getSpawnLocation());
	}
	// Урон
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player)
			event.setCancelled(true);
	}
	// Никогда не голоден
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
	}
	// Заглушка стандартных команд
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCommandPreprocces(PlayerCommandPreprocessEvent pcpe) {
		if(BannedCommands.onCommandPreProcess(pcpe.getMessage(), pcpe.getPlayer())) {
			pcpe.setCancelled(true);
			pcpe.getRecipients().clear();
			pcpe.getPlayer().sendMessage(unknownCmd);
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onServerCommand(ServerCommandEvent sce) {
		if(BannedCommands.onCommandPreProcess(sce.getCommand(), null)) {
			sce.setCancelled(true);
			Log.consoleAdminWarn("Комманда \""+sce.getCommand()+"\" была отменена.");
		}
	}
	// Сообщение в чат
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();
		event.setCancelled(true);
		ClientInfo clientInfo = ClientInfo.getFromPlayer(player);
		if(clientInfo.isLoggedIn()) {
			getServer().broadcastMessage("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GRAY + ": " + message);
		}
		//TODO:
	}
	// Сброс предмета
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	// Избавление от мусора
	@EventHandler
	public void onItemSpawn(final ItemSpawnEvent event) {
		event.getEntity().remove();
	}
	// Постоянная не дождливая погода
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if(event.toWeatherState()){
			event.getWorld().setStorm(false);
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onThunerChange(ThunderChangeEvent event) {
		if(event.toThunderState()){
			event.getWorld().setStorm(false);
			event.setCancelled(true);
		}
	}
	// Молния
	@EventHandler
	public void onLightning(LightningStrikeEvent event) {
		event.setCancelled(true);
	}
	// Перетекание воды и лавы
	@EventHandler
	public void onFromTo(BlockFromToEvent event) {
		if(!Modifications.isInModificationMode())
			event.setCancelled(true);
	}
}
