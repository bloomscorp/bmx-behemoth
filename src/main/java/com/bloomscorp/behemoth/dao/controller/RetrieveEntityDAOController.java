package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;

import java.util.List;

public interface RetrieveEntityDAOController<E extends BehemothORM> {
	E retrieveEntity(Long id);
	List<E> retrieveEntityList();
}