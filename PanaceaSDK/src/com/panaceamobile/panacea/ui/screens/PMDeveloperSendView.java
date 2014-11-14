package com.panaceamobile.panacea.ui.screens;

import javax.microedition.lcdui.TextField;

import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.ui.controls.ScrollingTextField;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.VirtualKeyboard;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class PMDeveloperSendView extends VerticalFieldManager {
	private ButtonField sendButton;
	private EditField subjectField;
	private ScrollingTextField messageField;
	public PMDeveloperSendView()
	{
		subjectField = new EditField("Subject:","");
		add(subjectField);
		LabelField l = new LabelField("Message:")
		{
			public void layout(int width, int height)
			{
				super.layout(width, height);
				setExtent(getFont().getAdvance(getText()+5),getFont().getHeight());
			}
		};
		HorizontalFieldManager h = new HorizontalFieldManager();
		
		h.add(l);
		
		messageField = new ScrollingTextField(100);
		h.add(messageField);
		add(h);
		sendButton = new ButtonField("Send"); 
		
		
		sendButton.setChangeListener(new FieldChangeListener()
		{
			public void fieldChanged(Field field, int context) {
				PanaceaSDK.getInstance().debug_push_outbound_message_send(getSubject(), getMessage(), null);
						getScreen().close();
				
			}
		});
		add(sendButton);
	}
	
	protected boolean keyDown(int keycode, int time) {  
	    int key = Keypad.key(keycode);
	        if(key==Characters.ESCAPE){
	            this.getScreen().close();
	            return true;
	        }       
	        return super.keyDown(keycode, time);
	    }
	public String getSubject()
	{
		return subjectField.getText();
	}
	public String getMessage()
	{
		return messageField.getText();
	}
	
	public void show()
	{
		final PopupScreen screen = new PopupScreen(this);
		
		VirtualKeyboard virtualKeyboard = screen.getVirtualKeyboard();
		if (virtualKeyboard != null && (DeviceInfo.getDeviceName().indexOf("9800") >= 0
			|| DeviceInfo.getDeviceName().indexOf("9810") >= 0))
		{
			virtualKeyboard.setVisibility(VirtualKeyboard.HIDE_FORCE);
		}
		
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{

			public void run()
			{
				UiApplication.getUiApplication().pushModalScreen(screen);

				sendButton.setFocus();
			}
		});
	}
}
