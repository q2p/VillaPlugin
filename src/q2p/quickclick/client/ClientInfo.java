package q2p.quickclick.client;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import q2p.quickclick.Abort;
import q2p.quickclick.MainSerializer;
import q2p.quickclick.QuickClick;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ClientInfo implements MetadataValue {

	/*
		.dat

		boolean preferesRussian
		boolean isAdmin
		byte passwordLength
		byte[] password
	*/

	private static final int maxLoginFailures = 3;

	private final String name;

	public String name() {
		return name;
	}
	private final String lowerCaseName;
	public String lowerCaseName() {
		return lowerCaseName;
	}
	private String note = "";
	private boolean banned = false;
	private Player player;
	private boolean preferesRussian = false;
	private boolean admin = false;
	public boolean isAdmin() {
		return admin;
	}
	public boolean preferesRussian() {
		return preferesRussian;
	}
	public Player getPlayer() {
		assert player != null;
		return player;
	}

	private byte[] authPassword = null;

	private boolean authLoggedIn = false;
	public boolean isLoggedIn() {
		return authLoggedIn;
	}

	private byte attemptsLeft = maxLoginFailures;

	private ClientInfo(final Player player, final String playerName) {
		this.player = player;
		if(player != null) {
			for(final MetadataValue metadataValue : player.getMetadata("cli"))
				player.removeMetadata("cli", metadataValue.getOwningPlugin());
			player.setMetadata("cli", this);
		}

		name = playerName;
		lowerCaseName = name.toLowerCase();

		try {
			Path playerPath = MainSerializer.resourcesPath().resolve("players/data/"+lowerCaseName+".player");
			if(Files.exists(playerPath)) {
				ByteBuffer bb = MainSerializer.loadBytes(playerPath, 3+32);
				preferesRussian = bb.get() != 0;
				admin = bb.get() != 0;
				final byte passwordLength = bb.get();
				if(passwordLength == 0) {
					authPassword = null;
				} else {
					authPassword = new byte[passwordLength];
					bb.get(authPassword);
				}
			}
		} catch(final Throwable t) {
			Abort.message(t.getMessage(), t);
		}
	}

	public static ClientInfo getOffline(final String lowerCasePlayerName) {
		return new ClientInfo(null, lowerCasePlayerName);
	}

	public static ClientInfo getOnline(final Player player) {
		ClientInfo clientInfo = new ClientInfo(player, player.getName());
		assert !ClientPool.online.containsKey(clientInfo.lowerCaseName);
		ClientPool.online.put(clientInfo.lowerCaseName, clientInfo);
		return clientInfo;
	}

	public boolean isRegistered() {
		return authPassword != null;
	}

	public void setAdmin(boolean isAdmin) {
		this.admin = isAdmin;
		save();
	}

	private void save() {
		ByteBuffer bb = ByteBuffer.allocate(3+(authPassword == null ? 0 : authPassword.length));
		bb.put(preferesRussian ? (byte)1 : (byte)0);
		bb.put(admin ? (byte)1 : (byte)0);
		if(authPassword == null) {
			bb.put((byte)0);
		} else {
			bb.put((byte)authPassword.length);
			bb.put(authPassword);
		}
		bb.flip();
		MainSerializer.saveBytes("players/data/"+lowerCaseName+".player", bb);
	}

	enum LoginResult {
		SUCCESS, WRONG_PASSWORD, OUT_OF_ATTEMPTS
	}
	public void register(final byte[] password) {
		assert authPassword != null;
		this.authPassword = password;
		this.authLoggedIn = true;
		this.attemptsLeft = maxLoginFailures;
		save();
	}
	public LoginResult tryToLogin(final byte[] password) {
		assert password != null;
		if(Arrays.equals(authPassword, password)) {
			authLoggedIn = true;
			attemptsLeft = maxLoginFailures;
			return LoginResult.SUCCESS;
		}

		attemptsLeft--;
		if(attemptsLeft == 0)
			return LoginResult.OUT_OF_ATTEMPTS;
		return LoginResult.WRONG_PASSWORD;
	}
	public LoginResult tryToChangePassword(byte[] oldPassword, byte[] newPassword) {
		assert oldPassword != null && newPassword != null;
		if(Arrays.equals(authPassword, oldPassword)) {
			attemptsLeft = maxLoginFailures;
			authPassword = newPassword;
			save();
			return LoginResult.SUCCESS;
		}

		attemptsLeft--;
		if(attemptsLeft == 0)
			return LoginResult.OUT_OF_ATTEMPTS;
		return LoginResult.WRONG_PASSWORD;
	}

	public static ClientInfo getFromPlayer(final Player player) {
		return (ClientInfo) player.getMetadata("cli").get(0).value();
	}

	public Object value() { return this; }
	public int asInt() { return -1; }
	public float asFloat() { return 0; }
	public double asDouble() { return 0; }
	public long asLong() { return 0; }
	public short asShort() { return 0; }
	public byte asByte() { return 0; }
	public boolean asBoolean() { return false; }
	public String asString() { return null; }
	public Plugin getOwningPlugin() { return QuickClick.getPluginInstance(); }
	public void invalidate() {}
}
