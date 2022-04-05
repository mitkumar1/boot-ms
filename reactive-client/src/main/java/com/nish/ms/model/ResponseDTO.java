package com.nish.ms.model;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Component
@NoArgsConstructor
//  ResponseDTO = CustomerDTO + ProductDTO
public class ResponseDTO {

	 private  CustomerDTO customer;
	 private  ProductDTO product;
}
