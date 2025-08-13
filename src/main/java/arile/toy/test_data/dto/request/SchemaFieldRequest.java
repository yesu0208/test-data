package arile.toy.test_data.dto.request;

import arile.toy.test_data.domain.constant.MockDataType;
import arile.toy.test_data.dto.SchemaFieldDto;

import java.time.LocalDateTime;

public record SchemaFieldRequest( // id, auditing 필요 x
        String fieldName,
        MockDataType mockDataType,
        Integer fieldOrder,
        Integer blankPercent,
        String typeOptionJson,
        String forceValue
        ) {

    // request -> Dto
    public SchemaFieldDto toDto() {
        return SchemaFieldDto.of(
                this.fieldName(),
                this.mockDataType(),
                this.fieldOrder(),
                this.blankPercent(),
                this.typeOptionJson(),
                this.forceValue()
        );
    }

    // Dto -> request는 필요 x (request는 주는 것은 아니므로)
}
