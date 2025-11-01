package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.RoomDTO;
import com.dcm.demo.model.Room;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RoomMapper extends BaseMapper<Room, RoomDTO, RoomDTO> {
}
