# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

## ============ 说明 ==============================
#一颗星,表示只是保持该包下的类名，而子包下的类名还是会被混淆；
#两颗星,表示把本包和所含子包下的类名都保持；用以上方法保持类后，你会发现类名虽然未混淆，但里面的具体方法和变量命名还是变了。
#-keep class cn.hadcn.test.**
#-keep class cn.hadcn.test.*

#这时如果既想保持类名，又想保持里面的内容不被混淆，我们就需要以下方法了:
#-keep class cn.hadcn.test.* {*;}

# -keep关键字
# keep：包留类和类中的成员，防止他们被混淆
# keepnames:保留类和类中的成员防止被混淆，但成员如果没有被引用将被删除
# keepclassmembers :只保留类中的成员，防止被混淆和移除。
# keepclassmembernames:只保留类中的成员，但如果成员没有被引用将被删除。
# keepclasseswithmembers:如果当前类中包含指定的方法，则保留类和类成员，否则将被混淆。
# keepclasseswithmembernames:如果当前类中包含指定的方法，则保留类和类成员，如果类成员没有被引用，则会被移除


#--------------------------1.实体类---------------------------------
# 如果使用了Gson之类的工具要使被它解析的JavaBean类即实体类不被混淆。（这里填写自己项目中存放bean对象的具体路径）
-keep class com.wiwide.soldout.bean.**{*;}

#--------------------------2.第三方包-------------------------------

#Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.* { *;}
-dontwarn com.google.gson.**

-keep class **$$ViewBinder { *; }

#-------------------------3.与js互相调用的类------------------------


#-------------------------4.反射相关的类和方法----------------------


#-------------------------5.基本不用动区域--------------------------
#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

#混淆时是否记录日志
-verbose

#优化  不优化输入的类文件
-dontoptimize

#预校验
-dontpreverify

# 保留sdk系统自带的一些内容 【例如：-keepattributes *Annotation* 会保留Activity的被@override注释的onCreate、onDestroy方法等】
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# 记录生成的日志数据,gradle build时在本项根目录输出
# apk 包内所有 class 的内部结构
-dump proguard/class_files.txt
# 未混淆的类和成员
-printseeds proguard/seeds.txt
# 列出从 apk 中删除的代码
-printusage proguard/unused.txt
# 混淆前后的映射
-printmapping proguard/mapping.txt


# 避免混淆泛型
-keepattributes Signature
# 抛出异常时保留代码行号,保持源文件以及行号
-keepattributes SourceFile,LineNumberTable

#-----------------------------6.默认保留区-----------------------
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    public native <methods>;
}

# ============忽略警告，否则打包可能会不成功=============
-ignorewarnings
