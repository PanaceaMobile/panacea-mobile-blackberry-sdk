package com.panaceamobile.panacea.ui.screens;

import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Result;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastManager;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver.PMBroadcastListener;
import com.panaceamobile.panacea.ui.controllers.PMScreenController;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public class PMBaseScreen extends MainScreen implements PMBroadcastListener
{
	protected PMLocalBroadcastReceiver receiver;
	private static boolean startup = true;
	private static boolean hasResized = false;
	public static boolean isActivityActive = false;
	public static boolean isOfflineMode = false;

	public PMBaseScreen()
	{
		receiver = new PMLocalBroadcastReceiver();
		receiver.register(this);
	}
	public boolean onSavePrompt()
	{
	    return true;
	}
	public PMBaseScreen(long style)
	{
		super(style);
		receiver = new PMLocalBroadcastReceiver();
		receiver.register(this);
	}

	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);		
		menu.add(sendMenuItem);		
		menu.add(settingsMenuItem);	
		menu.add(purgeSettingsMenuItem);
	}
	private MenuItem sendMenuItem = new MenuItem("Developer Send", 1, 10)
	{
		public void run()
		{
			PMDeveloperSendView popup = new PMDeveloperSendView();
			popup.show();
		}
	};
	private MenuItem settingsMenuItem = new MenuItem("Settings",9999, 9999)
	{
		public void run()
		{
			PMSettingsScreen s = new PMSettingsScreen();
			UiApplication.getUiApplication().pushScreen(s);
		}
	};

	private MenuItem purgeSettingsMenuItem = new MenuItem("Purge Settings",99999, 99999)
	{
		public void run()
		{
			Dialog d = new Dialog(Dialog.D_YES_NO,"Are you sure you want to purge the settings? This will cause the app to deregister.",Dialog.NO,null,0l);
			int result = d.doModal();
			if(result==Dialog.YES)
			{
				PMMessageStore.getInstance().clearIndicator();
				PMMessageStore.getInstance().deleteMessageCache();
				PMPreferencesHelper.purgeSettings();
				
				System.exit(0);
				
			}
		}
	};
	/**
	 * Checks if any WebService is busy and shows progress bar in ActionBar
	 */
	public void checkLoading()
	{
		boolean loading = PanaceaSDK.getInstance().isBusy();
		//setProgressBarIndeterminateVisibility(loading);
	}



	public void onPreDataExecute(String tag) {
		// TODO Auto-generated method stub
		
	}



	public void onPostDataSuccess(String tag, String result, int status,
			String message, String method) {
		// TODO Auto-generated method stub
		
	}



	public void onPostDataFailure(String tag, String result, int httpCode,
			String message, String method) {
		// TODO Auto-generated method stub
		
	}
	



}
