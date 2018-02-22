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
package io.aeron.logbuffer;

import org.agrona.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.Position;

import static io.aeron.logbuffer.FrameDescriptor.*;
import static io.aeron.protocol.DataHeaderFlyweight.HEADER_LENGTH;

/**
 * A term buffer reader.
 * <p>
 * <b>Note:</b> Reading from the term is thread safe, but each thread needs its own instance of this class.
 */
public class TermReader
{
    /**
     * Reads data from a term in a log buffer and updates a passed {@link Position} so progress is not lost in the
     * event of an exception.
     * <p>
     * If a fragmentsLimit of 0 or less is passed then at least one read will be attempted.
     *
     * @param termBuffer         to be read for fragments.
     * @param termOffset         within the buffer that the read should begin.
     * @param handler            the handler for data that has been read
     * @param fragmentsLimit     limit the number of fragments read.
     * @param header             to be used for mapping over the header for a given fragment.
     * @param errorHandler       to be notified if an error occurs during the callback.
     * @param currentPosition    prior to reading further fragments
     * @param subscriberPosition to be updated after reading with new position
     * @return the number of fragments read
     */
    public static int read(
        final UnsafeBuffer termBuffer,
        final int termOffset,
        final FragmentHandler handler,
        final int fragmentsLimit,
        final Header header,
        final ErrorHandler errorHandler,
        final long currentPosition,
        final Position subscriberPosition)
    {
        int fragmentsRead = 0;
        int offset = termOffset;
        final int capacity = termBuffer.capacity();
        header.buffer(termBuffer);

        try
        {
            do
            {
                final int frameLength = frameLengthVolatile(termBuffer, offset);
                if (frameLength <= 0)
                {
                    break;
                }

                final int frameOffset = offset;
                offset += BitUtil.align(frameLength, FRAME_ALIGNMENT);

                if (!isPaddingFrame(termBuffer, frameOffset))
                {
                    header.offset(frameOffset);

                    handler.onFragment(termBuffer, frameOffset + HEADER_LENGTH, frameLength - HEADER_LENGTH, header);

                    ++fragmentsRead;
                }
            }
            while (fragmentsRead < fragmentsLimit && offset < capacity);
        }
        catch (final Throwable t)
        {
            errorHandler.onError(t);
        }
        finally
        {
            final long newPosition = currentPosition + (offset - termOffset);
            if (newPosition > currentPosition)
            {
                subscriberPosition.setOrdered(newPosition);
            }
        }

        return fragmentsRead;
    }

    /**
     * Reads data from a term in a log buffer.
     * <p>
     * If a fragmentsLimit of 0 or less is passed then at least one read will be attempted.
     * Note: this method has users outside of Aeron
     *
     * @param termBuffer     to be read for fragments.
     * @param termOffset     within the buffer that the read should begin.
     * @param handler        the handler for data that has been read
     * @param fragmentsLimit limit the number of fragments read.
     * @param header         to be used for mapping over the header for a given fragment.
     * @param errorHandler   to be notified if an error occurs during the callback.
     * @return the number of fragments read
     */
    public static long read(
        final UnsafeBuffer termBuffer,
        final int termOffset,
        final FragmentHandler handler,
        final int fragmentsLimit,
        final Header header,
        final ErrorHandler errorHandler)
    {
        int fragmentsRead = 0;
        int offset = termOffset;
        final int capacity = termBuffer.capacity();

        try
        {
            do
            {
                final int frameLength = frameLengthVolatile(termBuffer, offset);
                if (frameLength <= 0)
                {
                    break;
                }

                final int frameOffset = offset;
                offset += BitUtil.align(frameLength, FRAME_ALIGNMENT);

                if (!isPaddingFrame(termBuffer, frameOffset))
                {
                    header.buffer(termBuffer);
                    header.offset(frameOffset);

                    handler.onFragment(termBuffer, frameOffset + HEADER_LENGTH, frameLength - HEADER_LENGTH, header);

                    ++fragmentsRead;
                }
            }
            while (fragmentsRead < fragmentsLimit && offset < capacity);
        }
        catch (final Throwable t)
        {
            errorHandler.onError(t);
        }

        return pack(offset, fragmentsRead);
    }

    /**
     * Pack the values for fragmentsRead and offset into a long for returning on the stack.
     *
     * @param offset        value to be packed.
     * @param fragmentsRead value to be packed.
     * @return a long with both ints packed into it.
     */
    public static long pack(final int offset, final int fragmentsRead)
    {
        return ((long)offset << 32) | fragmentsRead;
    }

    /**
     * The number of fragments that have been read.
     *
     * @param readOutcome into which the fragments read value has been packed.
     * @return the number of fragments that have been read.
     */
    public static int fragmentsRead(final long readOutcome)
    {
        return (int)readOutcome;
    }

    /**
     * The offset up to which the term has progressed.
     *
     * @param readOutcome into which the offset value has been packed.
     * @return the offset up to which the term has progressed.
     */
    public static int offset(final long readOutcome)
    {
        return (int)(readOutcome >>> 32);
    }
}
