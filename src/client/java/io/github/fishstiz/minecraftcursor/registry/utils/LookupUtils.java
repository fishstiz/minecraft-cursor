package io.github.fishstiz.minecraftcursor.registry.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

public class LookupUtils {
    public static final String NAMESPACE = "intermediary";
    public static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();

    public static MethodHandle getMethodHandle(Class<?> targetClass, String methodName, String descriptor, Class<?> type, @Nullable Class<?>... argTypes) throws IllegalAccessException, NoSuchMethodException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        return lookup.findVirtual(targetClass,
                RESOLVER.mapMethodName(
                        NAMESPACE,
                        RESOLVER.unmapClassName(NAMESPACE, targetClass.getName()),
                        methodName,
                        descriptor
                ),
                MethodType.methodType(type).appendParameterTypes(argTypes)
        );
    }

    public static VarHandle getVarHandle(Class<?> targetClass, String fieldName, Class<?> type) throws IllegalAccessException, NoSuchFieldException {
        return getVarHandle(targetClass, fieldName, getFieldDescriptor(type), type);
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

    public static String getFieldDescriptor(Class<?> type) {
        String className = RESOLVER.unmapClassName(NAMESPACE, type.getName());
        return "L" + className.replace('.', '/') + ";";
    }
}
