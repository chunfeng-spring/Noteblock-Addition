package com.chunfeng.noteadd;

import com.chunfeng.noteadd.block.NoteRegulator;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoteblockAddition implements ModInitializer {
	public static final String MOD_ID = "noteblock-addition";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		NoteRegulatorPacket.registerReceiver();
		NoteRegulator.registerBlock();
		NoteRegulator.registerItems();
		NoteRegulatorPlaceHandler.register();
	}
}