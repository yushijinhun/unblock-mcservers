package yushijinhun.unblockmcservers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BlockedServersTransformer implements ClassFileTransformer {

	private static final Logger LOGGER = Logger.getLogger(BlockedServersTransformer.class.getCanonicalName());
	private static final String[] nonTransformablePackages = new String[] { "java.", "javax.", "com.sun.", "com.oracle.", "jdk.", "sun.", "oracle.", "com.oracle.", "net.java.", "javassist" };

	@Override
	public byte[] transform(ClassLoader loader, final String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {
			try {
				if (canTransform(className)) {
					ClassReader reader = new ClassReader(classfileBuffer);
					ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
					final AtomicBoolean transformed = new AtomicBoolean();
					reader.accept(new ClassVisitor(Opcodes.ASM4, writer) {

						@Override
						public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
							MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
							return new MethodVisitor(Opcodes.ASM4, mv) {

								@Override
								public void visitLdcInsn(Object cst) {
									if (cst instanceof String) {
										String transformedStr = transformString((String) cst);
										if (transformedStr != null) {
											super.visitLdcInsn(transformedStr);
											transformed.set(true);
											LOGGER.log(Level.INFO, "Transformed " + className + " [" + cst + "]->[" + transformedStr + "]");
											return;
										}
									}
									super.visitLdcInsn(cst);
								}

							};
						}

					}, 0);
					if (transformed.get()) {
						return writer.toByteArray();
					}
				}
			} catch (Throwable e) {
				LOGGER.log(Level.WARNING, "Caught an exception when transforming " + className, e);
			}
		}
		return null;
	}

	private boolean canTransform(String classInternalName) {
		String name = classInternalName.replace('/', '.');
		for (String nonTransformablePackage : nonTransformablePackages) {
			if (name.startsWith(nonTransformablePackage)) {
				return false;
			}
		}
		return true;
	}

	private String transformString(String origin) {
		String str = origin;
		str = str.replaceAll("https://sessionserver.mojang.com/blockedservers", "");
		return origin.equals(str) ? null : str;
	}

}
