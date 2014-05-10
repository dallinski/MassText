package com.dallinc.masstexter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Build;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

public class ChangeLog {
    private static final int API_LEVEL = 0;
    private static final String EOCL = "END_OF_CHANGE_LOG";
    private static final String NO_VERSION = "";
    private static final String TAG = "ChangeLog";
    private static final String VERSION_KEY = "PREFS_VERSION_KEY";
    private final Context context;
    private String lastVersion;
    private Listmode listMode = Listmode.NONE;
    private StringBuffer sb = null;
    private String thisVersion;

    static {
        ChangeLog.API_LEVEL = Integer.parseInt((String)(Build.VERSION.SDK));
    }

    public ChangeLog(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences((Context)(context)));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public ChangeLog(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.lastVersion = sharedPreferences.getString("PREFS_VERSION_KEY", "");
        Log.d((String)("ChangeLog"), (String)(("lastVersion: " + this.lastVersion)));
        try {
            this.thisVersion = context.getPackageManager().getPackageInfo((String)(context.getPackageName()), (int)(0)).versionName;
        }
        catch (PackageManager.NameNotFoundException var4_3) {
            this.thisVersion = "";
            Log.e((String)("ChangeLog"), (String)("could not get version name from manifest!"));
            var4_3.printStackTrace();
        }
        Log.d((String)("ChangeLog"), (String)(("appVersion: " + this.thisVersion)));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    private void closeList() {
        if (this.listMode == Listmode.ORDERED) {
            this.sb.append("</ol></div>\n");
        } else if (this.listMode == Listmode.UNORDERED) {
            this.sb.append("</ul></div>\n");
        }
        this.listMode = Listmode.NONE;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    private AlertDialog getDialog(boolean bl) {
        WebView webView = new WebView(this.context);
        if (ChangeLog.API_LEVEL >= 11) {
            Compatibility.setViewLayerTypeSoftware((View)(webView));
        }
        webView.setBackgroundColor(0);
        webView.loadDataWithBaseURL((String)(null), this.getLog(bl), "text/html", "UTF-8", (String)(null));
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        Resources resources = this.context.getResources();
        int n = (bl) ? (2131099664) : (2131099665);
        builder.setTitle((CharSequence)(resources.getString(n))).setView((View)(webView)).setCancelable(false).setPositiveButton((CharSequence)(this.context.getResources().getString(2131099666)), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                ChangeLog.this.updateVersionInPreferences();
            }
        }));
        if (bl) return builder.create();
        builder.setNegativeButton(2131099667, (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                ChangeLog.this.getFullLogDialog().show();
            }
        }));
        return builder.create();
    }

    /*
     * Exception decompiling
     */
    private String getLog(boolean var1_1) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // java.lang.ClassCastException: org.benf.cfr.reader.bytecode.analysis.parse.statement.AssignmentSimple cannot be cast to org.benf.cfr.reader.bytecode.analysis.parse.statement.CaseStatement
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.buildSwitchCases(Op03SimpleStatement.java:4627)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.replaceRawSwitch(Op03SimpleStatement.java:4574)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.replaceRawSwitches(Op03SimpleStatement.java:4856)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:295)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:111)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:78)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:359)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:645)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:578)
        // org.benf.cfr.reader.Main.doJar(Main.java:108)
        // com.njlabs.showjava.AppProcessActivity$4.run(AppProcessActivity.java:412)
        // java.lang.Thread.run(Thread.java:841)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    private void openList(Listmode listmode) {
        if (this.listMode == listmode) return;
        this.closeList();
        if (listmode == Listmode.ORDERED) {
            this.sb.append("<div class='list'><ol>\n");
        } else if (listmode == Listmode.UNORDERED) {
            this.sb.append("<div class='list'><ul>\n");
        }
        this.listMode = listmode;
    }

    private void updateVersionInPreferences() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences((Context)(this.context)).edit();
        editor.putString("PREFS_VERSION_KEY", this.thisVersion);
        editor.commit();
    }

    public boolean firstRun() {
        if (!(this.lastVersion.equals((Object)(this.thisVersion)))) return true;
        return false;
    }

    public boolean firstRunEver() {
        return "".equals((Object)(this.lastVersion));
    }

    public String getFullLog() {
        return this.getLog(true);
    }

    public AlertDialog getFullLogDialog() {
        return this.getDialog(true);
    }

    public String getLastVersion() {
        return this.lastVersion;
    }

    public String getLog() {
        return this.getLog(false);
    }

    public AlertDialog getLogDialog() {
        return this.getDialog(this.firstRunEver());
    }

    public String getThisVersion() {
        return this.thisVersion;
    }

    void setLastVersion(String string) {
        this.lastVersion = string;
    }

    @SuppressLint(value={"NewApi"})
    static class Compatibility {
        Compatibility() {
        }

        static void setViewLayerTypeSoftware(View view) {
            view.setLayerType(1, (Paint)(null));
        }
    }

    static final class Listmode
    extends Enum<Listmode> {
        private static final /* synthetic */ Listmode[] ENUM$VALUES;
        public static final /* enum */ Listmode NONE = new Listmode("NONE", 0);
        public static final /* enum */ Listmode ORDERED = new Listmode("ORDERED", 1);
        public static final /* enum */ Listmode UNORDERED = new Listmode("UNORDERED", 2);

        static {
            Listmode[] arrlistmode = new Listmode[]{Listmode.NONE, Listmode.ORDERED, Listmode.UNORDERED};
            Listmode.ENUM$VALUES = arrlistmode;
        }

        private Listmode(String string2, int string2) {
            String string3;
            int n;
            super(string3, n);
        }

        public static Listmode valueOf(String string) {
            return (Listmode)(Enum.valueOf((Class)(Listmode.class), (String)(string)));
        }

        public static Listmode[] values() {
            Listmode[] arrlistmode = Listmode.ENUM$VALUES;
            int n = arrlistmode.length;
            Listmode[] arrlistmode2 = new Listmode[n];
            System.arraycopy((Object)(arrlistmode), (int)(0), (Object)(arrlistmode2), (int)(0), (int)(n));
            return arrlistmode2;
        }
    }

}

