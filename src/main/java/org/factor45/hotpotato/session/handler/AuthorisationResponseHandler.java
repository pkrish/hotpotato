/*
 * Copyright 2010 Bruno de Carvalho
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.factor45.hotpotato.session.handler;

import org.factor45.hotpotato.request.HttpRequestFuture;
import org.factor45.hotpotato.response.HttpResponseProcessor;
import org.factor45.hotpotato.session.HttpSession;
import org.factor45.hotpotato.session.HttpSessionFutureListener;
import org.factor45.hotpotato.session.RecursiveAwareHttpRequest;
import org.factor45.hotpotato.utils.HostPortAndUri;

/**
 * @author <a:mailto="bruno.carvalho@wit-software.com" />Bruno de Carvalho</a>
 */
public class AuthorisationResponseHandler implements ResponseCodeHandler {

    // ResponseCodeHandler --------------------------------------------------------------------------------------------

    @Override
    public int[] handlesResponseCodes() {
        return new int[]{401};
    }

    @Override
    public <T> void handleResponse(HttpSession session, HttpRequestFuture<T> originalFuture,
                                   HttpRequestFuture<T> future, HostPortAndUri target,
                                   RecursiveAwareHttpRequest request, HttpResponseProcessor<T> processor) {
        if (request.isFailedAuth()) {
            originalFuture.setSuccess(future.getProcessedResult(), future.getResponse());
            return;
        }

        request.failedAuth();
        HttpRequestFuture<T> nextWrapper = session.execute(target, request, processor);
        nextWrapper.addListener(new HttpSessionFutureListener<T>(session, nextWrapper, target, request, processor));
    }
}
