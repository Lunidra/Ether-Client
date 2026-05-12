package luni.ether.feature.module;

import luni.ether.core.EtherClient;
import luni.ether.core.event.EventHandler;
import luni.ether.core.event.impl.TickEvent;
import luni.ether.feature.module.mods.misc.ClickGUI;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();



    public void init() {
        register(new ClickGUI());

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