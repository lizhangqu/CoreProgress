/**
 * Copyright 2017 区长
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.lizhangqu.coreprogress;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 带进度的输出流
 */
class ProgressOutputStream extends OutputStream {
    private final OutputStream stream;
    private final ProgressCallback listener;

    private long total;
    private long totalWritten;

    ProgressOutputStream(OutputStream stream, ProgressCallback listener, long total) {
        this.stream = stream;
        this.listener = listener;
        this.total = total;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.stream.write(b, off, len);
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1);
            return;
        }
        if (len < b.length) {
            this.totalWritten += len;
        } else {
            this.totalWritten += b.length;
        }
        this.listener.onProgressChanged(this.totalWritten, this.total, (this.totalWritten * 1.0F) / this.total);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1);
            return;
        }
        this.totalWritten++;
        this.listener.onProgressChanged(this.totalWritten, this.total, (this.totalWritten * 1.0F) / this.total);
    }

    @Override
    public void close() throws IOException {
        if (this.stream != null) {
            this.stream.close();
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.stream != null) {
            this.stream.flush();
        }
    }
}
