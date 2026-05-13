//package luni.ether.feature.module;
//
//import luni.ether.feature.module.mods.QoL.ChatEnhancer;
//import luni.ether.feature.module.mods.render.ServerInfoModule;
//import luni.ether.feature.module.mods.render.TestModule;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class ModuleManager {
//
//    private static final List<Module> modules = new ArrayList<>();
//
//    public static void init() {
//        // ALWAYS use register()
//        register(new TestModule());
//        register(new ServerInfoModule());
//        register(new ChatEnhancer());
//        //register(new CapePhysics());
//        //register(new Fullbright());
//    }
//
//    // =========================
//    // Registration
//    // =========================
//
//    public static void register(Module module) {
//        if (module == null) return;
//
//        // prevent duplicates
//        if (modules.contains(module)) {
//            System.out.println("[Ether] Tried to register duplicate module: " + module.getName());
//            return;
//        }
//
//        modules.add(module);
//    }
//
//    // =========================
//    // Key Handling (VERY IMPORTANT)
//    // =========================
//
//    public static void onKeyPressed(int key) {
//        if (key == -1) return;
//
//        for (Module module : modules) {
//            if (module.getKey() == key) {
//                module.toggle();
//            }
//        }
//    }
//
//    // =========================
//    // Getters
//    // =========================
//
//    public static List<Module> getModules() {
//        return Collections.unmodifiableList(modules);
//    }
//
//    public static List<Module> getEnabledModules() {
//        return modules.stream()
//                .filter(Module::isEnabled)
//                .toList();
//    }
//
//    public static Module getByName(String name) {
//        return modules.stream()
//                .filter(m -> m.getName().equalsIgnoreCase(name))
//                .findFirst()
//                .orElse(null);
//    }
//
//    // renamed because returning ONE is misleading
//    public static List<Module> getByKey(int key) {
//        return modules.stream()
//                .filter(m -> m.getKey() == key)
//                .toList();
//    }
//
//    public static List<Module> getModulesByCategory(Category category) {
//        return getModules().stream()
//                .filter(m -> m.getCategory() == category)
//                .toList();
//    }
//
//    public static <T extends Module> T get(Class<T> clazz) {
//        for (Module module : modules) {
//            if (clazz.isInstance(module)) {
//                return clazz.cast(module);
//            }
//        }
//        return null;
//    }
//}

package luni.ether.feature.module;

import luni.ether.core.EtherClient;
import luni.ether.core.event.EventHandler;
import luni.ether.core.event.impl.TickEvent;
import luni.ether.feature.module.mods.QoL.ChatEnhancer;
import luni.ether.feature.module.mods.misc.ClickGUI;
import luni.ether.feature.module.mods.render.Freelook;
import luni.ether.feature.module.mods.render.Fullbright;
import luni.ether.feature.module.mods.render.ServerInfoModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();



    public void init() {
        register(new ClickGUI());
        register(new ChatEnhancer());
        register(new Freelook());
        register(new Fullbright());
        register(new ServerInfoModule());

        EtherClient.get()
                .getContext()
                .getEventBus()
                .register(this);
        // Register modules here (later)
    }



    public void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module getByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::isEnabled)
                .toList();
    }

    public List<Module> getByCategory(Category category) {
        List<Module> result = new ArrayList<>();

        for (Module m : modules) {
            if (m.getCategory() == category) {
                result.add(m);
            }
        }

        return result;
    }

    public void onKeyPressed(int key) {
        for (Module module : modules) {
            if (module.getKey() == key) {
                module.toggle();
            }
        }
    }

    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    public void onRender() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender();
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

}