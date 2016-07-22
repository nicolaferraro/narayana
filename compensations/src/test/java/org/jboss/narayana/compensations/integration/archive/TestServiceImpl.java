/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.narayana.compensations.integration.archive;

import org.jboss.narayana.compensations.api.Compensatable;
import org.jboss.narayana.compensations.api.TxConfirm;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

@Stateless
@WebService(serviceName = TestService.SERVICE_NAME, portName = TestService.PORT_NAME, name = TestService.PORT_NAME, targetNamespace = TestService.NAMESPACE)
public class TestServiceImpl implements TestService {

    @Compensatable
    @TxConfirm(TestConfirmationHandler.class)
    @WebMethod
    public void compensatableMethod() {
        System.out.println(TestServiceImpl.class.getSimpleName() + ".compensatableMethod");
    }

    @WebMethod
    public int getConfirmationHandlerInvocationsCounter() {
        return TestConfirmationHandler.getInvocationsCounter();
    }

}
