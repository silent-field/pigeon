package com.slient.pigeon.filter;

public interface IFilter extends Comparable<IFilter> {
    String name();

    /**
     * 优先级序号，序号越小越排前
     * @return
     */
    int order();

    /**
     * 初始化
     */
    void init();

    /**
     * 过滤器类型
     * @return
     */
    String type();

    /**
     * 是否可用
     *
     * @return
     */
    boolean enable();

    /**
     * 如果 {@linkplain IFilter#enable()} 返回true，则run方法会被调用
     * @return
     */
    void run(FilterContext context);
}