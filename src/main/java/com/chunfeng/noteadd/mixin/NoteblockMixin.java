package com.chunfeng.noteadd.mixin;

import com.chunfeng.noteadd.config.SoundConfig;
import com.chunfeng.noteadd.block.NoteRegulatorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
<<<<<<< HEAD
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
=======
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

<<<<<<< HEAD
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
@Mixin(NoteBlock.class)
public class NoteblockMixin {

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

<<<<<<< HEAD
	@Unique
	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	@Unique
	private static final ThreadLocal<net.minecraft.util.math.random.Random> THREAD_LOCAL_RANDOM =
			ThreadLocal.withInitial(Random::createThreadSafe);

=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
	@Inject(
			method = "onSyncedBlockEvent",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
<<<<<<< HEAD
	private void adjustNote(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
=======
	private void adjustPitch(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
		BlockPos abovePos = pos.up();
		BlockEntity blockEntity = world.getBlockEntity(abovePos);

		SoundEvent soundEvent = state.get(NoteBlock.INSTRUMENT).getSound().value();
		int note = state.get(NoteBlock.NOTE);
		float pitch = NoteBlock.getNotePitch(note);
		float volume = 1.0F;
<<<<<<< HEAD
		int delay = 0;
=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b

		// 检查下方方块是否有自定义映射
		BlockPos belowPos = pos.down();
		Block belowBlock = world.getBlockState(belowPos).getBlock();
		String blockId = Registries.BLOCK.getId(belowBlock).toString();
		// 自定义映射存在就使用其对应的音色，不存在则使用原版默认
		for (SoundConfig.SoundMapping mapping : SoundConfig.getAllMappings()) {
			if (mapping.getBlock().equals(blockId)) {
				Identifier soundId = Identifier.tryParse(mapping.getSound());
				if (soundId != null) {
					soundEvent = Registries.SOUND_EVENT.get(soundId);
					// 如果未注册，创建临时音效对象
					if (soundEvent == null) {
						soundEvent = SoundEvent.of(soundId);
					}
				}
				break;
			}
		}

<<<<<<< HEAD
		// 检查上方方块是否为音符盒调节器
		if (blockEntity instanceof NoteRegulatorEntity regulator) {
            int centOffset = regulator.getCent();
			int octaveOffset = regulator.getOctave();
			int volumeValue = regulator.getVolume();
			delay = regulator.getDelay();

			double totalCentOffset = centOffset + (octaveOffset * 1200.0);
			double adjustment = Math.pow(2.0, totalCentOffset / 1200.0);
			volume = volumeValue / 100.0F;
			pitch = (float) (pitch * adjustment);
		}

		// 延迟播放（有点bug）
		if (delay > 0) {
			SoundEvent finalSoundEvent = soundEvent;
			float finalPitch = pitch;
			float finalVolume = volume;

			long delayNanos = delay * 1_000_000L;

			scheduler.schedule(() -> {
				Random threadRandom = THREAD_LOCAL_RANDOM.get();
				long seed = threadRandom.nextLong();

				world.playSound(
						null,
						pos.getX() + 0.5,
						pos.getY() + 0.5,
						pos.getZ() + 0.5,
						finalSoundEvent,
						SoundCategory.RECORDS,
						finalVolume,
						finalPitch,
						seed
				);
			}, delayNanos, TimeUnit.NANOSECONDS);
		} else {
			world.playSound(
					null,
					pos.getX() + 0.5,
					pos.getY() + 0.5,
					pos.getZ() + 0.5,
					soundEvent,
					SoundCategory.RECORDS,
					volume,
					pitch,
					world.random.nextLong()
			);
		}
=======
		// 检查上方方块是否有音符盒调节器
		if (blockEntity instanceof NoteRegulatorEntity) {
			NoteRegulatorEntity centBlockEntity = (NoteRegulatorEntity) blockEntity;

			// 从方块实体获取数据
			int centOffset = centBlockEntity.getCentOffset();
			int octaveOffset = centBlockEntity.getOctaveOffset();
			int volumeValue = centBlockEntity.getVolume();
			// 总音分偏移 = 音分偏移 + 八度偏移（1八度=1200音分）
			double totalCentOffset = centOffset + (octaveOffset * 1200.0);
			// 计算调整因子
			double adjustment = Math.pow(2.0, totalCentOffset / 1200.0);
			// 计算最终音量
			volume = volumeValue / 100.0F;
			// 计算最终音高
			pitch = (float) (pitch * adjustment);
		}

		world.playSound(
				null,
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5,
				soundEvent,
				SoundCategory.RECORDS,
				volume,
				pitch,
				world.random.nextLong()
		);
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b

		cir.setReturnValue(true);
		cir.cancel();
	}
}