package com.bloomscorp.behemoth.controller;

import com.bloomscorp.alfred.LogBook;
import com.bloomscorp.alfred.cron.CronManager;
import com.bloomscorp.alfred.orm.AuthenticationLog;
import com.bloomscorp.alfred.orm.LOG_TYPE;
import com.bloomscorp.alfred.orm.Log;
import com.bloomscorp.alfred.support.ReporterID;
import com.bloomscorp.behemoth.orm.BehemothORM;
import com.bloomscorp.behemoth.pojo.BehemothMiddlewareResult;
import com.bloomscorp.behemoth.service.BehemothMiddleware;
import com.bloomscorp.behemoth.service.BehemothMiddlewareRunner;
import com.bloomscorp.behemoth.service.BehemothPreCheck;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.hastar.code.ErrorCode;
import com.bloomscorp.nverse.NVerseAuthorityResolver;
import com.bloomscorp.nverse.NVerseGatekeeper;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.pojo.NVerseRole;
import com.bloomscorp.nverse.pojo.NVerseTenant;
import com.bloomscorp.nverse.sanitizer.HttpRequestDumpSanitizer;
import com.bloomscorp.nverse.sanitizer.NVerseSanitizer;
import com.bloomscorp.nverse.validator.NVerseValidator;
import com.bloomscorp.raintree.RainTree;
import com.bloomscorp.raintree.RainTreeResponse;
import com.bloomscorp.raintree.restful.RainEnhancedResponse;
import com.bloomscorp.raintree.restful.RainEntity;
import com.bloomscorp.raintree.restful.RainFailedEntity;
import com.bloomscorp.raintree.restful.RainResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPostEntityController<
	B extends LogBook<L, A, T, E, R>,
	L extends Log,
	A extends AuthenticationLog,
	T extends NVerseTenant<E, R>,
	E extends Enum<E>,
	R extends NVerseRole<E>
> extends AbstractGetEntityController<B, L, A, T, E, R> implements PostEntityController {

	public AbstractPostEntityController(
		RainTree rainTree,
		LogBook<L, A, T, E, R> logBook,
		CronManager<B, L, A, T, E, R> cron,
		NVerseGatekeeper<T, E, R> gatekeeper,
		NVerseAuthorityResolver<T, E, R> authorityResolver,
		HttpRequestDumpSanitizer httpRequestDumpSanitizer
	) {
		super(
			rainTree,
			logBook,
			cron,
			gatekeeper,
			authorityResolver,
			httpRequestDumpSanitizer
		);
	}

	private <N> void setVersion(N entity) {
		if (
			(entity instanceof BehemothORM)
			&& (((BehemothORM) entity).id != null)
			&& (((BehemothORM) entity).id == 0)
		) {
			((BehemothORM) entity).version = 0L;
		}
	}

	private <
		N,
		W extends BehemothControllerWorker<P>,
		P
	> @NotNull RainTreeResponse post(
		@NotNull NVerseValidator<N> validator,
		N entity,
		@NotNull NVerseSanitizer<N, N> sanitizer,
		W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {

		if (!validator.validate(
			sanitizer.getSanitized(entity)
		)) return new RainTreeResponse(
			false,
			ErrorCode.decode(
				ErrorCode.INVALID_INFORMATION
			)
		);

		this.setVersion(entity);

		P result = worker.work();

		if (result instanceof Integer && (enhancedResponse == null))
			return RainResponse.prepareActionResponse((Integer) result);

		return enhancedResponse.prepareResponse(result);
	}

	private <
		N1,
		N2,
		W extends BehemothControllerWorker<RainEntity<N2>>
		> @NotNull RainEntity<N2> post(
		@NotNull NVerseValidator<N1> validator,
		N1 entity,
		@NotNull NVerseSanitizer<N1, N1> sanitizer,
		W worker
	) {

		if (!validator.validate(
			sanitizer.getSanitized(entity)
		)) return new RainFailedEntity<>(
			ErrorCode.decode(
				ErrorCode.INVALID_INFORMATION
			)
		);

		this.setVersion(entity);

		return worker.work();
	}

	@Override
	public <N, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntity(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		List<BehemothMiddleware<?, ?>> middlewares,
		W worker
	) {
		return this.postEntityEnhancedResponse(
			request,
			methodName,
			surveillanceCode,
			unAuthPostMessage,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			middlewares,
			worker,
			null
		);
	}

	@Override
	public <N, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntity(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		W worker
	) {
		return this.postEntity(
			request,
			methodName,
			surveillanceCode,
			unAuthPostMessage,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			new ArrayList<>(),
			worker
		);
	}

	@Override
	public <
		N1,
		N2,
		W extends BehemothControllerWorker<RainEntity<N2>>
	> String postEntityCustomResponse(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
		NVerseValidator<N1> validator,
		NVerseSanitizer<N1, N1> sanitizer,
		N1 entity,
		W worker
	) {

		BehemothPreCheck<B, L, A, T, E, R> preCheck = new BehemothPreCheck<>(
			this.getRainTree(),
			this.getLogBook(),
			this.getCron(),
			this.getGatekeeper(),
			this.getAuthorityResolver(),
			this.getHttpRequestDumpSanitizer()
		);

		if (!preCheck.success(
			request,
			surveillanceCode,
			unAuthPostMessage,
			this.getClassName(),
			methodName
		)) return preCheck.failureResponse();

		RainEntity<N2> response = this.post(validator, entity, sanitizer, worker);

		if (response.success)
			super.scheduleLog(
				request,
				successLogMessage,
				this.getLogBook().prepareLogReporter(
					preCheck.getUser(),
					ReporterID.prepareID(
						this.getClassName(),
						methodName
					)
				)
			);

		return this.getRainTree().renderResponse(response);
	}

	@Override
	public <
		N,
		W extends BehemothControllerWorker<P>,
		P
	> RainTreeResponse postEntityEnhancedResponse(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		List<BehemothMiddleware<?, ?>> middlewares,
		W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {

		BehemothPreCheck<B, L, A, T, E, R> preCheck = new BehemothPreCheck<>(
			this.getRainTree(),
			this.getLogBook(),
			this.getCron(),
			this.getGatekeeper(),
			this.getAuthorityResolver(),
			this.getHttpRequestDumpSanitizer()
		);

		if (!preCheck.success(
			request,
			surveillanceCode,
			unAuthPostMessage,
			this.getClassName(),
			methodName
		)) return new RainTreeResponse(
			false,
			preCheck.failureMessage()
		);

		BehemothMiddlewareResult middlewareResult = BehemothMiddlewareRunner.run(middlewares);

		if (!middlewareResult.success())
			return new RainTreeResponse(
				false,
				middlewareResult.middleware().getErrorMessage()
			);

		RainTreeResponse response = this.post(validator, entity, sanitizer, worker, enhancedResponse);

		if (response.success)
			super.scheduleLog(
				request,
				successLogMessage,
				this.getLogBook().prepareLogReporter(
					preCheck.getUser(),
					ReporterID.prepareID(
						this.getClassName(),
						methodName
					)
				)
			);

		return response;
	}

	@Override
	public <
		N,
		W extends BehemothControllerWorker<P>,
		P
		> RainTreeResponse postEntityEnhancedResponse(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {
		return this.postEntityEnhancedResponse(
			request,
			methodName,
			surveillanceCode,
			unAuthPostMessage,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			new ArrayList<>(),
			worker,
			enhancedResponse
		);
	}

	@Override
	public <N, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntityUnauthorized(
		NVerseHttpRequestWrapper request,
		String methodName,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		List<BehemothMiddleware<?, ?>> middlewares,
		W worker
	) {
		return this.postEntityEnhancedResponseUnauthorized(
			request,
			methodName,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			middlewares,
			worker,
			null
		);
	}

	@Override
	public <N, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntityUnauthorized(
		NVerseHttpRequestWrapper request,
		String methodName,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		W worker
	) {
		return this.postEntityUnauthorized(
			request,
			methodName,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			new ArrayList<>(),
			worker
		);
	}

	@Override
	public <
		N1,
		N2,
		W extends BehemothControllerWorker<RainEntity<N2>>
	> String postEntityCustomResponseUnauthorized(
		NVerseHttpRequestWrapper request,
		String methodName,
		String successLogMessage,
		NVerseValidator<N1> validator,
		NVerseSanitizer<N1, N1> sanitizer,
		N1 entity,
		W worker
	) {

		RainEntity<N2> response = this.post(validator, entity, sanitizer, worker);

		if (response.success)
			super.scheduleLog(
				request,
				successLogMessage,
				this.getLogBook().prepareUnauthorizedLogReporter(
					ReporterID.prepareID(
						this.getClassName(),
						methodName
					)
				)
			);

		return this.getRainTree().renderResponse(response);
	}

	@Override
	public <N, W extends BehemothControllerWorker<P>, P> RainTreeResponse postEntityEnhancedResponseUnauthorized(
		NVerseHttpRequestWrapper request,
		String methodName,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		List<BehemothMiddleware<?, ?>> middlewares,
		W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {

		BehemothMiddlewareResult middlewareResult = BehemothMiddlewareRunner.run(middlewares);

		if (!middlewareResult.success())
			return new RainTreeResponse(
				false,
				middlewareResult.middleware().getErrorMessage()
			);

		RainTreeResponse response = this.post(validator, entity, sanitizer, worker, enhancedResponse);

		if (response.success)
			super.scheduleLog(
				request,
				successLogMessage,
				this.getLogBook().prepareUnauthorizedLogReporter(
					ReporterID.prepareID(
						this.getClassName(),
						methodName
					)
				)
			);

		return response;
	}

	@Override
	public <N, W extends BehemothControllerWorker<P>, P> RainTreeResponse postEntityEnhancedResponseUnauthorized(
		NVerseHttpRequestWrapper request,
		String methodName,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {
		return this.postEntityEnhancedResponseUnauthorized(
			request,
			methodName,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			new ArrayList<>(),
			worker,
			enhancedResponse
		);
	}
}
