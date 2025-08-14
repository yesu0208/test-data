package arile.toy.test_data.service;

import arile.toy.test_data.domain.TableSchema;
import arile.toy.test_data.domain.constant.ExportFileType;
import arile.toy.test_data.dto.TableSchemaDto;
import arile.toy.test_data.repository.TableSchemaRepository;
import arile.toy.test_data.service.exporter.MockDataFileExporterContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SchemaExportService {

    private final MockDataFileExporterContext mockDataFileExporterContext;
    private final TableSchemaRepository tableSchemaRepository;

    public String export(ExportFileType fileType, TableSchemaDto dto, Integer rowCount){

        if (dto.userId() != null) { // 로그인 한 경우 + 기존에 저장된 것일 때
            tableSchemaRepository.findByUserIdAndSchemaName(dto.userId(), dto.schemaName()) // optional
                    .ifPresent(TableSchema::markExported); // 이미 존재한 것이면, exportedAt 입력
        }
        // 로그인 안하거나, 했지만 저장 안한 경우
        return mockDataFileExporterContext.export(fileType, dto, rowCount);
    }
}
