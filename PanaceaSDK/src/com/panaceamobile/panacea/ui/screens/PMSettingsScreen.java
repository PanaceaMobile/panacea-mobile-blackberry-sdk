package com.panaceamobile.panacea.ui.screens;

import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PMUtils;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.ui.controls.SpacerField;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

public class PMSettingsScreen extends MainScreen implements FieldChangeListener{
	
	private ButtonField applyButton;
	private EditField name;
	public PMSettingsScreen()
	{
		LabelField title = new LabelField();
		title.setText("Settings");
		title.setFont(title.getFont().derive(Font.PLAIN,10,Ui.UNITS_pt));
		title.setPadding(4, 4, 4, 4);
		add(title);
		
		SpacerField spacer = new SpacerField(1,15);
		add(spacer);
		SeparatorField s3 = new SeparatorField();
		add(s3);
		LabelField anotherTitle = new LabelField();
		anotherTitle.setText("Profile");
		anotherTitle.setPadding(4, 4, 4, 4);
		anotherTitle.setFont(title.getFont().derive(Font.PLAIN,9,Ui.UNITS_pt));
		add(anotherTitle);
		HorizontalFieldManager f = new HorizontalFieldManager();
		LabelField nameLabel = new LabelField()
		{
			public void layout(int width, int height)
			{
				super.layout(Display.getWidth()*1/6, getFont().getHeight() + 4);
				setExtent(Display.getWidth()*1/6, getFont().getHeight() + 4);
			}
		};
		nameLabel.setPadding(4, 4, 4, 4);
		nameLabel.setMargin(7, 5,5, 0);
		nameLabel.setText("Name: ");
		nameLabel.setFont(nameLabel.getFont().derive(Font.PLAIN,7,Ui.UNITS_pt));
		f.add(nameLabel);
		name = new EditField()
		{
			public void layout(int width, int height)
			{
				super.layout(Display.getWidth()*1/2, getFont().getHeight() + 4);
				setExtent(Display.getWidth()*1/2, getFont().getHeight() + 4);
			}
		};
		XYEdges xyEdge = new XYEdges(2, 2, 2, 2);
		XYEdges xyEdgeColors = new XYEdges(0x00dddddd, 0x00dddddd, 0x00dddddd, 0x00dddddd);
		Border aBorder = BorderFactory.createSimpleBorder(xyEdge, xyEdgeColors, Border.STYLE_SOLID);
		name.setText(PMPreferencesHelper.getGivenName());
		name.setFont(nameLabel.getFont());
		name.setBorder(aBorder);
		name.setPadding(4, 4, 4, 4);
		name.setMargin(5, 5,5, 5);
		f.add(name);
		applyButton = new ButtonField("Apply");
		applyButton.setFont(nameLabel.getFont());
		applyButton.setChangeListener(this);
		f.add(applyButton);
		add(f);
		LabelField subtitle1 = new LabelField();
		subtitle1.setPadding(4, 4, 4, 4);
		subtitle1.setText("Device ID");
		subtitle1.setFont(subtitle1.getFont().derive(Font.PLAIN,8,Ui.UNITS_pt));
		add(subtitle1);
		LabelField deviceId = new LabelField(PMPreferencesHelper.getUniqueDeviceId(),LabelField.FOCUSABLE);
		deviceId.setPadding(4, 4, 4, 4);
		deviceId.setFont(nameLabel.getFont());
		add(deviceId);
		SeparatorField s = new SeparatorField();
		add(s);
		
		LabelField title2 = new LabelField();
		title2.setText("About");
		title2.setPadding(4, 4, 4, 4);
		title2.setFont(title2.getFont().derive(Font.PLAIN,9,Ui.UNITS_pt));
		add(title2);
		LabelField build = new LabelField();
		build.setText("Version: " + PanaceaSDK.getInstance().getBuildNumber());
		build.setFont(nameLabel.getFont());
		build.setPadding(4, 4, 4, 4);
		add(build);
		SeparatorField s2 = new SeparatorField();
		add(s2);
	}

	public void fieldChanged(Field field, int context) {
		if(field == applyButton && !PMUtils.isBlankOrNull(name.getText()))
		{
			PanaceaSDK.getInstance().updateDeviceName(name.getText());
			
		}
		
	}

}
