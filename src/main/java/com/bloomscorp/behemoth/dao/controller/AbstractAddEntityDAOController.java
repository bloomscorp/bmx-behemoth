package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;
import com.bloomscorp.hastar.code.ActionCode;
import org.hibernate.HibernateException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class AbstractAddEntityDAOController<
	E extends BehemothORM,
	R extends JpaRepository<E, Long>
> extends AbstractRetrieveEntityDAOController<E, R> implements AddEntityDAOController<E> {

	public AbstractAddEntityDAOController(R repository) {
		super(repository);
	}

	@Override
	public int addNewEntity(E entity) {
		try {
			if (this.getRepository().save(entity).id > 0)
				return ActionCode.INSERT_SUCCESS;
			return ActionCode.INSERT_FAILURE;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return ActionCode.INSERT_FAILURE;
		}
	}

	@Override
	public int addNewEntityAndFlush(E entity) {
		try {
			if (this.getRepository().saveAndFlush(entity).id > 0)
				return ActionCode.INSERT_SUCCESS;
			return ActionCode.INSERT_FAILURE;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return ActionCode.INSERT_FAILURE;
		}
	}

	@Override
	public Long addNewEntity(E entity, boolean getID) {
		try {
			entity = this.getRepository().save(entity);
			if (entity.id > 0)
				return entity.id;
			return (long) ActionCode.INSERT_FAILURE;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return (long) ActionCode.INSERT_FAILURE;
		}
	}

	@Override
	public int addNewEntities(List<E> entities) {
		try {
			List<E> savedEntities = this.getRepository().saveAllAndFlush(entities);
			for (E entity : savedEntities)
				if (entity.id == 0)
					return ActionCode.INSERT_FAILURE;
			return ActionCode.INSERT_SUCCESS;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return ActionCode.INSERT_FAILURE;
		}
	}
}
