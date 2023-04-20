package com.bloomscorp.behemoth.controller;

import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.sanitizer.NVerseSanitizer;
import com.bloomscorp.nverse.validator.NVerseValidator;
import com.bloomscorp.raintree.RainTreeResponse;

public interface PostEntityController {
    <E, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntity(
            NVerseHttpRequestWrapper request,
            String methodName,
            int surveillanceCode,
            String unAuthPostMessage,
            String successLogMessage,
            NVerseValidator<E> validator,
            NVerseSanitizer<E, E> sanitizer,
            E entity,
            W worker
    );
    <E, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntityUnauthorized(
            NVerseHttpRequestWrapper request,
            String methodName,
            String successLogMessage,
            NVerseValidator<E> validator,
            NVerseSanitizer<E, E> sanitizer,
            E entity,
            W worker
    );
}
