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
    private int octaveOffset = 0; // 八度偏移：-2~2
    private int centOffset = 0; // 音分偏移：-100~100
    private int volume = 100; // 音量百分比：0%~100%

    public NoteRegulatorEntity(BlockPos pos, BlockState state) {
        super(NoteRegulator.NOTE_REGULATOR_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        octaveOffset = nbt.getInt("octaveOffset");
        centOffset = nbt.getInt("centOffset");
        volume = nbt.getInt("volume");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("octaveOffset", octaveOffset);
        nbt.putInt("centOffset", centOffset);
        nbt.putInt("volume", volume);
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

    public int getOctaveOffset() {
        return octaveOffset;
    }

    public void setOctaveOffset(int octaveOffset) {
        this.octaveOffset = octaveOffset;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public int getCentOffset() {
        return centOffset;
    }

    public void setCentOffset(int centOffset) {
        this.centOffset = centOffset;
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
}