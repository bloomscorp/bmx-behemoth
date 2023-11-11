package com.bloomscorp.behemoth.controller;

import com.bloomscorp.alfred.support.ReporterID;
import com.bloomscorp.behemoth.service.BehemothMiddleware;
import com.bloomscorp.behemoth.service.BehemothPreCheck;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.sanitizer.NVerseSanitizer;
import com.bloomscorp.nverse.validator.NVerseValidator;
import com.bloomscorp.raintree.restful.RainEntity;

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

    <W extends BehemothControllerWorker<RainEntity<?>>> String getEntityCustomResponse(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthAccessMessage,
        W worker
    );
}
