操作系统：Windows11
JDK版本：Java JDK 21.0.4

源代码编译方法：
使用PowerShell 在src的根目录下执行 

> javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })

编译成功后，按如下操作执行字节码
> cd out
> java zh.ppt.main.Slide 