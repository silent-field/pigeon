package com.slient.pigeon.filter;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/13
 */
public abstract class AbstractFilter implements IFilter {
    @Override
    public String name() {
        return getClass().getSimpleName();
    }

    @Override
    public void init() {

    }

    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public int compareTo(IFilter o) {
        if (order() > o.order()) {
            return 1;
        } else if (order() < o.order()) {
            return -1;
        }
        return 0;
    }
}
