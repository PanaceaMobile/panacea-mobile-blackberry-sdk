package com.panaceamobile.panacea.service;

import java.util.Date;
import java.util.Vector;

import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolder;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderListener;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderRegistry;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;
import net.rim.device.api.util.Arrays;

import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessage;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;
import com.panaceamobile.panacea.ui.screens.PMMessageScreen;

public class PMMenuItems {
	

   

    /**
     * Mark Opened menu item. After the method marks the message read, it fires
     * an update event.
     */
    public static class MarkOpenedContextMenu extends ApplicationMenuItem
    {
        /**
         * Creates a new ApplicationMenuItem instance with provided menu position
         * 
         * @param order Display order of this item, lower numbers correspond to  higher placement in the menu
         */
        public MarkOpenedContextMenu(int order)
        {
            super(order);

//            // Set icon for GCM menu
//            EncodedImage eiMarkOpened = EncodedImage.getEncodedImageResource("img/sm_mark_opened.png");
//            if(eiMarkOpened != null)
//            {
//                Image image = ImageFactory.createImage(eiMarkOpened);
//                this.setIcon(image);
//            }
        }


        /**
         * Marks the context message opened
         * 
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(Object context)
        {
            if(context instanceof PMReceivedMessage)
            {
            	PMReceivedMessage message = (PMReceivedMessage) context;
            	PMMessageStore.getInstance().markMessageRead(message);
                PMMessageStore.updateIndicator(-1);
            }
            
            return context;
        }


        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return "Mark Demo Message Read";
        }
    }
    

    /**
     * Mark Unread menu item. After the method marks the message unread, it
     * fires an update event.
     */
    public static class MarkUnreadContextMenu extends ApplicationMenuItem
    {
        /**
         * Creates a new ApplicationMenuItem instance with provided menu position
         * 
         * @param order Display order of this item, lower numbers correspond to higher placement in the menu
         */
        public MarkUnreadContextMenu(int order)
        {
            super(order);

            // Set icon for GCM menu
//            EncodedImage eiMarkUnOpened = EncodedImage.getEncodedImageResource("img/sm_mark_unopened.png");
//            if(eiMarkUnOpened != null)
//            {
//                Image image = ImageFactory.createImage(eiMarkUnOpened);
//                this.setIcon(image);
//            }
        }


        /**
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(Object context)
        {
            if(context instanceof PMReceivedMessage)
            {
                // Mark the context message unread
            	PMReceivedMessage message = (PMReceivedMessage) context;
                PMMessageStore.getInstance().markMessageAsNew(message);
                PMMessageStore.updateIndicator(-1);
            }
            
            return context;
        }


        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return "Mark Demo Message Unread";
        }
    }
    

    /**
     * Open Context menu item. Marks read and opens the selected message for
     * viewing.
     */
    public static class OpenContextMenu extends ApplicationMenuItem
    {
        /**
         * Creates a new ApplicationMenuItem instance with provided menu position
         * 
         * @param order Display order of this item, lower numbers correspond to higher placement in the menu
         */
        public OpenContextMenu(int order)
        {
            super(order);

            // Set icon for GCM menu
//            EncodedImage eiOpen = EncodedImage.getEncodedImageResource("img/sm_open.png");
//            if(eiOpen != null)
//            {
//                Image image = ImageFactory.createImage(eiOpen);
//                this.setIcon(image);
//            }
        }


        /**
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(Object context)
        {
            if(context instanceof PMMessage)
            {
            	PMMessage message = (PMMessage) context;

                // Update status if message is new
               

                // Show message
//                PMMessageScreen previewScreen = new PMMessageScreen(message.getThreadID());
//                UiApplication uiApplication = UiApplication.getUiApplication();
//                uiApplication.pushScreen(previewScreen);
//                uiApplication.requestForeground();
            }
            return context;
        }


        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return "View Demo Message";
        }
    }


    /**
     * @see ApplicationMessageFolderListener#actionPerformed(int, ApplicationMessage[], ApplicationMessageFolder)
     */
    public void actionPerformed(int action, final ApplicationMessage[] messages, ApplicationMessageFolder folder)
    {
     
    }
}
