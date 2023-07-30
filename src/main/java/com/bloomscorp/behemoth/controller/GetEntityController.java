package com.bloomscorp.behemoth.controller;

import com.bloomscorp.behemoth.service.BehemothMiddleware;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;

import java.util.List;

public interface GetEntityController {

    <W extends BehemothControllerWorker<String>> String getEntity(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker
    );

    <W extends BehemothControllerWorker<String>> String getEntity(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthAccessMessage,
            W worker
    );
}
