package com.clashers.asynctasks.interfaces;

import java.util.ArrayList;

import com.clashers.infrastructure.datastructures.UserDataFromServer;

public interface INearUsersParent {

	void UpdateUsersNearBy(ArrayList<UserDataFromServer> usersList);
}
