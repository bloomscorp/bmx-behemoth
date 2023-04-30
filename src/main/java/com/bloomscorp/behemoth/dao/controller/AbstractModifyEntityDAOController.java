package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;
import com.bloomscorp.hastar.code.ActionCode;
import org.hibernate.HibernateException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class AbstractModifyEntityDAOController<
	E extends BehemothORM,
	R extends JpaRepository<E, Long>
	> extends AbstractAddEntityDAOController<E, R> implements ModifyEntityDAOController<E> {

	public AbstractModifyEntityDAOController(R repository) {
		super(repository);
	}

	@Override
	public int modifyEntity(E entity) {
		try {
			if (this.getRepository().saveAndFlush(entity).id > 0)
				return ActionCode.UPDATE_SUCCESS;
			return ActionCode.UPDATE_FAILURE;
		} catch (HibernateException | IllegalArgumentException ignored) {
			// TODO: log exception here
			return ActionCode.UPDATE_FAILURE;
		}
	}

	@Override
	public int modifyEntities(List<E> entities) {
		try {
			List<E> savedEntities = this.getRepository().saveAllAndFlush(entities);
			for (E entity : savedEntities)
				if (entity.id == 0)
					return ActionCode.UPDATE_FAILURE;
			return ActionCode.UPDATE_SUCCESS;
		} catch (HibernateException ignored) {
			// TODO: log exception here
			return ActionCode.UPDATE_FAILURE;
		}
	}
}
