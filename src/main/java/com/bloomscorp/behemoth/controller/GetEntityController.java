package com.bloomscorp.behemoth.controller;

import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;

public interface GetEntityController {
    <W extends BehemothControllerWorker<String>> String getEntity(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthAccessMessage,
            W worker
    );
}
