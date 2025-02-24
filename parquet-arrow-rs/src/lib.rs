use parquet::arrow::arrow_reader::{ParquetRecordBatchReader, ParquetRecordBatchReaderBuilder};
use std::ffi::{c_char, CString};

#[unsafe(no_mangle)]
pub extern "C" fn new_parquet_reader(file: *const c_char) -> ParquetRecordBatchReader {
    panic!("not impl yet")
}
