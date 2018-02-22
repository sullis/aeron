/*
 * Copyright 2014-2018 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.driver.cmd;

import io.aeron.driver.media.ReceiveChannelEndpoint;
import io.aeron.driver.Receiver;

public class RemoveCoolDownCmd implements ReceiverCmd
{
    private final int sessionId;
    private final int streamId;
    private final ReceiveChannelEndpoint channelEndpoint;

    public RemoveCoolDownCmd(final ReceiveChannelEndpoint channelEndpoint, final int sessionId, final int streamId)
    {
        this.sessionId = sessionId;
        this.streamId = streamId;
        this.channelEndpoint = channelEndpoint;
    }

    public void execute(final Receiver receiver)
    {
        receiver.onRemoveCoolDown(channelEndpoint, sessionId, streamId);
    }
}
