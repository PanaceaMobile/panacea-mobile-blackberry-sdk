package com.panaceamobile.panacea.ui.screens;

import java.util.Enumeration;
import java.util.Hashtable;


import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Result;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.push.LinkedHashtable;
import com.panaceamobile.panacea.ui.PanaceaTheme;
import com.panaceamobile.panacea.ui.controllers.PMDismissListener;
import com.panaceamobile.panacea.ui.controls.Row;
import com.panaceamobile.panacea.ui.controls.RowSelectionListener;
import com.panaceamobile.panacea.ui.controls.SubjectRow;
public class PMSubjectListScreen extends  PMBaseScreen implements FieldChangeListener, RowSelectionListener
{
	
	public static final int CANCEL = 1000;
	public static final int SELECTED = 2000;
	private String subject;
	private SubjectRow selectedRow;
	PMDismissListener dismissListener;
	
	public String getSelectedSubject()
	{
		return subject;
	}
	public PMSubjectListScreen(PMDismissListener dismissListener)
	{
		super();
		this.dismissListener = dismissListener;
		reloadContent();
	}
	
	public void reloadContent()
	{
		setContent(PanaceaSDK.getInstance().getSubjectCounts());
	}
	private void setContent(LinkedHashtable results)
	{
		deleteAll();
		if(results.size()==1)
		{
			
			add(PanaceaSDK.getInstance().getTheme().getLabelField("No messages have been received"));
		}
		else
		{
			SubjectRow allsubjects = PanaceaSDK.getInstance().getTheme().getSubjectRow(PMMessageStore.ALL_UNREAD_MESSAGES_SUBJECT,(Integer)results.get(PMMessageStore.ALL_UNREAD_MESSAGES_SUBJECT));
			allsubjects.setChangeListener(this);
			add(allsubjects);
			Enumeration e = results.keys();
			while(e.hasMoreElements())
			{			
				
				String key = (String) e.nextElement();
				if(!key.equals(PMMessageStore.ALL_UNREAD_MESSAGES_SUBJECT))
				{
					SubjectRow r =  PanaceaSDK.getInstance().getTheme().getSubjectRow(key,(Integer) results.get(key));
					r.setChangeListener(this);
					r.setRowSelectionListener(this);
					add(r);
				}
				
			}
		}
		
		
	}


	
	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);	
		menu.add(clearMenuItem);
		if(this.selectedRow!=null)
		{
			menu.add(deleteSubject);
		}
	}

	private MenuItem clearMenuItem = new MenuItem("Clear messages", 2, 20)
	{
		public void run()
		{
			PanaceaSDK.getInstance().updateMessages(true);
		}
	};
	private MenuItem deleteSubject = new MenuItem("Delete Subject", 3, 30)
	{
		public void run()
		{
			PanaceaSDK.getInstance().markSubjectDeleted(selectedRow.getSubject());
			reloadContent();
		}
	};

	public void fieldChanged(Field field, int context) {
		if(context==FieldChangeListener.PROGRAMMATIC)
			return;
		if(context==1)
		{
			SubjectRow row = (SubjectRow)field;
			subject = row.getSubject();
			
			dismissListener.dismiss(this, SELECTED);
		}
		
		
	}
	public void onPostDataSuccess(String tag, String result, int status,
			String message, String method) {
		checkLoading();
		isOfflineMode = false;


		if ((Tag.MESSAGE_RECEIVED.equals(tag)||Tag.MESSAGE_SENT.equals(tag)))
		{
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					reloadContent();
				}
			});
			
		}
		
	}
	public void rowSelected(Row selectedRow) {
		this.selectedRow = (SubjectRow) selectedRow;
		
	}
	public void rowUnselected(Row unselectedRow) {
		if(selectedRow==unselectedRow)
			selectedRow=null;
		
	}
	
}
