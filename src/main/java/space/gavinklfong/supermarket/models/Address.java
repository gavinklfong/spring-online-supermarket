package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@UserDefinedType("address_type")
public class Address {
    @Column("street_address")
    String streetAddress;

    @Column("city")
    String city;

    @Column("country")
    String country;

    @Column("zipcode")
    String zipcode;
}
