package com.bloomscorp.behemoth.dao.controller;

import com.bloomscorp.behemoth.orm.BehemothORM;

public interface CRUDEntityDAOController<E extends BehemothORM>
	extends RetrieveEntityDAOController<E>,
			AddEntityDAOController<E>,
			ModifyEntityDAOController<E>,
			DeleteEntityDAOController<E> {
}
