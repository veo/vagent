package com.sf;

import java.util.List;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Attach {
    public static void main(String[] args) throws Exception {
        String agentFile = Attach.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            agentFile = java.net.URLDecoder.decode(agentFile, "UTF-8");
        } catch (Exception ignored) {}
        agentFile = new java.io.File(agentFile).getAbsolutePath();
        String agentArgs = agentFile;
        if (args.length > 0) {
            agentArgs = agentArgs + "^" + args[0];
        }
        List<VirtualMachineDescriptor> vmList = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : vmList) {
            String name = vmd.displayName();
            try {
                if (name.length() > 0 && !agentFile.contains(name)) {
                    System.out.println("-------------------");
                    System.out.println("name >>> " + vmd.displayName());
                    System.out.println("id >>> " + vmd.id());
                    VirtualMachine vm = VirtualMachine.attach(vmd);
                    vm.loadAgent(agentFile, agentArgs);
                    vm.detach();
                    System.out.println("success");
                }
            } catch (Exception ignored) {
                System.out.println("fail");
            }
        }
    }
}
