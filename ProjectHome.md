Java Application for copying files using [FTPS Protocol](http://en.wikipedia.org/wiki/FTPS).
This java client uses the Apache libraries [commons-net](http://commons.apache.org/net/).

You can download the latest release 1.2 from this [url](http://ftps-client-java.googlecode.com/svn/trunk/ftps/build/ftps_v1.2)

Some features:

  * Multiplatform (yes! it is written ALL in Java SE)
  * Sys logging (using `log4j-1.2.16.jar`, default log file is located on this path `$USER_HOME/.ftps/log/ftps.log`; you can change configuration on the file `$FTPS_HOME/res/log4j.properties`)
  * Listener API (Java interface `uk.co.marcoratto.ftps.listeners.Listener`, see [Listeners](Listeners.md) for more details)
  * Code Coverage: 75.4%

The available parameters are the following:

| **Parameter** | **Mandatory** | **Default** | **Description** |
|:--------------|:--------------|:------------|:----------------|
| **-source**   | Yes           | n/d         | The source directory. This can be a local path or a remote path of the form `user[:password]@host:/directory/path`. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character.|
| **-target**   | Yes           | n/d         | The target directory. This can be a local path or a remote path of the form `user[:password]@host:/directory/path`. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character.|
| **-port**     | No            | 990         |The port to connect to on the remote host.|
| **-r**        | No            | false       | Search the file recursively.|
| **-ask**      | No            | false       | Ask to digit the password.|
| **-b**        | No            | false       | The file transfer is binary mode (default to false, ASCII). |
| **-d**        | No            | false       | Delete remote file (download) or local file (upload) after download or upload. |
| **-o**        | No            | false       | Overwrite target file if exists, else error (default false). |
| **-e**        | No            | false       | Use EPSV with IPv4. |
| **-k secs**   | No            | -1          | Use keep-alive timer, setControlKeepAliveTimeout. |
| **-w msec**   | No            | -1          | Wait time for keep-alive reply, setControlKeepAliveReplyTimeout (default 1). |
| **-t all|valid|none** | No            | none=JVM default | Use one of the built-in TrustManager implementations. |
| **-retry #**  | No            | 0           | Number of retry in case of errors. |
| **-keyStore** | No            | null        |	Location of the file holding the private key. |
| **-keyStorePassword** | No            | null        | Passphrase for your private key (default to an empty string).|
| **-privateKeyAlias** | No            | null        | The alias of your private key.|
| **-keyPassword** | No            | null        | The password of your  private key.|
| **-trustStore** | No            | null        | Location of the file holding the server certificate.|
| **-trustStorePassword** | No            | null        | Passphrase for your TrustStore (default to an empty string).|

[![](http://www2.clustrmaps.com/stats/maps-no_clusters/code.google.com-p-ftps-client-java--thumb.jpg)](http://www2.clustrmaps.com/user/15910a5ea)