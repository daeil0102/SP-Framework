package net.teujaem.fabric.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;

public class SPFrameworkDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(SPFrameworkDataGenerator fabricDataGenerator) {
        SPFrameworkDataGenerator.Pack pack = fabricDataGenerator.createPack();
    }
}
