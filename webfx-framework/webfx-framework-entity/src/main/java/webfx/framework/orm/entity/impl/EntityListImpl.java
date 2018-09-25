package webfx.framework.orm.entity.impl;

import webfx.framework.orm.entity.Entities;
import webfx.framework.orm.entity.Entity;
import webfx.framework.orm.entity.EntityList;
import webfx.framework.orm.entity.EntityStore;
import webfx.platforms.core.util.collection.Collections;
import webfx.framework.expression.Expression;

import java.util.ArrayList;

/**
 * @author Bruno Salmon
 */
public class EntityListImpl<E extends Entity> extends ArrayList<E> implements EntityList<E> {

    private final Object listId;
    private final EntityStore store;

    public EntityListImpl(Object listId, EntityStore store) {
        this.listId = listId;
        this.store = store;
    }

    @Override
    public Object getListId() {
        return listId;
    }

    @Override
    public EntityStore getStore() {
        return store;
    }

    @Override
    public void orderBy(Expression<E>... orderExpressions) {
        Entities.orderBy(this, orderExpressions);
    }

    @Override
    public String toString() {
        return Collections.toStringWithLineFeeds(this);
    }
}