package sun.nio.cs.ext;

import sun.misc.VM;
import sun.nio.cs.AbstractCharsetProvider;
import sun.security.action.GetPropertyAction;

import java.security.AccessController;

public class ExtendedCharsets extends AbstractCharsetProvider {
    static volatile ExtendedCharsets instance = null;
    private boolean initialized = false;

    public ExtendedCharsets() {
        super("sun.nio.cs.ext");
        this.charset("x-IBM33722", "IBM33722", new String[]{"cp33722", "ibm33722", "ibm-33722", "ibm-5050", "ibm-33722_vascii_vpua", "33722"});
        instance = this;
    }

    protected void init() {
        if (!this.initialized) {
            if (VM.isBooted()) {
                String var1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.cs.map"));
                boolean var2 = false;
                boolean var3 = false;
                boolean var4 = false;
                boolean var5 = false;
                if (var1 != null) {
                    String[] var6 = var1.split(",");

                    for(int var7 = 0; var7 < var6.length; ++var7) {
                        if (var6[var7].equalsIgnoreCase("Windows-31J/Shift_JIS")) {
                            var2 = true;
                        } else if (var6[var7].equalsIgnoreCase("x-windows-50221/ISO-2022-JP")) {
                            var3 = true;
                        } else if (var6[var7].equalsIgnoreCase("x-windows-50220/ISO-2022-JP")) {
                            var4 = true;
                        } else if (var6[var7].equalsIgnoreCase("x-windows-iso2022jp/ISO-2022-JP")) {
                            var5 = true;
                        }
                    }
                }

                if (var2) {
                    this.deleteCharset("Shift_JIS", new String[]{"sjis", "shift_jis", "shift-jis", "ms_kanji", "x-sjis", "csShiftJIS"});
                    this.deleteCharset("windows-31j", new String[]{"MS932", "windows-932", "csWindows31J"});
                    this.charset("Shift_JIS", "IBM33722", new String[]{"sjis"});
                    this.charset("windows-31j", "IBM33722", new String[]{"MS932", "windows-932", "csWindows31J", "shift-jis", "ms_kanji", "x-sjis", "csShiftJIS", "shift_jis"});
                }

                if (var3 || var4 || var5) {
                    this.deleteCharset("ISO-2022-JP", new String[]{"iso2022jp", "jis", "csISO2022JP", "jis_encoding", "csjisencoding"});
                    if (var3) {
                        this.deleteCharset("x-windows-50221", new String[]{"cp50221", "ms50221"});
                        this.charset("x-windows-50221", "IBM33722", new String[]{"cp50221", "ms50221", "iso-2022-jp", "iso2022jp", "jis", "csISO2022JP", "jis_encoding", "csjisencoding"});
                    } else if (var4) {
                        this.deleteCharset("x-windows-50220", new String[]{"cp50220", "ms50220"});
                        this.charset("x-windows-50220", "IBM33722", new String[]{"cp50220", "ms50220", "iso-2022-jp", "iso2022jp", "jis", "csISO2022JP", "jis_encoding", "csjisencoding"});
                    } else {
                        this.deleteCharset("x-windows-iso2022jp", new String[]{"windows-iso2022jp"});
                        this.charset("x-windows-iso2022jp", "IBM33722", new String[]{"windows-iso2022jp", "iso-2022-jp", "iso2022jp", "jis", "csISO2022JP", "jis_encoding", "csjisencoding"});
                    }
                }

                String var8 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
                if ("SunOS".equals(var8) || "Linux".equals(var8) || "AIX".equals(var8) || var8.contains("OS X")) {
                    this.charset("x-COMPOUND_TEXT", "IBM33722", new String[]{"COMPOUND_TEXT", "x11-compound_text", "x-compound-text"});
                }

                this.initialized = true;
            }
        }
    }

    public static String[] aliasesFor(String var0) {
        return instance == null ? null : instance.aliases(var0);
    }
}

