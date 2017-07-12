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
import java.io.InputStream;

/**
 * 带进度的输入流
 */
class ProgressInputStream extends InputStream {
    private final InputStream stream;
    private final ProgressCallback listener;

    private long total;
    private long totalRead;


    ProgressInputStream(InputStream stream, ProgressCallback listener, long total) {
        this.stream = stream;
        this.listener = listener;
        this.total = total;
    }


    @Override
    public int read() throws IOException {
        int read = this.stream.read();
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1);
            return read;
        }
        if (read >= 0) {
            this.totalRead++;
            this.listener.onProgressChanged(this.totalRead, this.total, (this.totalRead * 1.0F) / this.total);
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = this.stream.read(b, off, len);
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1);
            return read;
        }
        if (read >= 0) {
            this.totalRead += read;
            this.listener.onProgressChanged(this.totalRead, this.total, (this.totalRead * 1.0F) / this.total);
        }
        return read;
    }

    @Override
    public void close() throws IOException {
        if (this.stream != null) {
            this.stream.close();
        }
    }

}