package org.jtelegraph;

import java.text.MessageFormat;

import javax.swing.ImageIcon;

public enum SimplicioIconProvider implements IconProvider {

	APPLICATION, APPLICATION_WARNING, CALCULATOR, CALENDAR, CAMERA, CLOCK, COFFEE, COMPUTER, DIRECTION_DOWN, DIRECTION_LEFT, DIRECTION_RIGHT, DIRECTION_UP, DISC, DISKETTE, DOCUMENT, DOCUMENT_ADD, DOCUMENT_DELETE, DOCUMENT_EDIT, DOCUMENT_SEARCH, DOCUMENT_WARNING, FILE, FILE_ADD, FILE_DELETE, FILE_EDIT, FILE_SEARCH, FILE_WARNING, FOLDER, FOLDER_ADD, FOLDER_DELETE, FOLDER_EMPTY, FOLDER_SEARCH, FOLDER_WARNING, HOME, LOAD_DOWNLOAD, LOAD_UPLOAD, MAIL, MAIL_DELETE, MAIL_RECEIVE, MAIL_SEARCH, MAIL_SEND, MAIL_WARNING, MAIL_WRITE, MESSAGE, NOTIFICATION_ADD, NOTIFICATION_DONE, NOTIFICATION_ERROR, NOTIFICATION_REMOVE, NOTIFICATION_WARNING, PIECHART, PLAYER_FASTFORWARD, PLAYER_PAUSE, PLAYER_PLAY, PLAYER_RECORD, PLAYER_REWIND, PLAYER_STOP, RSS, SEARCH, SECURITY_KEY, SECURITY_KEYANDLOCK, SECURITY_LOCK, SECURITY_UNLOCK, SHOPPINGCART_ADD, SHOPPINGCART_CHECKOUT, SHOPPINGCART_REMOVE, SHOPPINGCART_WARNING, STAR_EMPTY, STAR_FULL, STAR_HALF, USER, USER_ADD, USER_DELETE, USER_MANAGE, USER_WARNING, VOLUME, VOLUME_DOWN, VOLUME_MUTE, VOLUME_UP

	;

	String ICON_PATH = "/simplicio/{0}.png";

	@Override
	public ImageIcon getIcon() {
		return new ImageIcon(getClass().getResource(
				MessageFormat.format(ICON_PATH, toString().toLowerCase())));
	}
}
