package com.slient.pigeon.filter;

/**
 * @author gy
 */
public abstract class PigeonFilter implements IFilter {
    @Override
    public void init() {

    }

    @Override
    public int compareTo(IFilter filter) {
        return this.order() - filter.order();
    }
}
