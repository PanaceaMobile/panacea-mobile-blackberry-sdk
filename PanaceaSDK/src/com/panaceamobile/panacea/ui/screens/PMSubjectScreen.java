package com.panaceamobile.panacea.ui.screens;

import java.util.Vector;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;

import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.ui.controllers.PMDismissListener;
import com.panaceamobile.panacea.ui.controls.Row;
import com.panaceamobile.panacea.ui.controls.RowSelectionListener;
import com.panaceamobile.panacea.ui.controls.ThreadRow;

public class PMSubjectScreen extends PMBaseScreen implements
		FieldChangeListener, RowSelectionListener {

	public static final int CANCEL = 3000;
	public static final int SELECTED = 4000;
	public static final int NO_CONTENT = 5000;
	private PMDismissListener dismissListener;
	private ThreadRow selectedRow;
	private String subject;
	private int threadId;
	
	public int getSelectedThreadId()
	{
		return threadId;
	}
	public String getSubject()
	{
		return subject;
	}
	
	
	public PMSubjectScreen(PMDismissListener dismissListener, String subject)
	{
		super();
		this.dismissListener = dismissListener;
		this.subject = subject;
		reloadContent(subject);
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
					reloadContent(subject);
				}
			});
			
		}
		
	}
	
	
	public void reloadContent(String subject)
	{
		Vector results = PMMessageStore.getInstance().getMessagesForSubject(subject);
		if(results.size()>0)
			setContent(PMMessageStore.getInstance().getMessagesForSubject(subject));
		else
			dismissListener.dismiss(this, NO_CONTENT);
	}
	private void setContent(Vector results)
	{
		if(results.size()==0)
			this.close();
		else
		{
			deleteAll();
			for(int i=0;i<results.size();i++)
			{
				PMReceivedMessage receivedMessage = (PMReceivedMessage) results.elementAt(i);
				
				
					ThreadRow r = new ThreadRow(receivedMessage);
					r.setChangeListener(this);
					r.setRowSelectionListener(this);
					add(r);
			}
		}
	}

	
	
	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);		
		if(this.selectedRow!=null)
			menu.add(deleteThreadMenuItem);
	}

	private MenuItem deleteThreadMenuItem = new MenuItem("Delete Thread", 2, 20)
	{
		public void run()
		{
			PanaceaSDK.getInstance().markThreadDeleted(selectedRow.getThreadId());
			reloadContent(subject);
		}
	};
	


	
	public void fieldChanged(Field field, int context) {
		if(context==FieldChangeListener.PROGRAMMATIC)
			return;
		if(context==1)
		{
			ThreadRow row = (ThreadRow)field;
			this.threadId = row.getThreadId();
			dismissListener.dismiss(this, SELECTED);
		}
		
	}
	public void rowSelected(Row selectedRow) {
		this.selectedRow = (ThreadRow) selectedRow;
		
	}
	public void rowUnselected(Row unselectedRow) {
		if(this.selectedRow == unselectedRow)
			this.selectedRow = null;
		
	}
	
}
