version 0.0.1
-------------

 - 支持UI层回调与非UI层回调

version 0.0.2
-------------

 - 支持UI层开始和结束的请请求或响应回调

version 0.0.3
-------------

 - 重构代码，踢出多余类

version 1.0.0
-------------

 - 重构代码，使用输入/输出流进行支持
 - 修改groupId从cn.edu.zafu到io.github.lizhangqu
 - 不向前兼容

version 1.0.1
-------------

 - UI线程Handler懒初始化
 - ProgressUIListener如果已经在UI线程中则直接回调，否则切换到UI线程回调
 
version 1.0.2
-------------

 - ProgressListener中onProgressStart和onProgressFinish回调方法添加
 - ProgressUIListener中onUIProgressStart和onUIProgressFinish回调方法添加
