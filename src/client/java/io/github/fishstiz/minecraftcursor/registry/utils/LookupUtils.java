package io.github.fishstiz.minecraftcursor.registry.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

public class LookupUtils {
    public static final String NAMESPACE = "intermediary";
    public static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();

    public static MethodHandle getMethodHandle(Class<?> targetClass, String methodName, String descriptor, Class<?> type, Class<?>... methodTypeArgs) throws IllegalAccessException, NoSuchMethodException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        return lookup.findVirtual(targetClass,
                RESOLVER.mapMethodName(
                        NAMESPACE,
                        RESOLVER.unmapClassName(NAMESPACE, targetClass.getName()),
                        methodName,
                        descriptor
                ),
                MethodType.methodType(type).appendParameterTypes(methodTypeArgs)
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
}
