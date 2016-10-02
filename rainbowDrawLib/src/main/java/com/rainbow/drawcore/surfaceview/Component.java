package com.rainbow.drawcore.surfaceview;

/**
 * Created by rainbow on 16/8/11.
 */
public interface Component {

    public void select();

    public void remove();

    public boolean selfClear();

    public int getType();
}
