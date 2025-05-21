package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;
import com.bloomscorp.hastar.code.ActionCode;
import org.hibernate.HibernateException;
import org.jetbrains.annotations.NotNull;
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
	public int addNewEntity(@NotNull E entity) {
		try {
			entity.id = null;
			if (this.getRepository().save(entity).id > 0)
				return ActionCode.INSERT_SUCCESS;
			return ActionCode.INSERT_FAILURE;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return ActionCode.INSERT_FAILURE;
		}
	}

	@Override
	@Deprecated
	public int addNewEntityCompat(E entity) {
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
	public int addNewEntityAndFlush(@NotNull E entity) {
		try {
			entity.id = null;
			if (this.getRepository().saveAndFlush(entity).id > 0)
				return ActionCode.INSERT_SUCCESS;
			return ActionCode.INSERT_FAILURE;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return ActionCode.INSERT_FAILURE;
		}
	}

	@Override
	@Deprecated
	public int addNewEntityAndFlushCompat(E entity) {
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
	public Long addNewEntity(@NotNull E entity, boolean getID) {
		try {
			entity.id = null;
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
	@Deprecated
	public Long addNewEntityCompat(E entity, boolean getID) {
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
	public int addNewEntities(@NotNull List<E> entities) {
		try {

			System.out.println("[Behemoth] Hibernate Version: " + org.hibernate.Version.getVersionString());

			for (E entity : entities)
				entity.id = null;

			List<E> savedEntities = this.getRepository().saveAllAndFlush(entities);
			for (E entity : savedEntities) {
				if (entity.id == 0)
					return ActionCode.INSERT_FAILURE;
			}
			return ActionCode.INSERT_SUCCESS;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return ActionCode.INSERT_FAILURE;
		}
	}

	@Override
	@Deprecated
	public int addNewEntitiesCompat(List<E> entities) {
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
