CoreProgress is a framework to support OkHttp upload and download progress
====================================

[ ![Download](https://api.bintray.com/packages/lizhangqu/maven/coreprogress/images/download.svg) ](https://bintray.com/lizhangqu/maven/coreprogress/_latestVersion)

Changelog
---------

Current version 1.0.2 released on 13th July 2017

See details in [CHANGELOG](https://github.com/lizhangqu/CoreProgress/blob/master/CHANGELOG.md)


Examples
--------

I have provided a sample .
See samples [here on Github](https://github.com/lizhangqu/CoreProgress/tree/master/sample)
To run Sample application, clone the repository and use android studio to compile, install it on a connected device.

You can alse see the test code [here on Github](https://github.com/lizhangqu/CoreProgress/blob/master/library/src/test/java/io/github/lizhangqu/coreprogress/ProgressTest.java)

Usage
-----


**Gradle**

```
dependencies {
  compile 'io.github.lizhangqu:coreprogress:1.0.2'
}
```


**upload**

```
//client
OkHttpClient okHttpClient = new OkHttpClient();
//request builder
Request.Builder builder = new Request.Builder();
builder.url(url);

//your original request body
MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
bodyBuilder.addFormDataPart("testFile", file.getName(), RequestBody.create(null, file));
MultipartBody body = bodyBuilder.build();

//wrap your original request body with progress
RequestBody requestBody = ProgressHelper.withProgress(body, new ProgressUIListener() {

    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
    @Override
    public void onUIProgressStart(long totalBytes) {
        super.onUIProgressStart(totalBytes);
        Log.e("TAG", "onUIProgressStart:" + totalBytes);
    }

    @Override
    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
        Log.e("TAG", "=============start===============");
        Log.e("TAG", "numBytes:" + numBytes);
        Log.e("TAG", "totalBytes:" + totalBytes);
        Log.e("TAG", "percent:" + percent);
        Log.e("TAG", "speed:" + speed);
        Log.e("TAG", "============= end ===============");
        uploadProgress.setProgress((int) (100 * percent));
        uploadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + "  MB/秒");
    }
    
    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
    @Override
    public void onUIProgressFinish() {
        super.onUIProgressFinish();
        Log.e("TAG", "onUIProgressFinish:");
        Toast.makeText(getApplicationContext(), "结束上传", Toast.LENGTH_SHORT).show();
    }
    
});

//post the wrapped request body
builder.post(requestBody);
//call
Call call = okHttpClient.newCall(builder.build());
//enqueue
call.enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
        Log.e("TAG", "=============onFailure===============");
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.e("TAG", "=============onResponse===============");
        Log.e("TAG", "request headers:" + response.request().headers());
        Log.e("TAG", "response headers:" + response.headers());
    }
});
```

if you don't need callback in UI thread, you can use ProgressListener to callback in your caller's original Thread.

**download**

```
//client
OkHttpClient okHttpClient = new OkHttpClient();
//request builder
Request.Builder builder = new Request.Builder();
builder.url(url);
builder.get();
//call
Call call = okHttpClient.newCall(builder.build());
//enqueue
call.enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
        Log.e("TAG", "=============onFailure===============");
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.e("TAG", "=============onResponse===============");
        Log.e("TAG", "request headers:" + response.request().headers());
        Log.e("TAG", "response headers:" + response.headers());
        
        //your original response body
        ResponseBody body = response.body()
        //wrap the original response body with progress
        ResponseBody responseBody = ProgressHelper.withProgress(body, new ProgressUIListener() {
        
            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressStart(long totalBytes) {
                super.onUIProgressStart(totalBytes);
                Log.e("TAG", "onUIProgressStart:" + totalBytes);
            }
            
            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                Log.e("TAG", "=============start===============");
                Log.e("TAG", "numBytes:" + numBytes);
                Log.e("TAG", "totalBytes:" + totalBytes);
                Log.e("TAG", "percent:" + percent);
                Log.e("TAG", "speed:" + speed);
                Log.e("TAG", "============= end ===============");
                downloadProgeress.setProgress((int) (100 * percent));
                downloadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + " MB/秒");
            }
            
            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressFinish() {
                super.onUIProgressFinish();
                Log.e("TAG", "onUIProgressFinish:");
                Toast.makeText(getApplicationContext(), "结束上传", Toast.LENGTH_SHORT).show();
            }
            
        });
        //read the body to file
        BufferedSource source = responseBody.source();
        File outFile = new File("sdcard/temp.file");
        outFile.delete();
        outFile.getParentFile().mkdirs();
        outFile.createNewFile();
        BufferedSink sink = Okio.buffer(Okio.sink(outFile));
        source.readAll(sink);
        sink.flush();
        source.close();
    }
});
        
```

if you don't need callback in UI thread, you can use ProgressListener to callback in your caller's original Thread.

**callback in original thread**

if you don't need callback in UI thread, you can use ProgressListener to callback in your caller's original Thread.

```
//callback in original thread.
ProgressListener progressListener = new ProgressListener() {

    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
    @Override
    public void onProgressStart(long totalBytes) {
        super.onProgressStart(totalBytes);
    }

    @Override
    public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {

    }

    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
    @Override
    public void onProgressFinish() {
        super.onProgressFinish();
    }
};
```


## License

    Copyright 2017 区长

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

