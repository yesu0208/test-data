package arile.toy.test_data.dto.request;

import arile.toy.test_data.dto.TableSchemaDto;

import java.util.List;
import java.util.stream.Collectors;

public record TableSchemaRequest(
        String schemaName,
        List<SchemaFieldRequest> schemaFields
) {
    public static TableSchemaRequest of(
            String schemaName,
            List<SchemaFieldRequest> schemaFields
    ){
        return new TableSchemaRequest(schemaName, schemaFields);
    }

    // request -> Dto
    public TableSchemaDto toDto(String userId) {
        return TableSchemaDto.of(
                schemaName(),
                userId,
                null,
                schemaFields.stream()
                        .map(SchemaFieldRequest::toDto)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    // Dto -> request는 필요 x (request는 주는 것은 아니므로)
}
