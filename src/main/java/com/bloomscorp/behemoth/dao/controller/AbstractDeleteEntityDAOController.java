package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractDeleteEntityDAOController<
	E extends BehemothORM,
	R extends JpaRepository<E, Long>
> extends AbstractModifyEntityDAOController<E, R> implements DeleteEntityDAOController<E> {

	public AbstractDeleteEntityDAOController(R repository) {
		super(repository);
	}

	@Override
	public boolean deleteEntity(E entity) {
		try {
			if (entity == null)
				return false;
			this.getRepository().delete(entity);
			return true;
		} catch (Exception e) {
			// TODO: log
			return false;
		}
	}

	@Override
	public boolean deleteEntityByID(Long id) {
		try {
			this.getRepository().deleteById(id);
			return true;
		} catch (Exception e) {
			// TODO: log
			return false;
		}
	}
}
