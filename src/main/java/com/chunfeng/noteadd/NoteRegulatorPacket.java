package com.chunfeng.noteadd;

import com.chunfeng.noteadd.block.NoteRegulatorEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class NoteRegulatorPacket {
    public static final Identifier ID = new Identifier("noteblock-addition", "note_regulator");

    public static void sendToServer(BlockPos pos, int octave, int cent, int volume, int delay) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(octave);
        buf.writeInt(cent);
        buf.writeInt(volume);
        buf.writeInt(delay);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void registerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(ID,
                (MinecraftServer server, ServerPlayerEntity player,
                 ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
                    // 读取数据
                    BlockPos pos = buf.readBlockPos();
                    int octave = buf.readInt();
                    int cent = buf.readInt();
                    int volume = buf.readInt();
                    int delay = buf.readInt();

                    server.execute(() -> {
                        // 更新方块实体
                        if (player.getWorld().getBlockEntity(pos) instanceof NoteRegulatorEntity blockEntity) {
                            blockEntity.setOctave(octave);
                            blockEntity.setCent(cent);
                            blockEntity.setVolume(volume);
                            blockEntity.setDelay(delay);
                        }
                    });
                }
        );
    }
}