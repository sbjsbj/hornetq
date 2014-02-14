/*
 * Copyright 2005-2014 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.hornetq.spi.core.remoting;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.SendAcknowledgementHandler;
import org.hornetq.core.client.impl.ClientLargeMessageInternal;
import org.hornetq.core.client.impl.ClientMessageInternal;
import org.hornetq.core.client.impl.ClientSessionInternal;
import org.hornetq.core.message.impl.MessageInternal;
import org.hornetq.core.protocol.core.impl.PacketImpl;
import org.hornetq.core.protocol.core.impl.wireformat.SessionSendContinuationMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionSendLargeMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionSendMessage;

/**
 * @author Clebert Suconic
 */

public abstract class SessionContext
{
   protected ClientSessionInternal session;


   public ClientSessionInternal getSession()
   {
      return session;
   }

   public void setSession(ClientSessionInternal session)
   {
      this.session = session;
   }


   public abstract void closeConsumer(ClientConsumer consumer) throws HornetQException;

   public abstract void sendConsumerCredits(ClientConsumer consumer, int credits);

   public abstract boolean supportsLargeMessage();

   /**
    * TODO: Move this to ConsumerContext
    * @param consumerID
    * @param clientLargeMessage
    * @param largeMessageSize
    * @throws Exception
    */
   protected void handleReceiveLargeMessage(long consumerID, ClientLargeMessageInternal clientLargeMessage, long largeMessageSize) throws Exception
   {
      ClientSessionInternal session = this.session;
      if (session != null)
      {
         session.handleReceiveLargeMessage(consumerID, clientLargeMessage, largeMessageSize);
      }
   }

   /**
    * TODO: Move this to ConsumerContext
    * @param consumerID
    * @param message
    * @throws Exception
    */
   protected void handleReceiveMessage(final long consumerID, final ClientMessageInternal message) throws Exception
   {

      ClientSessionInternal session = this.session;
      if (session != null)
      {
         session.handleReceiveMessage(consumerID, message);
      }
   }

   // TODO : move this to ConsumerContext
   protected void handleReceiveContinuation(final long consumerID, byte[] chunk, int flowControlSize, boolean isContinues) throws Exception
   {
      ClientSessionInternal session = this.session;
      if (session != null)
      {
         session.handleReceiveContinuation(consumerID, chunk, flowControlSize, isContinues);
      }
   }

   // TODO: move this to ProducerContext
   protected void handleReceiveProducerCredits(SimpleString address, int credits)
   {
      ClientSessionInternal session = this.session;
      if (session != null)
      {
         session.handleReceiveProducerCredits(address, credits);
      }

   }

   // TODO: move this to ProducerContext
   protected void handleReceiveProducerFailCredits(SimpleString address, int credits)
   {
      ClientSessionInternal session = this.session;
      if (session != null)
      {
         session.handleReceiveProducerFailCredits(address, credits);
      }

   }

   public abstract int getCreditsOnSendingFull(MessageInternal msgI);

   public abstract void sendFullMessage(MessageInternal msgI, boolean sendBlocking, SendAcknowledgementHandler handler) throws HornetQException;

   /**
    * it should return the number of credits (or bytes) used to send this packet
    * @param msgI
    * @return
    * @throws HornetQException
    */
   public abstract int sendInitialChunkOnLargeMessage(MessageInternal msgI) throws HornetQException;


   public abstract int sendLargeMessageChunk(MessageInternal msgI, long messageBodySize, boolean sendBlocking, boolean lastChunk, byte[] chunk, SendAcknowledgementHandler messageHandler) throws HornetQException;


}
