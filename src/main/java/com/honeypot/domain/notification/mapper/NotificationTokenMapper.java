package com.honeypot.domain.notification.mapper;

import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.entity.NotificationToken;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationTokenMapper {

    @Mapping(target = "id", source = "notificationTokenId")
    NotificationToken toEntity(NotificationTokenDto dto);

    @InheritInverseConfiguration
    NotificationTokenDto toDto(NotificationToken entity);

}