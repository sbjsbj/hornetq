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
package org.hornetq.core.protocol.stomp.v11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQBuffers;
import org.hornetq.core.protocol.stomp.Stomp;
import org.hornetq.core.protocol.stomp.StompFrame;

/**
 * @author <a href="mailto:hgao@redhat.com">Howard Gao</a>
 */
public class StompFrameV11 extends StompFrame
{
   private static final byte[] END_OF_FRAME = new byte[]{0, '\n'};

   //stomp 1.1 talks about repetitive headers.
   private final List<Header> allHeaders = new ArrayList<Header>();

   public StompFrameV11(String command, Map<String, String> headers, byte[] content)
   {
      super(command, headers, content);
   }

   public StompFrameV11(String command)
   {
      super(command);
   }

   @Override
   public HornetQBuffer toHornetQBuffer() throws Exception
   {
      if (buffer == null)
      {
         if (bytesBody != null)
         {
            buffer = HornetQBuffers.dynamicBuffer(bytesBody.length + 512);
         }
         else
         {
            buffer = HornetQBuffers.dynamicBuffer(512);
         }

         StringBuffer head = new StringBuffer();
         head.append(command);
         head.append(Stomp.NEWLINE);
         // Output the headers.
         for (Header h : allHeaders)
         {
            head.append(h.getEscapedKey());
            head.append(Stomp.Headers.SEPARATOR);
            head.append(h.getEscapedValue());
            head.append(Stomp.NEWLINE);
         }
         // Add a newline to separate the headers from the content.
         head.append(Stomp.NEWLINE);

         buffer.writeBytes(head.toString().getBytes(StandardCharsets.UTF_8));
         if (bytesBody != null)
         {
            buffer.writeBytes(bytesBody);
         }

         buffer.writeBytes(END_OF_FRAME);

         size = buffer.writerIndex();
      }
      return buffer;
   }

   @Override
   public void addHeader(String key, String val)
   {
      if (!headers.containsKey(key))
      {
         headers.put(key, val);
         allHeaders.add(new Header(key, val));
      }
      else if (!key.equals(Stomp.Headers.CONTENT_LENGTH))
      {
         allHeaders.add(new Header(key, val));
      }
   }


}
