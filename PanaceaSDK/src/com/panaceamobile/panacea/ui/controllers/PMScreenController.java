package com.panaceamobile.panacea.ui.controllers;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Result;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastManager;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver.PMBroadcastListener;
import com.panaceamobile.panacea.ui.screens.PMMessageScreen;
import com.panaceamobile.panacea.ui.screens.PMSubjectListScreen;
import com.panaceamobile.panacea.ui.screens.PMSubjectScreen;
import com.panaceamobile.panacea.ui.screens.RegisterScreen;

public final class PMScreenController implements PMDismissListener, PMBroadcastListener
{
	protected PMLocalBroadcastReceiver receiver;
	
	private PMSubjectListScreen subjectListScreen;
	private PMSubjectScreen subjectScreen;
	private PMMessageScreen messageScreen;
	private RegisterScreen registerScreen;
	
	public void refreshCurrentScreen()
	{
		if(subjectListScreen.isDisplayed())
		{
			subjectListScreen.reloadContent();
		}
		else if(subjectScreen.isDisplayed())
		{
			subjectScreen.reloadContent(subjectScreen.getSubject());
		}
		else if(messageScreen.isDisplayed())
		{
			messageScreen.reloadContent(messageScreen.getThreadId());
		}
	}
	public PMScreenController()
	{
		receiver = new PMLocalBroadcastReceiver();
		receiver.register(this);
		
		UiApplication.getUiApplication().invokeLater(new Runnable() 
		{
			
			public void run() 
			{
		
				if(PMPreferencesHelper.isVerified())
				{
					goToInbox(false);
				}
				else
				{
					goToLogin(false);
				}
			}
		});
		
		PanaceaSDK.getInstance().registerDevice();
	}
	
	private void goToLogin(boolean clearStack)
	{
		
		
		if(registerScreen == null)
				registerScreen = new RegisterScreen();
		if(UiApplication.getUiApplication().getActiveScreen()!=registerScreen)
		{
			if(clearStack)
				UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
			UiApplication.getUiApplication().pushScreen(registerScreen);
		}
	}
	
	private void goToInbox(boolean clearStack)
	{
		if(subjectListScreen ==null )
				subjectListScreen = new PMSubjectListScreen(this);
		
		if(UiApplication.getUiApplication().getActiveScreen()!=subjectListScreen)
		{
			if(clearStack)
			{
				UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
			}
			if(UiApplication.getUiApplication().getActiveScreen()!=subjectListScreen)
			{	
				UiApplication.getUiApplication().pushScreen(subjectListScreen);
			}
			
		}
		subjectListScreen.reloadContent();
	}

	private void goToSubject(String subject )
	{
		subjectScreen = new PMSubjectScreen(this,subject);
		if( UiApplication.getUiApplication().getActiveScreen()!=subjectScreen)
		{
			
			UiApplication.getUiApplication().pushScreen(subjectScreen);
		}
	}
	
	private void goToThread(int threadId)
	{
		messageScreen = new PMMessageScreen(this,threadId);
		if(UiApplication.getUiApplication().getActiveScreen()!=messageScreen)
		{
			
			UiApplication.getUiApplication().pushScreen(messageScreen);
		}
	}
	
	/**
	 * Checks if any WebService is busy and shows progress bar in ActionBar
	 */
	private void checkLoading()
	{
		boolean loading = PanaceaSDK.getInstance().isBusy();
		//setProgressBarIndeterminateVisibility(loading);
	}
	
	public void onPreDataExecute(String tag) {
		checkLoading();
		
	}

	public void onPostDataSuccess(String tag, String result, int status,
			final String message, String method) {
		checkLoading();


		if (Result.REGISTER_SUCCESS.equals(result))
		{
			PanaceaSDK.getInstance().updateLocation();
			PanaceaSDK.getInstance().updateMessages(false);
			
			if(registerScreen!=null &&UiApplication.getUiApplication().getActiveScreen()==registerScreen)
			UiApplication.getUiApplication().invokeLater(new Runnable() 
			{
				public void run() 
				{
			
					goToInbox(true);
				}
			});
		}
		else if (Result.NOT_VERIFIED.equals(result))
		{
			UiApplication.getUiApplication().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Dialog d = new Dialog(Dialog.OK,message,0,null,Screen.NO_VERTICAL_SCROLL);
					d.doModal();
					goToLogin(true);
				}
			});
			PanaceaSDK.getInstance().registerDevice();
		}

		else if (Result.REQUEST_PHONE_NUMBER.equals(result))
		{
			UiApplication.getUiApplication().invokeLater(new Runnable() 
			{
				public void run() 
				{
			
					goToLogin(true);
				}
			});
		}
		else if (Result.REQUEST_VERIFICATION_CODE.equals(result))
		{
			UiApplication.getUiApplication().invokeLater(new Runnable() 
			{
				public void run() 
				{
					goToLogin(true);
				}
			});
		}
		
	}
	
	
	public void onPostDataFailure(String tag, String result, int httpCode,
			String message, String method) {
		checkLoading();

		
		
	}

	public void dismiss(Screen screenToDismiss, int dismissalType) {
		{
			if(screenToDismiss instanceof PMSubjectListScreen)
			{
				if(dismissalType == PMSubjectListScreen.SELECTED)
				{
					PMSubjectListScreen screen = (PMSubjectListScreen) screenToDismiss;
					String subject = screen.getSelectedSubject();
					goToSubject(subject);
				}
				
			}
			else if(screenToDismiss instanceof PMSubjectScreen)
			{
				if(dismissalType == PMSubjectScreen.SELECTED)
				{
					PMSubjectScreen screen = (PMSubjectScreen)screenToDismiss;
					int thread = screen.getSelectedThreadId();
					goToThread(thread);
				}
				else if(dismissalType == PMSubjectScreen.NO_CONTENT)
				{
					goToInbox(true);
				}
			}
			else if(screenToDismiss instanceof PMMessageScreen)
			{
				if(dismissalType == PMMessageScreen.NO_CONTENT)
				{
					goToInbox(true);
				}
			}
			
		
		}
		
	}
}
