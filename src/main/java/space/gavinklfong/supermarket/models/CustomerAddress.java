package space.gavinklfong.supermarket.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Frozen;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Map;
import java.util.UUID;

@Builder(toBuilder = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("customer_address")
public class CustomerAddress {

    @PrimaryKey
    CustomerAddressKey customerAddressKey;

    @Frozen
    Address address;
}
