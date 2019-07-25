##################################通过混淆配置#################################
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
-dontoptimize

# 混淆时是否记录日志
-verbose
-ignorewarnings

# 保留Annotation不混淆
-keepattributes *Annotation*

# 避免混淆泛型
-keepattributes Signature
-keepattributes Exceptions,InnerClasses

-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
-keepattributes Deprecated,Synthetic,EnclosingMethod

# 重命名抛出异常时的文件名称
-renamesourcefileattribute SourceFile
-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application {*;}

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference
#自定义控件不要混淆
-keep public class * extends android.view.View {*;}
#adapter也不能混淆
-keep public class * extends android.widget.BaseAdapter {*;}

-keep public class * extends android.widget.CusorAdapter{*;}
-keepnames class * implements java.io.Serializable #比如我们要向activity传递对象使用了Serializable接口的时候，这时候这个类及类里面#的所有内容都不能混淆
-keepclassmembers class * implements java.io.Serializable {
*;
}



-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

-keep class webserver.bean.** {*;}
-keep class webserver.responsebean.** {*;}

-keep class database.bean.** {*;}

 -keep class com.j256.**
 -keepclassmembers class com.j256.** { *; }
 -keep enum com.j256.**
 -keepclassmembers enum com.j256.** { *; }
 -keep interface com.j256.**
 -keepclassmembers interface com.j256.** { *; }
 -keepattributes *Annotation*
 -keepclassmembers class * { @com.j256.ormlite.field.DatabaseField *; }

-keep class com.nanohttpd.** {*;}
-dontwarn org.nanohttpd.**

-keep class com.yanzhenjie.permission.** {*;}

-keep class javax.** {*;}
-keep class javax.servlet.** {*;}

-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*
