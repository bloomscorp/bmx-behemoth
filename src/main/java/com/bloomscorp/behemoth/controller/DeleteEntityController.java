package com.bloomscorp.behemoth.controller;

import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.raintree.RainTreeResponse;

public interface DeleteEntityController {

    <E, W extends BehemothControllerWorker<Boolean>> RainTreeResponse deleteEntity(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthPostMessage,
            String successLogMessage,
            E entity,
            W worker
    );

    <W extends BehemothControllerWorker<Boolean>> RainTreeResponse deleteEntityByID(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthPostMessage,
            String successLogMessage,
            Long id,
            W worker
    );
}
