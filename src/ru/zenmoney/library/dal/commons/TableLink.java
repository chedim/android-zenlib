package ru.zenmoney.library.dal.commons;

import ru.zenmoney.library.dal.model.AbstractModel;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 7:25
 * To change this template use File | Settings | File Templates.
 */
public class TableLink {
    public Class<? extends AbstractModel> target;
    public boolean hard;
    public boolean skip;

    public TableLink(Class<? extends AbstractModel> target) {
        this(target, true, false);
    }

    public TableLink (Class<? extends AbstractModel> target, boolean hard, boolean skip) {
        this.target = target;
        this.hard = hard;
        this.skip = skip;
    }
}
