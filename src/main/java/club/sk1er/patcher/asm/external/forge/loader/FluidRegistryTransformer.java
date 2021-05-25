/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.asm.external.forge.loader;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FluidRegistryTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fluids.FluidRegistry"};
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
            String methodName = methodNode.name;

            if (methodName.equals("getBucketFluids")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(returnFasterSet());
                break;
            }
        }
    }

    private InsnList returnFasterSet() {
        InsnList list = new InsnList();
        list.add(
            new FieldInsnNode(
                Opcodes.GETSTATIC,
                "net/minecraftforge/fluids/FluidRegistry",
                "bucketFluids",
                "Ljava/util/Set;"));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "java/util/Collections",
                "unmodifiableSet",
                "(Ljava/util/Set;)Ljava/util/Set;",
                false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}