/**
 *  Disclaimer
 *  This project was created by Ryan Davern.
 *  Start Date: 30/03/2016.
 *  
 *  Copyright (C) 2017 Ryan Davern - All Rights Reserved.
 *  You may not use, distribute, monetize or modify this code under the terms of the Copyright Act 1994.
 *  You may use the compiled program, which can be downloaded at https://www.beatplaylist.com/. Any modified versions or versions uploaded to a different website is against TOS (https://www.beatplaylist.com/terms).
 *  
 *  For more information on the Copyright Act 1994, please visit http://www.legislation.govt.nz/act/public/1994/0143/latest/DLM345634.html.
 */

package com.beatplaylist.utilities.permission;

import java.util.ArrayList;
import java.util.List;

import com.beatplaylist.utilities.user.AccountType;
import com.beatplaylist.utilities.user.UserManager;

public class PermissionManager {

	private static PermissionManager instance = new PermissionManager();

	public static PermissionManager getInstance() {
		return instance;
	}

	public List<Permission> permissions = new ArrayList<>();

	public void addPermission(Permission permission) {
		this.permissions.add(permission);
	}

	public void clearPermissions() {
		this.permissions.clear();
	}

	public Permission getPermissionByName(String name) {
		for (Permission permission : this.permissions) {
			if (permission.getPermissionName().equalsIgnoreCase(name))
				return permission;
		}
		return null;
	}
	
	public boolean isPermissionDisabled(PermissionName name) {
		Permission permission = getPermissionByName(name.name());
		if (permission.getPermissionState() == PermissionState.DISABLED || permission.getPermissionState() == PermissionState.DEVELOPER && !UserManager.getInstance().getUser().accountType.isEqualOrLarger(AccountType.MODERATOR))
			return true;
		return false;
	}
}