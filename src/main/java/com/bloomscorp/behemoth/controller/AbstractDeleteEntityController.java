package com.bloomscorp.behemoth.controller;

import com.bloomscorp.alfred.LogBook;
import com.bloomscorp.alfred.cron.CronManager;
import com.bloomscorp.alfred.orm.AuthenticationLog;
import com.bloomscorp.alfred.orm.Log;
import com.bloomscorp.alfred.support.ReporterID;
import com.bloomscorp.behemoth.service.BehemothPreCheck;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.hastar.code.ActionCode;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDeleteEntityController<
	B extends LogBook<L, A, T, E, R>,
	L extends Log,
	A extends AuthenticationLog,
	T extends NVerseTenant<E, R>,
	E extends Enum<E>,
	R extends NVerseRole<E>
> extends AbstractPostEntityController<B, L, A, T, E, R> implements DeleteEntityController {

	public AbstractDeleteEntityController(
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

	@Contract(pure = true)
	private @NotNull String prepareResponseMessage(boolean success) {
		return ActionCode.message(success ? ActionCode.DELETE_SUCCESS : ActionCode.DELETE_FAILURE);
	}

	private @NotNull RainTreeResponse performDelete(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthDeleteMessage,
		String successLogMessage,
		BehemothControllerWorker<RainTreeResponse> prepareResponse
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
			unAuthDeleteMessage,
			this.getClassName(),
			methodName
		)) return new RainTreeResponse(
			false,
			preCheck.failureMessage()
		);

		RainTreeResponse response = prepareResponse.work();

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

	private <
		N,
		W extends BehemothControllerWorker<P>,
		P
	> @NotNull RainTreeResponse delete(
		@NotNull W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {
		P result = worker.work();

		if (result instanceof Integer && (enhancedResponse == null))
			return RainResponse.prepareActionResponse((Integer) result);

		return enhancedResponse.prepareResponse(result);
	}

	private <
		N,
		W extends BehemothControllerWorker<P>,
		P
	> @NotNull RainTreeResponse delete(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthDeleteMessage,
		String successLogMessage,
		W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {
		return this.performDelete(
			request,
			methodName,
			surveillanceCode,
			unAuthDeleteMessage,
			successLogMessage,
			() -> this.delete(worker, enhancedResponse)
		);
	}

	@Override
	public <W extends BehemothControllerWorker<Boolean>> @NotNull RainTreeResponse delete(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthDeleteMessage,
		String successLogMessage,
		W worker
	) {
		return this.performDelete(
			request,
			methodName,
			surveillanceCode,
			unAuthDeleteMessage,
			successLogMessage,
			() -> {
				boolean success = worker.work();
				return new RainTreeResponse(
					success,
					this.prepareResponseMessage(success)
				);
			}
		);
	}

	@Override
	public <
		N,
		W extends BehemothControllerWorker<P>,
		P
	> RainTreeResponse deleteEntityEnhancedResponse(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthDeleteMessage,
		String successLogMessage,
		N entity,
		W worker,
		RainEnhancedResponse<N, P> enhancedResponse
	) {
		return this.delete(
			request,
			methodName,
			surveillanceCode,
			unAuthDeleteMessage,
			successLogMessage,
			worker,
			enhancedResponse
		);
	}
}
