/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package de.jexp;

import de.jexp.idcompression.*;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.parameters.TimeValue;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class IdCompressionBenchmark {

    public static final int START = 7;
    public static final int MULTIPLY = 11;

    @State(value = Scope.Thread)
    public static class SignedLongEncoderHolder extends LongEncoderHolder {
        public SignedLongEncoderHolder() {
            super(new SignedLongBase128Encoder());
        }
    }

    @State(value = Scope.Thread)
    public static class UnSignedLongEncoderHolder extends LongEncoderHolder {
        public UnSignedLongEncoderHolder() {
            super(new UnsignedLongBase128Encoder());
        }

        @Override
        void nextValue() {
            super.nextValue();
            if (value < 0) value = START;
        }
    }
    @State(value = Scope.Thread)
    public static class SimpleLongEncoderHolder extends LongEncoderHolder {
        public SimpleLongEncoderHolder() {
            super(new SimpleLongEncoder());
        }
    }
    @State(value = Scope.Thread)
    public static class StoringLongEncoderHolder extends LongEncoderHolder {
        public StoringLongEncoderHolder() {
            super(new StoringLongEncoder());
        }
    }

    public static class LongEncoderHolder {
        private final LongEncoder encoder;
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        long value = START;

        public LongEncoderHolder(LongEncoder encoder) {
            this.encoder = encoder;
        }

        @CompilerControl(CompilerControl.Mode.INLINE)
        public int encode() {
            buffer.rewind();
            return encoder.encode(buffer, value);
        }

        @CompilerControl(CompilerControl.Mode.INLINE)
        public long decode() {
            buffer.rewind();
            long decoded = encoder.decode(buffer);
            if (decoded != value) throw new AssertionError("Incorrectly decoded, original: "+ value+" != decoded: "+decoded);
            return decoded;
        }

        @CompilerControl(CompilerControl.Mode.INLINE)
        void nextValue() {
            value*=MULTIPLY;
        }

        long benchmark() {
            encode();
            long decoded = decode();
            nextValue();
            return decoded;
        }
    }

    @GenerateMicroBenchmark
    public long testStoringLongEncoder(StoringLongEncoderHolder longEncoder) {
        longEncoder.encode();
        long decoded = longEncoder.decode();
        longEncoder.nextValue();
        return decoded;
    }

    @GenerateMicroBenchmark
    public long testSimpleLongEncoder(SimpleLongEncoderHolder longEncoder) {
        longEncoder.encode();
        long decoded = longEncoder.decode();
        longEncoder.nextValue();
        return decoded;
    }

    @GenerateMicroBenchmark
    public long testUnsignedEncoder(UnSignedLongEncoderHolder longEncoder) {
        longEncoder.encode();
        long decoded = longEncoder.decode();
        longEncoder.nextValue();
        return decoded;
    }
    @GenerateMicroBenchmark
    public long testSignedEncoder(SignedLongEncoderHolder longEncoder) {
        longEncoder.encode();
        long decoded = longEncoder.decode();
        longEncoder.nextValue();
        return decoded;
    }

    public static void main(String[] args) throws RunnerException {
	    Options opt = new OptionsBuilder()
	            .include(".*" + IdCompressionBenchmark.class.getSimpleName() + ".*")
	            .forks(1)
                .warmupIterations(3)
                .measurementTime(TimeValue.seconds(1))
                .measurementIterations(5)
                .timeUnit(TimeUnit.MILLISECONDS)
	            .build();

	    new Runner(opt).run();
	}
}
