package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BehemothCRUDDAOController<
	E extends BehemothORM,
	R extends JpaRepository<E, Long>
> extends AbstractDeleteEntityDAOController<E, R> implements CRUDEntityDAOController<E> {

	public BehemothCRUDDAOController(R repository) {
		super(repository);
	}
}
