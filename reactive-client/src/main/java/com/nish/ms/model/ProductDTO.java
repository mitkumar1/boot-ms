package com.nish.ms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    Long id;

    Categories categories;

    String brandName;

    String description;

    Timestamp dateCreated;
    Timestamp lastModified;

}
