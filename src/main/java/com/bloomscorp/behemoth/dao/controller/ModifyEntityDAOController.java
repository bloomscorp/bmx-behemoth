package com.bloomscorp.behemoth.dao.controller;

import java.util.List;

public interface ModifyEntityDAOController<E> {
	int modifyEntity(E entity);
	int modifyEntities(List<E> entities);
}
