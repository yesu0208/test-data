package arile.toy.test_data.service.exporter;

import arile.toy.test_data.domain.constant.ExportFileType;
import org.springframework.stereotype.Component;

@Component
public class TSVFileExporter extends DelimiterBasedFileExporter implements MockDataFileExporter {

    @Override
    public ExportFileType getType() {
        return ExportFileType.TSV;
    }

    @Override
    public String getDelimiter() {
        return "\t";
    }

}
