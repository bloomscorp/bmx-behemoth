package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public abstract class AbstractRetrieveEntityDAOController<
	E extends BehemothORM,
	R extends JpaRepository<E, Long>
> implements RetrieveEntityDAOController<E> {

	private final R repository;

	@Override
	public E retrieveEntity(@NonNull Long id) {
		List<E> entities = this.repository.findAllById(Collections.singletonList(id));
		if (entities.isEmpty()) return null;
		return entities.get(0);
	}

	@Override
	public List<E> retrieveEntityList() {
		List<E> entities = this.repository.findAll();
		if (entities.isEmpty()) return null;
		return entities;
	}
}
