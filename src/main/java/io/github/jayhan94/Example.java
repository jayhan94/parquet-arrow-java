package io.github.jayhan94;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class Example {
    public static void main(String[] args) {
        NativeLibInfo.load();
        try (Arena arena = Arena.ofConfined()) {
            Linker linker = Linker.nativeLinker();
            SymbolLookup libLookup = SymbolLookup.libraryLookup(NativeLibInfo.libPath(), arena);
            MemorySegment func = libLookup.findOrThrow("new_parquet_reader");
            FunctionDescriptor functionDescriptor = FunctionDescriptor.of(ValueLayout.ADDRESS,
                                                                          ValueLayout.ADDRESS);
            MethodHandle handle = linker.downcallHandle(func, functionDescriptor);
            System.out.println("result is " + handle.invoke(arena.allocateFrom("hello")));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
