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

package com.beatplaylist.utilities.network.serialized;

public enum PacketType {

	PING, //
	SEND_ERROR_REPORT, //
	GET_SHOP, //
	SUBMIT_FEEDBACK, //

	// LOGIN
	LOGIN, //
	GET_ACCESS_KEY, //
	GET_ACCESS_KEY_STATE, //
	SEND_LOGIN_CODE, //
	CREATE_ACCOUNT, //
	CHECK_FOR_VALID_USERNAME, //
	GET_ADBLOCK, //
	GET_USER_BLOCK_LIST, //
	GET_USER_PERMISSIONS, //

	// 2FA
	REMOVE_2FA, //
	GENERATE_2FA_CODE, //
	CONFIRM_2FA_CODE, //

	// PLAYLIST
	GET_YOUTUBE_PLAYLIST, // Load a YouTube playlist
	GET_ALL_FEATURED_PLAYLISTS, // Load all featured playlists
	GET_MOST_RECENT_FEATURED_PLAYLISTS, // Load featured playlist - playlist_view page
	SUBMIT_FEATURED_PLAYLIST, //
	GET_PLAYLIST, // Get playlist information from a url
	CREATE_PLAYLIST, // Create a playlist
	ADD_SONG_TO_PLAYLIST, // Add a song to a playlist
	DELETE_PLAYLIST, // Delete a playlist
	GET_USER_PLAYLISTS, // Get user public playlists and display them on their profile page
	LOAD_PLAYLISTS, // Load all user created and followed playlists
	SAVE_PLAYLIST, // Save a YouTube playlist
	FOLLOW_PLAYLIST, // Follow a playlist / album
	UPDATE_PLAYLIST, // Update playlist details
	MERGE_PLAYLISTS, // Merge multiple playlists into one playlist
	UPDATE_TAGS, // Update playlist tags or genres

	// PROFILE
	GET_PROFILE, // Load a users profile page
	UPDATE_ACCOUNT, // Update personal profile page details
	UPDATE_PASSWORD, // Update personal account password
	CANCEL_SUBSCRIPTION, // Cancel premium subscription - Currently not in use
	SET_PROFILE_IMAGE, // Update personal account profile picture
	GET_SOCIAL_ACCOUNTS, // Get users linked social media accounts
	UPDATE_FOLLOW_STATE, // Follow or unfollow a user
	GET_FOLLOWING, // Get users following / follower list
	GET_FOLLOWING_FOLLOWER_COUNT, // Get users following & follower count
	RESET_PROFILE_PICTURE, // Reset caller profile picture

	// PINS
	GET_PINS, // Get users pins

	// MUSIC
	STORE_ANALYTIC, //
	EDIT_SONG_METADATA, // Edit a songs metadata - This only updates for the individual user
	GET_SONG_SETTINGS, // Get song metadata
	LIKE_OR_UNLIKE_SONG, // Like or unlike a song
	HAS_LIKED_SONG, //
	GET_LIKED_MUSIC_COUNT, //

	// CRYPTO
	GET_BALANCE, // Get Rhythm + BNB Balance
	DISCONNECT_WALLET, // Disconnect wallet from account
	HAS_SYNCED_WALLET, // Check if wallet is synced

	// HUB
	GET_SUGGESTED_USERS, // Get suggested users for Rhythm Hub
	
	// SETTINGS
	LINK_GOOGLE, //
	REVOKE_ACCOUNT, //
	CONFIRM_SOCIAL, //
	REQUEST_SOCIAL_URL;
}