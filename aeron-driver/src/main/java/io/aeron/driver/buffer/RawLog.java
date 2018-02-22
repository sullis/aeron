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
package io.aeron.driver.buffer;

import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

/**
 * Represents the collection of term and associated state buffers for the image between a publisher and subscriber
 * image for the replicated log.
 */
public interface RawLog extends AutoCloseable
{
    /**
     * The length of each term in bytes.
     *
     * @return the length of each term in bytes.
     */
    int termLength();

    /**
     * An array of term buffer partitions.
     *
     * @return an array of the term buffer partitions.
     */
    UnsafeBuffer[] termBuffers();

    /**
     * The meta data storage for the overall log.
     *
     * @return the meta data storage for the overall log.
     */
    UnsafeBuffer metaData();

    /**
     * Slice the underlying buffer to provide an array of term buffers in order.
     *
     * @return slices of the underlying buffer to provide an array of term buffers in order.
     */
    ByteBuffer[] sliceTerms();

    /**
     * Get the fully qualified file name for the log file.
     *
     * @return the fully qualified file name for the log file.
     */
    String fileName();

    void close();
}
