use parquet::arrow::arrow_reader::{ParquetRecordBatchReader, ParquetRecordBatchReaderBuilder};
use std::ffi::{c_char, CStr};
use std::fs::File;

#[unsafe(no_mangle)]
pub extern "C" fn new_parquet_reader(file: *const c_char) -> i64 {
    let path = unsafe { CStr::from_ptr(file).to_str().unwrap().to_string() };
    let file = File::open(path).unwrap();
    let reader = Box::new(
        ParquetRecordBatchReaderBuilder::try_new(file)
            .unwrap()
            .with_batch_size(8192)
            .build()
            .unwrap(),
    );
    // return addr as java long
    Box::into_raw(reader).addr() as i64
}

#[unsafe(no_mangle)]
pub extern "C" fn next_batch(reader_pointer: i64) {
    let mut reader = unsafe {
        (reader_pointer as *mut ParquetRecordBatchReader).as_mut().unwrap()
    };
    match reader.next() {
        None => (),
        Some(result) => match result {
            Ok(batch) => {
                println!("batch is {:?}", batch);
            }
            Err(err) => panic!("{}", format!("{}", err)),
        },
    }
}

#[unsafe(no_mangle)]
pub extern "C" fn destroy_reader(reader_pointer: i64) {
    let _ = unsafe { Box::from_raw(reader_pointer as *mut ParquetRecordBatchReader) };
}
