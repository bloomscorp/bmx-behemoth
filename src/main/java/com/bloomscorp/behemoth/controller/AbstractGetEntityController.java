package com.bloomscorp.behemoth.controller;

import com.bloomscorp.alfred.LogBook;
import com.bloomscorp.alfred.cron.CronManager;
import com.bloomscorp.alfred.orm.AuthenticationLog;
import com.bloomscorp.alfred.orm.LOG_TYPE;
import com.bloomscorp.alfred.orm.Log;
import com.bloomscorp.alfred.support.ReporterID;
import com.bloomscorp.behemoth.pojo.BehemothMiddlewareResult;
import com.bloomscorp.behemoth.service.BehemothMiddleware;
import com.bloomscorp.behemoth.service.BehemothMiddlewareRunner;
import com.bloomscorp.behemoth.service.BehemothPreCheck;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
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
import com.bloomscorp.raintree.restful.RainEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class AbstractGetEntityController<
    B extends LogBook<L, A, T, E, R>,
    L extends Log,
    A extends AuthenticationLog,
    T extends NVerseTenant<E, R>,
    E extends Enum<E>,
    R extends NVerseRole<E>
> implements GetEntityController {


    private final RainTree rainTree;
    private final LogBook<L, A, T, E, R> logBook;
    private final CronManager<B, L, A, T, E, R> cron;
    private final NVerseGatekeeper<T, E, R> gatekeeper;
    private final NVerseAuthorityResolver<T, E, R> authorityResolver;
    private final HttpRequestDumpSanitizer httpRequestDumpSanitizer;

    @Setter
    private String className;

    private String getEntity(
        NVerseHttpRequestWrapper request,
        BehemothControllerWorker<String> prepareResponse,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        boolean shouldLog,
        String successLogMessage
    ) {

        BehemothPreCheck<B, L, A, T, E, R> preCheck = new BehemothPreCheck<>(
            this.rainTree,
            this.logBook,
            this.cron,
            this.gatekeeper,
            this.authorityResolver,
            this.httpRequestDumpSanitizer
        );

        if (!preCheck.success(
            request,
            surveillanceCode,
            unAuthAccessMessage,
            this.className,
            methodName
        )) return preCheck.failureResponse();

        if (shouldLog)
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

        return prepareResponse.work();
    }

    private String getEntity(
        NVerseHttpRequestWrapper request,
        BehemothControllerWorker<String> prepareResponse,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage
    ) {
        return this.getEntity(
            request,
            prepareResponse,
            methodName,
            surveillanceCode,
            unAuthAccessMessage,
            false,
            null
        );
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
    public <W extends BehemothControllerWorker<String>> String getEntity(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker,
        boolean shouldLog,
        String successLogMessage
    ) {
        return this.getEntity(
            request,
            () -> {

                BehemothMiddlewareResult middlewareResult = BehemothMiddlewareRunner.run(middlewares);

                if (!middlewareResult.success())
                    return this.rainTree.failureResponse(
                        middlewareResult.middleware().getErrorMessage()
                    );

                return worker.work();
            },
            methodName,
            surveillanceCode,
            unAuthAccessMessage,
            shouldLog,
            successLogMessage
        );
    }

    @Override
    public <W extends BehemothControllerWorker<String>> String getEntity(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker
    ) {
        return this.getEntity(
            request,
            methodName,
            surveillanceCode,
            unAuthAccessMessage,
            middlewares,
            worker,
            false,
            null
        );
    }

    @Override
    public <W extends BehemothControllerWorker<String>> String getEntity(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        String successLogMessage,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker
    ) {
        return this.getEntity(
            request,
            methodName,
            surveillanceCode,
            unAuthAccessMessage,
            middlewares,
			worker,
            true,
            successLogMessage
        );
    }

    @Override
    public <W extends BehemothControllerWorker<String>> String getEntity(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        W worker
    ) {
        return this.getEntity(
            request,
            methodName,
            surveillanceCode,
            unAuthAccessMessage,
            new ArrayList<>(),
            worker
        );
    }

    @Override
    public <W extends BehemothControllerWorker<RainEntity<?>>> String getEntityCustomResponse(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        W worker
    ) {
        return this.getEntity(
            request,
            () -> this.getRainTree().renderResponse(worker.work()),
            methodName,
            surveillanceCode,
            unAuthAccessMessage
        );
    }

    public abstract void init();
}
