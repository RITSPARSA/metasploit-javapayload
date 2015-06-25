package com.metasploit.meterpreter.android;

import java.io.File;

import com.metasploit.meterpreter.Meterpreter;
import com.metasploit.meterpreter.TLVPacket;
import com.metasploit.meterpreter.command.Command;

public class check_root_android implements Command {

    private static final int TLV_EXTENSIONS = 20000;
    private static final int TLV_TYPE_CHECK_ROOT_BOOL = TLVPacket.TLV_META_TYPE_BOOL
            | (TLV_EXTENSIONS + 9019);

    @Override
    public int execute(Meterpreter meterpreter, TLVPacket request,
                       TLVPacket response) throws Exception {

        response.addOverflow(TLV_TYPE_CHECK_ROOT_BOOL, isRooted());

        return ERROR_SUCCESS;
    }

    public static boolean isRooted() {
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }

	    //Added check for SuperSU
            file = new File("/system/app/SuperSU.apk");
            if (file.exists()) {
                return true;
            }

	    //Fixed extensions in /system/app/,
	    //as per Samsung Galaxy S4 i9505 running Android v5.0.1
            file = new File("/system/app/Superuser");
            if (file.exists()) {
                return true;
            }

            file = new File("/system/app/SuperSU");
            if (file.exists()) {
                return true;
            }



        } catch (Exception e1) {

        }

        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su")
                || canExecuteCommand("/system/xbin/su")
                || canExecuteCommand("which su");
    }

    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

}
