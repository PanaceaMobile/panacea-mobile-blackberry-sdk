package com.panaceamobile.panacea.ui.controls;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class SpacerField extends Field {
    int _width;
    int _height;

    public SpacerField(int width, int height) {
        super();
        _width = width;
        _height = height;
    }

    public SpacerField(int width, int height, long style) {
        super(style);
        _width = width;
        _height = height;
    }

    protected void layout(int w, int h) {
        int actWidth = Math.min(_width, w);
        int actHeight = Math.min(_height, h);
        setExtent(actWidth, actHeight);
    }
    
    public void setWidth( int width ) {
    	_width = width;
    }

    public void setHeight( int height ) {
    	_height = height;
    }

    protected void paint(Graphics g) {
        // nothing to do
    }

    public int getPreferredWidth() {
        return _width;
    }

    public int getPreferredHeight() {
        return _height;
    }
}
