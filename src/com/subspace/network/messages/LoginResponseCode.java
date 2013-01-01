package com.subspace.network.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum LoginResponseCode {
	LoginOK(0x00), // - Login OK
	Unregistered(0x01), // Unregistered Player *a1
	BadPassword(0x02), // Bad Password
	AreanFull(0x03), // Arena is Full
	LockedOutZone(0x04), // Locked Out of Zone
	PermissionOnlyArena(0x05), // Permission Only Arena
	SpecatorPermissionOnly(0x06), // Permission to Spectate Only
	TooManyPoints(0x07), // Too many points to Play here
	ConnectionToSlow(0x08), // Connection is too Slow
	PermissionOnlyArena2(0x09), // Permission Only Arena
	ServerFull(0x0A), // Server is Full
	InvalidName(0x0B), // Invalid Name
	OffensiveName(0x0C), // Offensive Name
	NoBiller(0x0D), // No Active Biller *a2
	ServerBusy(0x0E), // Server Busy, try Later
	RestrictedZone(0x10), // Restricted Zone *a3
	DemoVersion(0x11), // Demo Version Detected
	TooManyDemoUsers(0x12), // Too many Demo users
	DemoNotAllowed(0x13), // Demo Versions not Allowed
	RestrictedZoneModAccessOnly(0xFF); // Restricted Zone, Mod Access Required

	private final int type;

	private LoginResponseCode(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	private static final Map<Integer, LoginResponseCode> map = new HashMap<Integer, LoginResponseCode>();

	static {
		for (LoginResponseCode type : LoginResponseCode.values()) {
			map.put(type.type, type);
		}
	}

	public static LoginResponseCode fromCode(int type) {
		if (map.containsKey(type)) {
			return map.get(type);
		}
		throw new NoSuchElementException(type + "not found");
	}
}
