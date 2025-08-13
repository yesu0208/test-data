package arile.toy.test_data.dto.response;

import arile.toy.test_data.dto.TableSchemaDto;

public record SimpleTableSchemaResponse(
        String schemaName,
        String userId
) {

    public static SimpleTableSchemaResponse fromDto(TableSchemaDto dto) {
        return new SimpleTableSchemaResponse(dto.schemaName(), dto.userId());
    }
}
