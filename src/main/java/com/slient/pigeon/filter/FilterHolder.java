package com.slient.pigeon.filter;

import com.slient.pigeon.filter.error.ExceptionFilter;
import com.slient.pigeon.filter.finish.FinishFilter;
import com.slient.pigeon.filter.post.PostFilter;
import com.slient.pigeon.filter.preroute.PreRouteFilter;
import com.slient.pigeon.filter.route.RouterFilter;

import java.util.*;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
public class FilterHolder {
    private FilterHolder() {
        add(new PreRouteFilter());
        add(new RouterFilter());
        add(new PostFilter());
        add(new FinishFilter());
        add(new ExceptionFilter());
    }

    private static class InstanceHolder {
        private static final FilterHolder instance = new FilterHolder();
    }

    public static FilterHolder instance() {
        return InstanceHolder.instance;
    }

    private Map<String, IFilter> filters = new HashMap<>();
    private Map<String, List<IFilter>> groupByType = new HashMap<>();

    void add(IFilter filter) {
        this.filters.putIfAbsent(filter.name(), filter);
        this.groupByType.putIfAbsent(filter.type(), new ArrayList<>()).add(filter);
        Collections.sort(groupByType.get(filter.type()));
    }

    public int size() {
        return this.filters.size();
    }

    public Collection<IFilter> getAllFilters() {
        return this.filters.values();
    }

    public Collection<IFilter> getByType(String type) {
        return this.groupByType.get(type);
    }
}
