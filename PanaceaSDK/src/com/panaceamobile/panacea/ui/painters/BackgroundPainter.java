package com.panaceamobile.panacea.ui.painters;

import net.rim.device.api.ui.Graphics;

/**
 * Defines an interface that can be used to paint a custom background on a
 * field. This abstracts the painting away from the field, which makes the
 * custom code easier to read, and to customise.
 */
public interface BackgroundPainter
{

	/**
	 * {@link BackgroundPainter} implementations must implement this method
	 * to return the height of the field they are painting.
	 * 
	 * @return
	 *         The preferred height of the field to be painted.
	 */
	public int getPreferredHeight();

	/**
	 * {@link BackgroundPainter} implementations must implement this method
	 * to perform any custom painting that is required for the background of
	 * the field.
	 * 
	 * @param graphics
	 *        {@link Graphics} object to use for painting.
	 */
	public void paintBackground(Graphics graphics);
}
