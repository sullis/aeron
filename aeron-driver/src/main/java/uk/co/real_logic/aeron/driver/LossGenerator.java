/*
 * Copyright 2014 Real Logic Ltd.
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

package uk.co.real_logic.aeron.driver;

import java.net.InetSocketAddress;

/**
 * Interface for loss generators
 */
@FunctionalInterface
public interface LossGenerator
{
    /**
     * Should frame be dropped?
     *
     * @param address of source of frame
     * @param length of frame on wire
     * @return true to drop, false to process
     */
    boolean shouldDropFrame(InetSocketAddress address, int length);
}
