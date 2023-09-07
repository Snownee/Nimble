package snownee.nimble;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public final class NimbleDatagen implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		generator.createPack().addProvider((FabricDataOutput $) -> new KiwiLanguageProvider($));
	}

}
