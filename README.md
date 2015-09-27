CoreProgress is a framework to support OkHttp upload and download progress
====================================

[ ![Download](https://api.bintray.com/packages/lizhangqu/maven/coreprogress/images/download.svg) ](https://bintray.com/lizhangqu/maven/coreprogress/_latestVersion)

Changelog
---------

Current version 0.0.2 released on 10th Sept 2015

See details in [CHANGELOG](https://github.com/lizhangqu/CoreProgress/blob/master/CHANGELOG.md)



Examples
--------

I have provided a sample .
See samples [here on Github](https://github.com/lizhangqu/CoreProgress/tree/master/sample)
To run Sample application, simply clone the repository and use android studio to compile,  install it on connected device



Usage
-----


**Gradle**

```
dependencies {
  compile 'cn.edu.zafu:coreprogress:0.0.2'
}
```

Notice that it will not compile Okhttp library,you should add it yourself.

**upload**

```
//OkHttpClient请求Client
private static final OkHttpClient client = new OkHttpClient();

//这个是非ui线程回调，不可直接操作UI
final ProgressRequestListener progressListener = new ProgressRequestListener() {
	@Override
	public void onRequestProgress(long bytesWrite, long contentLength, boolean done) {
		Log.e("TAG", "bytesWrite:" + bytesWrite);
		Log.e("TAG", "contentLength" + contentLength);
		Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
		Log.e("TAG", "done:" + done);
		Log.e("TAG", "================================");
	}
};
//这个是ui线程回调，可直接操作UI
final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
	@Override
	public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
		Log.e("TAG", "bytesWrite:" + bytesWrite);
		Log.e("TAG", "contentLength" + contentLength);
		Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
		Log.e("TAG", "done:" + done);
		Log.e("TAG", "================================");
	}
};
//文件
File file = new File("文件路径");
//构造上传请求，类似web表单
RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
		.addFormDataPart("hello", "android")
		.addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
		.addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), file))
		.build();

//进行包装，使其支持进度回调
final Request request = new Request.Builder().url("上传的服务器路径").post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener)).build();
//开始请求
client.newCall(request).enqueue(new Callback() {
	@Override
	public void onFailure(Request request, IOException e) {
		Log.e("TAG", "error ", e);
	}

	@Override
	public void onResponse(Response response) throws IOException {
		Log.e("TAG", response.body().string());
	}
});
```

**download**

```
//OkHttpClient请求Client
private static final OkHttpClient client = new OkHttpClient();

//这个是非ui线程回调，不可直接操作UI
final ProgressResponseListener progressResponseListener = new ProgressResponseListener() {
	@Override
	public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
		Log.e("TAG", "bytesRead:" + bytesRead);
		Log.e("TAG", "contentLength:" + contentLength);
		Log.e("TAG", "done:" + done);
		if (contentLength != -1) {
			//长度未知的情况下回返回-1
			Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
		}
		Log.e("TAG", "================================");
	}
};


//这个是ui线程回调，可直接操作UI
final UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
	@Override
	public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
		Log.e("TAG", "bytesRead:" + bytesRead);
		Log.e("TAG", "contentLength:" + contentLength);
		Log.e("TAG", "done:" + done);
		if (contentLength != -1) {
			//长度未知的情况下回返回-1
			Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
		}
		Log.e("TAG", "================================");
		//ui层回调
	}
};

//构造请求
final Request request1 = new Request.Builder()
		.url("下载的服务器路径")
		.build();

//包装Response使其支持进度回调
ProgressHelper.addProgressResponseListener(client, uiProgressResponseListener).newCall(request1).enqueue(new Callback() {
	@Override
	public void onFailure(Request request, IOException e) {
		Log.e("TAG", "error ", e);
	}

	@Override
	public void onResponse(Response response) throws IOException {
		Log.e("TAG", response.body().string());
	}
});
```

**start or finish(version 0.0.2 support)**


If you need listener the start or finish callback,you need to override the method **onUIRequestStart** or **onUIRequestFinish** in **UIProgressRequestListener**,**onUIResponseStart** or **onUIResponseFinish** in **UIProgressResponseListener**


```
//这个是ui线程回调，可直接操作UI
final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
    @Override
    public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
        Toast.makeText(getApplicationContext(), bytesWrite + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUIRequestStart(long bytesWrite, long contentLength, boolean done) {
        Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUIRequestFinish(long bytesWrite, long contentLength, boolean done) {
        Toast.makeText(getApplicationContext(),"end",Toast.LENGTH_SHORT).show();
    }
};
```

```
//这个是ui线程回调，可直接操作UI
final UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
    @Override
    public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
       Toast.makeText(getApplicationContext(), bytesRead + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUIResponseStart(long bytesRead, long contentLength, boolean done) {
        Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUIResponseFinish(long bytesRead, long contentLength, boolean done) {
        Toast.makeText(getApplicationContext(),"end",Toast.LENGTH_SHORT).show();
    }
};
```


## License

    Copyright 2015 ZhangQu Li

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

