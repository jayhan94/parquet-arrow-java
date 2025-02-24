# parquet-arrow-java

Read/Write parquet files into/from arrow in modern java(23).

I don't want to re-implement this library from scratch because there is already a [Rust
implementation](https://crates.io/crates/parquet) available.
I just need to provide a Java binding for it. I will try to implement this binding using the modern Java
Foreign Function & Memory API.