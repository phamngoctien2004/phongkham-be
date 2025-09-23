package com.dcm.demo.mapper;

public interface BaseMapper <E,Req,Res> {
    E toEntity(Req request);
    Res toResponse(E entity);
}
