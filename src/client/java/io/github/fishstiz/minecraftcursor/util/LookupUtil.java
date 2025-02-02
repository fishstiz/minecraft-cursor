package io.github.fishstiz.minecraftcursor.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

public class LookupUtil {
    public static final String NAMESPACE = "intermediary";
    public static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();

    public static MethodHandle getMethodHandle(Class<?> targetClass, String methodName, String returnTypeName) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException {
        Class<?> returnTypeClass = Class.forName(RESOLVER.mapClassName(NAMESPACE, returnTypeName));
        return getMethodHandle(targetClass, methodName, returnTypeClass);
    }

    public static MethodHandle getMethodHandle(Class<?> targetClass, String methodName, Class<?> returnType, Class<?>... argTypes) throws IllegalAccessException, NoSuchMethodException {
        return getMethodHandle(targetClass, methodName, getMethodDescriptor(returnType, argTypes), returnType, argTypes);
    }

    public static MethodHandle getMethodHandle(Class<?> targetClass, String methodName, String descriptor, Class<?> returnType, Class<?>... argTypes) throws IllegalAccessException, NoSuchMethodException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        return lookup.findVirtual(targetClass,
                RESOLVER.mapMethodName(
                        NAMESPACE,
                        RESOLVER.unmapClassName(NAMESPACE, targetClass.getName()),
                        methodName,
                        descriptor
                ),
                MethodType.methodType(returnType).appendParameterTypes(argTypes)
        );
    }

    public static VarHandle getVarHandle(String targetClassName, String fieldName, Class<?> type) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> targetClass = Class.forName(RESOLVER.mapClassName(NAMESPACE, targetClassName));
        return getVarHandle(targetClass, fieldName, type);
    }

    public static VarHandle getVarHandle(Class<?> targetClass, String fieldName, Class<?> type) throws IllegalAccessException, NoSuchFieldException {
        return getVarHandle(targetClass, fieldName, getTypeDescriptor(type), type);
    }

    public static VarHandle getVarHandle(Class<?> targetClass, String fieldName, String descriptor, Class<?> type) throws IllegalAccessException, NoSuchFieldException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        return lookup.findVarHandle(targetClass,
                RESOLVER.mapFieldName(
                        NAMESPACE,
                        RESOLVER.unmapClassName(NAMESPACE, targetClass.getName()),
                        fieldName,
                        descriptor
                ),
                type
        );
    }

    public static VarHandle getStaticVarHandle(Class<?> targetClass, String fieldName, String descriptor, Class<?> type) throws IllegalAccessException, NoSuchFieldException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        return lookup.findStaticVarHandle(targetClass,
                RESOLVER.mapFieldName(
                        NAMESPACE,
                        RESOLVER.unmapClassName(NAMESPACE, targetClass.getName()),
                        fieldName,
                        descriptor
                ),
                type
        );
    }

    public static String getMethodDescriptor(Class<?> returnType, Class<?>... argTypes) {
        StringBuilder descriptor = new StringBuilder();

        descriptor.append("(");
        for (Class<?> argType : argTypes) {
            descriptor.append(getTypeDescriptor(argType));
        }
        descriptor.append(")");

        descriptor.append(getTypeDescriptor(returnType));

        return descriptor.toString();
    }

    private static String getTypeDescriptor(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == void.class) return "V";
            if (type == boolean.class) return "Z";
            if (type == byte.class) return "B";
            if (type == char.class) return "C";
            if (type == short.class) return "S";
            if (type == int.class) return "I";
            if (type == long.class) return "J";
            if (type == float.class) return "F";
            if (type == double.class) return "D";
        } else if (type.isArray()) {
            return "[" + getTypeDescriptor(type.getComponentType());
        } else {
            return getReferenceDescriptor(type);
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }

    private static String getReferenceDescriptor(Class<?> type) {
        String className = RESOLVER.unmapClassName(NAMESPACE, type.getName());
        return "L" + className.replace('.', '/') + ";";
    }
}
