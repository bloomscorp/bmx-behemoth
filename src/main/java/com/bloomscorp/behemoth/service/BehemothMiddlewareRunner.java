package com.bloomscorp.behemoth.service;

import com.bloomscorp.behemoth.pojo.BehemothMiddlewareResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BehemothMiddlewareRunner {

	@Contract("_ -> new")
	public static @NotNull BehemothMiddlewareResult run(
		@NotNull List<BehemothMiddleware<?, ?>> middlewares
	) {

		for (BehemothMiddleware<?, ?> middleware : middlewares) {
			if (!middleware.execute()) {
				return new BehemothMiddlewareResult(
					false,
					middleware
				);
			}
		}

		return new BehemothMiddlewareResult(true, null);
	}
}
