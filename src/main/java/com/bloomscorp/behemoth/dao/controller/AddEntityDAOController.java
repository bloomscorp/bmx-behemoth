package com.bloomscorp.behemoth.dao.controller;

import java.util.List;

public interface AddEntityDAOController<E> {
	int addNewEntity(E entity);
	int addNewEntityCompat(E entity);
	int addNewEntityAndFlush(E entity);
	int addNewEntityAndFlushCompat(E entity);
	Long addNewEntity(E entity, boolean getID);
	Long addNewEntityCompat(E entity, boolean getID);
	int addNewEntities(List<E> entities);
	int addNewEntitiesCompat(List<E> entities);
}
