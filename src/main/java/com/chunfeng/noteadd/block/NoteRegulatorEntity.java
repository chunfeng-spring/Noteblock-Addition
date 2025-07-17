package com.chunfeng.noteadd.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class NoteRegulatorEntity extends BlockEntity {
<<<<<<< HEAD
    private int octave = 0; // 八度偏移：-2~2
    private int cent = 0; // 音分偏移：-100~100
    private int volume = 100; // 音量百分比：0%~100%
    private int delay = 0; // 延迟播放：0-200ms
=======
    private int octaveOffset = 0; // 八度偏移：-2~2
    private int centOffset = 0; // 音分偏移：-100~100
    private int volume = 100; // 音量百分比：0%~100%
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b

    public NoteRegulatorEntity(BlockPos pos, BlockState state) {
        super(NoteRegulator.NOTE_REGULATOR_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
<<<<<<< HEAD
        octave = nbt.getInt("octave");
        cent = nbt.getInt("cent");
        volume = nbt.getInt("volume");
        delay = nbt.getInt("delay");
=======
        octaveOffset = nbt.getInt("octaveOffset");
        centOffset = nbt.getInt("centOffset");
        volume = nbt.getInt("volume");
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
<<<<<<< HEAD
        nbt.putInt("octave", octave);
        nbt.putInt("cent", cent);
        nbt.putInt("volume", volume);
        nbt.putInt("delay", delay);
=======
        nbt.putInt("octaveOffset", octaveOffset);
        nbt.putInt("centOffset", centOffset);
        nbt.putInt("volume", volume);
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

<<<<<<< HEAD
    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
=======
    public int getOctaveOffset() {
        return octaveOffset;
    }

    public void setOctaveOffset(int octaveOffset) {
        this.octaveOffset = octaveOffset;
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

<<<<<<< HEAD
    public int getCent() {
        return cent;
    }

    public void setCent(int cent) {
        this.cent = cent;
=======
    public int getCentOffset() {
        return centOffset;
    }

    public void setCentOffset(int centOffset) {
        this.centOffset = centOffset;
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
<<<<<<< HEAD

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
}