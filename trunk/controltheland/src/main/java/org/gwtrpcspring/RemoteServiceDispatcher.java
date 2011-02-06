/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */
package org.gwtrpcspring;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;

/**
 * @author earmlta
 */
public class RemoteServiceDispatcher extends RemoteServiceServlet {
    /**
     *
     */
    private static final long serialVersionUID = 7132243934574518263L;

    public RemoteServiceDispatcher() {
    }

    @Override
    public String processCall(String payload) throws SerializationException {
        try {
            RPCRequest rpcRequest = RPC.decodeRequest(payload, null, this);
            onAfterRequestDeserialized(rpcRequest);
            Object service = getService(rpcRequest.getMethod()
                    .getDeclaringClass());

            return invokeAndEncodeResponse(service, rpcRequest.getMethod(),
                    rpcRequest.getParameters(), rpcRequest
                            .getSerializationPolicy());
        } catch (IncompatibleRemoteServiceException ex) {
            log(
                    "An IncompatibleRemoteServiceException was thrown while processing this call.",
                    ex);
            return RPC.encodeResponseForFailure(null, ex);
        }
    }

    protected String invokeAndEncodeResponse(Object target,
                                             Method serviceMethod, Object[] args,
                                             SerializationPolicy serializationPolicy)
            throws SerializationException {

        try {
            RemoteServiceUtil.setThreadLocals(getThreadLocalRequest(),
                    getThreadLocalResponse(), getServletContext());

            // Hook in case subclass needs to prepare
            onBeforeServiceCall(target);
            String response = RPC.invokeAndEncodeResponse(target, serviceMethod,
                    args, serializationPolicy);
            // Hook in case subclass needs to cleanup
            onAfterServiceCall(target);

            RemoteServiceUtil.clearThreadLocals();

            return response;
        } catch (SerializationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * getService will look in context for bean with name of interface. It also
     * verifies the bean implements the given interface.
     *
     * @param serviceInterfaceName
     * @return
     * @throws BeansException
     * @throws ClassNotFoundException
     */
    protected Object getService(Class<?> type) throws BeansException {

        Object service = BeanFactoryUtils.beanOfTypeIncludingAncestors(
                WebApplicationContextUtils
                        .getWebApplicationContext(getServletContext()), type);
        return service;
    }

    /**
     * Hook for sub classes to be able to prepare for call.
     *
     * @param service
     */
    protected void onBeforeServiceCall(Object service) {

    }

    /**
     * Hook in case sub class needs to cleanup.
     *
     * @param service
     */
    protected void onAfterServiceCall(Object service) {

	}

}
