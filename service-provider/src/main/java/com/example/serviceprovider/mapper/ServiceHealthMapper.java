package com.example.serviceprovider.mapper;

import com.example.serviceprovider.dto.ServiceInformation;
import com.example.serviceprovider.repository.model.ServiceHealthDomain;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceHealthMapper extends EntityMapper<ServiceInformation, ServiceHealthDomain> {
}
