package com.panaceamobile.panacea.sdk;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;

import com.panaceamobile.panacea.service.PanaceaService;

public class PanaceaApplication extends UiApplication implements
		GlobalEventListener {

	static PanaceaSDK sdk;

	public PanaceaApplication() {

		PanaceaService service = new PanaceaService();
		service.initPermissions();
		service.initPushChannel();

		service.run();
	}

	public PanaceaApplication(String sdkKey, boolean showUi) {
		if (PanaceaSDK.getInstance() == null)
			sdk = new PanaceaSDK(sdkKey);

		long event_key = net.rim.device.api.util.StringUtilities
				.stringHashToLong(ApplicationDescriptor
						.currentApplicationDescriptor().getName()
						+ "_event_key");
		PMPreferencesHelper.setEventKey(event_key);
		ApplicationDescriptor app = ApplicationDescriptor
				.currentApplicationDescriptor();

		PMPreferencesHelper.setApplicationName(app.getModuleName());
		addGlobalEventListener(this);
		if (showUi)
			sdk.showUI();

	}

	public void eventOccurred(long guid, int data0, int data1, Object object0,
			Object object1) {
		if (guid == PMPreferencesHelper.getEventKey()) {
			sdk.updateMessages(false);
		}
	}

	public Bitmap getAppIcon()
	{
		return Bitmap.getBitmapResource("icon.png");
	}
}
