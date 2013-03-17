package ru.zenmoney.library.api.commons;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 3:20
 * To change this template use File | Settings | File Templates.
 */
public class Revision {
    public Long local;
    public Long remote;

    public Revision(Long local, Long remote) {
        this.local = local;
        this.remote = remote;
    }
}
