package com.asmirnov.quizlistclient.dto;

import com.asmirnov.quizlistclient.model.Module;

public class ModuleAdditionalInfo {
    private Module module;
    private Integer itemsCount;

    public ModuleAdditionalInfo(Module module, Integer itemsCount) {
        this.module = module;
        this.itemsCount = itemsCount;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }
}
