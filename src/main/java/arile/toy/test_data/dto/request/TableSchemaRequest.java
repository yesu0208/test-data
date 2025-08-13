package arile.toy.test_data.dto.request;

import arile.toy.test_data.dto.TableSchemaDto;

import java.util.List;
import java.util.stream.Collectors;

public record TableSchemaRequest(
        String schemaName,
        String userId,
        List<SchemaFieldRequest> schemaFields
) {
    public static TableSchemaRequest of(
            String schemaName,
            String userId,
            List<SchemaFieldRequest> schemaFields
    ){
        return new TableSchemaRequest(schemaName, userId, schemaFields);
    }

    // request -> Dto
    public TableSchemaDto toDto() {
        return TableSchemaDto.of(
                schemaName(),
                userId(),
                null,
                schemaFields.stream()
                        .map(SchemaFieldRequest::toDto)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    // Dto -> request는 필요 x (request는 주는 것은 아니므로)
}
