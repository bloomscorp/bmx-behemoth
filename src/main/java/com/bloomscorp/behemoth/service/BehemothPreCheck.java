package com.bloomscorp.behemoth.service;

import com.bloomscorp.alfred.LogBook;
import com.bloomscorp.alfred.cron.CronManager;
import com.bloomscorp.alfred.orm.AuthenticationLog;
import com.bloomscorp.alfred.orm.LOG_TYPE;
import com.bloomscorp.alfred.orm.Log;
import com.bloomscorp.alfred.support.ReporterID;
import com.bloomscorp.hastar.code.ErrorCode;
import com.bloomscorp.nverse.NVerseAuthorityResolver;
import com.bloomscorp.nverse.NVerseGatekeeper;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.NVerseSurveillanceReport;
import com.bloomscorp.nverse.pojo.NVerseRole;
import com.bloomscorp.nverse.pojo.NVerseTenant;
import com.bloomscorp.nverse.sanitizer.HttpRequestDumpSanitizer;
import com.bloomscorp.nverse.support.Constant;
import com.bloomscorp.raintree.RainTree;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class BehemothPreCheck<
	B extends LogBook<L, A, T, E, R>,
	L extends Log,
	A extends AuthenticationLog,
	T extends NVerseTenant<E, R>,
	E extends Enum<E>,
	R extends NVerseRole<E>
> {

	private final RainTree rainTree;
	private final LogBook<L, A, T, E, R> logBook;
	private final CronManager<B, L, A, T, E, R> cron;
	private final NVerseGatekeeper<T, E, R> gatekeeper;
	private final NVerseAuthorityResolver<T, E, R> authorityResolver;
	private final HttpRequestDumpSanitizer httpRequestDumpSanitizer;

	private NVerseSurveillanceReport surveillanceReport;
	private T user;

	public boolean success(
		@NotNull NVerseHttpRequestWrapper request,
		int surveillanceCode,
		String unAuthAccessRequest,
		String className,
		String methodName
	) {

		String authorizationHeader = request.getHeader(Constant.REQUEST_HEADER_AUTHORIZATION);
		this.user = this.authorityResolver.resolveUserInformationFromAuthorizationToken(authorizationHeader);
		this.surveillanceReport = this.gatekeeper.runSurveillance(user, surveillanceCode);

		if(surveillanceReport.failed()) {
			this.cron.scheduleLogTask(
				unAuthAccessRequest,
				this.logBook.prepareLogReporter(
					user,
					ReporterID.prepareID(
						className,
						methodName
					)
				),
				LOG_TYPE.ALERT,
				this.httpRequestDumpSanitizer.getSanitized(request)
			);
			return false;
		}

		return true;
	}

	public String failureResponse() {
		return this.rainTree.failureResponse(this.failureMessage());
	}

	public String failureMessage() {
		return ErrorCode.decode(this.surveillanceReport.errorCode());
	}
}
