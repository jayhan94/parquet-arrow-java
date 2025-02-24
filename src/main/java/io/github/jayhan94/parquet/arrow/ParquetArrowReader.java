package io.github.jayhan94.parquet.arrow;

import java.io.Closeable;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class ParquetArrowReader implements Closeable {
    private final Arena arena;
    private final Linker linker;
    private final SymbolLookup symbolLookup;

    // state
    private final MemorySegment reader;

    // function handle
    private final MethodHandle nextBatchFuncHandle;

    public ParquetArrowReader(String path) throws IOException {
        this.arena = Arena.ofShared();
        linker = Linker.nativeLinker();
        symbolLookup = SymbolLookup.libraryLookup(NativeLibInfo.libPath(), arena);
        MemorySegment newParquetReaderFunc = symbolLookup.findOrThrow("new_parquet_reader");
        FunctionDescriptor parquetReaderFuncDesc = FunctionDescriptor.of(ValueLayout.ADDRESS,
                                                                         ValueLayout.ADDRESS);
        MethodHandle handle = linker.downcallHandle(newParquetReaderFunc, parquetReaderFuncDesc);
        try {
            reader = (MemorySegment) handle.invoke(arena.allocateFrom(path));
        } catch (Throwable e) {
            throw new IOException(e);
        }

        MemorySegment nextBatchFunc = symbolLookup.findOrThrow("next_batch");
        FunctionDescriptor nextBatchFuncDesc = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
        nextBatchFuncHandle = linker.downcallHandle(nextBatchFunc, nextBatchFuncDesc);
    }

    public boolean hasNext() {
        return true;
    }

    public void nextBatch() throws IOException {
        try {
            nextBatchFuncHandle.invoke(reader);
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            MemorySegment destroyFunc = symbolLookup.findOrThrow("destroy_reader");
            FunctionDescriptor destroyDesc = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
            MethodHandle handle = linker.downcallHandle(destroyFunc, destroyDesc);
            handle.invoke(reader);
        } catch (Throwable throwable) {
            throw new IOException(throwable);
        } finally {
            arena.close();
        }
    }
}
