package com.honeypot.domain.notification.mapper;

import com.honeypot.domain.notification.dto.NotificationHistoryDto;
import com.honeypot.domain.notification.entity.NotificationHistory;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationHistoryMapper {

    @Mapping(target = "id", source = "notificationHistoryId")
    NotificationHistory toEntity(NotificationHistoryDto dto);

    @InheritInverseConfiguration
    NotificationHistoryDto toDto(NotificationHistory entity);

}