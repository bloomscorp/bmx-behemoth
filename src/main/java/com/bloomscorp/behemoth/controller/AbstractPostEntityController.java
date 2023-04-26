package com.bloomscorp.behemoth.controller;

import com.bloomscorp.alfred.LogBook;
import com.bloomscorp.alfred.cron.CronManager;
import com.bloomscorp.alfred.orm.AuthenticationLog;
import com.bloomscorp.alfred.orm.LOG_TYPE;
import com.bloomscorp.alfred.orm.Log;
import com.bloomscorp.alfred.support.ReporterID;
import com.bloomscorp.behemoth.contract.service.BehemothPreCheck;
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
import com.bloomscorp.raintree.restful.RainResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

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

//	private <N, W extends BehemothControllerWorker<Integer>> @NotNull RainTreeResponse post(
//		@NotNull NVerseValidator<N> validator,
//		N entity,
//		@NotNull NVerseSanitizer<N, N> sanitizer,
//		W worker
//	) {
//
//		if (!validator.validate(
//			sanitizer.getSanitized(entity)
//		)) return new RainTreeResponse(
//			false,
//			ErrorCode.decode(
//				ErrorCode.INVALID_INFORMATION
//			)
//		);
//
//		return RainResponse.prepareActionResponse(worker.work());
//	}

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

		P result = worker.work();

		if (result instanceof Integer && (enhancedResponse == null))
			return RainResponse.prepareActionResponse((Integer) result);

		return enhancedResponse.prepareResponse(result);
	}

	public void scheduleLog(NVerseHttpRequestWrapper request, String logMessage, String reporter) {
		this.getCron().scheduleLogTask(
			logMessage,
			reporter,
			LOG_TYPE.ALERT,
			this.getHttpRequestDumpSanitizer().getSanitized(request)
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
		return this.postEntityEnhancedResponse(
			request,
			methodName,
			surveillanceCode,
			unAuthPostMessage,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			worker,
			null
		);
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

		RainTreeResponse response = this.post(validator, entity, sanitizer, worker, enhancedResponse);

		if (response.success)
			this.scheduleLog(
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
	public <N, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntityUnauthorized(
		NVerseHttpRequestWrapper request,
		String methodName,
		String successLogMessage,
		NVerseValidator<N> validator,
		NVerseSanitizer<N, N> sanitizer,
		N entity,
		W worker
	) {
		return this.postEntityEnhancedResponseUnauthorized(
			request,
			methodName,
			successLogMessage,
			validator,
			sanitizer,
			entity,
			worker,
			null
		);
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

		RainTreeResponse response = this.post(validator, entity, sanitizer, worker, enhancedResponse);

		if (response.success)
			this.scheduleLog(
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
}
