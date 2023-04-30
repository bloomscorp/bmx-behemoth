package com.bloomscorp.behemoth.controller;

import com.bloomscorp.alfred.LogBook;
import com.bloomscorp.alfred.cron.CronManager;
import com.bloomscorp.alfred.orm.AuthenticationLog;
import com.bloomscorp.alfred.orm.Log;
import com.bloomscorp.alfred.support.ReporterID;
import com.bloomscorp.behemoth.contract.service.BehemothPreCheck;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.hastar.code.ActionCode;
import com.bloomscorp.nverse.NVerseAuthorityResolver;
import com.bloomscorp.nverse.NVerseGatekeeper;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.pojo.NVerseRole;
import com.bloomscorp.nverse.pojo.NVerseTenant;
import com.bloomscorp.nverse.sanitizer.HttpRequestDumpSanitizer;
import com.bloomscorp.raintree.RainTree;
import com.bloomscorp.raintree.RainTreeResponse;
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

	private String prepareResponseMessage(boolean success) {
		return ActionCode.message(success ? ActionCode.DELETE_SUCCESS : ActionCode.DELETE_FAILURE);
	}

	private <W extends BehemothControllerWorker<Boolean>> @NotNull RainTreeResponse delete(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
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
		)) return new RainTreeResponse(
			false,
			preCheck.failureMessage()
		);

		boolean success = worker.work();

		RainTreeResponse response = new RainTreeResponse(
			success,
			this.prepareResponseMessage(success)
		);

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
	public <N, W extends BehemothControllerWorker<Boolean>> RainTreeResponse deleteEntity(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
		N entity,
		W worker
	) {
		return this.delete(
			request,
			methodName,
			surveillanceCode,
			unAuthPostMessage,
			successLogMessage,
			worker
		);
	}

	@Override
	public <W extends BehemothControllerWorker<Boolean>> RainTreeResponse deleteEntityByID(
		NVerseHttpRequestWrapper request,
		String methodName,
		int surveillanceCode,
		String unAuthPostMessage,
		String successLogMessage,
		Long id,
		W worker
	) {
		return this.delete(
			request,
			methodName,
			surveillanceCode,
			unAuthPostMessage,
			successLogMessage,
			worker
		);
	}
}
