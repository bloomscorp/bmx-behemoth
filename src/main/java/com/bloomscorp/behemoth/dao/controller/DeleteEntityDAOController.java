package com.bloomscorp.behemoth.dao.controller;

public interface DeleteEntityDAOController<E> {
	boolean deleteEntity(E entity);
	boolean deleteEntityByID(Long id);
}
