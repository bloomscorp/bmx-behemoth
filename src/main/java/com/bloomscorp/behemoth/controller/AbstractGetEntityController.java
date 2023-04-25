package com.bloomscorp.behemoth.controller;

import com.bloomscorp.alfred.LogBook;
import com.bloomscorp.alfred.cron.CronManager;
import com.bloomscorp.alfred.orm.AuthenticationLog;
import com.bloomscorp.alfred.orm.Log;
import com.bloomscorp.behemoth.contract.service.BehemothPreCheck;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseAuthorityResolver;
import com.bloomscorp.nverse.NVerseGatekeeper;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.pojo.NVerseRole;
import com.bloomscorp.nverse.pojo.NVerseTenant;
import com.bloomscorp.nverse.sanitizer.HttpRequestDumpSanitizer;
import com.bloomscorp.raintree.RainTree;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    @Override
    public <W extends BehemothControllerWorker<String>> String getEntity(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthAccessMessage,
            W worker
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

        return worker.work();
    }

    public abstract void init();
}
