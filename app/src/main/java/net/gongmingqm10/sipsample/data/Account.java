package net.gongmingqm10.sipsample.data;

import java.io.Serializable;

public class Account implements Serializable {
    private String username;
    private String password;
    private String domain;

    public Account(String username, String password, String domain) {
        this.username = username;
        this.password = password;
        this.domain = domain;
    }

    public Account(String username, String password) {
        this(username, password, "");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDomain() {
        return domain;
    }
}
