package com.chunfeng.noteadd.mixin;

import com.chunfeng.noteadd.config.SoundConfig;
import com.chunfeng.noteadd.block.NoteRegulatorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(NoteBlock.class)
public class NoteblockMixin {

	@Unique
	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	@Unique
	private static final ThreadLocal<Random> THREAD_LOCAL_RANDOM =
			ThreadLocal.withInitial(Random::createThreadSafe);

	@Inject(
			method = "playNote(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void forcePlayNote(Entity entity, BlockState state, World world, BlockPos pos, CallbackInfo ci) {
		world.addSyncedBlockEvent(pos, (NoteBlock)(Object)this, 0, 0);
		world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
		ci.cancel();
	}

	@Redirect(
			method = "onSyncedBlockEvent",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"
			)
	)
	private void redirectPlaySound(World world, PlayerEntity player, double x, double y, double z,
								   RegistryEntry<SoundEvent> sound, SoundCategory category,
								   float volume, float pitch, long seed) {
		BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

		// 初始化参数
		float finalVolume = 1.0F;
		float finalPitch = pitch;
		int delay = 0;

		// 处理音符盒调节器
		BlockPos abovePos = pos.up();
		BlockEntity blockEntity = world.getBlockEntity(abovePos);

		if (blockEntity instanceof NoteRegulatorEntity regulator) {
			int centOffset = regulator.getCent();
			int octaveOffset = regulator.getOctave();
			int volumeValue = regulator.getVolume();
			delay = regulator.getDelay();

			double totalCentOffset = centOffset + (octaveOffset * 1200.0);
			double adjustment = Math.pow(2.0, totalCentOffset / 1200.0);
			finalVolume = volumeValue / 100.0f;
			finalPitch = (float) (pitch * adjustment);
		}

		// 处理自定义映射
		BlockPos belowPos = pos.down();
		Block belowBlock = world.getBlockState(belowPos).getBlock();
		String blockId = Registries.BLOCK.getId(belowBlock).toString();
		SoundConfig.SoundMapping[] mappings = SoundConfig.getMappingsArray();
		SoundEvent finalSoundEvent = sound.value(); // 默认使用原版音效

		for (SoundConfig.SoundMapping mapping : mappings) {
			if (mapping.getBlock().equals(blockId)) {
				finalSoundEvent = SoundEvent.of(new Identifier(mapping.getSound()));
				break;
			}
		}

		// 处理延迟播放（有点bug）
		if (delay > 0) {
			SoundEvent delaySoundEvent = finalSoundEvent;
			float delayPitch = finalPitch;
			float delayVolume = finalVolume;
			long delayNanos = delay * 1_000_000L;

			scheduler.schedule(() -> {
				Random threadRandom = THREAD_LOCAL_RANDOM.get();
				long delaySeed = threadRandom.nextLong();

				world.playSound(
						null,
						x, y, z,
						delaySoundEvent,
						SoundCategory.RECORDS,
						delayVolume,
						delayPitch,
						delaySeed
				);
			}, delayNanos, TimeUnit.NANOSECONDS);
		} else {
			world.playSound(
					null,
					x, y, z,
					finalSoundEvent,
					SoundCategory.RECORDS,
					finalVolume,
					finalPitch,
					seed
			);
		}
	}
}