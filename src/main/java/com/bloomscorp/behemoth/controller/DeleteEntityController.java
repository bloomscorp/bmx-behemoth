package com.bloomscorp.behemoth.controller;

import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.sanitizer.NVerseSanitizer;
import com.bloomscorp.nverse.validator.NVerseValidator;
import com.bloomscorp.raintree.RainTreeResponse;
import com.bloomscorp.raintree.restful.RainEnhancedResponse;

public interface DeleteEntityController {

    <E, W extends BehemothControllerWorker<Boolean>> RainTreeResponse deleteEntity(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthDeleteMessage,
            String successLogMessage,
            E entity,
            W worker
    );

    <W extends BehemothControllerWorker<Boolean>> RainTreeResponse deleteEntityByID(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthDeleteMessage,
            String successLogMessage,
            Long id,
            W worker
    );

    <E, W extends BehemothControllerWorker<R>, R> RainTreeResponse deleteEntityEnhancedResponse(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthDeleteMessage,
        String successLogMessage,
        E entity,
        W worker,
        RainEnhancedResponse<E, R> enhancedResponse
    );
}
