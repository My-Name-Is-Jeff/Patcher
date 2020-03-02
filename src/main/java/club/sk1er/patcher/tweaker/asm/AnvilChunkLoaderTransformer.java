package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class AnvilChunkLoaderTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.world.chunk.storage.AnvilChunkLoader"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("loadChunk__Async")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && FMLDeobfuscatingRemapper.INSTANCE.map(((MethodInsnNode) next).name).equals("read")) {
                        methodNode.instructions.insertBefore(next.getNext().getNext(), closeInputStream());
                    }
                }
            }
        }
    }

    private InsnList closeInputStream() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 6));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/DataInputStream", "close", "()V", false));
        return list;
    }
}