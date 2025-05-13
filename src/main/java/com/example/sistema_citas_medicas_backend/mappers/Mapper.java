package com.example.sistema_citas_medicas_backend.mappers;

public interface Mapper<A, B>{
    B mapTo(A a);
    A mapFrom(B b);
}
