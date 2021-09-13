package com.slient.pigeon.http;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.util.Asserts;

public class Endpoint {
    @Getter
    private String ip;
    @Getter
    private int port;

    public Endpoint() {
    }

    public Endpoint(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Endpoint(String address) {
        Asserts.notEmpty(address, "Endpoint.address");
        String[] splits = address.split(":");
        Asserts.check(2 == splits.length, "Endpoint.address must by ip:port");
        this.ip = splits[0];
        this.port = Integer.valueOf(splits[1]);
    }

    public int hashCode() {
        return (new HashCodeBuilder()).append(this.ip).append(this.port).toHashCode();
    }

    public boolean equals(Object object) {
        boolean flag = false;
        if (object != null && Endpoint.class.isAssignableFrom(object.getClass())) {
            Endpoint rhs = (Endpoint) object;
            flag = (new EqualsBuilder()).append(this.ip, rhs.ip).append(this.port, rhs.port).isEquals();
        }

        return flag;
    }

    public String toString() {
        return String.format("EndPoint{'%s:%d'}", this.ip, this.port);
    }
}