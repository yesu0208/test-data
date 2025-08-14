package arile.toy.test_data.dto.request;

import arile.toy.test_data.domain.constant.ExportFileType;
import arile.toy.test_data.dto.TableSchemaDto;

import java.util.List;
import java.util.stream.Collectors;

public record TableSchemaExportRequest(
        String schemaName,
        Integer rowCount,
        ExportFileType fileType,
        List<SchemaFieldRequest> schemaFields
) {
    public static TableSchemaExportRequest of(
            String schemaName,
            Integer rowCount,
            ExportFileType fileType,
            List<SchemaFieldRequest> schemaFields
    ){
        return new TableSchemaExportRequest(schemaName, rowCount, fileType, schemaFields);
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
